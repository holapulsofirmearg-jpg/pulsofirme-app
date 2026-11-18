package com.example.pulsofirme.screens

import androidx.compose.runtime.saveable.rememberSaveable

import com.example.pulsofirme.ui.theme.enTema

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.SensacionDiaria
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val VerdeDet: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
private val RojoDet: Color
    @Composable get() = Color(0xFFC0392B).enTema()
private val GrisDet: Color
    @Composable get() = Color(0xFF6B7280).enTema()
private val GrisVacioDet: Color
    @Composable get() = Color(0xFFF1F3F5).enTema()
// Un día de los últimos 7
private data class DiaPaciente(
    val letra: String,
    val esHoy: Boolean,
    val sensacion: String?,
    val tomas: Int,
    val tomasTotal: Int // 0 = ese día no tenía tomas
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalPatientDetailScreen(
    // Recibe el UID del paciente. Si alguna pantalla vieja todavía manda
    // el nombre, se busca entre los pacientes vinculados.
    nombrePaciente: String,
    onBack: () -> Unit,
    onVerProgresoCompleto: () -> Unit = {}
) {
    val db = FirebaseFirestore.getInstance()
    val miUid = FirebaseAuth.getInstance().currentUser?.uid

    var pacienteUid by rememberSaveable { mutableStateOf("") }
    var nombre by rememberSaveable { mutableStateOf("Paciente") }
    var diagnostico by rememberSaveable { mutableStateOf("Sin diagnóstico informado") }
    var fechaNacimiento by rememberSaveable { mutableStateOf("No informada") }
    var genero by rememberSaveable { mutableStateOf("No informado") }
    var mano by rememberSaveable { mutableStateOf("No informada") }
    var quickDash by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var noEncontrado by rememberSaveable { mutableStateOf(false) }

    // Semana + permisos que dio el paciente a este profesional
    var semana by remember { mutableStateOf<List<DiaPaciente>>(emptyList()) }
    var horarios by remember { mutableStateOf<List<String>>(emptyList()) }
    var comparteProgreso by rememberSaveable { mutableStateOf(true) }
    var comparteMedicacion by rememberSaveable { mutableStateOf(true) }

    // Uso del guante registrado por el paciente
    var usoGuante by remember { mutableStateOf(UsoGuante.Resumen()) }

    DisposableEffect(pacienteUid) {
        val dejarDeEscuchar = if (pacienteUid.isNotBlank()) {
            UsoGuante.escuchar(pacienteUid) { usoGuante = it }
        } else null
        onDispose { dejarDeEscuchar?.invoke() }
    }

    LaunchedEffect(nombrePaciente) {
        cargando = true
        try {
            val doc = buscarPaciente(db, miUid, nombrePaciente)
            if (doc == null) {
                noEncontrado = true
                return@LaunchedEffect
            }
            pacienteUid = doc.id

            nombre = listOfNotNull(doc.getString("nombre"), doc.getString("apellido"))
                .filter { it.isNotBlank() }.joinToString(" ").ifBlank { "Paciente" }
            val ds = doc.get("diagnosticos") as? List<*>
            diagnostico = ds?.filterIsInstance<String>()?.joinToString(" y ")?.ifBlank { null }
                ?: doc.getString("diagnostico") ?: "Sin diagnóstico informado"
            fechaNacimiento = doc.getString("fechaNacimiento")?.ifBlank { null } ?: "No informada"
            genero = doc.getString("genero")?.ifBlank { null } ?: "No informado"
            mano = doc.getString("mano")?.ifBlank { null } ?: "No informada"

            // Permisos de este profesional (si nunca se tocaron, están activados)
            if (miUid != null) {
                comparteProgreso = doc.getBoolean("permisosProfesionales.$miUid.progreso") ?: true
                comparteMedicacion = doc.getBoolean("permisosProfesionales.$miUid.medicacion") ?: true
            }

            // Últimos 7 días
            val clave = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val letras = listOf("L", "M", "M", "J", "V", "S", "D")
            val dias = (6 downTo 0).map { atras -> Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -atras) } }
            val desdeClave = clave.format(dias.first().time)

            val sensaciones = (doc.get("sensaciones") as? Map<*, *>)
                ?.mapNotNull { (k, v) -> (k as? String)?.let { kk -> (v as? String)?.let { kk to it } } }
                ?.toMap().orEmpty()

            val activa = doc.getBoolean("medicacionActiva") == true
            val horariosPac = (doc.get("medicacionHorarios") as? List<*>)?.filterIsInstance<String>().orEmpty()
            val diasMed = (doc.get("medicacionDias") as? List<*>)?.mapNotNull { (it as? Number)?.toInt() }?.toSet().orEmpty()
            horarios = if (activa) horariosPac else emptyList()

            val tomasPorDia = if (comparteMedicacion) {
                try {
                    db.collection("usuarios").document(doc.id).collection("tomasMedicacion")
                        .whereGreaterThanOrEqualTo("fecha", desdeClave).get().await()
                        .documents.mapNotNull { it.getString("fecha") }.groupingBy { it }.eachCount()
                } catch (_: Exception) { emptyMap() }
            } else emptyMap()

            val hoy = clave.format(Calendar.getInstance().time)
            semana = dias.map { c ->
                val k = clave.format(c.time)
                val indice = (c.get(Calendar.DAY_OF_WEEK) + 5) % 7 // lunes = 0
                DiaPaciente(
                    letra = letras[indice],
                    esHoy = k == hoy,
                    sensacion = sensaciones[k],
                    tomas = tomasPorDia[k] ?: 0,
                    tomasTotal = if (activa && indice in diasMed) horariosPac.size else 0
                )
            }

            // Último QuickDASH
            quickDash = try {
                db.collection("usuarios").document(doc.id).collection("quickdash")
                    .orderBy("fecha", Query.Direction.DESCENDING).limit(1).get().await()
                    .documents.firstOrNull()?.getDouble("puntaje")
                    ?.let { String.format(Locale.US, "%.1f puntos sobre 100", it) }
            } catch (_: Exception) { null }
        } catch (_: Exception) {
            noEncontrado = true
        } finally {
            cargando = false
        }
    }

    val textoUsoGuante = buildString {
        if (usoGuante.enUsoDesde != null) append("🟢 Lo tiene puesto ahora\n")
        append("Hoy: ${UsoGuante.formatear(usoGuante.minutosHoy)}\n")
        append("Últimos 7 días: ${UsoGuante.formatear(usoGuante.minutosSemana)}")
        append(" (${usoGuante.diasConUsoSemana} de 7 días)")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del paciente") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (cargando) LinearProgressIndicator(Modifier.fillMaxWidth())

            if (noEncontrado && !cargando) {
                Text(
                    "No pudimos cargar a este paciente. Puede que haya quitado el vínculo.",
                    color = GrisDet
                )
                return@Column
            }

            Text(nombre, fontSize = 28.sp, fontWeight = FontWeight.Bold)

            DetailCard("Diagnóstico", diagnostico)
            DetailCard("Fecha de nacimiento", fechaNacimiento)
            DetailCard("Género", genero)
            DetailCard("Mano del guante", mano)
            DetailCard("Último QuickDASH", quickDash ?: "Todavía no hay un resultado guardado")

            // ---------- Cómo se sintió ----------
            TarjetaSemana(
                icono = Icons.Filled.SentimentSatisfied,
                titulo = "Cómo se sintió (últimos 7 días)",
                compartido = comparteProgreso
            ) {
                val hoySens = semana.lastOrNull()?.sensacion
                Text(
                    text = "Hoy: ${hoySens ?: "todavía no lo registró"}",
                    fontWeight = FontWeight.SemiBold,
                    color = if (hoySens?.contains("Dolor", true) == true) RojoDet else Color.Unspecified
                )
                Spacer(Modifier.height(12.dp))
                FilaDias(semana) { d ->
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(if (d.sensacion != null) SensacionDiaria.colorDe(d.sensacion) else GrisVacioDet)
                    )
                }
                val usadas = semana.mapNotNull { it.sensacion }.distinct()
                if (usadas.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    usadas.forEach { s ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                            Box(Modifier.size(9.dp).clip(CircleShape).background(SensacionDiaria.colorDe(s)))
                            Spacer(Modifier.width(8.dp))
                            Text(s, fontSize = 13.sp, color = GrisDet)
                        }
                    }
                }
            }

            // ---------- Medicación ----------
            TarjetaSemana(
                icono = Icons.Filled.Medication,
                titulo = "Medicación (últimos 7 días)",
                compartido = comparteMedicacion
            ) {
                val total = semana.sumOf { it.tomasTotal }
                val hechas = semana.sumOf { minOf(it.tomas, it.tomasTotal) }
                if (horarios.isEmpty() && semana.none { it.tomas > 0 }) {
                    Text("No tiene recordatorios de medicación activos.", color = GrisDet)
                } else {
                    Text(
                        text = if (total == 0) "Sin tomas programadas esta semana" else "Tomas registradas: $hechas de $total",
                        fontWeight = FontWeight.SemiBold,
                        color = if (total > 0 && hechas < total) RojoDet else VerdeDet
                    )
                    if (horarios.isNotEmpty()) {
                        Text("Horarios: ${horarios.joinToString(" · ")} hs", fontSize = 13.sp, color = GrisDet)
                    }
                    Spacer(Modifier.height(12.dp))
                    FilaDias(semana) { d ->
                        val completo = d.tomasTotal > 0 && d.tomas >= d.tomasTotal
                        val falto = d.tomasTotal > 0 && d.tomas < d.tomasTotal
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        completo -> VerdeDet
                                        falto -> RojoDet.copy(alpha = 0.15f)
                                        else -> GrisVacioDet
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                completo -> Icon(Icons.Filled.Check, contentDescription = "Tomó todas", tint = Color.White, modifier = Modifier.size(18.dp))
                                falto -> Text("${d.tomas}/${d.tomasTotal}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RojoDet)
                                else -> {}
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Verde: tomó todas · Rojo: le faltó alguna · Gris: no tenía tomas",
                        fontSize = 12.sp,
                        color = GrisDet
                    )
                }
            }

            DetailCard("Uso del guante (registrado por el paciente)", textoUsoGuante)

            Button(
                onClick = onVerProgresoCompleto,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
            ) {
                Text("Ver progreso completo")
            }

            Text(
                text = "Los datos se cargan desde la cuenta del paciente y respetan los permisos que te dio.",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

/**
 * Busca al paciente: primero como UID; si no existe, por nombre entre los
 * pacientes vinculados a este profesional (para pantallas que mandan el nombre).
 */
private suspend fun buscarPaciente(
    db: FirebaseFirestore,
    miUid: String?,
    valor: String
): DocumentSnapshot? {
    if (valor.isBlank()) return null

    try {
        val directo = db.collection("usuarios").document(valor).get().await()
        if (directo.exists() && directo.getString("rol") == "paciente") return directo
    } catch (_: Exception) { /* no era un UID o no hay permiso: probamos por nombre */ }

    if (miUid == null) return null
    val yo = db.collection("usuarios").document(miUid).get().await()
    val uids = (yo.get("pacientesVinculados") as? List<*>)?.filterIsInstance<String>().orEmpty()
    for (uid in uids) {
        try {
            val d = db.collection("usuarios").document(uid).get().await()
            val completo = listOfNotNull(d.getString("nombre"), d.getString("apellido"))
                .filter { it.isNotBlank() }.joinToString(" ")
            if (completo.equals(valor, true) || d.getString("nombre").equals(valor, true)) return d
        } catch (_: Exception) { }
    }
    return null
}

@Composable
private fun TarjetaSemana(
    icono: ImageVector,
    titulo: String,
    compartido: Boolean,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icono, contentDescription = null, tint = VerdeDet, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(titulo, color = Color.Gray, fontSize = 13.sp)
            }
            Spacer(Modifier.height(8.dp))
            if (compartido) {
                contenido()
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = GrisDet, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("El paciente no lo compartió con vos.", color = GrisDet)
                }
            }
        }
    }
}

@Composable
private fun FilaDias(semana: List<DiaPaciente>, circulo: @Composable (DiaPaciente) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        semana.forEach { d ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                circulo(d)
                Spacer(Modifier.height(4.dp))
                Text(
                    d.letra,
                    fontSize = 12.sp,
                    color = if (d.esHoy) VerdeDet else GrisDet,
                    fontWeight = if (d.esHoy) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun DetailCard(titulo: String, valor: String) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(titulo, color = Color.Gray, fontSize = 13.sp)
            Spacer(Modifier.height(4.dp))
            Text(valor, fontWeight = FontWeight.SemiBold, lineHeight = 20.sp)
        }
    }
}