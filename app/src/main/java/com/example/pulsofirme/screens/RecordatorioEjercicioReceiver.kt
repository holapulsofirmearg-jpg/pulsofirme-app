package com.example.pulsofirme.alarmas

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Se ejecuta cuando suena la alarma diaria: avisa (si hace falta)
 * y deja programada la del día siguiente.
 */
class RecordatorioEjercicioReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (!RecordatorioEjercicio.hechoHoy(context)) {
            RecordatorioEjercicio.mostrarNotificacion(context)
        }

        RecordatorioEjercicio.programarProxima(context)
    }
}