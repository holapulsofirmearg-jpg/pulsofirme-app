package com.example.pulsofirme.alarmas

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object AlarmaScheduler {

    private const val PREFS = "alarmas_medicacion"
    private const val KEY_HORARIOS = "horarios"
    private const val KEY_DIAS = "dias"
    private const val KEY_ACTIVO = "activo"
    private const val MAX_ALARMAS = 20
    private const val CODIGO_POSPONER = 999

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    // ---------- LEER LO GUARDADO ----------

    fun horariosGuardados(context: Context): List<String> =
        (prefs(context).getString(KEY_HORARIOS, "") ?: "")
            .split(";").filter { it.isNotBlank() }

    // Devuelve null si nunca se guardó nada (la pantalla usa lunes a viernes por defecto)
    fun diasGuardados(context: Context): Set<Int>? {
        val texto = prefs(context).getString(KEY_DIAS, null) ?: return null
        return texto.split(";").mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun activoGuardado(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ACTIVO, true)

    // ---------- GUARDAR / ELIMINAR ----------

    // dias: 0 = lunes ... 6 = domingo (igual que en MedicationScreen)
    fun guardarYProgramar(
        context: Context,
        horarios: List<String>,
        dias: Set<Int>,
        activo: Boolean
    ) {
        prefs(context).edit()
            .putString(KEY_HORARIOS, horarios.joinToString(";"))
            .putString(KEY_DIAS, dias.joinToString(";"))
            .putBoolean(KEY_ACTIVO, activo)
            .apply()
        reprogramarTodo(context)
        // Para que el tutor sepa a qué hora le toca
        TomasMedicacion.sincronizar(horarios, dias, activo)
    }

    // Apaga el recordatorio al instante (deja guardados los horarios y días)
    fun desactivar(context: Context) {
        prefs(context).edit().putBoolean(KEY_ACTIVO, false).apply()
        cancelarTodo(context)
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pendingDisparo(context, CODIGO_POSPONER))
        TomasMedicacion.sincronizar(horariosGuardados(context), diasGuardados(context) ?: emptySet(), false)
    }

    fun eliminarTodo(context: Context) {
        prefs(context).edit().clear().apply()
        TomasMedicacion.sincronizar(emptyList(), emptySet(), false)
        cancelarTodo(context)
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pendingDisparo(context, CODIGO_POSPONER))
    }

    // ---------- PROGRAMACIÓN ----------

    fun reprogramarTodo(context: Context) {
        cancelarTodo(context)
        if (!prefs(context).getBoolean(KEY_ACTIVO, false)) return

        val horarios = horariosGuardados(context)
        val dias = diasGuardados(context) ?: return
        if (dias.isEmpty()) return

        horarios.take(MAX_ALARMAS).forEachIndexed { i, horario ->
            val millis = proximoDisparo(horario, dias) ?: return@forEachIndexed
            programar(context, millis, i, horario)
        }
    }

    fun posponer(context: Context, minutos: Int, horario: String) {
        val millis = System.currentTimeMillis() + minutos * 60_000L
        programar(context, millis, CODIGO_POSPONER, horario)
    }

    private fun programar(context: Context, millis: Long, codigo: Int, horario: String) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val disparo = pendingDisparo(context, codigo, horario)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            // Sin permiso de alarma exacta: igual suena, pero puede atrasarse un poco
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, disparo)
        } else {
            val abrirAlarma = PendingIntent.getActivity(
                context,
                codigo,
                Intent(context, AlarmaActivity::class.java).putExtra("horario", horario),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            am.setAlarmClock(AlarmManager.AlarmClockInfo(millis, abrirAlarma), disparo)
        }
    }

    private fun cancelarTodo(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for (i in 0 until MAX_ALARMAS) {
            am.cancel(pendingDisparo(context, i))
        }
    }



    private fun pendingDisparo(context: Context, codigo: Int, horario: String = ""): PendingIntent {
        val intent = Intent(context, AlarmaReceiver::class.java).putExtra("horario", horario)
        return PendingIntent.getBroadcast(
            context,
            codigo,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    // Busca la próxima fecha (hoy o en los próximos 7 días) que coincida con un día elegido
    private fun proximoDisparo(horario: String, dias: Set<Int>): Long? {
        val partes = horario.take(5).split(":")
        val hora = partes.getOrNull(0)?.trim()?.toIntOrNull() ?: return null
        val minuto = partes.getOrNull(1)?.trim()?.toIntOrNull() ?: return null
        val ahora = System.currentTimeMillis()

        for (offset in 0..7) {
            val c = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, offset)
                set(Calendar.HOUR_OF_DAY, hora)
                set(Calendar.MINUTE, minuto)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            if (c.timeInMillis <= ahora) continue
            val indiceDia = (c.get(Calendar.DAY_OF_WEEK) + 5) % 7 // lunes=0 ... domingo=6
            if (indiceDia in dias) return c.timeInMillis
        }
        return null
    }
}