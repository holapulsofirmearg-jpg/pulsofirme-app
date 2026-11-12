package com.example.pulsofirme.alarmas

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.pulsofirme.notificaciones.NotificacionesRepo

class AlarmaReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val horario = intent.getStringExtra("horario") ?: ""

        // Queda registrada en la pantalla Novedades
        NotificacionesRepo.agregar(
            context,
            tipo = "medicacion",
            titulo = "Hora de tu medicación",
            detalle = if (horario.isNotBlank()) "Sonó tu recordatorio de las $horario." else "Sonó tu recordatorio de medicación."
        )

        mostrarAlarma(context, horario)
        // Deja programada la próxima vez que tiene que sonar
        AlarmaScheduler.reprogramarTodo(context)
    }

    private fun mostrarAlarma(context: Context, horario: String) {
        crearCanal(context)

        val pantallaAlarma = PendingIntent.getActivity(
            context,
            0,
            Intent(context, AlarmaActivity::class.java)
                .putExtra("horario", horario)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Hora de tu medicación")
            .setContentText(if (horario.isNotBlank()) "Recordatorio de las $horario" else "Tomá tu medicación")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pantallaAlarma, true)
            .setContentIntent(pantallaAlarma)
            .setOngoing(true)
            .setAutoCancel(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        NotificationManagerCompat.from(context).notify(NOTIF_ID, notificacion)
    }

    companion object {
        const val CANAL_ID = "alarma_medicacion"
        const val NOTIF_ID = 4001

        fun crearCanal(context: Context) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
            val manager = context.getSystemService(NotificationManager::class.java)
            if (manager.getNotificationChannel(CANAL_ID) != null) return

            val canal = NotificationChannel(
                CANAL_ID,
                "Alarmas de medicación",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Suena a la hora de tomar la medicación"
                enableVibration(true)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }
            manager.createNotificationChannel(canal)
        }
    }
}

// Vuelve a programar las alarmas cuando el celular se reinicia
class ReinicioReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            AlarmaScheduler.reprogramarTodo(context)
        }
    }
}