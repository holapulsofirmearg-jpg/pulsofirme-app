package com.example.pulsofirme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object ProgresoData {
    var ejerciciosCompletados by mutableStateOf(0)

    // Última evaluación QuickDASH (null = todavía no hizo ninguna)
    var puntajeQuickDash by mutableStateOf<Float?>(null)
    var fechaQuickDash by mutableStateOf<Long?>(null)
}