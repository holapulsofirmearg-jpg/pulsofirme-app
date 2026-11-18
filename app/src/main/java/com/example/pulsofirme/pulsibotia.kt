package com.example.pulsofirme

import com.example.pulsofirme.screens.IdiomaApp

import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.ai.Chat
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date

// PulsiBot con IA real (Gemini a través de Firebase AI Logic).
// - La clave de la IA la maneja Firebase: NO va dentro de la app.
// - Antes de cada charla arma un resumen con los datos reales del usuario
//   (respetando los permisos que dio el paciente) y se lo pasa a la IA.
// - Si no hay internet o la IA falla, devuelve null y la pantalla usa
//   las respuestas locales de siempre.
class PulsiBotIA {

    companion object {
        // Modelo recomendado por Firebase (nov. 2026). Si Google lo retira, cambialo acá.
        const val MODELO = "gemini-3.8-flash"

        // Se suma a las instrucciones cuando la app está en inglés
        private const val IDIOMA_INGLES = """

LANGUAGE (MOST IMPORTANT RULE)
The app is set to English. Always answer in simple, warm English,
even if the user writes in Spanish. Use the English names of the app sections
(Home, Exercises, Progress, Device, Alerts, Account, Settings)."""
    }

    private var chat: Chat? = null

    /** Lee los datos del usuario y prepara la charla. Devuelve false si no pudo. */
    suspend fun preparar(rol: String): Boolean = try {
        val contexto = armarContexto(rol)
        val modelo = Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel(
                modelName = MODELO,
                systemInstruction = content {
                    text(instrucciones(rol, contexto) + if (IdiomaApp.esIngles) IDIOMA_INGLES else "")
                },
                generationConfig = generationConfig {
                    temperature = 0.5f
                    maxOutputTokens = 500
                }
            )
        chat = modelo.startChat()
        true
    } catch (e: Exception) {
        chat = null
        false
    }

    /** Respuesta de la IA, o null si falló (sin internet, límite de uso, etc.). */
    suspend fun responder(mensaje: String): String? = try {
        chat?.sendMessage(mensaje)?.text?.trim()?.ifBlank { null }
    } catch (e: Exception) {
        null
    }

    // =====================================================================
    // INSTRUCCIONES (cómo tiene que comportarse PulsiBot)
    // =====================================================================

    private fun instrucciones(rol: String, contexto: String): String = """
Sos PulsiBot, el asistente de la app PulsoFirme (Argentina).
PulsoFirme es un guante inteligente + app para personas con Parkinson y artritis:
estabiliza temblores con micromotores, acompaña ejercicios de rehabilitación y permite
que tutores y profesionales sigan el progreso del paciente.
Lo crearon 4 estudiantes de la Escuela Experimental PROA (sede Alta Gracia, Córdoba):
Ivan Correa, Gustavo Carrasco, Ana Cabagna y Morena Martinez.
Contacto y soporte: holapulsofirmearg@gmail.com (o Cuenta → Ayuda y soporte).

Estás hablando con un usuario de rol: $rol.

CÓMO RESPONDÉS
- Español rioplatense, con voseo, cálido y simple. Pensá que muchos usuarios son personas mayores.
- Respuestas cortas: 2 a 4 oraciones como máximo. Sin tablas. Usá listas solo si son pasos.
- Usá los datos del usuario de abajo para personalizar (nombre, racha, ejercicios, QuickDASH, sensación).
- Si un dato no está en el resumen, decí que no lo tenés. NUNCA inventes números ni datos.
- Si preguntan por algo de la app, explicá dónde está (Inicio, Ejercicios, Progreso, Alertas, Dispositivo, Cuenta).
- Solo hablás de PulsoFirme, rehabilitación, el guante, ejercicios, bienestar y uso de la app.
  Si preguntan otra cosa, redirigí amablemente.

LÍMITES DE SALUD (obligatorios)
- No diagnosticás, no interpretás estudios y no indicás medicamentos, dosis ni cambios de tratamiento:
  eso lo decide su profesional.
- Si describe dolor fuerte, una caída, un síntoma nuevo o que empeora: que deje el ejercicio
  y consulte a su médico. Ante una emergencia: llamar al 107 (SAME) o al 911.
- Los ejercicios son orientativos. Si duele, se para.
- No recomiendes ejercicios Premium a quien no tiene Premium: podés mencionar que existen.

EJERCICIOS DE LA APP
- Gratis Parkinson: Extensión de dedos, Toques de dedos, Pronación y supinación,
  Abrir y cerrar la mano, Golpeteo alternado de dedos.
- Gratis Artritis: Movilidad de muñecas, Puño y extensión, Rodar una pelota blanda,
  Oposición del pulgar, Deslizamiento de dedos.
- Premium Parkinson: ${EjerciciosPremium.seleccion("Parkinson").joinToString { it.nombre }}.
- Premium Artritis: ${EjerciciosPremium.seleccion("Artritis").joinToString { it.nombre }}.

DATOS ACTUALES DEL USUARIO (de la base de datos, hoy)
$contexto
""".trimIndent()

    // =====================================================================
    // DATOS REALES DEL USUARIO
    // =====================================================================

    private suspend fun armarContexto(rol: String): String {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return "- Sin sesión iniciada."
        val db = FirebaseFirestore.getInstance()
        val yo = db.collection("usuarios").document(uid).get().await()

        return when (rol) {
            "profesional" -> {
                val pacientes = (yo.get("pacientesVinculados") as? List<*>)?.size ?: 0
                buildString {
                    appendLine("- Nombre: ${yo.getString("nombre").orEmpty()}")
                    appendLine("- Profesión: ${yo.getString("especialidad") ?: yo.getString("profesion").orEmpty()}")
                    appendLine("- Pacientes vinculados: $pacientes")
                    appendLine("- Plan: ${if (UserData.esPremium) "Premium Profesional" else "Gratis (1 paciente)"}")
                    appendLine("- Para ver datos de un paciente, indicale que entre en Pacientes y elija al paciente.")
                }
            }

            "tutor", "cuidador" -> {
                val pacUid = yo.getString("pacienteVinculadoUid").orEmpty()
                if (pacUid.isBlank()) return "- Nombre del tutor: ${yo.getString("nombre").orEmpty()}\n- Todavía no tiene un paciente vinculado."
                val pac = db.collection("usuarios").document(pacUid).get().await()
                val comparte = pac.getBoolean("permisosTutor.progreso") ?: true
                buildString {
                    appendLine("- Nombre del tutor: ${yo.getString("nombre").orEmpty()}")
                    appendLine("- Paciente vinculado: ${nombreCompleto(pac)}")
                    if (comparte) {
                        append(resumenPaciente(pac, db))
                    } else {
                        appendLine("- El paciente NO comparte su progreso con el tutor. No des datos de progreso.")
                    }
                }
            }

            else -> buildString {
                appendLine("- Es el paciente. Nombre: ${nombreCompleto(yo)}")
                append(resumenPaciente(yo, db))
            }
        }
    }

    private fun nombreCompleto(doc: DocumentSnapshot): String =
        listOfNotNull(doc.getString("nombre"), doc.getString("apellido"))
            .filter { it.isNotBlank() }.joinToString(" ").ifBlank { "sin nombre" }

    private suspend fun resumenPaciente(pac: DocumentSnapshot, db: FirebaseFirestore): String {
        val ref = db.collection("usuarios").document(pac.id)
        val diagnosticos = (pac.get("diagnosticos") as? List<*>)?.filterIsInstance<String>().orEmpty()
        val premium = pac.getBoolean("esPremium") == true || pac.getBoolean("premiumDemo") == true

        // Ejercicios de los últimos 60 días
        val desde = Timestamp(Date(System.currentTimeMillis() - 60L * 24 * 60 * 60 * 1000))
        val ejercicios = try {
            ref.collection("ejerciciosRealizados").whereGreaterThanOrEqualTo("fecha", desde).get().await().documents
        } catch (e: Exception) { emptyList() }

        val unDia = 24L * 60 * 60 * 1000
        fun dia(ms: Long) = Calendar.getInstance().apply {
            timeInMillis = ms
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val hoy = dia(System.currentTimeMillis())
        val registros = ejercicios.mapNotNull { d ->
            val ms = d.getTimestamp("fecha")?.toDate()?.time ?: return@mapNotNull null
            Triple(dia(ms), d.getString("ejercicio") ?: "Ejercicio", d.getLong("segundos") ?: 0L)
        }
        val dias = registros.map { it.first }.toHashSet()
        var cursor = if (hoy in dias) hoy else hoy - unDia
        var racha = 0
        while (cursor in dias) { racha++; cursor -= unDia }

        val deHoy = registros.filter { it.first == hoy }
        val semana = registros.count { it.first > hoy - 7 * unDia }
        val ultimos = registros.sortedByDescending { it.first }.map { it.second }.distinct().take(3)

        // Último QuickDASH
        val quickdash = try {
            ref.collection("quickdash").orderBy("fecha", Query.Direction.DESCENDING).limit(1).get().await()
                .documents.firstOrNull()
        } catch (e: Exception) { null }

        val sensacion = SensacionDiaria.deHoy(pac.getString("sensacionHoy"), pac.getString("sensacionFecha"))


        return buildString {
            appendLine("- Diagnóstico: ${diagnosticos.joinToString().ifBlank { "no cargado" }}")
            appendLine("- Mano del guante: ${pac.getString("mano").orEmpty().ifBlank { "no cargada" }}")
            appendLine("- Plan: ${if (premium) "Premium" else "Gratis"}")
            appendLine("- Ejercicios hechos hoy: ${deHoy.size}${if (deHoy.isNotEmpty()) " (${deHoy.joinToString { it.second }}), ${deHoy.sumOf { it.third } / 60} min en total" else ""}")
            appendLine("- Ejercicios en los últimos 7 días: $semana")
            appendLine("- Racha actual: $racha día(s) seguidos")
            if (ultimos.isNotEmpty()) appendLine("- Últimos ejercicios: ${ultimos.joinToString()}")
            appendLine("- Cómo se siente hoy: ${sensacion ?: "todavía no lo registró"}")
            if (quickdash != null) {
                val puntaje = quickdash.getDouble("puntaje")
                val fecha = quickdash.getTimestamp("fecha")?.toDate()?.time
                val haceDias = fecha?.let { ((hoy - dia(it)) / unDia).toInt() }
                appendLine("- Último QuickDASH: ${puntaje?.let { String.format("%.0f", it) } ?: "?"} sobre 100 " +
                        "(más bajo = menos dificultad)${haceDias?.let { ", hace $it día(s)" } ?: ""}")
            } else {
                appendLine("- QuickDASH: todavía no hizo ninguno")
            }
        }
    }
}