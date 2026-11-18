package com.example.pulsofirme.screens

import androidx.compose.runtime.saveable.rememberSaveable

import com.example.pulsofirme.ui.theme.enTema

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Locale

private val VerdeNotifPro = Color(0xFF07884A)
private val VerdeClaroNotifPro = Color(0xFFEAF8F0)
private val GrisNotifPro: Color
    @Composable get() = Color(0xFF6B7280).enTema()
private val NaranjaNotifPro = Color(0xFFF59E0B)

data class NotificacionProfesionalFirebase(
    val id: String, val pacienteUid: String, val paciente: String, val titulo: String,
    val descripcion: String, val fecha: Timestamp?, val categoria: String,
    val icono: ImageVector, val color: Color, val fondo: Color, val noLeida: Boolean
)

@Composable
fun ProfessionalNotificationsScreen(onBack: () -> Unit, onPacienteClick: (String) -> Unit = {}) {
    val db = FirebaseFirestore.getInstance(); val uid = FirebaseAuth.getInstance().currentUser?.uid
    var filtro by rememberSaveable { mutableStateOf("Todas") }; var cargando by remember { mutableStateOf(true) }
    var notificaciones by remember { mutableStateOf<List<NotificacionProfesionalFirebase>>(emptyList()) }
    var leidasHasta by remember { mutableLongStateOf(0L) }

    fun cargar() {
        if (uid == null) { cargando = false; return }
        cargando = true
        db.collection("usuarios").document(uid).get().addOnSuccessListener { prof ->
            leidasHasta = prof.getTimestamp("notificacionesLeidasHasta")?.toDate()?.time ?: 0L
            val pacientes = (prof.get("pacientesVinculados") as? List<*>)?.mapNotNull { it as? String }?.distinct().orEmpty()
            if (pacientes.isEmpty()) { notificaciones = emptyList(); cargando = false; return@addOnSuccessListener }
            val lista = mutableListOf<NotificacionProfesionalFirebase>(); var pendientes = pacientes.size
            pacientes.forEach { pUid ->
                db.collection("usuarios").document(pUid).get().addOnSuccessListener { pDoc ->
                    val nombre = listOfNotNull(pDoc.getString("nombre"), pDoc.getString("apellido")).filter { it.isNotBlank() }.joinToString(" ").ifBlank { "Paciente" }
                    var subPendientes = 2
                    fun terminarSub() { subPendientes--; if (subPendientes == 0) { pendientes--; if (pendientes == 0) { notificaciones = lista.sortedByDescending { it.fecha?.toDate()?.time ?: 0L }; cargando = false } } }
                    db.collection("usuarios").document(pUid).collection("ejerciciosRealizados").orderBy("fecha", Query.Direction.DESCENDING).limit(3).get().addOnCompleteListener { t ->
                        if (t.isSuccessful) t.result.documents.forEach { d ->
                            val fecha = d.getTimestamp("fecha"); val ejercicio = d.getString("ejercicio") ?: "Ejercicio"
                            lista += NotificacionProfesionalFirebase("ej_${d.id}", pUid, nombre, "Ejercicio completado", "$ejercicio · ${d.getLong("repeticiones") ?: 0} repeticiones", fecha, "Actividad", Icons.Filled.CheckCircle, VerdeNotifPro, VerdeClaroNotifPro, (fecha?.toDate()?.time ?: 0L) > leidasHasta)
                        }; terminarSub()
                    }
                    db.collection("usuarios").document(pUid).collection("quickdash").orderBy("fecha", Query.Direction.DESCENDING).limit(2).get().addOnCompleteListener { t ->
                        if (t.isSuccessful) t.result.documents.forEach { d ->
                            val fecha = d.getTimestamp("fecha"); val puntaje = d.getDouble("puntaje")
                            lista += NotificacionProfesionalFirebase("qd_${d.id}", pUid, nombre, "Nueva evaluación QuickDASH", if (puntaje != null) "Puntaje registrado: ${String.format(Locale.getDefault(), "%.1f", puntaje)}" else "Nueva evaluación registrada", fecha, "Progreso", Icons.Filled.ShowChart, NaranjaNotifPro, Color(0xFFFFF4DB), (fecha?.toDate()?.time ?: 0L) > leidasHasta)
                        }; terminarSub()
                    }
                }.addOnFailureListener { pendientes--; if (pendientes == 0) { notificaciones = lista.sortedByDescending { it.fecha?.toDate()?.time ?: 0L }; cargando = false } }
            }
        }.addOnFailureListener { cargando = false }
    }
    LaunchedEffect(uid) { cargar() }
    val visibles = if (filtro == "Todas") notificaciones else notificaciones.filter { it.categoria == filtro }
    Scaffold(containerColor = Color.White.enTema()) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp, vertical = 16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Volver") }
                Text("Notificaciones", Modifier.weight(1f), fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10152B).enTema())
                TextButton(onClick = {
                    if (uid != null) db.collection("usuarios").document(uid).update("notificacionesLeidasHasta", FieldValue.serverTimestamp()).addOnSuccessListener { leidasHasta = System.currentTimeMillis(); notificaciones = notificaciones.map { it.copy(noLeida = false) } }
                }) { Text("Marcar leídas") }
            }
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth().background(Color(0xFFF7F8FA).enTema(), RoundedCornerShape(16.dp))) {
                listOf("Todas", "Actividad", "Progreso").forEach { op -> Surface(Modifier.weight(1f).clickable { filtro = op }, color = if (filtro == op) VerdeNotifPro else Color.Transparent, shape = RoundedCornerShape(16.dp)) { Text(op, Modifier.padding(vertical = 12.dp), color = if (filtro == op) Color.White else GrisNotifPro, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center) } }
            }
            Spacer(Modifier.height(14.dp))
            if (cargando) LinearProgressIndicator(Modifier.fillMaxWidth())
            LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (!cargando && visibles.isEmpty()) item { Text("Todavía no hay actividad de tus pacientes para mostrar.", color = GrisNotifPro, modifier = Modifier.padding(vertical = 24.dp)) }
                items(visibles, key = { it.id }) { n -> NotificationProfessionalCardFirebase(n) { onPacienteClick(n.pacienteUid) } }
            }
        }
    }
}

@Composable
private fun NotificationProfessionalCardFirebase(n: NotificacionProfesionalFirebase, onClick: () -> Unit) {
    val fechaTexto = n.fecha?.toDate()?.let { SimpleDateFormat("dd/MM · HH:mm", Locale.getDefault()).format(it) } ?: "Sin fecha"
    Card(Modifier.fillMaxWidth().clickable { onClick() }, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = if (n.noLeida) Color(0xFFF3FCF7).enTema() else Color.White.enTema()), border = CardDefaults.outlinedCardBorder()) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).background(n.fondo, CircleShape), contentAlignment = Alignment.Center) { Icon(n.icono, null, tint = n.color); if (n.noLeida) Box(Modifier.align(Alignment.TopStart).size(9.dp).background(VerdeNotifPro, CircleShape)) }
            Spacer(Modifier.width(13.dp)); Column(Modifier.weight(1f)) { Text(n.paciente, fontWeight = FontWeight.Bold); Text(n.titulo, fontWeight = FontWeight.SemiBold, color = n.color); Text(n.descripcion, fontSize = 13.sp, color = GrisNotifPro); Spacer(Modifier.height(5.dp)); Text(fechaTexto, fontSize = 12.sp, color = GrisNotifPro) }; Icon(Icons.Filled.ChevronRight, "Ver paciente", tint = GrisNotifPro)
        }
    }
}