package com.example.pulsofirme

import com.example.pulsofirme.screens.trf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Datos REALES del paciente vinculado, vistos desde la cuenta del tutor.
 * Lo usan las pantallas de Progreso, Alertas, Evolución y QuickDASH del tutor.
 * Respeta los permisos que el paciente eligió en "Privacidad y datos"
 * (y las reglas de Firestore también lo controlan).
 */

data class EjercicioPaciente(
    val fechaMs: Long,
    val nombre: String,
    val segundos: Long,
    val repeticiones: Long
)

data class EvaluacionQuickDash(
    val fechaMs: Long,
    val puntaje: Double,
    // Una respuesta por pregunta: 0 = sin dificultad … 4 = máxima dificultad
    val respuestas: List<Int>
)

data class PacienteTutor(
    val nombre: String,
    val comparteProgreso: Boolean,
    val comparteMedicacion: Boolean,
    val comparteUbicacion: Boolean,
    // Último año, del más nuevo al más viejo (vacío si no comparte progreso)
    val ejercicios: List<EjercicioPaciente>,
    // Del más nuevo al más viejo (vacío si no comparte progreso)
    val evaluaciones: List<EvaluacionQuickDash>,
    // Cómo se siente hoy (null si no lo registró)
    val sensacionHoy: String?,
    // Horarios de hoy que ya pasaron hace más de una hora y no marcó "Ya la tomé"
    val medicacionPendiente: List<String>
)

sealed class EstadoPacienteTutor {
    object Cargando : EstadoPacienteTutor()
    object SinPaciente : EstadoPacienteTutor()
    data class Error(val mensaje: String) : EstadoPacienteTutor()
    data class Listo(val paciente: PacienteTutor) : EstadoPacienteTutor()
}

object PacienteDelTutor {

    private const val UN_DIA = 24L * 60 * 60 * 1000

    suspend fun cargar(): EstadoPacienteTutor {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: return EstadoPacienteTutor.Error("No hay una sesión iniciada.")

        return try {
            val db = FirebaseFirestore.getInstance()
            val yo = db.collection("usuarios").document(uid).get().await()
            val pacUid = yo.getString("pacienteVinculadoUid").orEmpty()
            if (pacUid.isBlank()) return EstadoPacienteTutor.SinPaciente

            val pacRef = db.collection("usuarios").document(pacUid)
            val pac = pacRef.get().await()

            val nombre = listOfNotNull(pac.getString("nombre"), pac.getString("apellido"))
                .filter { it.isNotBlank() }
                .joinToString(" ")
                .ifBlank { "Tu paciente" }

            // Mismos valores por defecto que la app del paciente
            val comparteProgreso = pac.getBoolean("permisosTutor.progreso") ?: true
            val comparteMedicacion = pac.getBoolean("permisosTutor.medicacion") ?: true
            val comparteUbicacion = pac.getBoolean("permisosTutor.ubicacion") ?: false

            var ejercicios = emptyList<EjercicioPaciente>()
            var evaluaciones = emptyList<EvaluacionQuickDash>()

            if (comparteProgreso) {
                val desde = Timestamp(Date(System.currentTimeMillis() - 366L * UN_DIA))
                ejercicios = pacRef.collection("ejerciciosRealizados")
                    .whereGreaterThanOrEqualTo("fecha", desde).get().await()
                    .documents.mapNotNull { d ->
                        val ms = d.getTimestamp("fecha")?.toDate()?.time ?: return@mapNotNull null
                        EjercicioPaciente(
                            fechaMs = ms,
                            nombre = d.getString("ejercicio")?.ifBlank { null } ?: "Ejercicio",
                            segundos = d.getLong("segundos") ?: 0L,
                            repeticiones = d.getLong("repeticiones") ?: 0L
                        )
                    }
                    .sortedByDescending { it.fechaMs }

                evaluaciones = pacRef.collection("quickdash")
                    .orderBy("fecha", Query.Direction.DESCENDING).limit(20).get().await()
                    .documents.mapNotNull { d ->
                        val ms = d.getTimestamp("fecha")?.toDate()?.time ?: return@mapNotNull null
                        val puntaje = d.getDouble("puntaje") ?: return@mapNotNull null
                        val respuestas = (d.get("respuestas") as? List<*>)
                            ?.mapNotNull { (it as? Number)?.toInt() }
                            .orEmpty()
                        EvaluacionQuickDash(ms, puntaje, respuestas)
                    }
            }

            val sensacionHoy = if (comparteProgreso) {
                SensacionDiaria.deHoy(pac.getString("sensacionHoy"), pac.getString("sensacionFecha"))
            } else null

            val medicacionPendiente = if (comparteMedicacion) {
                medicacionPendienteDeHoy(pac, pacRef)
            } else emptyList()

            EstadoPacienteTutor.Listo(
                PacienteTutor(
                    nombre = nombre,
                    comparteProgreso = comparteProgreso,
                    comparteMedicacion = comparteMedicacion,
                    comparteUbicacion = comparteUbicacion,
                    ejercicios = ejercicios,
                    evaluaciones = evaluaciones,
                    sensacionHoy = sensacionHoy,
                    medicacionPendiente = medicacionPendiente
                )
            )
        } catch (e: Exception) {
            EstadoPacienteTutor.Error(
                ErroresFirebase.datos(e, "No se pudieron cargar los datos del paciente. Intentá de nuevo.")
            )
        }
    }

    // Misma lógica que los avisos en segundo plano (AvisosTutor)
    private suspend fun medicacionPendienteDeHoy(
        pac: com.google.firebase.firestore.DocumentSnapshot,
        pacRef: com.google.firebase.firestore.DocumentReference
    ): List<String> {
        if (pac.getBoolean("medicacionActiva") != true) return emptyList()
        val dias = (pac.get("medicacionDias") as? List<*>)?.mapNotNull { (it as? Number)?.toInt() }.orEmpty()
        val indiceHoy = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7 // lunes = 0
        if (indiceHoy !in dias) return emptyList()

        val horarios = (pac.get("medicacionHorarios") as? List<*>)?.filterIsInstance<String>().orEmpty()
        val tomadas = pacRef.collection("tomasMedicacion")
            .whereEqualTo("fecha", SensacionDiaria.claveHoy()).get().await()
            .documents.mapNotNull { it.getString("horario") }.toSet()

        val ahora = Calendar.getInstance()
        val minutosAhora = ahora.get(Calendar.HOUR_OF_DAY) * 60 + ahora.get(Calendar.MINUTE)

        return horarios.filter { h ->
            val partes = h.split(":")
            val hora = partes.getOrNull(0)?.toIntOrNull() ?: return@filter false
            val minutosToma = hora * 60 + (partes.getOrNull(1)?.toIntOrNull() ?: 0)
            minutosAhora >= minutosToma + 60 && h !in tomadas
        }
    }

    // ---------- Cálculos para las pantallas ----------

    fun inicioDeDia(ms: Long): Long = Calendar.getInstance().apply {
        timeInMillis = ms
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    /** Días seguidos con ejercicio (si hoy todavía no hizo, cuenta desde ayer). */
    fun racha(ejercicios: List<EjercicioPaciente>): Int {
        val dias = ejercicios.map { inicioDeDia(it.fechaMs) }.toHashSet()
        val hoy = inicioDeDia(System.currentTimeMillis())
        var cursor = if (hoy in dias) hoy else hoy - UN_DIA
        var racha = 0
        while (cursor in dias) {
            racha++
            cursor -= UN_DIA
        }
        return racha
    }

    fun deHoy(ejercicios: List<EjercicioPaciente>): List<EjercicioPaciente> {
        val hoy = inicioDeDia(System.currentTimeMillis())
        return ejercicios.filter { inicioDeDia(it.fechaMs) == hoy }
    }

    /** Ejercicios de la semana actual, de lunes (0) a domingo (6). */
    fun porDiaDeEstaSemana(ejercicios: List<EjercicioPaciente>): List<Int> {
        val indiceHoy = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7
        val lunes = inicioDeDia(System.currentTimeMillis()) - indiceHoy * UN_DIA
        val cuentas = IntArray(7)
        ejercicios.forEach {
            val dia = ((inicioDeDia(it.fechaMs) - lunes) / UN_DIA).toInt()
            if (dia in 0..6) cuentas[dia]++
        }
        return cuentas.toList()
    }

    fun deUltimosDias(ejercicios: List<EjercicioPaciente>, dias: Int): List<EjercicioPaciente> {
        val desde = inicioDeDia(System.currentTimeMillis()) - (dias - 1) * UN_DIA
        return ejercicios.filter { it.fechaMs >= desde }
    }

    /** Días completos desde el último ejercicio (null si nunca hizo). */
    fun diasSinEjercicio(ejercicios: List<EjercicioPaciente>): Int? {
        val ultimo = ejercicios.maxOfOrNull { it.fechaMs } ?: return null
        return ((inicioDeDia(System.currentTimeMillis()) - inicioDeDia(ultimo)) / UN_DIA).toInt()
    }

    fun textoMinutos(segundos: Long): String = when {
        segundos <= 0 -> "0 min"
        segundos < 60 -> "$segundos s"
        segundos < 3600 -> "${segundos / 60} min"
        else -> trf("{0} h {1} min", segundos / 3600, (segundos % 3600) / 60)
    }

    fun textoFecha(ms: Long): String =
        SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "AR")).format(Date(ms))

    fun textoFechaCorta(ms: Long): String =
        SimpleDateFormat("d 'de' MMMM", Locale("es", "AR")).format(Date(ms))

    fun textoHace(ms: Long): String {
        val dias = ((inicioDeDia(System.currentTimeMillis()) - inicioDeDia(ms)) / UN_DIA).toInt()
        return when (dias) {
            0 -> "Hoy"
            1 -> "Ayer"
            else -> trf("Hace {0} días", dias)
        }
    }

    /** QuickDASH: 0 = sin dificultad, 100 = dificultad máxima (igual que la app del paciente). */
    fun interpretacionQuickDash(puntaje: Double): String = when {
        puntaje < 15 -> "Sin dificultad o muy leve"
        puntaje < 40 -> "Dificultad leve"
        puntaje < 60 -> "Dificultad moderada"
        else -> "Dificultad importante"
    }

    // Las 11 preguntas del QuickDASH, en el mismo orden que las responde el paciente
    val preguntasQuickDash = listOf(
        "Abrir un frasco nuevo o muy apretado",
        "Hacer tareas pesadas de la casa (lavar paredes o pisos)",
        "Cargar una bolsa del super o un bolso",
        "Lavarse la espalda",
        "Cortar la comida con cuchillo",
        "Hacer actividades recreativas que exigen fuerza o impacto en el brazo, hombro o mano",
        "Cuánto interfirió el problema en sus actividades con familia, amigos o vecinos",
        "Cuánto lo limitó en su trabajo o en sus actividades de todos los días",
        "Dolor en el brazo, hombro o mano",
        "Hormigueo o sensación de agujas en el brazo, hombro o mano",
        "Dificultad para dormir por el dolor en el brazo, hombro o mano"
    )

    private val opcionesDificultad = listOf(
        "Ninguna dificultad", "Dificultad leve", "Dificultad moderada", "Dificultad severa", "No lo puede hacer"
    )
    private val opcionesInterferencia = listOf("Nada", "Un poco", "Moderadamente", "Bastante", "Muchísimo")
    private val opcionesLimitacion = listOf(
        "Nada limitado", "Un poco limitado", "Moderadamente limitado", "Muy limitado", "No pudo hacerlo"
    )
    private val opcionesSintoma = listOf("Ninguno", "Leve", "Moderado", "Severo", "Muy severo")
    private val opcionesSueno = listOf(
        "Ninguna dificultad", "Dificultad leve", "Dificultad moderada", "Dificultad severa", "No pudo dormir"
    )

    /** Texto de la respuesta elegida para la pregunta [indice] (0 a 10). */
    fun textoRespuesta(indice: Int, respuesta: Int): String {
        val opciones = when (indice) {
            in 0..5 -> opcionesDificultad
            6 -> opcionesInterferencia
            7 -> opcionesLimitacion
            8, 9 -> opcionesSintoma
            else -> opcionesSueno
        }
        return opciones.getOrNull(respuesta) ?: "Sin respuesta"
    }
}

/**
 * Carga los datos del paciente al abrir la pantalla.
 * Devuelve el estado y una función para volver a intentar.
 */
@Composable
fun rememberPacienteDelTutor(): Pair<EstadoPacienteTutor, () -> Unit> {
    var estado by remember { mutableStateOf<EstadoPacienteTutor>(EstadoPacienteTutor.Cargando) }
    var intento by remember { mutableIntStateOf(0) }

    LaunchedEffect(intento) {
        estado = EstadoPacienteTutor.Cargando
        estado = PacienteDelTutor.cargar()
    }

    return estado to { intento++ }
}