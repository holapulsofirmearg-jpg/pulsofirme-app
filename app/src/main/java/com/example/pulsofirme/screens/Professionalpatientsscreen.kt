package com.example.pulsofirme.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

data class PacienteProfesionalFirebase(
    val uid: String = "",
    val nombre: String = "",
    val diagnostico: String = "Sin diagnóstico informado",
    val estado: String = "Activo"
)

// Plan gratuito: 1 paciente. Premium: hasta 5.
private const val MAX_PACIENTES_GRATIS = 1
private const val MAX_PACIENTES_PREMIUM = 5

@Composable
fun ProfessionalPatientsScreen(
    rutaActual: String = "caregiver_patients",
    onNavigate: (String) -> Unit,
    // Se llama al tocar un paciente: manda su UID (el nombre se puede repetir)
    onPacienteClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val profesionalUid = auth.currentUser?.uid

    var busqueda by remember { mutableStateOf("") }
    var codigo by remember { mutableStateOf("") }
    var vinculando by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(true) }
    var esPremium by remember { mutableStateOf(false) }
    var pacientes by remember { mutableStateOf<List<PacienteProfesionalFirebase>>(emptyList()) }
    var pacienteADesvincular by remember { mutableStateOf<PacienteProfesionalFirebase?>(null) }

    val maxPacientes = if (esPremium) MAX_PACIENTES_PREMIUM else MAX_PACIENTES_GRATIS

    fun cargarPacientes() {
        if (profesionalUid == null) { cargando = false; return }
        cargando = true
        db.collection("usuarios").document(profesionalUid).get()
            .addOnSuccessListener { profesional ->
                esPremium = profesional.getBoolean("esPremium") == true ||
                        profesional.getBoolean("premiumDemo") == true

                val uids = (profesional.get("pacientesVinculados") as? List<*>)
                    ?.mapNotNull { it as? String }?.distinct().orEmpty()
                if (uids.isEmpty()) { pacientes = emptyList(); cargando = false; return@addOnSuccessListener }

                val acumulados = mutableListOf<PacienteProfesionalFirebase>()
                val obsoletos = mutableListOf<String>()
                var pendientes = uids.size

                uids.forEach { uid ->
                    db.collection("usuarios").document(uid).get()
                        .addOnCompleteListener { tarea ->
                            val doc = tarea.result
                            if (tarea.isSuccessful && doc != null && doc.exists() && doc.getString("rol") == "paciente") {
                                val diagnosticos = doc.get("diagnosticos") as? List<*>
                                val diagnostico = diagnosticos?.firstOrNull()?.toString()
                                    ?: doc.getString("diagnostico") ?: "Sin diagnóstico informado"
                                val nombreCompleto = listOfNotNull(doc.getString("nombre"), doc.getString("apellido"))
                                    .filter { it.isNotBlank() }.joinToString(" ").ifBlank { "Paciente" }
                                acumulados += PacienteProfesionalFirebase(uid, nombreCompleto, diagnostico, "Activo")
                            } else {
                                // El paciente revocó el acceso o ya no existe: se limpia el vínculo viejo.
                                val ex = tarea.exception
                                val sinPermiso = ex is FirebaseFirestoreException &&
                                        ex.code == FirebaseFirestoreException.Code.PERMISSION_DENIED
                                val noExiste = tarea.isSuccessful && (doc == null || !doc.exists())
                                if (sinPermiso || noExiste) obsoletos += uid
                            }
                            pendientes--
                            if (pendientes == 0) {
                                pacientes = acumulados.sortedBy { it.nombre }
                                cargando = false

                                if (obsoletos.isNotEmpty()) {
                                    val limpieza = hashMapOf<String, Any>(
                                        "pacientesVinculados" to FieldValue.arrayRemove(*obsoletos.toTypedArray())
                                    )
                                    obsoletos.forEach { limpieza["vinculosPacientes.$it"] = FieldValue.delete() }
                                    db.collection("usuarios").document(profesionalUid).update(limpieza)
                                }
                            }
                        }
                }
            }
            .addOnFailureListener { cargando = false; Toast.makeText(context, "No se pudieron cargar los pacientes", Toast.LENGTH_SHORT).show() }
    }

    LaunchedEffect(profesionalUid) { cargarPacientes() }

    fun vincularPaciente() {
        val codigoLimpio = codigo.trim()
        if (profesionalUid == null) { Toast.makeText(context, "No hay sesión profesional", Toast.LENGTH_SHORT).show(); return }
        if (codigoLimpio.length != 6 || !codigoLimpio.all { it.isDigit() }) { Toast.makeText(context, "Ingresá el código de 6 dígitos del paciente", Toast.LENGTH_SHORT).show(); return }
        if (pacientes.size >= maxPacientes) {
            val mensaje = if (esPremium) "Alcanzaste el máximo de $MAX_PACIENTES_PREMIUM pacientes"
            else "El plan gratuito permite vincular $MAX_PACIENTES_GRATIS paciente. Pasate a Premium para vincular hasta $MAX_PACIENTES_PREMIUM."
            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
            return
        }
        vinculando = true

        // El código se canjea en la colección "codigos", que guarda el uid del paciente.
        db.collection("codigos").document(codigoLimpio).get()
            .addOnSuccessListener { codigoDoc ->
                val pacienteUid = codigoDoc.getString("uid").orEmpty()
                if (!codigoDoc.exists() || pacienteUid.isBlank()) {
                    vinculando = false
                    Toast.makeText(context, "No existe un paciente con ese código", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }
                if (pacientes.any { it.uid == pacienteUid }) {
                    vinculando = false
                    Toast.makeText(context, "Ese paciente ya está vinculado", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val profRef = db.collection("usuarios").document(profesionalUid)
                val pacRef = db.collection("usuarios").document(pacienteUid)

                db.runBatch { batch ->
                    batch.update(
                        profRef,
                        mapOf(
                            "pacientesVinculados" to FieldValue.arrayUnion(pacienteUid),
                            "codigoUsado" to codigoLimpio,
                            "vinculosPacientes.$pacienteUid" to FieldValue.serverTimestamp()
                        )
                    )
                    batch.update(pacRef, "profesionalesVinculados", FieldValue.arrayUnion(profesionalUid))
                }.addOnSuccessListener {
                    vinculando = false
                    codigo = ""
                    Toast.makeText(context, "Paciente vinculado correctamente", Toast.LENGTH_SHORT).show()
                    cargarPacientes()
                }.addOnFailureListener {
                    vinculando = false
                    Toast.makeText(context, "No se pudo vincular el paciente", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener {
                vinculando = false
                Toast.makeText(context, "No se pudo verificar el código", Toast.LENGTH_LONG).show()
            }
    }

    fun desvincularPaciente(paciente: PacienteProfesionalFirebase) {
        if (profesionalUid == null) return
        val profRef = db.collection("usuarios").document(profesionalUid)
        val pacRef = db.collection("usuarios").document(paciente.uid)
        db.runBatch { batch ->
            batch.update(
                profRef,
                mapOf(
                    "pacientesVinculados" to FieldValue.arrayRemove(paciente.uid),
                    "vinculosPacientes.${paciente.uid}" to FieldValue.delete()
                )
            )
            batch.update(pacRef, "profesionalesVinculados", FieldValue.arrayRemove(profesionalUid))
        }.addOnSuccessListener {
            pacienteADesvincular = null
            Toast.makeText(context, "Paciente desvinculado", Toast.LENGTH_SHORT).show()
            cargarPacientes()
        }.addOnFailureListener { Toast.makeText(context, "No se pudo desvincular el paciente", Toast.LENGTH_LONG).show() }
    }

    val filtrados = pacientes.filter { busqueda.isBlank() || it.nombre.contains(busqueda, true) || it.diagnostico.contains(busqueda, true) }

    pacienteADesvincular?.let { paciente ->
        AlertDialog(
            onDismissRequest = { pacienteADesvincular = null },
            title = { Text("Desvincular paciente") },
            text = { Text("¿Querés desvincular a ${paciente.nombre}? El paciente no se elimina: solamente se quita el vínculo profesional.") },
            confirmButton = { TextButton(onClick = { desvincularPaciente(paciente) }, modifier = Modifier.heightIn(min = 48.dp)) { Text("Desvincular", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { pacienteADesvincular = null }, modifier = Modifier.heightIn(min = 48.dp)) { Text("Cancelar") } }
        )
    }

    Scaffold(bottomBar = {
        NavigationBar {
            listOf(Triple("Inicio", Icons.Filled.Home, "caregiver_home"), Triple("Pacientes", Icons.Filled.Groups, "caregiver_patients"), Triple("Progreso", Icons.Filled.ShowChart, "caregiver_progress"), Triple("Cuenta", Icons.Filled.Person, "caregiver_account")).forEach { item ->
                NavigationBarItem(selected = rutaActual == item.third, onClick = { onNavigate(item.third) }, icon = { Icon(item.second, item.first) }, label = { Text(item.first, fontSize = 13.sp) })
            }
        }
    }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Text("Pacientes", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text("${pacientes.size}/$maxPacientes pacientes vinculados", color = Color.Gray)
                if (!esPremium) {
                    Text("Plan gratuito: 1 paciente. Con Premium vinculás hasta $MAX_PACIENTES_PREMIUM.", color = Color.Gray, fontSize = 13.sp)
                }
            }
            item {
                OutlinedTextField(value = codigo, onValueChange = { if (it.length <= 6 && it.all(Char::isDigit)) codigo = it }, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp), label = { Text("Código del paciente") }, placeholder = { Text("6 dígitos") }, singleLine = true)
                Spacer(Modifier.height(8.dp))
                Button(onClick = { vincularPaciente() }, enabled = !vinculando && pacientes.size < maxPacientes, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) { Text(if (vinculando) "Vinculando..." else "Vincular paciente") }
            }
            item { OutlinedTextField(value = busqueda, onValueChange = { busqueda = it }, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp), leadingIcon = { Icon(Icons.Filled.Search, null) }, label = { Text("Buscar paciente") }, singleLine = true) }
            if (cargando) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
            if (!cargando && filtrados.isEmpty()) item { Text("Todavía no hay pacientes vinculados.", color = Color.Gray) }
            items(filtrados, key = { it.uid }) { paciente ->
                Card(Modifier.fillMaxWidth().clickable(role = Role.Button, onClickLabel = "Ver paciente ${paciente.nombre}") { onPacienteClick(paciente.uid) }, shape = RoundedCornerShape(16.dp)) {
                    Row(Modifier.fillMaxWidth().heightIn(min = 72.dp).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) { Text(paciente.nombre, fontWeight = FontWeight.Bold); Text(paciente.diagnostico, color = Color.Gray); Text(paciente.estado, color = Color(0xFF1B7A4B)) }
                        IconButton(onClick = { pacienteADesvincular = paciente }, modifier = Modifier.size(56.dp)) { Icon(Icons.Filled.DeleteOutline, "Desvincular", tint = MaterialTheme.colorScheme.error) }
                        Icon(Icons.Filled.ChevronRight, null)
                    }
                }
            }
        }
    }
}