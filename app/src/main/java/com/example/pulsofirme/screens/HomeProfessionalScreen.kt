package com.example.pulsofirme.screens

import androidx.compose.runtime.saveable.rememberSaveable

import com.example.pulsofirme.ui.theme.enTema

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Date

private val VerdePH: Color
    @Composable get() = Color(0xFF07884A).enTema()
private val VerdeClaroPH: Color
    @Composable get() = Color(0xFFEAF8F0).enTema()
private val AzulClaroPH: Color
    @Composable get() = Color(0xFFEAF6FF).enTema()
private val GrisPH: Color
    @Composable get() = Color(0xFF6B7280).enTema()
@Composable
fun HomeProfessionalScreen(
    nombreProfesional: String,
    matricula: String = "",
    lugarTrabajo: String = "",
    especialidad: String = "",
    rutaActual: String = "caregiver_home",
    onNavigate: (String) -> Unit,
    onVerPacientes: () -> Unit,
    onEjerciciosRealizados: () -> Unit = {},
    onTiempoActividad: () -> Unit = {},
    onCumplimiento: () -> Unit = {},
    onEvolucionPacientes: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onPulsiBot: () -> Unit = {}
) {
    val db = FirebaseFirestore.getInstance()
    val uidProfesional = FirebaseAuth.getInstance().currentUser?.uid
    var cantidadPacientes by remember { mutableIntStateOf(0) }
    var esPremium by rememberSaveable { mutableStateOf(false) }
    var ejerciciosSemana by remember { mutableIntStateOf(0) }
    var segundosSemana by remember { mutableLongStateOf(0L) }
    var pacientesActivos by remember { mutableIntStateOf(0) }
    var pacientesMejora by remember { mutableIntStateOf(0) }
    var notificacionesNoLeidas by remember { mutableIntStateOf(0) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(uidProfesional) {
        if (uidProfesional == null) { cargando = false; return@LaunchedEffect }
        db.collection("usuarios").document(uidProfesional).get().addOnSuccessListener { prof ->
            esPremium = prof.getBoolean("esPremium") ?: false
            val pacientes = (prof.get("pacientesVinculados") as? List<*>)?.mapNotNull { it as? String }?.distinct().orEmpty()
            cantidadPacientes = pacientes.size
            val leidasHasta = prof.getTimestamp("notificacionesLeidasHasta")?.toDate()?.time ?: 0L
            if (pacientes.isEmpty()) { cargando = false; return@addOnSuccessListener }
            val desde = Timestamp(Date(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000))
            var pendientes = pacientes.size
            pacientes.forEach { pacienteUid ->
                var tuvoActividad = false
                db.collection("usuarios").document(pacienteUid).collection("ejerciciosRealizados")
                    .whereGreaterThanOrEqualTo("fecha", desde).get().addOnCompleteListener { tareaEj ->
                        if (tareaEj.isSuccessful) {
                            ejerciciosSemana += tareaEj.result.size()
                            var suma = 0L
                            tareaEj.result.documents.forEach { d ->
                                suma += d.getLong("segundos") ?: 0L
                                val fecha = d.getTimestamp("fecha")?.toDate()?.time ?: 0L
                                if (fecha > leidasHasta) notificacionesNoLeidas++
                            }
                            segundosSemana += suma
                            tuvoActividad = tareaEj.result.size() > 0
                        }
                        if (tuvoActividad) pacientesActivos++
                        db.collection("usuarios").document(pacienteUid).collection("quickdash")
                            .orderBy("fecha", Query.Direction.DESCENDING).limit(2).get().addOnCompleteListener { tareaQ ->
                                if (tareaQ.isSuccessful) {
                                    val docs = tareaQ.result.documents
                                    if (docs.isNotEmpty()) {
                                        val f = docs.first().getTimestamp("fecha")?.toDate()?.time ?: 0L
                                        if (f > leidasHasta) notificacionesNoLeidas++
                                    }
                                    if (docs.size >= 2) {
                                        val actual = docs[0].getDouble("puntaje")
                                        val anterior = docs[1].getDouble("puntaje")
                                        if (actual != null && anterior != null && actual < anterior) pacientesMejora++
                                    }
                                }
                                pendientes--
                                if (pendientes == 0) cargando = false
                            }
                    }
            }
        }.addOnFailureListener { cargando = false }
    }

    val tiempoTexto = remember(segundosSemana) {
        val h = segundosSemana / 3600
        val m = (segundosSemana % 3600) / 60
        if (h > 0) trf("{0} h {1} min", h, m) else "${m} min"
    }
    val actividadTexto = if (cantidadPacientes == 0) "0%" else "${(pacientesActivos * 100 / cantidadPacientes)}%"

    Scaffold(containerColor = Color.White.enTema(), bottomBar = {
        NavigationBar(containerColor = Color.White.enTema()) {
            listOf(
                Triple("Inicio", Icons.Filled.Home, "caregiver_home"),
                Triple("Pacientes", Icons.Filled.Groups, "caregiver_patients"),
                Triple("Progreso", Icons.Filled.ShowChart, "caregiver_progress"),
                Triple("Cuenta", Icons.Filled.Person, "caregiver_account")
            ).forEach { item ->
                NavigationBarItem(selected = rutaActual == item.third, onClick = { onNavigate(item.third) },
                    icon = { Icon(item.second, item.first) }, label = { Text(tr(item.first), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = VerdePH, selectedTextColor = VerdePH, indicatorColor = VerdeClaroPH))
            }
        }
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 22.dp, vertical = 22.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(trf("Hola, {0}", nombreProfesional), fontSize = 29.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10152B).enTema())
                    Text(tr(if (matricula.isBlank()) "Matrícula sin completar" else trf("Matrícula: {0}", matricula)), fontSize = 16.sp, color = GrisPH)
                }
                Box(Modifier.size(48.dp).clip(CircleShape).clickable(role = Role.Button, onClickLabel = tr("Abrir PulsiBot")) { onPulsiBot() }, contentAlignment = Alignment.Center) {
                    Image(painterResource(R.drawable.pulsibot), "PulsiBot", Modifier.fillMaxSize())
                }
                Spacer(Modifier.width(6.dp))
                Box(Modifier.size(56.dp).clickable(role = Role.Button, onClickLabel = tr("Ver notificaciones")) { onNotificaciones() }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.NotificationsNone, "Notificaciones", Modifier.size(31.dp), tint = Color(0xFF737B8F).enTema())
                    if (notificacionesNoLeidas > 0) Box(Modifier.align(Alignment.TopEnd).size(21.dp).background(Color(0xFFD92525).enTema(), CircleShape), contentAlignment = Alignment.Center) {
                        Text(tr(if (notificacionesNoLeidas > 9) "9+" else notificacionesNoLeidas.toString()), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Apartment, null, Modifier.size(38.dp), tint = Color(0xFF737B8F).enTema()); Spacer(Modifier.width(14.dp))
                Column { Text(tr(lugarTrabajo.ifBlank { "Lugar de trabajo sin completar" }), fontSize = 18.sp, fontWeight = FontWeight.SemiBold); Text(tr(especialidad.ifBlank { "Especialidad sin completar" }), fontSize = 14.sp, color = GrisPH) }
            }
            if (cargando) { Spacer(Modifier.height(12.dp)); LinearProgressIndicator(Modifier.fillMaxWidth()) }
            Spacer(Modifier.height(22.dp))
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = VerdePH)) {
                Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(66.dp).background(Color(0xFF10A75F).enTema(), CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Filled.Groups, null, tint = Color.White, modifier = Modifier.size(39.dp)) }
                    Spacer(Modifier.width(15.dp)); Column(Modifier.weight(1f)) { Text(tr("Pacientes vinculados"), color = Color.White, fontSize = 16.sp); Text(tr("$cantidadPacientes / ${if (esPremium) 5 else 1}"), color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Bold); Text(tr(if (esPremium) "Plan Premium: hasta 5 pacientes" else "Plan gratuito: 1 paciente"), color = Color.White.copy(alpha = .72f), fontSize = 13.sp) }
                    Surface(Modifier.heightIn(min = 56.dp).clickable(role = Role.Button) { onVerPacientes() }, color = Color.White, shape = RoundedCornerShape(50)) { Row(Modifier.heightIn(min = 56.dp).padding(horizontal = 12.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.Groups, null, tint = VerdePH, modifier = Modifier.size(19.dp)); Spacer(Modifier.width(5.dp)); Text(tr("Ver pacientes"), color = Color(0xFF164A31).enTema(), fontSize = 13.sp, fontWeight = FontWeight.SemiBold); Icon(Icons.Filled.ChevronRight, null, tint = VerdePH) } }
                }
            }
            Spacer(Modifier.height(18.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaPH(Icons.Filled.FitnessCenter, ejerciciosSemana.toString(), "Ejercicios realizados", "Últimos 7 días", VerdeClaroPH, Modifier.weight(1f), onEjerciciosRealizados)
                TarjetaPH(Icons.Filled.Schedule, tiempoTexto, "Tiempo total", "Últimos 7 días", AzulClaroPH, Modifier.weight(1f), onTiempoActividad)
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaPH(Icons.Filled.TrackChanges, actividadTexto, "Pacientes activos", "Con actividad en 7 días", VerdeClaroPH, Modifier.weight(1f), onCumplimiento)
                TarjetaPH(Icons.Filled.TrendingUp, "$pacientesMejora / $cantidadPacientes", "Mejora QuickDASH", "Comparando 2 evaluaciones", AzulClaroPH, Modifier.weight(1f), onEvolucionPacientes)
            }
        }
    }
}

@Composable
private fun TarjetaPH(icono: androidx.compose.ui.graphics.vector.ImageVector, valor: String, titulo: String, subtitulo: String, fondo: Color, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Card(modifier.clickable(role = Role.Button) { onClick() }.heightIn(min = 165.dp), shape = RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = Color.White.enTema()), border = CardDefaults.outlinedCardBorder()) {
        Column(Modifier.fillMaxWidth().padding(15.dp)) { Box(Modifier.size(43.dp).background(fondo, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Icon(icono, null, tint = VerdePH, modifier = Modifier.size(24.dp)) }; Spacer(Modifier.height(8.dp)); Text(tr(valor), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10152B).enTema()); Text(tr(titulo), fontSize = 13.sp, color = GrisPH); Text(tr(subtitulo), fontSize = 12.sp, color = GrisPH) }
    }
}