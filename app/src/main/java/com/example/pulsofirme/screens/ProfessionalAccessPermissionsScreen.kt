package com.example.pulsofirme.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

private val VerdePA = Color(0xFF07884A)
private val VerdeClaroPA = Color(0xFFEAF8F0)
private val AzulClaroPA = Color(0xFFEEF7F8)
private val AzulTextoPA = Color(0xFF17365D)
private val GrisTextoPA = Color(0xFF7A8294)
private val BordePA = Color(0xFFE5E7EB)

data class PacientePermisosProfesional(
    val uid: String,
    val nombre: String,
    val vinculadoDesde: String
)

private data class PermisoDefinido(
    val clave: String,
    val icono: ImageVector,
    val titulo: String,
    val descripcion: String,
    val descripcionBloqueada: String
)

// Lo que el paciente autoriza por defecto. Si el paciente define algo distinto
// en usuarios/{paciente}/permisosProfesionales/{uidProfesional}, eso tiene prioridad.
private val AUTORIZADO_POR_DEFECTO = mapOf(
    "progreso" to true,
    "quickdash" to true,
    "medicacion" to true,
    "datosMedicos" to true,
    "ubicacion" to false,
    "reportes" to true
)

private val PERMISOS_DEFINIDOS = listOf(
    PermisoDefinido(
        "progreso", Icons.Filled.ShowChart, "Progreso y ejercicios",
        "Podés ver su progreso, ejercicios completados y estadísticas.",
        "El paciente no ha autorizado compartir su progreso."
    ),
    PermisoDefinido(
        "quickdash", Icons.Filled.Description, "Resultados QuickDASH",
        "Podés ver sus resultados y evolución en QuickDASH.",
        "El paciente no ha autorizado compartir sus resultados."
    ),
    PermisoDefinido(
        "medicacion", Icons.Filled.Notifications, "Alertas de medicación",
        "Podés ver sus tomas registradas y recibir alertas.",
        "El paciente no ha autorizado compartir su medicación."
    ),
    PermisoDefinido(
        "datosMedicos", Icons.Filled.Favorite, "Datos médicos",
        "Podés ver su diagnóstico, notas médicas y zona tratada.",
        "El paciente no ha autorizado compartir sus datos médicos."
    ),
    PermisoDefinido(
        "ubicacion", Icons.Filled.LocationOn, "Ubicación",
        "Podés ver su ubicación cuando la comparte.",
        "El paciente no ha autorizado compartir su ubicación."
    ),
    PermisoDefinido(
        "reportes", Icons.Filled.BarChart, "Reportes de evolución",
        "Podés ver y descargar sus reportes de evolución.",
        "El paciente no ha autorizado compartir sus reportes."
    )
)

private fun formatearFechaPA(timestamp: Timestamp): String =
    SimpleDateFormat("dd/MM/yyyy", Locale("es", "AR")).format(timestamp.toDate())

@Composable
fun ProfessionalAccessPermissionsScreen(
    onBack: () -> Unit,
    onPacienteClick: (String, String) -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val profesionalUid = auth.currentUser?.uid

    var cargando by remember { mutableStateOf(true) }
    var pacientes by remember { mutableStateOf<List<PacientePermisosProfesional>>(emptyList()) }

    LaunchedEffect(profesionalUid) {
        if (profesionalUid == null) {
            cargando = false
            return@LaunchedEffect
        }

        db.collection("usuarios").document(profesionalUid).get()
            .addOnSuccessListener { profesional ->
                val uids = (profesional.get("pacientesVinculados") as? List<*>)
                    ?.mapNotNull { it as? String }?.distinct().orEmpty()
                val fechas = profesional.get("vinculosPacientes") as? Map<*, *>

                if (uids.isEmpty()) {
                    pacientes = emptyList()
                    cargando = false
                    return@addOnSuccessListener
                }

                val acumulados = mutableListOf<PacientePermisosProfesional>()
                var pendientes = uids.size

                uids.forEach { uid ->
                    db.collection("usuarios").document(uid).get()
                        .addOnCompleteListener { tarea ->
                            val doc = tarea.result
                            if (tarea.isSuccessful && doc != null && doc.exists() && doc.getString("rol") == "paciente") {
                                val nombreCompleto = listOfNotNull(doc.getString("nombre"), doc.getString("apellido"))
                                    .filter { it.isNotBlank() }
                                    .joinToString(" ")
                                    .ifBlank { "Paciente" }
                                val fecha = (fechas?.get(uid) as? Timestamp)?.let { formatearFechaPA(it) }.orEmpty()
                                acumulados += PacientePermisosProfesional(uid, nombreCompleto, fecha)
                            }
                            pendientes--
                            if (pendientes == 0) {
                                pacientes = acumulados.sortedBy { it.nombre }
                                cargando = false
                            }
                        }
                }
            }
            .addOnFailureListener {
                cargando = false
                Toast.makeText(context, "No se pudieron cargar los pacientes", Toast.LENGTH_SHORT).show()
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(42.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = AzulTextoPA
                )
            }
            Text(
                "Permisos de acceso",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AzulTextoPA
            )
        }

        Spacer(Modifier.height(18.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(VerdeClaroPA)
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            CajaIconoPA(Icons.Filled.Key)

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    "Configurá qué información podés consultar de cada paciente.",
                    color = AzulTextoPA,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 21.sp
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    "Algunos permisos requieren autorización del paciente.",
                    color = GrisTextoPA,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(Modifier.height(26.dp))

        Text(
            "Pacientes vinculados (${pacientes.size})",
            color = AzulTextoPA,
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp
        )

        Spacer(Modifier.height(12.dp))

        if (cargando) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        } else if (pacientes.isEmpty()) {
            Text(
                "Todavía no tenés pacientes vinculados.",
                color = GrisTextoPA,
                fontSize = 14.sp
            )
        }

        pacientes.forEach { paciente ->
            PacientePermisoFila(
                paciente = paciente,
                onClick = { onPacienteClick(paciente.uid, paciente.nombre) }
            )
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun PacientePermisoFila(
    paciente: PacientePermisosProfesional,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF8FAF9))
            .clickable { onClick() }
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(VerdeClaroPA),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Person,
                contentDescription = null,
                tint = VerdePA,
                modifier = Modifier.size(27.dp)
            )
        }

        Spacer(Modifier.width(13.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                paciente.nombre,
                color = AzulTextoPA,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
            if (paciente.vinculadoDesde.isNotBlank()) {
                Spacer(Modifier.height(3.dp))
                Text(
                    "Vinculado desde ${paciente.vinculadoDesde}",
                    color = GrisTextoPA,
                    fontSize = 13.sp
                )
            }
        }

        Icon(
            Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = GrisTextoPA
        )
    }
}

@Composable
fun ProfessionalPatientPermissionsScreen(
    pacienteUid: String,
    nombrePaciente: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val profesionalUid = auth.currentUser?.uid

    var cargando by remember { mutableStateOf(true) }
    var fecha by remember { mutableStateOf("") }

    // Lo que el paciente autoriza (el profesional no lo puede cambiar).
    var autorizados by remember { mutableStateOf(AUTORIZADO_POR_DEFECTO) }

    // Lo que el profesional decide ver dentro de lo autorizado.
    var preferencias by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }

    LaunchedEffect(pacienteUid, profesionalUid) {
        if (profesionalUid == null || pacienteUid.isBlank()) {
            cargando = false
            return@LaunchedEffect
        }

        db.collection("usuarios").document(profesionalUid).get()
            .addOnSuccessListener { profesional ->
                val prefs = (profesional.get("preferenciasPacientes") as? Map<*, *>)
                    ?.get(pacienteUid) as? Map<*, *>

                preferencias = prefs?.entries
                    ?.mapNotNull { (clave, valor) ->
                        if (clave is String && valor is Boolean) clave to valor else null
                    }
                    ?.toMap()
                    .orEmpty()

                fecha = ((profesional.get("vinculosPacientes") as? Map<*, *>)
                    ?.get(pacienteUid) as? Timestamp)
                    ?.let { formatearFechaPA(it) }
                    .orEmpty()
            }

        db.collection("usuarios").document(pacienteUid).get()
            .addOnSuccessListener { paciente ->
                val concedidos = (paciente.get("permisosProfesionales") as? Map<*, *>)
                    ?.get(profesionalUid) as? Map<*, *>

                autorizados = AUTORIZADO_POR_DEFECTO.mapValues { (clave, porDefecto) ->
                    (concedidos?.get(clave) as? Boolean) ?: porDefecto
                }
                cargando = false
            }
            .addOnFailureListener {
                cargando = false
                Toast.makeText(
                    context,
                    "No se pudieron cargar los permisos del paciente",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun cambiarPreferencia(clave: String, valor: Boolean) {
        if (profesionalUid == null) return
        val anterior = preferencias
        preferencias = preferencias + (clave to valor)

        db.collection("usuarios").document(profesionalUid)
            .update("preferenciasPacientes.$pacienteUid.$clave", valor)
            .addOnFailureListener {
                preferencias = anterior
                Toast.makeText(
                    context,
                    "No se pudo guardar el cambio",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(42.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = AzulTextoPA
                )
            }
            Text(
                "Permisos de acceso",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AzulTextoPA
            )
        }

        Spacer(Modifier.height(18.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFF8FAF9))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(VerdeClaroPA),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = null,
                    tint = VerdePA,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    nombrePaciente.ifBlank { "Paciente" },
                    color = AzulTextoPA,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                if (fecha.isNotBlank()) {
                    Text(
                        "Vinculado desde $fecha",
                        color = GrisTextoPA,
                        fontSize = 13.sp
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(50),
                color = VerdeClaroPA
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = VerdePA,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Autorizado",
                        color = VerdePA,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(25.dp))

        Text(
            "Información que podés consultar",
            color = AzulTextoPA,
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp
        )

        Spacer(Modifier.height(10.dp))

        if (cargando) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }

        PERMISOS_DEFINIDOS.forEach { permiso ->
            val autorizado = autorizados[permiso.clave] ?: false

            PermisoDetallePA(
                icono = permiso.icono,
                titulo = permiso.titulo,
                descripcion = if (autorizado) permiso.descripcion else permiso.descripcionBloqueada,
                checked = autorizado && (preferencias[permiso.clave] ?: true),
                onCheckedChange = { cambiarPreferencia(permiso.clave, it) },
                bloqueado = !autorizado
            )
        }

        Spacer(Modifier.height(18.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(VerdeClaroPA)
                .padding(15.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Filled.Info,
                contentDescription = null,
                tint = VerdePA,
                modifier = Modifier.size(21.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "Si algún permiso aparece desactivado y bloqueado, significa que el paciente no lo ha autorizado aún.",
                color = GrisTextoPA,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }

        Spacer(Modifier.height(30.dp))
    }
}

@Composable
private fun PermisoDetallePA(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    bloqueado: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CajaIconoPA(icono)

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                titulo,
                color = AzulTextoPA,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(3.dp))
            Text(
                descripcion,
                color = GrisTextoPA,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            if (bloqueado) {
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Lock,
                        contentDescription = null,
                        tint = Color(0xFFA0A7B4),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        "Requiere autorización del paciente.",
                        color = Color(0xFFA0A7B4),
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(Modifier.width(10.dp))

        Switch(
            checked = checked,
            onCheckedChange = if (bloqueado) null else onCheckedChange,
            enabled = !bloqueado,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VerdePA,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFD9D6DE),
                disabledUncheckedThumbColor = Color(0xFF9894A1),
                disabledUncheckedTrackColor = Color(0xFFE5E2E8)
            )
        )
    }

    HorizontalDivider(color = BordePA)
}

@Composable
private fun CajaIconoPA(icono: ImageVector) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(VerdeClaroPA),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icono,
            contentDescription = null,
            tint = VerdePA,
            modifier = Modifier.size(23.dp)
        )
    }
}