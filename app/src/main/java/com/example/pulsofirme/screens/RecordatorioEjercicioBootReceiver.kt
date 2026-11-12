package com.example.pulsofirme.alarmas

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Cuando el celular se reinicia, Android borra las alarmas:
 * acá se vuelve a programar el recordatorio de ejercicio.
 */
class RecordatorioEjercicioBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            RecordatorioEjercicio.programarProxima(context)
        }
    }
}