package com.example.pulsofirme.alarmas

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.pulsofirme.R
import com.example.pulsofirme.screens.MainActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Recordatorio diario de "ejercicio pendiente".
 * Es una alarma local: funciona con la app cerrada y sin internet.
 */
object RecordatorioEjercicio {

    const val CANAL_ID = "recordatorio_ejercicio"

    const val HORA_POR_DEFECTO = 18
    const val MINUTO_POR_DEFECTO = 0

    private const val PREFS = "pulsofirme_recordatorio_ejercicio"
    private const val CLAVE_ACTIVO = "activo"
    private const val CLAVE_HORA = "hora"
    private const val CLAVE_MINUTO = "minuto"
    private const val CLAVE_ULTIMO_EJERCICIO = "ultimo_ejercicio"
    private const val CLAVE_RACHA = "racha_dias"
    private const val CLAVE_AVISAR_RACHA = "avisar_racha"

    private const val REQUEST_CODE_ALARMA = 7301
    private const val ID_NOTIFICACION = 7302

    private fun prefs(context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun hoy(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private fun ayer(): String {
        val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(c.time)
    }

    // -----------------------------------------------------------------
    // Programar / cancelar
    // -----------------------------------------------------------------

    /** Activa el recordatorio diario a la hora indicada. */
    fun programar(
        context: Context,
        hora: Int = HORA_POR_DEFECTO,
        minuto: Int = MINUTO_POR_DEFECTO
    ) {
        val app = context.applicationContext
        prefs(app).edit()
            .putBoolean(CLAVE_ACTIVO, true)
            .putInt(CLAVE_HORA, hora)
            .putInt(CLAVE_MINUTO, minuto)
            .apply()

        programarProxima(app)
    }

    /** Programa la próxima alarma (la de hoy si todavía no pasó, si no la de mañana). */
    fun programarProxima(context: Context) {
        val app = context.applicationContext
        val preferencias = prefs(app)

        if (!preferencias.getBoolean(CLAVE_ACTIVO, false)) return

        val hora = preferencias.getInt(CLAVE_HORA, HORA_POR_DEFECTO)
        val minuto = preferencias.getInt(CLAVE_MINUTO, MINUTO_POR_DEFECTO)

        val ahora = Calendar.getInstance()
        val proxima = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= ahora.timeInMillis) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val alarmManager = app.getSystemService(AlarmManager::class.java) ?: return
        val pendingIntent = crearPendingIntent(app)

        val puedeExacta =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                    alarmManager.canScheduleExactAlarms()

        if (puedeExacta) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                proxima.timeInMillis,
                pendingIntent
            )
        } else {
            // Sin permiso de alarma exacta: llega, pero puede demorar unos minutos.
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                proxima.timeInMillis,
                pendingIntent
            )
        }
    }

    /** Desactiva el recordatorio y cancela la alarma pendiente. */
    fun cancelar(context: Context) {
        val app = context.applicationContext
        prefs(app).edit().putBoolean(CLAVE_ACTIVO, false).apply()

        val alarmManager = app.getSystemService(AlarmManager::class.java) ?: return
        alarmManager.cancel(crearPendingIntent(app))
    }

    private fun crearPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, RecordatorioEjercicioReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_ALARMA,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    // -----------------------------------------------------------------
    // Ejercicio hecho hoy (para no avisar de más)
    // -----------------------------------------------------------------

    fun marcarHechoHoy(context: Context) {
        val p = prefs(context)
        val ultimo = p.getString(CLAVE_ULTIMO_EJERCICIO, null)

        // Racha: ya contaba hoy = no cambia; hizo ayer = suma 1; si no = vuelve a 1
        val nuevaRacha = when (ultimo) {
            hoy() -> p.getInt(CLAVE_RACHA, 1).coerceAtLeast(1)
            ayer() -> p.getInt(CLAVE_RACHA, 0) + 1
            else -> 1
        }

        p.edit()
            .putString(CLAVE_ULTIMO_EJERCICIO, hoy())
            .putInt(CLAVE_RACHA, nuevaRacha)
            .apply()
    }

    /**
     * Días seguidos haciendo ejercicio (en este celular).
     * Si hizo hoy o ayer sigue viva; si pasó más de un día, vuelve a 0.
     */
    fun rachaActual(context: Context): Int {
        val p = prefs(context)
        val ultimo = p.getString(CLAVE_ULTIMO_EJERCICIO, null)
        return if (ultimo == hoy() || ultimo == ayer()) p.getInt(CLAVE_RACHA, 0) else 0
    }

    /** "Avisarme de mi racha" (switch de Configurar alertas). Por defecto activado. */
    fun setAvisarRacha(context: Context, activo: Boolean) {
        prefs(context).edit().putBoolean(CLAVE_AVISAR_RACHA, activo).apply()
    }

    private fun avisarRacha(context: Context): Boolean =
        prefs(context).getBoolean(CLAVE_AVISAR_RACHA, true)

    fun hechoHoy(context: Context): Boolean =
        prefs(context).getString(CLAVE_ULTIMO_EJERCICIO, null) == hoy()

    // -----------------------------------------------------------------
    // Notificación
    // -----------------------------------------------------------------

    @SuppressLint("MissingPermission")
    fun mostrarNotificacion(context: Context) {
        val app = context.applicationContext

        // Android 13+: sin permiso no se puede mostrar la notificación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(app, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        crearCanal(app)

        val abrirApp = Intent(app, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingAbrir = PendingIntent.getActivity(
            app,
            0,
            abrirApp,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Si tiene una racha de 2 días o más y todavía no hizo hoy, el aviso la menciona
        val racha = rachaActual(app)
        val conRacha = avisarRacha(app) && racha >= 2

        val titulo = if (conRacha) "¡No pierdas tu racha de $racha días!"
        else "¿Hiciste tus ejercicios hoy?"
        val texto = if (conRacha) "Con unos minutos de ejercicio hoy seguís sumando. Tocá para empezar."
        else "Dedicale unos minutos a tu mano. Tocá para empezar."

        val notificacion = NotificationCompat.Builder(app, CANAL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingAbrir)
            .build()

        NotificationManagerCompat.from(app).notify(ID_NOTIFICACION, notificacion)
    }

    private fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        if (manager.getNotificationChannel(CANAL_ID) != null) return

        val canal = NotificationChannel(
            CANAL_ID,
            "Recordatorio de ejercicio",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Te avisa si todavía no hiciste tu ejercicio del día"
        }
        manager.createNotificationChannel(canal)
    }
}