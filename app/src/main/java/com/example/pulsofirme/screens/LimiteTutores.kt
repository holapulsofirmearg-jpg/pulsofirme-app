package com.example.pulsofirme

object LimiteTutores {

    const val MAX_GRATIS = 1
    const val MAX_PREMIUM = 3

    fun maximo(pacienteEsPremium: Boolean): Int =
        if (pacienteEsPremium) MAX_PREMIUM else MAX_GRATIS

    fun puedeSumarOtro(tutoresActuales: Int, pacienteEsPremium: Boolean): Boolean =
        tutoresActuales < maximo(pacienteEsPremium)

    fun mensajeLimite(pacienteEsPremium: Boolean): String =
        if (pacienteEsPremium)
            "Este paciente ya llegó al máximo de $MAX_PREMIUM tutores."
        else
            "Este paciente ya tiene $MAX_GRATIS tutor. Con Premium puede sumar hasta $MAX_PREMIUM."
}