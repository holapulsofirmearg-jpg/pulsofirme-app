package com.example.pulsofirme.screens

import com.example.pulsofirme.ui.theme.enTema

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import com.example.pulsofirme.alarmas.RecordatorioEjercicio
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.ProgresoData
import com.example.pulsofirme.SensacionDiaria
import com.example.pulsofirme.UserData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val VerdePrincipal: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
private val VerdeOscuro: Color
    @Composable get() = Color(0xFF1B4332).enTema()
private val VerdeTarjeta: Color
    @Composable get() = Color(0xFFEAF5EC).enTema()
private val VerdeBarraSuave: Color
    @Composable get() = Color(0xFFC9E6D4).enTema()
private val GrisTexto: Color
    @Composable get() = Color(0xFF8A8F98).enTema()
private val NegroTexto: Color
    @Composable get() = Color(0xFF1A1A1A).enTema()
private data class DatosProgreso(
    val tituloGrafico: String,
    val etiquetas: List<String>,
    val cantidades: List<Int>,
    val indiceDestacado: Int
)

private data class RegistroEjercicio(val fechaMs: Long, val nombre: String, val segundos: Long)

private data class UltimaEvaluacion(val fechaMs: Long, val puntaje: Double)

private fun inicioDeDia(ms: Long): Calendar = Calendar.getInstance().apply {
    timeInMillis = ms
    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
}

// Hoy = 0 -> Lunes ... 6 -> Domingo
private fun indiceDiaDeHoy(): Int = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7

private fun datosDe(pestania: String, registros: List<RegistroEjercicio>): DatosProgreso {
    val ahora = Calendar.getInstance()
    return when (pestania) {
        "Mes" -> {
            val cuentas = IntArray(4)
            registros.forEach {
                val c = Calendar.getInstance().apply { timeInMillis = it.fechaMs }
                if (c.get(Calendar.YEAR) == ahora.get(Calendar.YEAR) && c.get(Calendar.MONTH) == ahora.get(Calendar.MONTH)) {
                    cuentas[((c.get(Calendar.DAY_OF_MONTH) - 1) / 7).coerceAtMost(3)]++
                }
            }
            DatosProgreso("Ejercicios por semana", listOf("S1", "S2", "S3", "S4"), cuentas.toList(),
                ((ahora.get(Calendar.DAY_OF_MONTH) - 1) / 7).coerceAtMost(3))
        }
        "Año" -> {
            val cuentas = IntArray(12)
            registros.forEach {
                val c = Calendar.getInstance().apply { timeInMillis = it.fechaMs }
                if (c.get(Calendar.YEAR) == ahora.get(Calendar.YEAR)) cuentas[c.get(Calendar.MONTH)]++
            }
            DatosProgreso("Ejercicios por mes", listOf("E", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D"),
                cuentas.toList(), ahora.get(Calendar.MONTH))
        }
        else -> {
            val lunes = inicioDeDia(System.currentTimeMillis()).apply { add(Calendar.DAY_OF_YEAR, -indiceDiaDeHoy()) }.timeInMillis
            val cuentas = IntArray(7)
            registros.forEach {
                val dias = ((inicioDeDia(it.fechaMs).timeInMillis - lunes) / (24L * 60 * 60 * 1000)).toInt()
                if (dias in 0..6) cuentas[dias]++
            }
            DatosProgreso("Ejercicios por día", listOf("L", "M", "M", "J", "V", "S", "D"), cuentas.toList(), indiceDiaDeHoy())
        }
    }
}

private fun textoFecha(ms: Long): String {
    val dias = ((inicioDeDia(System.currentTimeMillis()).timeInMillis - inicioDeDia(ms).timeInMillis) / (24L * 60 * 60 * 1000)).toInt()
    return when (dias) {
        0 -> "Hoy"
        1 -> "Ayer"
        else -> "Hace $dias días"
    }
}

// Días seguidos con ejercicio (si hoy no hizo, cuenta desde ayer)
private fun rachaDesde(registros: List<RegistroEjercicio>): Int {
    val unDia = 24L * 60 * 60 * 1000
    val dias = registros.map { inicioDeDia(it.fechaMs).timeInMillis }.toHashSet()
    val hoy = inicioDeDia(System.currentTimeMillis()).timeInMillis
    var cursor = if (hoy in dias) hoy else hoy - unDia
    var racha = 0
    while (cursor in dias) { racha++; cursor -= unDia }
    return racha
}

private fun textoDuracion(seg: Long): String = if (seg < 60) "$seg s" else "${seg / 60} min"

private fun textoDificultad(puntaje: Double): String = when {
    puntaje < 25 -> "dificultad leve"
    puntaje < 50 -> "dificultad moderada"
    else -> "dificultad importante"
}

@Composable
fun ProgressScreen(
    rutaActual: String,
    onNavigate: (String) -> Unit,
    onQuickDashClick: () -> Unit = {}
) {
    var pestania by remember { mutableStateOf("Semana") }

    // Datos reales del paciente (último año)
    var registros by remember { mutableStateOf<List<RegistroEjercicio>>(emptyList()) }
    var evaluacion by remember { mutableStateOf<UltimaEvaluacion?>(null) }
    var cargando by remember { mutableStateOf(true) }
    // Sensación por día: { "2026-11-03": "Rigidez baja", ... }
    var sensaciones by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    val miUid = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(miUid) {
        if (miUid == null) { cargando = false; return@LaunchedEffect }
        val db = FirebaseFirestore.getInstance()
        val desde = Timestamp(Date(System.currentTimeMillis() - 366L * 24 * 60 * 60 * 1000))
        db.collection("usuarios").document(miUid).collection("ejerciciosRealizados")
            .whereGreaterThanOrEqualTo("fecha", desde).get()
            .addOnSuccessListener { res ->
                registros = res.documents.mapNotNull { d ->
                    val ms = d.getTimestamp("fecha")?.toDate()?.time ?: return@mapNotNull null
                    RegistroEjercicio(ms, d.getString("ejercicio") ?: "Ejercicio", d.getLong("segundos") ?: 0L)
                }.sortedByDescending { it.fechaMs }
                cargando = false
            }
            .addOnFailureListener { cargando = false }
        db.collection("usuarios").document(miUid).get()
            .addOnSuccessListener { doc ->
                val mapa = doc.get("sensaciones") as? Map<*, *> ?: return@addOnSuccessListener
                sensaciones = mapa.mapNotNull { (k, v) ->
                    val clave = k as? String ?: return@mapNotNull null
                    val texto = v as? String ?: return@mapNotNull null
                    clave to texto
                }.toMap()
            }
        db.collection("usuarios").document(miUid).collection("quickdash")
            .orderBy("fecha", Query.Direction.DESCENDING).limit(1).get()
            .addOnSuccessListener { res ->
                val d = res.documents.firstOrNull() ?: return@addOnSuccessListener
                val ms = d.getTimestamp("fecha")?.toDate()?.time ?: return@addOnSuccessListener
                val p = d.getDouble("puntaje") ?: return@addOnSuccessListener
                evaluacion = UltimaEvaluacion(ms, p)
            }
    }

    val datos = datosDe(pestania, registros)
    val maximo = (datos.cantidades.maxOrNull() ?: 0).coerceAtLeast(1)

    // Racha real: sale de Firestore (igual que la ve el tutor). Mientras carga,
    // muestra la que tiene guardada este celular.
    val contexto = LocalContext.current
    val racha = if (cargando) RecordatorioEjercicio.rachaActual(contexto) else rachaDesde(registros)
    val proximaMeta = listOf(3, 7, 15, 30, 60, 100).firstOrNull { it > racha } ?: (racha + 30)

    Scaffold(
        containerColor = Color.White.enTema(),
        bottomBar = {
            NavigationBar(containerColor = Color.White.enTema()) {
                itemsMenuInferior.forEach { item ->
                    NavigationBarItem(
                        selected = rutaActual == item.ruta,
                        onClick = { onNavigate(item.ruta) },
                        icon = { Icon(item.icono, contentDescription = item.label) },
                        label = { Text(item.label, fontSize = 13.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = VerdePrincipal,
                            selectedTextColor = VerdePrincipal,
                            unselectedIconColor = Color(0xFF9CA3AF).enTema(),
                            unselectedTextColor = Color(0xFF9CA3AF).enTema(),
                            indicatorColor = VerdeTarjeta
                        )
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            Text("Progreso", fontSize = 15.sp, color = GrisTexto)
            Spacer(Modifier.height(4.dp))
            Text(
                "Tu evolución",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeOscuro
            )

            Spacer(Modifier.height(22.dp))

            // Pestañas Semana / Mes / Año
            Row(verticalAlignment = Alignment.CenterVertically) {
                listOf("Semana", "Mes", "Año").forEach { t ->
                    val seleccionada = pestania == t
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (seleccionada) VerdePrincipal else Color.Transparent)
                            .clickable(role = Role.Tab) { pestania = t }
                            .heightIn(min = 56.dp)
                            .padding(horizontal = 22.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = t,
                            fontSize = 15.sp,
                            color = if (seleccionada) Color.White.enTema() else NegroTexto,
                            fontWeight = if (seleccionada) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                }
            }

            Spacer(Modifier.height(18.dp))

            // Banner de racha
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(VerdePrincipal)
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        when (racha) {
                            0 -> "Sin racha todavía"
                            1 -> "1 día de racha"
                            else -> "$racha días de racha"
                        },
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (racha == 0) "Hacé un ejercicio hoy para empezar"
                        else "Seguí así para llegar a $proximaMeta",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Ejercicios completados (contador real)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(VerdeTarjeta)
                    .padding(18.dp)
            ) {
                Text("Ejercicios completados (último año)", fontSize = 14.sp, color = GrisTexto)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = (if (cargando) ProgresoData.ejerciciosCompletados else registros.size).toString(),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = NegroTexto
                )
            }

            Spacer(Modifier.height(26.dp))

            // Gráfico de barras
            Text(datos.tituloGrafico, fontSize = 16.sp, color = GrisTexto)
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(
                    if (datos.cantidades.size > 8) 5.dp else 12.dp
                )
            ) {
                datos.cantidades.forEachIndexed { i, cantidad ->
                    val valor = cantidad.toFloat() / maximo
                    val esHoy = i == datos.indiceDestacado
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            cantidad.toString(),
                            fontSize = 12.sp,
                            color = if (esHoy) VerdePrincipal else GrisTexto
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((8 + valor * 109).dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (esHoy) VerdePrincipal else VerdeBarraSuave)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            datos.etiquetas[i],
                            fontSize = if (datos.cantidades.size > 8) 10.sp else 13.sp,
                            color = if (esHoy) VerdePrincipal else GrisTexto,
                            fontWeight = if (esHoy) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // Cómo te sentiste esta semana
            Text("Cómo te sentiste esta semana", fontSize = 16.sp, color = GrisTexto)
            Spacer(Modifier.height(14.dp))
            SemanaSensaciones(sensaciones)

            Spacer(Modifier.height(28.dp))

            // Historial reciente
            Text("Historial reciente", fontSize = 16.sp, color = GrisTexto)
            Spacer(Modifier.height(8.dp))

            if (registros.isEmpty()) {
                Text(
                    if (cargando) "Cargando…" else "Todavía no hiciste ejercicios. ¡Empezá hoy!",
                    fontSize = 15.sp, color = GrisTexto, modifier = Modifier.padding(vertical = 14.dp)
                )
            } else {
                registros.take(3).forEach {
                    ItemHistorial(it.nombre, "${textoFecha(it.fechaMs)}, ${textoDuracion(it.segundos)}")
                }
            }

            Spacer(Modifier.height(24.dp))

            // Autoevaluación
            Text("Autoevaluación", fontSize = 16.sp, color = GrisTexto)
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .heightIn(min = 56.dp)
                    .clickable(role = Role.Button) { onQuickDashClick() }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(VerdeTarjeta),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.FactCheck,
                        contentDescription = null,
                        tint = VerdePrincipal,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "Evaluación QuickDASH",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NegroTexto
                    )
                    Text(
                        evaluacion?.let { "${textoFecha(it.fechaMs)}, ${textoDificultad(it.puntaje)}" }
                            ?: "Todavía no hiciste la evaluación",
                        fontSize = 13.sp, color = GrisTexto
                    )
                }
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = GrisTexto
                )
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
private fun SemanaSensaciones(sensaciones: Map<String, String>) {
    val claves = SensacionDiaria.clavesSemanaActual()
    val hoy = SensacionDiaria.claveHoy()
    val letras = listOf("L", "M", "M", "J", "V", "S", "D")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        claves.forEachIndexed { i, clave ->
            val texto = sensaciones[clave]
            val esHoy = clave == hoy
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            if (texto != null) SensacionDiaria.colorDe(texto)
                            else Color(0xFFF1F3F5).enTema()
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (texto != null) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = texto,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    letras[i],
                    fontSize = 13.sp,
                    color = if (esHoy) VerdePrincipal else GrisTexto,
                    fontWeight = if (esHoy) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }

    Spacer(Modifier.height(12.dp))

    // Referencias: solo las sensaciones que aparecen esta semana
    val usadas = claves.mapNotNull { sensaciones[it] }.distinct()
    if (usadas.isEmpty()) {
        Text(
            "Todavía no registraste cómo te sentís esta semana. Hacelo desde Inicio.",
            fontSize = 13.sp, color = GrisTexto, lineHeight = 18.sp
        )
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            usadas.forEach { texto ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(SensacionDiaria.colorDe(texto))
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(texto, fontSize = 13.sp, color = NegroTexto)
                }
            }
        }
    }
}

@Composable
private fun ItemHistorial(nombre: String, detalle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Filled.Check,
            contentDescription = null,
            tint = VerdePrincipal,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(14.dp))
        Column {
            Text(
                nombre,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = NegroTexto
            )
            Text(detalle, fontSize = 13.sp, color = GrisTexto)
        }
    }
}