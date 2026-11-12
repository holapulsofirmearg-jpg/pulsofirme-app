package com.example.pulsofirme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class Idioma(val codigo: String, val nombre: String)

val idiomasDisponibles = listOf(
    Idioma("es", "Español"),
    Idioma("en", "English")
)

object AppState {
    var idioma by mutableStateOf("es")
    var nombreUsuario by mutableStateOf("")
}

data class Textos(
    val splashSub: String,
    val bienvenido: String,
    val bienestar: String,
    val registrarse: String,
    val cuidador: String,
    val iniciarSesion: String,
    val s1Plain: String,
    val s1Blue: String,
    val s1Green: String,
    val s1Desc: String,
    val s2Plain: String,
    val s2Blue: String,
    val s2Green: String,
    val s2Desc: String,
    val s3Plain: String,
    val s3Blue: String,
    val s3Green: String,
    val s3Desc: String
)

val textosPorIdioma: Map<String, Textos> = mapOf(
    "es" to Textos(
        splashSub = "Tecnología que acompaña cada movimiento",
        bienvenido = "Bienvenido",
        bienestar = "Tu bienestar en tus manos",
        registrarse = "Registrarse",
        cuidador = "Acceder como cuidador",
        iniciarSesion = "Iniciar sesión",
        s1Plain = "Bienvenidos a ",
        s1Blue = "Pulso",
        s1Green = " Firme",
        s1Desc = "Tu compañero para el monitoreo y rehabilitación de Parkinson y Artritis. Realizá ejercicios guiados para mejorar la movilidad de tus manos y otras extremidades, y seguí tu progreso día a día. Estamos acá para acompañarte en cada movimiento.",
        s2Plain = "Todo tu progreso, ",
        s2Blue = "en un solo ",
        s2Green = "lugar",
        s2Desc = "Realizá ejercicios personalizados para mejorar tu movilidad, seguí tu evolución día a día y recibí recomendaciones para tu bienestar.\n\nEstamos con vos en cada paso de tu tratamiento.",
        s3Plain = "Control y acceso ",
        s3Blue = "compartido ",
        s3Green = "de datos",
        s3Desc = "¡Excelente! Ahora, tu médico, tutor, padre o amigo autorizado podrá monitorizar tu progreso, ver tu historial de sesiones y estadísticas en tiempo real para apoyarte en tu recuperación. Todo controlado por ti desde la app."
    ),
    "en" to Textos(
        splashSub = "Technology that supports every movement",
        bienvenido = "Welcome",
        bienestar = "Your wellbeing in your hands",
        registrarse = "Sign up",
        cuidador = "Access as caregiver",
        iniciarSesion = "Log in",
        s1Plain = "Welcome to ",
        s1Blue = "Pulso",
        s1Green = " Firme",
        s1Desc = "Your companion for monitoring and rehabilitation of Parkinson's and Arthritis. Do guided exercises to improve the mobility of your hands and other limbs, and track your progress day by day. We are here to support you in every movement.",
        s2Plain = "All your progress, ",
        s2Blue = "in one ",
        s2Green = "place",
        s2Desc = "Do personalized exercises to improve your mobility, track your progress day by day and receive recommendations for your wellbeing.\n\nWe are with you at every step of your treatment.",
        s3Plain = "Control and ",
        s3Blue = "shared access ",
        s3Green = "to data",
        s3Desc = "Excellent! Now your doctor, guardian, parent or authorized friend can monitor your progress, view your session history and real-time statistics to support your recovery. All controlled by you from the app."
    )
)

@Composable
fun textosActuales(): Textos {
    return textosPorIdioma[AppState.idioma] ?: textosPorIdioma["es"]!!
}