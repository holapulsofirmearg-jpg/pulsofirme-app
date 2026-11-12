package com.example.pulsofirme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import com.example.pulsofirme.UserData
import com.example.pulsofirme.notificaciones.NotificacionesRepo
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val AzulNot = Color(0xFF1565C0)
private val AzulOscuroNot = Color(0xFF0D3B66)
private val AzulSuaveNot = Color(0xFFE7F0FB)
private val GrisFondoNot = Color(0xFFF5F7FA)
private val GrisNot = Color(0xFF6B7280)
private val NegroNot = Color(0xFF1A1A1A)

data class Notificacion(
    val icono: ImageVector,
    val titulo: String,
    val detalle: String,
    val nueva: Boolean,
    val seccion: String = "Hoy",
    val hora: String = "",
    val id: Long = 0L
)

// Saludo según el género elegido en el perfil
private fun saludoBienvenida(): String {
    val nombre = UserData.nombreUsuario.ifBlank { "" }
    val coma = if (nombre.isBlank()) "" else " $nombre"
    return when (UserData.genero) {
        "Mujer" -> "¡Bienvenida$coma!"
        "Hombre" -> "¡Bienvenido$coma!"
        else -> "¡Te damos la bienvenida$coma!"
    }
}

private fun textoBienvenida(): String = when (UserData.genero) {
    "Mujer" -> "Ya estás lista para empezar. Acá vas a ver tus avisos y recordatorios."
    "Hombre" -> "Ya estás listo para empezar. Acá vas a ver tus avisos y recordatorios."
    else -> "Ya podés empezar. Acá vas a ver tus avisos y recordatorios."
}

private fun iconoPorTipo(tipo: String): ImageVector = when (tipo) {
    "bienvenida" -> Icons.Filled.Celebration
    "medicacion" -> Icons.Filled.Medication
    "ejercicio" -> Icons.AutoMirrored.Filled.DirectionsRun
    "progreso" -> Icons.AutoMirrored.Filled.TrendingUp
    "racha" -> Icons.Filled.Star
    "evaluacion" -> Icons.Filled.Description
    else -> Icons.Filled.Notifications
}

private fun inicioDelDia(ms: Long, diasAtras: Int = 0): Long =
    Calendar.getInstance().apply {
        timeInMillis = ms
        add(Calendar.DAY_OF_YEAR, -diasAtras)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

private fun seccionDe(ts: Long, ahora: Long): String = when {
    ts >= inicioDelDia(ahora) -> "Hoy"
    ts >= inicioDelDia(ahora, 1) -> "Ayer"
    else -> "Anteriores"
}

private fun horaRelativa(ts: Long, ahora: Long): String {
    val minutos = ((ahora - ts) / 60_000L).coerceAtLeast(0)
    val hhmm = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(ts))
    return when (seccionDe(ts, ahora)) {
        "Hoy" -> when {
            minutos < 1 -> "Recién"
            minutos < 60 -> "Hace $minutos min"
            minutos < 360 -> "Hace ${minutos / 60} h"
            else -> "Hoy, $hhmm"
        }
        "Ayer" -> "Ayer, $hhmm"
        else -> SimpleDateFormat("dd/MM, HH:mm", Locale.getDefault()).format(Date(ts))
    }
}

private fun aNotificacion(a: NotificacionesRepo.Aviso, ahora: Long, nueva: Boolean): Notificacion {
    val esBienvenida = a.tipo == "bienvenida"
    return Notificacion(
        icono = iconoPorTipo(a.tipo),
        titulo = if (esBienvenida) saludoBienvenida() else a.titulo,
        detalle = if (esBienvenida) textoBienvenida() else a.detalle,
        nueva = nueva,
        seccion = seccionDe(a.timestamp, ahora),
        hora = horaRelativa(a.timestamp, ahora),
        id = a.id
    )
}

// Lista de notificaciones (la usa también el globito rojo del panel de inicio)
fun notificacionesDelUsuario(): List<Notificacion> {
    val ahora = System.currentTimeMillis()
    return NotificacionesRepo.avisos.map { aNotificacion(it, ahora, nueva = !it.leida) }
}

@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    // Carga lo guardado y crea la bienvenida la primera vez
    remember {
        NotificacionesRepo.cargar(context)
        NotificacionesRepo.asegurarBienvenida(context)
        true
    }

    // Las que estaban sin leer al abrir se ven resaltadas en esta visita
    val idsNuevosAlAbrir = remember {
        NotificacionesRepo.avisos.filter { !it.leida }.map { it.id }.toSet()
    }

    // Reloj para que "Hace X min" se actualice solo
    var ahora by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        NotificacionesRepo.marcarTodasLeidas(context)
    }
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000)
            ahora = System.currentTimeMillis()
        }
    }

    val notificaciones = NotificacionesRepo.avisos.map {
        aNotificacion(it, ahora, nueva = it.id in idsNuevosAlAbrir || !it.leida)
    }
    val nuevas = notificaciones.count { it.nueva }
    val secciones = listOf("Hoy", "Ayer", "Anteriores").mapNotNull { nombre ->
        val items = notificaciones.filter { it.seccion == nombre }
        if (items.isEmpty()) null else nombre to items
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(48.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = AzulNot
                )
            }
            Spacer(Modifier.width(4.dp))
            Column {
                Text("Notificaciones", fontSize = 13.sp, color = GrisNot)
                Text(
                    text = "Novedades",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuroNot
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = if (nuevas > 0) "Tenés $nuevas sin leer" else "No tenés avisos nuevos",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = AzulNot
        )

        Spacer(Modifier.height(20.dp))

        // Sugerencia fija si todavía no cargó horarios de medicación
        if (UserData.horariosMedicacion.isEmpty()) {
            FilaNotificacion(
                Notificacion(
                    icono = Icons.Filled.Medication,
                    titulo = "Configurá tu medicación",
                    detalle = "Cargá los horarios de tus tomas para que te avisemos a tiempo.",
                    nueva = false,
                    hora = "Sugerencia"
                )
            )
            Spacer(Modifier.height(20.dp))
        }

        if (secciones.isEmpty()) {
            Text(
                text = "Todavía no tenés notificaciones.",
                fontSize = 15.sp,
                color = GrisNot
            )
        }

        secciones.forEach { (nombreSeccion, items) ->
            Text(
                text = nombreSeccion,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AzulOscuroNot
            )
            Spacer(Modifier.height(10.dp))

            items.forEach { n ->
                FilaNotificacion(n)
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun FilaNotificacion(n: Notificacion) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (n.nueva) AzulSuaveNot else GrisFondoNot)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (n.nueva) Color.White else AzulSuaveNot),
            contentAlignment = Alignment.Center
        ) {
            Icon(n.icono, contentDescription = null, tint = AzulNot, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = n.titulo,
                fontSize = 16.sp,
                fontWeight = if (n.nueva) FontWeight.Bold else FontWeight.SemiBold,
                color = NegroNot
            )
            Spacer(Modifier.height(2.dp))
            Text(n.detalle, fontSize = 13.sp, color = GrisNot, lineHeight = 18.sp)
            if (n.hora.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(n.hora, fontSize = 12.sp, color = AzulNot)
            }
        }
        if (n.nueva) {
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(AzulNot)
            )
        }
    }
}