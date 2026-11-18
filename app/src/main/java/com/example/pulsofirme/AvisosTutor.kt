package com.example.pulsofirme

import com.example.pulsofirme.screens.IdiomaApp
import com.example.pulsofirme.screens.tr
import com.example.pulsofirme.screens.trf

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.pulsofirme.screens.MainActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Avisos reales al tutor, SIN servidor (no necesita el plan Blaze).
 *
 * La app del tutor revisa en segundo plano, más o menos cada 1 hora,
 * los datos del paciente vinculado y muestra una notificación cuando:
 *  - son más de las 18 h y todavía no hizo ejercicio      (Alertas generales)
 *  - lleva 3 días o más sin hacer ejercicio               (Alertas generales)
 *  - registró dolor en "Cómo te sentís hoy"               (Alertas generales)
 *  - completó ejercicios hoy                              (Progreso)
 *  - completó una evaluación QuickDASH                    (Evaluación QuickDASH)
 *  - pasó 1 hora de su toma y no marcó "Ya la tomé"       (Alertas de medicación)
 *
 * Respeta lo que el tutor activó en "Configuración de alertas" y si el
 * paciente comparte su progreso. Cada aviso sale una sola vez por día.
 */
object AvisosTutor {

    const val CANAL = "avisos_tutor"
    private const val TRABAJO = "revision_avisos_tutor"

    /** Arranca la revisión periódica (si ya estaba, la deja como está). */
    fun programar(context: Context) {
        crearCanal(context)
        val pedido = PeriodicWorkRequestBuilder<AvisosTutorWorker>(1, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(TRABAJO, ExistingPeriodicWorkPolicy.KEEP, pedido)
    }

    /** Hace la revisión ya mismo y manda un aviso de prueba (para mostrar en la presentación). */
    fun probarAhora(context: Context) {
        crearCanal(context)
        val pedido = OneTimeWorkRequestBuilder<AvisosTutorWorker>()
            .setInputData(workDataOf(AvisosTutorWorker.PRUEBA to true))
            .build()
        WorkManager.getInstance(context).enqueue(pedido)
    }

    fun cancelar(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(TRABAJO)
    }

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL,
                tr("Avisos del paciente"),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = tr("Avisos sobre los ejercicios y el progreso del paciente que acompañás")
            }
            context.getSystemService(NotificationManager::class.java)
                ?.createNotificationChannel(canal)
        }
    }

    fun puedeNotificar(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED

    fun mostrar(context: Context, id: Int, titulo: String, texto: String) {
        if (!puedeNotificar(context)) return

        val abrirApp = PendingIntent.getActivity(
            context,
            id,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val aviso = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setColor(0xFF1B7A4B.toInt())
            .setContentTitle(titulo)
            .setContentText(texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
            .setContentIntent(abrirApp)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(id, aviso)
        } catch (e: SecurityException) {
            // Sin permiso de notificaciones: no se muestra
        }
    }
}

class AvisosTutorWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val PRUEBA = "prueba"

        private const val ID_SIN_EJERCICIO = 7101
        private const val ID_DIAS_SIN = 7102
        private const val ID_DOLOR = 7103
        private const val ID_PROGRESO = 7104
        private const val ID_QUICKDASH = 7105
        private const val ID_PRUEBA = 7199
        private const val ID_MEDICACION = 7200 // + índice del horario
    }

    override suspend fun doWork(): Result {
        // El worker corre con la app cerrada: cargamos el idioma elegido
        IdiomaApp.cargar(applicationContext)
        val esPrueba = inputData.getBoolean(PRUEBA, false)
        val ctx = applicationContext

        return try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return Result.success()
            val db = FirebaseFirestore.getInstance()
            val yo = db.collection("usuarios").document(uid).get().await()

            // Solo para tutores
            if (yo.getString("rol") != "tutor") return Result.success()

            val pacUid = yo.getString("pacienteVinculadoUid").orEmpty()
            if (pacUid.isBlank()) return Result.success()

            // Qué activó el tutor (mismos valores por defecto que la pantalla)
            val avisarGenerales = yo.getBoolean("tutorAlertaGenerales") ?: true
            val avisarProgreso = yo.getBoolean("tutorAlertaProgreso") ?: false
            val avisarQuickdash = yo.getBoolean("tutorAlertaQuickdash") ?: true
            val avisarMedicacion = yo.getBoolean("tutorAlertaMedicacion") ?: true

            val pacRef = db.collection("usuarios").document(pacUid)
            val pac = pacRef.get().await()
            val nombre = pac.getString("nombre")?.ifBlank { null } ?: "Tu paciente"

            if (esPrueba) {
                AvisosTutor.mostrar(
                    ctx, ID_PRUEBA,
                    tr("Avisos de PulsoFirme activados"),
                    trf("Así te vamos a avisar sobre los ejercicios y el progreso de {0}.", nombre)
                )
            }

            val prefs = ctx.getSharedPreferences("avisos_tutor_$uid", Context.MODE_PRIVATE)
            val hoy = SensacionDiaria.claveHoy()
            fun yaAvisado(clave: String) = prefs.getBoolean(clave, false)
            fun marcar(clave: String) = prefs.edit().putBoolean(clave, true).apply()

            // ---------- Medicación (permiso aparte: "Alertas de medicación") ----------
            val comparteMedicacion = pac.getBoolean("permisosTutor.medicacion") ?: true
            if (avisarMedicacion && comparteMedicacion && pac.getBoolean("medicacionActiva") == true) {
                val dias = (pac.get("medicacionDias") as? List<*>)?.mapNotNull { (it as? Number)?.toInt() }.orEmpty()
                val indiceHoy = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7 // lunes = 0
                if (indiceHoy in dias) {
                    val horarios = (pac.get("medicacionHorarios") as? List<*>)?.filterIsInstance<String>().orEmpty()
                    val tomadas = pacRef.collection("tomasMedicacion").whereEqualTo("fecha", hoy).get().await()
                        .documents.mapNotNull { it.getString("horario") }.toSet()
                    val ahora = Calendar.getInstance()
                    val minutosAhora = ahora.get(Calendar.HOUR_OF_DAY) * 60 + ahora.get(Calendar.MINUTE)

                    horarios.forEachIndexed { i, h ->
                        val partes = h.split(":")
                        val minutosToma = (partes.getOrNull(0)?.toIntOrNull() ?: return@forEachIndexed) * 60 +
                                (partes.getOrNull(1)?.toIntOrNull() ?: 0)
                        val pasoUnaHora = minutosAhora >= minutosToma + 60
                        if (pasoUnaHora && h !in tomadas && !yaAvisado("medicacion_${hoy}_$h")) {
                            AvisosTutor.mostrar(
                                ctx, ID_MEDICACION + i,
                                trf("{0} no registró su medicación de las {1}", nombre, h),
                                tr("Pasó más de una hora y no marcó \"Ya la tomé\". Puede que se haya olvidado.")
                            )
                            marcar("medicacion_${hoy}_$h")
                        }
                    }
                }
            }

            // Si el paciente no comparte su progreso, no se avisa nada de eso
            val comparte = pac.getBoolean("permisosTutor.progreso") ?: true
            if (!comparte) return Result.success()

            // ---------- Ejercicios de los últimos 30 días ----------
            val desde = Timestamp(Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000))
            val ejercicios = pacRef.collection("ejerciciosRealizados")
                .whereGreaterThanOrEqualTo("fecha", desde).get().await().documents
            val fechas = ejercicios.mapNotNull { it.getTimestamp("fecha")?.toDate()?.time }
            val deHoy = fechas.count { SensacionDiaria.claveDe(it) == hoy }
            val hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

            if (avisarGenerales) {
                // Todavía no hizo ejercicio (después de las 18 h)
                if (deHoy == 0 && hora >= 18 && !yaAvisado("sinEjercicio_$hoy")) {
                    AvisosTutor.mostrar(
                        ctx, ID_SIN_EJERCICIO,
                        trf("{0} todavía no hizo ejercicio hoy", nombre),
                        tr("Un mensajito para recordárselo puede ayudar a mantener la constancia.")
                    )
                    marcar("sinEjercicio_$hoy")
                }

                // Varios días sin ejercicio
                val ultimo = fechas.maxOrNull()
                val diasSin = if (ultimo == null) 30
                else ((inicioDeDia(System.currentTimeMillis()) - inicioDeDia(ultimo)) / (24L * 60 * 60 * 1000)).toInt()
                if (diasSin >= 3 && !yaAvisado("diasSin_$hoy")) {
                    AvisosTutor.mostrar(
                        ctx, ID_DIAS_SIN,
                        if (diasSin >= 30) trf("{0} no registra ejercicios hace un mes", nombre)
                        else trf("{0} lleva {1} días sin hacer ejercicio", nombre, diasSin),
                        tr("Puede ser un buen momento para acompañarlo/a en una rutina corta.")
                    )
                    marcar("diasSin_$hoy")
                }

                // Registró dolor hoy
                val sensacion = SensacionDiaria.deHoy(pac.getString("sensacionHoy"), pac.getString("sensacionFecha"))
                if (sensacion != null && sensacion.contains("Dolor", ignoreCase = true) && !yaAvisado("dolor_$hoy")) {
                    AvisosTutor.mostrar(
                        ctx, ID_DOLOR,
                        trf("{0} registró dolor hoy", nombre),
                        trf("Indicó \"{0}\" en cómo se siente. Si el dolor sigue o aumenta, conviene consultar a su profesional.", tr(sensacion))
                    )
                    marcar("dolor_$hoy")
                }
            }

            // ---------- Progreso: ejercicios completados hoy ----------
            if (avisarProgreso && deHoy > 0) {
                val avisadosHoy = prefs.getInt("progreso_$hoy", 0)
                if (deHoy > avisadosHoy) {
                    AvisosTutor.mostrar(
                        ctx, ID_PROGRESO,
                        trf("¡{0} está entrenando! 💪", nombre),
                        if (deHoy == 1) tr("Completó 1 ejercicio hoy.") else trf("Completó {0} ejercicios hoy.", deHoy)
                    )
                    prefs.edit().putInt("progreso_$hoy", deHoy).apply()
                }
            }

            // ---------- QuickDASH completado ----------
            if (avisarQuickdash) {
                val ultimaEval = pacRef.collection("quickdash")
                    .orderBy("fecha", Query.Direction.DESCENDING).limit(1).get().await()
                    .documents.firstOrNull()
                val ms = ultimaEval?.getTimestamp("fecha")?.toDate()?.time
                if (ms != null) {
                    val vista = prefs.getLong("ultimoQuickdash", 0L)
                    if (vista == 0L) {
                        // Primera revisión: se toma como punto de partida, sin avisar
                        prefs.edit().putLong("ultimoQuickdash", ms).apply()
                    } else if (ms > vista) {
                        val puntaje = ultimaEval.getDouble("puntaje")
                        AvisosTutor.mostrar(
                            ctx, ID_QUICKDASH,
                            trf("{0} completó su evaluación QuickDASH", nombre),
                            puntaje?.let { trf("Obtuvo {0} puntos sobre 100 (más bajo = menos dificultad).", it.toInt()) }
                                ?: tr("Podés ver el resultado en Progreso.")
                        )
                        prefs.edit().putLong("ultimoQuickdash", ms).apply()
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            // Sin internet o error momentáneo: se vuelve a intentar más tarde
            Result.retry()
        }
    }

    private fun inicioDeDia(ms: Long): Long = Calendar.getInstance().apply {
        timeInMillis = ms
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}