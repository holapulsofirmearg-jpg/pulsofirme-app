package com.example.pulsofirme

import androidx.compose.ui.graphics.Color
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Sensación diaria del paciente ("Cómo te sentís hoy").
 *
 * Se guarda en el documento del paciente (usuarios/{uid}):
 *  - sensacionHoy:   el texto elegido (ej. "Rigidez baja")
 *  - sensacionFecha: el día en formato yyyy-MM-dd
 *  - sensaciones:    historial por día { "2026-11-03": "Rigidez baja", ... }
 *
 * Si sensacionFecha no es de hoy, se considera que hoy todavía no la registró.
 */
object SensacionDiaria {

    private fun formato() = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun claveDe(ms: Long): String = formato().format(Date(ms))

    fun claveHoy(): String = claveDe(System.currentTimeMillis())

    /** Claves de lunes a domingo de la semana actual. */
    fun clavesSemanaActual(): List<String> {
        val c = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 12)
            val desdeLunes = (get(Calendar.DAY_OF_WEEK) + 5) % 7
            add(Calendar.DAY_OF_YEAR, -desdeLunes)
        }
        return (0..6).map {
            val clave = claveDe(c.timeInMillis)
            c.add(Calendar.DAY_OF_YEAR, 1)
            clave
        }
    }

    /** Guarda la sensación de hoy en Firestore. */
    fun guardar(
        uid: String,
        texto: String,
        onError: () -> Unit = {}
    ) {
        val clave = claveHoy()
        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(uid)
            .update(
                mapOf(
                    "sensacionHoy" to texto,
                    "sensacionFecha" to clave,
                    "sensaciones.$clave" to texto,
                    "sensacionActualizada" to FieldValue.serverTimestamp()
                )
            )
            .addOnFailureListener { onError() }
    }

    /** Devuelve la sensación de hoy, o null si hoy no la registró. */
    fun deHoy(sensacionHoy: String?, sensacionFecha: String?): String? =
        if (!sensacionHoy.isNullOrBlank() && sensacionFecha == claveHoy()) sensacionHoy else null

    /** Color de cada opción (el mismo que usa el inicio del paciente). */
    fun colorDe(texto: String?): Color = when (texto) {
        "Rigidez baja" -> Color(0xFF2E9E63)
        "Temblor moderado", "Hinchazón leve" -> Color(0xFFE0A030)
        "Dolor leve" -> Color(0xFF1565C0)
        else -> Color(0xFFD1D5DB)
    }
}