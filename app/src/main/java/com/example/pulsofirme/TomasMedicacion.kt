package com.example.pulsofirme.alarmas

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Tomas de medicación.
 *
 * - Cuando el paciente toca "Ya la tomé", se guarda en
 *   usuarios/{uid}/tomasMedicacion/{fecha}_{hora}  (la puede leer su tutor)
 * - Los horarios, días y si está activo se copian al documento del paciente
 *   (medicacionHorarios, medicacionDias, medicacionActiva) para que la app
 *   del tutor sepa a qué hora tenía que tomarla.
 * - También se guarda en el celular, para mostrar "Hoy: 1 de 2 tomas" sin internet.
 */
object TomasMedicacion {

    private const val PREFS = "tomas_medicacion"

    private fun hoy(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    /** "08:00 hs" -> "08:00" */
    fun hora(horario: String): String = horario.take(5).trim()

    /** El paciente marcó "Ya la tomé". */
    fun registrar(context: Context, horario: String) {
        val fecha = hoy()
        val h = hora(horario).ifBlank {
            SimpleDateFormat("HH:mm", Locale.US).format(Date())
        }

        // En el celular
        val prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val claves = prefs.getStringSet(fecha, emptySet()).orEmpty() + h
        prefs.edit().clear().putStringSet(fecha, claves).apply() // solo se guarda el día de hoy

        // En Firestore (para el tutor)
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("usuarios").document(uid)
            .collection("tomasMedicacion").document("${fecha}_${h.replace(":", "")}")
            .set(
                mapOf(
                    "fecha" to fecha,
                    "horario" to h,
                    "registrada" to FieldValue.serverTimestamp()

                )
            )
    }

    /** Horarios que ya marcó hoy en este celular (ej. "08:00"). */
    fun tomadasHoy(context: Context): Set<String> =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getStringSet(hoy(), emptySet()).orEmpty()

    /** Horarios que le tocan HOY según los días elegidos (vacío si está apagado). */
    fun horariosDeHoy(context: Context): List<String> {
        if (!AlarmaScheduler.activoGuardado(context)) return emptyList()
        val dias = AlarmaScheduler.diasGuardados(context) ?: return emptyList()
        val indiceHoy = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7 // lunes = 0
        if (indiceHoy !in dias) return emptyList()
        return AlarmaScheduler.horariosGuardados(context).map { hora(it) }.sorted()
    }

    /** Copia la configuración de la alarma a la cuenta del paciente. */
    fun sincronizar(horarios: List<String>, dias: Set<Int>, activo: Boolean) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("usuarios").document(uid)
            .update(
                mapOf(
                    "medicacionHorarios" to horarios.map { hora(it) }.sorted(),
                    "medicacionDias" to dias.sorted(),
                    "medicacionActiva" to activo
                )
            )
    }
}