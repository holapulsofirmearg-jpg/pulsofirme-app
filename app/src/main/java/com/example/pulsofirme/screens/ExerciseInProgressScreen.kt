package com.example.pulsofirme.screens

import androidx.compose.runtime.saveable.rememberSaveable

import com.example.pulsofirme.ui.theme.enTema

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import java.util.Locale
import com.example.pulsofirme.Accesibilidad
import com.example.pulsofirme.EjercicioPremium
import com.example.pulsofirme.EjerciciosPremium
import com.example.pulsofirme.ParametrosEjercicio
import com.example.pulsofirme.R
import com.example.pulsofirme.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private val Verde: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
private val VerdeClaro: Color
    @Composable get() = Color(0xFFEFF6F1).enTema()
private val BordeGris: Color
    @Composable get() = Color(0xFFE5E7EB).enTema()
private val AmbarPausa: Color
    @Composable get() = Color(0xFFBA7517).enTema()
private val AmbarPausaClaro: Color
    @Composable get() = Color(0xFFFAEEDA).enTema()
private val RojoAviso: Color
    @Composable get() = Color(0xFFC0392B).enTema()
private val RojoAvisoClaro: Color
    @Composable get() = Color(0xFFFBEAE8).enTema()
data class PasoEjercicio(
    val numero: Int,
    val titulo: String,
    val emoji: String = "",
    val detalle: String,
    val imagenRes: Int? = null
)

private val pasosExtensionDedos = listOf(
    PasoEjercicio(
        numero = 1,
        titulo = "Posición inicial",
        detalle = "Cerrá la mano manteniendo los dedos flexionados.",
        imagenRes = R.drawable.extension_de_dedos_parkinson
    ),
    PasoEjercicio(
        numero = 2,
        titulo = "Extendé los dedos",
        detalle = "Abrí la mano y extendé completamente los dedos.",
        imagenRes = R.drawable.extension_de_dedos_2_parkinson
    ),
    PasoEjercicio(
        numero = 3,
        titulo = "Mantené la posición",
        detalle = "Mantené la extensión unos segundos.",
        imagenRes = R.drawable.extension_de_dedos_3_parkinson
    )
)

private val pasosToqueDedos = listOf(
    PasoEjercicio(
        numero = 1,
        titulo = "Mano abierta",
        detalle = "Mantené la mano abierta y los dedos estirados.",
        imagenRes = R.drawable.toque_de_dedos1_parkinson
    ),
    PasoEjercicio(
        numero = 2,
        titulo = "Pulgar con índice",
        detalle = "Tocá la punta del pulgar con el dedo índice.",
        imagenRes = R.drawable.toque_de_dedos2_parkinson
    ),
    PasoEjercicio(
        numero = 3,
        titulo = "Tocá cada dedo",
        detalle = "Seguí tocando el pulgar con cada dedo, uno por uno, y volvé a empezar.",
        imagenRes = R.drawable.toque_de_dedos3_parkinson
    )
)


private val pasosPronacionSupinacion = listOf(
    PasoEjercicio(
        numero = 1,
        titulo = "Palma hacia arriba",
        detalle = "Colocá el brazo al frente con la palma de la mano hacia arriba.",
        imagenRes = R.drawable.pronacion_supunacion1_parkinson
    ),
    PasoEjercicio(
        numero = 2,
        titulo = "Palma hacia abajo",
        detalle = "Girá lentamente la mano hasta que la palma quede hacia abajo.",
        imagenRes = R.drawable.pronacion_supunacion2_parkinson
    ),
    PasoEjercicio(
        numero = 3,
        titulo = "Volvé a girar",
        detalle = "Volvé a girar la mano con la palma hacia arriba y repetí el movimiento.",
        imagenRes = R.drawable.pronacion_supunacion3_parkinson
    )
)


private val pasosAbrirCerrarMano = listOf(
    PasoEjercicio(
        numero = 1,
        titulo = "Mano abierta",
        detalle = "Mantené la mano abierta con los dedos bien estirados.",
        imagenRes = R.drawable.abrir_cerrar_mano1_parkinson
    ),
    PasoEjercicio(
        numero = 2,
        titulo = "Cerrá la mano",
        detalle = "Cerrá lentamente la mano formando un puño.",
        imagenRes = R.drawable.abrir_cerrar_mano2_parkinson
    ),
    PasoEjercicio(
        numero = 3,
        titulo = "Volvé a abrir",
        detalle = "Volvé a abrir la mano completamente y repetí el movimiento.",
        imagenRes = R.drawable.abrir_cerrar_mano3_parkinson
    )
)


private val pasosGolpeteoAlternado = listOf(
    PasoEjercicio(
        numero = 1,
        titulo = "Apoyá la mano",
        detalle = "Apoyá la mano sobre una superficie con los dedos relajados.",
        imagenRes = R.drawable.golpeteo_alternado1_parkinson
    ),
    PasoEjercicio(
        numero = 2,
        titulo = "Golpeá con un dedo",
        detalle = "Levantá y bajá un dedo dando pequeños golpes sobre la superficie.",
        imagenRes = R.drawable.golpeteo_alternado2_parkinson
    ),
    PasoEjercicio(
        numero = 3,
        titulo = "Alterná los dedos",
        detalle = "Alterná el golpeteo entre los diferentes dedos de forma continua.",
        imagenRes = R.drawable.golpeteo_alternado3_parkinson
    )
)


private val pasosRodarPelota = listOf(
    PasoEjercicio(
        numero = 1,
        titulo = "Tomá la pelota",
        detalle = "Tomá la pelota suavemente.",
        imagenRes = R.drawable.rodar_pelota1_artritis
    ),
    PasoEjercicio(
        numero = 2,
        titulo = "Rodá la pelota",
        detalle = "Rodá la pelota en la palma.",
        imagenRes = R.drawable.rodar_pelota2_artritis
    ),
    PasoEjercicio(
        numero = 3,
        titulo = "Cambiá de dirección",
        detalle = "Cambiá de dirección y repetí.",
        imagenRes = R.drawable.rodar_pelota3_artritis
    )
)


private val pasosOposicionPulgar = listOf(
    PasoEjercicio(
        numero = 1,
        titulo = "Mano abierta",
        detalle = "Mantené la mano abierta con los dedos estirados.",
        imagenRes = R.drawable.oposicion_pulgar1_artritis
    ),
    PasoEjercicio(
        numero = 2,
        titulo = "Prepará el movimiento",
        detalle = "Llevá el pulgar despacio hacia el dedo índice.",
        imagenRes = R.drawable.oposicion_pulgar2_artritis
    ),
    PasoEjercicio(
        numero = 3,
        titulo = "Tocá cada dedo",
        detalle = "Tocá con el pulgar cada dedo, uno por uno, y volvé a empezar.",
        imagenRes = R.drawable.oposicion_pulgar3_artritis
    )
)


private val pasosMovilidadMunecas = listOf(
    PasoEjercicio(
        numero = 1,
        titulo = "Cerrá suavemente los puños",
        detalle = "Cerrá suavemente los puños, sin apretar.",
        imagenRes = R.drawable.movilidad_munecas1_artritis
    ),
    PasoEjercicio(
        numero = 2,
        titulo = "Bajá los puños",
        detalle = "Bajá los puños lentamente, sin forzar.",
        imagenRes = R.drawable.movilidad_munecas2_artritis
    ),
    PasoEjercicio(
        numero = 3,
        titulo = "Hacé círculos suaves",
        detalle = "Hacé círculos suaves con las muñecas en ambas direcciones.",
        imagenRes = R.drawable.movilidad_munecas3_artritis
    )
)

private val pasosPunoExtension = listOf(
    PasoEjercicio(
        numero = 1,
        titulo = "Cerrá los puños",
        detalle = "Cerrá los puños suavemente.",
        imagenRes = R.drawable.puno_extension1_artritis
    ),
    PasoEjercicio(
        numero = 2,
        titulo = "Bajá los puños",
        detalle = "Bajá los puños sin forzar.",
        imagenRes = R.drawable.puno_extension2_artritis
    ),
    PasoEjercicio(
        numero = 3,
        titulo = "Hacé círculos suaves",
        detalle = "Hacé círculos suaves con las muñecas.",
        imagenRes = R.drawable.puno_extension3_artritis
    )
)

private val pasosDeslizamientoDedos = listOf(
    PasoEjercicio(
        numero = 1,
        titulo = "Dedos juntos",
        detalle = "Apoyá la mano sobre una superficie con los dedos juntos y estirados.",
        imagenRes = R.drawable.deslizamiento_dedos1_artritis
    ),
    PasoEjercicio(
        numero = 2,
        titulo = "Separá los dedos",
        detalle = "Deslizá lentamente los dedos hacia los lados, separándolos.",
        imagenRes = R.drawable.deslizamiento_dedos2_artritis
    ),
    PasoEjercicio(
        numero = 3,
        titulo = "Volvé a juntarlos",
        detalle = "Volvé a juntar los dedos suavemente y repetí el movimiento.",
        imagenRes = R.drawable.deslizamiento_dedos3_artritis
    )
)


// =====================================================================
// IMÁGENES DE LOS EJERCICIOS PREMIUM
// Para agregarle imágenes a otro ejercicio Premium:
//   1. Poné la imagen en res/drawable (minúsculas, sin espacios ni tildes).
//   2. Sumá una línea acá:  "id_del_ejercicio" to listOf(R.drawable.nombre)
// Con 1 imagen se usa en todos los pasos. Con varias, la 1ª va en el paso 1,
// la 2ª en el paso 2, etc. (si hay menos imágenes que pasos, repite la última).
// Los id están en EjerciciosPremium.kt (ej. "separar_dedos", "arrugar_toalla").
// =====================================================================
private val imagenesPremium: Map<String, List<Int>> = mapOf(
    "separar_dedos" to listOf(
        R.drawable.dedos1,
        R.drawable.dedos2,
        R.drawable.dedos3,
        R.drawable.dedos4
    ),
    "arrugar_toalla" to listOf(
        R.drawable.toalla1,
        R.drawable.toalla2,
        R.drawable.toalla3,
        R.drawable.toalla4
    ),
    "estiramiento_munieca_mesa" to listOf(
        R.drawable.muneca1,
        R.drawable.muneca2,
        R.drawable.muneca3,
        R.drawable.muneca4
    ),
    "abrir_frasco" to listOf(
        R.drawable.frasco1,
        R.drawable.frasco2,
        R.drawable.frasco3,
        R.drawable.frasco4
    ),
    "circulos_tobillo" to listOf(
        R.drawable.tobillo1,
        R.drawable.tobillo2,
        R.drawable.tobillo3,
        R.drawable.tobillo4
    ),

    // ---------- Parkinson ----------
    "rodar_lapiz" to listOf(
        R.drawable.lapiz1,
        R.drawable.lapiz2,
        R.drawable.lapiz3,
        R.drawable.lapiz4
    ),
    "palma_dorso" to listOf(
        R.drawable.palma1,
        R.drawable.palma2,
        R.drawable.palma3,
        R.drawable.palma4
    ),
    "pararse_sentarse" to listOf(
        R.drawable.sentarse1,
        R.drawable.sentarse2,
        R.drawable.sentarse3,
        R.drawable.sentarse4
    ),
    "hombros_amplios" to listOf(
        R.drawable.hombros1,
        R.drawable.hombros2,
        R.drawable.hombros3,
        R.drawable.hombros4
    ),
    "espalda_derecha" to listOf(
        R.drawable.espalda1,
        R.drawable.espalda2,
        R.drawable.espalda3,
        R.drawable.espalda4
    )
)

private enum class FaseEjercicio { SOSTENIENDO, PAUSA }

// Todo lo que cambia según el ejercicio
private data class ContenidoEjercicio(
    val descripcion: String,
    val pasos: List<PasoEjercicio>,
    val tituloActivo: String,
    val subtituloActivo: String,
    val tituloPausa: String,
    val subtituloPausa: String,
    val iconoActivo: ImageVector,
    val etiquetaActiva: String,
    val beneficios: List<Pair<String, String>>,
    // Contenido del diálogo de información
    val infoParaQue: String,
    val infoComoHacerlo: String,
    val infoCuanto: String,
    val infoCuandoParar: String,
    val infoQueMide: String,
    // Solo ejercicios Premium
    val advertencia: String? = null
)

private fun contenidoPremium(e: EjercicioPremium, p: ParametrosEjercicio): ContenidoEjercicio {
    val imagenes = imagenesPremium[e.id].orEmpty()
    return ContenidoEjercicio(
        descripcion = e.descripcion,
        pasos = e.pasos.mapIndexed { i, texto ->
            PasoEjercicio(
                numero = i + 1,
                titulo = "",
                detalle = texto,
                imagenRes = imagenes.getOrNull(i) ?: imagenes.lastOrNull()
            )
        },
        tituloActivo = "Hacé el movimiento",
        subtituloActivo = "Seguí los pasos de arriba",
        tituloPausa = "Relajá la mano",
        subtituloPausa = "Pausa entre repeticiones",
        iconoActivo = Icons.Filled.PanTool,
        etiquetaActiva = "En movimiento",
        beneficios = listOf(
            "" to e.descripcion,
            "" to "Trabaja: ${e.zona} · ${e.tipo}",
            "" to (p.nota ?: "Versión normal: hacelo a tu ritmo y sin forzar.")
        ),
        infoParaQue = e.descripcion,
        infoComoHacerlo = e.pasos.mapIndexed { i, t -> "${i + 1}. $t" }.joinToString("\n"),
        infoCuanto = "Hoy: ${p.repeticiones} repeticiones, unos ${p.segundos} segundos en total " +
                "(${p.version}, nivel ${p.nivel} de 5). " + (p.nota ?: ""),
        infoCuandoParar = e.advertencia,
        infoQueMide = "El guante puede registrar el movimiento de la mano y los dedos durante el ejercicio. " +
                EjerciciosPremium.AVISO_APP,
        advertencia = e.advertencia
    )
}

private fun contenidoDe(nombreEjercicio: String): ContenidoEjercicio =
    when {
        nombreEjercicio.contains("toques de dedos", ignoreCase = true) ||
                nombreEjercicio.contains("toque de dedos", ignoreCase = true) -> {
            ContenidoEjercicio(
                descripcion = "Ejercicio para mejorar la coordinación y la movilidad de los dedos.",
                pasos = pasosToqueDedos,
                tituloActivo = "Tocá los dedos",
                subtituloActivo = "Pulgar con cada dedo",
                tituloPausa = "Relajá la mano",
                subtituloPausa = "Pausa entre repeticiones",
                iconoActivo = Icons.Filled.PanTool,
                etiquetaActiva = "En movimiento",
                beneficios = listOf(
                    "" to "Mejora la coordinación de los dedos",
                    "" to "Favorece la movilidad y la destreza de la mano",
                    "" to "Ayuda a practicar movimientos finos y controlados"
                ),
                infoParaQue = "Trabaja la coordinación entre el pulgar y los demás dedos mediante movimientos lentos y controlados.",
                infoComoHacerlo = "Mantené la mano abierta. Tocá primero la punta del índice con el pulgar y continuá con los demás dedos, uno por uno, hasta llegar al meñique. Luego volvé a empezar.",
                infoCuanto = "Realizá el movimiento lentamente y completá las repeticiones indicadas en la pantalla.",
                infoCuandoParar = "Frená si sentís dolor, calambres o una molestia importante durante el movimiento.",
                infoQueMide = "El guante puede registrar el movimiento de los dedos durante la realización del ejercicio."
            )
        }

        nombreEjercicio.contains("pronacion", ignoreCase = true) ||
                nombreEjercicio.contains("pronación", ignoreCase = true) ||
                nombreEjercicio.contains("supinacion", ignoreCase = true) ||
                nombreEjercicio.contains("supinación", ignoreCase = true) -> {
            ContenidoEjercicio(
                descripcion = "Ejercicio de pronación y supinación para trabajar la rotación controlada del antebrazo y la mano.",
                pasos = pasosPronacionSupinacion,
                tituloActivo = "Girá la mano",
                subtituloActivo = "Palma arriba y palma abajo",
                tituloPausa = "Relajá el brazo",
                subtituloPausa = "Pausa entre repeticiones",
                iconoActivo = Icons.Filled.Autorenew,
                etiquetaActiva = "En movimiento",
                beneficios = listOf(
                    "" to "Mejora la movilidad del antebrazo",
                    "" to "Favorece el control de la rotación de la mano",
                    "" to "Ayuda a mantener movimientos más fluidos y coordinados"
                ),
                infoParaQue = "Trabaja la pronación y la supinación, es decir, la capacidad de girar el antebrazo para colocar la palma hacia arriba y hacia abajo.",
                infoComoHacerlo = "Colocá el brazo al frente con la palma hacia arriba. Girá lentamente la mano hasta dejar la palma hacia abajo. Después volvé a girarla hacia arriba y repetí el movimiento.",
                infoCuanto = "Realizá el movimiento lentamente y completá las repeticiones indicadas en la pantalla.",
                infoCuandoParar = "Frená si sentís dolor, calambres o una molestia importante durante el movimiento.",
                infoQueMide = "El guante puede registrar el movimiento y la rotación de la mano durante el ejercicio."
            )
        }

        nombreEjercicio.contains("abrir y cerrar", ignoreCase = true) ||
                nombreEjercicio.contains("abrir cerrar", ignoreCase = true) -> {
            ContenidoEjercicio(
                descripcion = "Ejercicio para practicar la apertura y el cierre controlado de la mano.",
                pasos = pasosAbrirCerrarMano,
                tituloActivo = "Abrí y cerrá la mano",
                subtituloActivo = "Movimiento lento y controlado",
                tituloPausa = "Relajá la mano",
                subtituloPausa = "Pausa entre repeticiones",
                iconoActivo = Icons.Filled.PanTool,
                etiquetaActiva = "En movimiento",
                beneficios = listOf(
                    "" to "Mejora la movilidad de los dedos y la mano",
                    "" to "Favorece la apertura y el cierre controlado",
                    "" to "Ayuda a mantener la coordinación de la mano"
                ),
                infoParaQue = "Trabaja la apertura y el cierre de la mano mediante movimientos lentos y controlados.",
                infoComoHacerlo = "Mantené la mano abierta con los dedos bien estirados. Cerrala lentamente formando un puño y después volvé a abrirla completamente. Repetí el movimiento sin forzar.",
                infoCuanto = "Realizá el movimiento lentamente y completá las repeticiones indicadas en la pantalla.",
                infoCuandoParar = "Frená si sentís dolor, calambres o una molestia importante durante el movimiento.",
                infoQueMide = "El guante puede registrar el movimiento de los dedos durante la apertura y el cierre de la mano."
            )
        }

        nombreEjercicio.contains("golpeteo alternado", ignoreCase = true) ||
                nombreEjercicio.contains("golpeteo", ignoreCase = true) -> {
            ContenidoEjercicio(
                descripcion = "Ejercicio para trabajar la coordinación y el control de los dedos mediante golpeteos alternados.",
                pasos = pasosGolpeteoAlternado,
                tituloActivo = "Golpeá con los dedos",
                subtituloActivo = "Alterná un dedo a la vez",
                tituloPausa = "Relajá la mano",
                subtituloPausa = "Pausa entre repeticiones",
                iconoActivo = Icons.Filled.PanTool,
                etiquetaActiva = "En movimiento",
                beneficios = listOf(
                    "" to "Mejora la coordinación de los dedos",
                    "" to "Favorece movimientos rápidos y controlados",
                    "" to "Ayuda a mantener la destreza de la mano"
                ),
                infoParaQue = "Trabaja la coordinación y el control independiente de los dedos mediante movimientos repetidos y alternados.",
                infoComoHacerlo = "Apoyá la mano sobre una superficie con los dedos relajados. Levantá y bajá un dedo dando pequeños golpes y después alterná el movimiento entre los diferentes dedos de forma continua.",
                infoCuanto = "Realizá el movimiento lentamente al principio y completá las repeticiones indicadas en la pantalla.",
                infoCuandoParar = "Frená si sentís dolor, calambres o una molestia importante durante el movimiento.",
                infoQueMide = "El guante puede registrar el movimiento de los dedos durante el golpeteo alternado."
            )
        }

        nombreEjercicio.contains("deslizamiento de dedos", ignoreCase = true) ||
                nombreEjercicio.contains("deslizamiento dedos", ignoreCase = true) -> {
            ContenidoEjercicio(
                descripcion = "Ejercicio para trabajar suavemente la movilidad y separación de los dedos.",
                pasos = pasosDeslizamientoDedos,
                tituloActivo = "Deslizá los dedos",
                subtituloActivo = "Separá y juntá suavemente",
                tituloPausa = "Relajá la mano",
                subtituloPausa = "Pausa entre repeticiones",
                iconoActivo = Icons.Filled.PanTool,
                etiquetaActiva = "En movimiento",
                beneficios = listOf(
                    "" to "Favorece la movilidad de los dedos",
                    "" to "Ayuda a trabajar la separación y aproximación de los dedos",
                    "" to "Practica movimientos suaves y controlados de la mano"
                ),
                infoParaQue = "Este ejercicio trabaja la movilidad de los dedos mediante movimientos suaves de separación y aproximación.",
                infoComoHacerlo = "Apoyá la mano sobre una superficie con los dedos juntos y estirados. Deslizá lentamente los dedos hacia los lados para separarlos. Después volvé a juntarlos suavemente y repetí el movimiento.",
                infoCuanto = "Realizá el movimiento lentamente y completá las repeticiones indicadas en la pantalla.",
                infoCuandoParar = "Frená si aparece dolor o una molestia importante durante el movimiento.",
                infoQueMide = "El guante puede registrar el movimiento y la separación de los dedos durante el ejercicio."
            )
        }

        nombreEjercicio.contains("oposición del pulgar", ignoreCase = true) ||
                nombreEjercicio.contains("oposicion del pulgar", ignoreCase = true) -> {
            ContenidoEjercicio(
                descripcion = "Ejercicio para trabajar la movilidad y coordinación del pulgar con los demás dedos.",
                pasos = pasosOposicionPulgar,
                tituloActivo = "Mové el pulgar",
                subtituloActivo = "Tocá cada dedo uno por uno",
                tituloPausa = "Relajá la mano",
                subtituloPausa = "Pausa entre repeticiones",
                iconoActivo = Icons.Filled.PanTool,
                etiquetaActiva = "En movimiento",
                beneficios = listOf(
                    "" to "Favorece la movilidad del pulgar",
                    "" to "Ayuda a trabajar la coordinación de los dedos",
                    "" to "Practica movimientos finos y controlados de la mano"
                ),
                infoParaQue = "Trabaja la oposición del pulgar y la coordinación con los demás dedos mediante movimientos suaves y controlados.",
                infoComoHacerlo = "Mantené la mano abierta con los dedos estirados. Después tocá con el pulgar cada dedo, uno por uno, y volvé a empezar.",
                infoCuanto = "Realizá el movimiento lentamente y completá las repeticiones indicadas en la pantalla.",
                infoCuandoParar = "Frená si sentís dolor o una molestia importante durante el movimiento.",
                infoQueMide = "El guante puede registrar el movimiento del pulgar y de los dedos durante el ejercicio."
            )
        }

        nombreEjercicio.contains("rodar una pelota", ignoreCase = true) ||
                nombreEjercicio.contains("rodar pelota", ignoreCase = true) -> {
            ContenidoEjercicio(
                descripcion = "Ejercicio con pelota blanda para trabajar la movilidad y el control de la mano.",
                pasos = pasosRodarPelota,
                tituloActivo = "Rodá la pelota",
                subtituloActivo = "Movimiento suave en la palma",
                tituloPausa = "Relajá la mano",
                subtituloPausa = "Pausa entre repeticiones",
                iconoActivo = Icons.Filled.PanTool,
                etiquetaActiva = "En movimiento",
                beneficios = listOf(
                    "" to "Favorece la movilidad de la mano",
                    "" to "Ayuda a trabajar movimientos suaves y controlados",
                    "" to "Estimula la coordinación de los dedos y la palma"
                ),
                infoParaQue = "Este ejercicio utiliza una pelota blanda para trabajar movimientos suaves y controlados de la mano.",
                infoComoHacerlo = "Tomá la pelota suavemente. Rodala en la palma de la mano y después cambiá de dirección para repetir el movimiento.",
                infoCuanto = "Realizá el movimiento lentamente y completá las repeticiones indicadas en la pantalla.",
                infoCuandoParar = "Frená si sentís dolor o una molestia importante durante el movimiento.",
                infoQueMide = "El guante puede registrar el movimiento de la mano y los dedos durante el ejercicio."
            )
        }

        nombreEjercicio.contains("puño y extensión", ignoreCase = true) ||
                nombreEjercicio.contains("puno y extension", ignoreCase = true) ||
                nombreEjercicio.contains("puño y extension", ignoreCase = true) ||
                nombreEjercicio.contains("puno y extensión", ignoreCase = true) -> {
            ContenidoEjercicio(
                descripcion = "Ejercicio para trabajar suavemente la movilidad de las manos y las muñecas.",
                pasos = pasosPunoExtension,
                tituloActivo = "Mové los puños",
                subtituloActivo = "Despacio y sin forzar",
                tituloPausa = "Relajá las manos",
                subtituloPausa = "Pausa entre repeticiones",
                iconoActivo = Icons.Filled.Autorenew,
                etiquetaActiva = "En movimiento",
                beneficios = listOf(
                    "" to "Favorece la movilidad de las manos y muñecas",
                    "" to "Ayuda a reducir la rigidez articular",
                    "" to "Practica movimientos suaves y controlados"
                ),
                infoParaQue = "Este ejercicio trabaja movimientos suaves de las manos y las muñecas.",
                infoComoHacerlo = "Cerrá los puños suavemente. Bajalos sin forzar y después realizá círculos suaves con las muñecas.",
                infoCuanto = "Realizá el movimiento lentamente y completá las repeticiones indicadas en la pantalla.",
                infoCuandoParar = "Frená si aparece dolor o una molestia importante durante el movimiento.",
                infoQueMide = "El guante puede registrar el movimiento de la mano y la muñeca durante el ejercicio."
            )
        }

        nombreEjercicio.contains("muñeca", ignoreCase = true) -> {
            ContenidoEjercicio(
                descripcion = "Mover lentamente la muñeca hacia arriba, abajo y en círculos, sin generar dolor. Hacer movimientos suaves y descansar si aparece molestia.",
                pasos = pasosMovilidadMunecas,
                tituloActivo = "Movés la muñeca",
                subtituloActivo = "Despacio y sin forzar",
                tituloPausa = "Relajá la muñeca",
                subtituloPausa = "Pausa entre repeticiones",
                iconoActivo = Icons.Filled.Autorenew,
                etiquetaActiva = "En movimiento",
                beneficios = listOf(
                    "🔄" to "Mejora el rango de movimiento de la muñeca",
                    "🦴" to "Reduce la rigidez y la inflamación articular",
                    "❤️" to "Alivia el dolor en las actividades diarias"
                ),
                infoParaQue = "Mantiene la articulación de la muñeca móvil y lubricada. En artritis, el movimiento suave y regular ayuda a que la rigidez matinal dure menos y a conservar la fuerza para tareas cotidianas.",
                infoComoHacerlo = "Sentate con la espalda apoyada y el antebrazo sobre una mesa, dejando la mano por fuera del borde. Movés solo la muñeca, no el brazo. Respirá normal y hacelo lento: el movimiento tiene que salir sin esfuerzo.",
                infoCuanto = "De 5 a 10 repeticiones, una o dos veces por día. Es mejor poco y todos los días que mucho de golpe.",
                infoCuandoParar = "Frená si aparece dolor, si la articulación se hincha o se pone caliente, o si la molestia sigue más de dos horas después. En un brote de artritis, consultá antes de continuar.",
                infoQueMide = "El guante registra el ángulo de flexión de la muñeca y la amplitud de cada círculo, para que veas si tu rango de movimiento mejora con las semanas."
            )
        }

        else -> {
            ContenidoEjercicio(
                descripcion = "Ejercicio para mejorar la movilidad y reducir la rigidez en las manos.",
                pasos = pasosExtensionDedos,
                tituloActivo = "Extendé los dedos",
                subtituloActivo = "Mantené la posición",
                tituloPausa = "Relajá la mano",
                subtituloPausa = "Pausa entre repeticiones",
                iconoActivo = Icons.Filled.PanTool,
                etiquetaActiva = "Sosteniendo",
                beneficios = listOf(
                    "🖐️" to "Mejora la movilidad de los dedos",
                    "🦴" to "Reduce la rigidez y la inflamación",
                    "❤️" to "Alivia el dolor y mejora la función de la mano"
                ),
                infoParaQue = "Trabaja la apertura completa de la mano, que en Parkinson tiende a perderse por la rigidez. Ayuda a mantener la destreza para abrocharse la ropa, escribir o agarrar objetos chicos.",
                infoComoHacerlo = "Apoyá el antebrazo en una mesa con la palma hacia abajo. Abrí los dedos de forma lenta y pareja, estirándolos todo lo que puedas sin tensionar. La lentitud es parte del ejercicio: hacerlo rápido no suma.",
                infoCuanto = "De 5 a 10 repeticiones, una o dos veces por día. Si podés, hacelo en el momento del día en que la medicación está haciendo mejor efecto.",
                infoCuandoParar = "Frená si sentís dolor, calambres o si la mano queda temblando más que antes de empezar. El cansancio leve es normal; el dolor no.",
                infoQueMide = "El guante mide cuánto llegás a abrir los dedos en cada repetición, así podés ver si la apertura mejora con el tiempo."
            )
        }
    }

@Composable
fun ExerciseInProgressScreen(
    nombreEjercicio: String,
    zona: String,
    tipo: String,
    onBack: () -> Unit,
    onInfoClick: () -> Unit,
    // repeticiones hechas, segundos reales transcurridos, repeticiones objetivo
    onFinish: (Int, Int, Int) -> Unit
) {
    val context = LocalContext.current
    val reducirAnimaciones = Accesibilidad.reducirAnimaciones

    // Ejercicio Premium (si corresponde) y parámetros de hoy: on/off + nivel adaptativo
    val premium = remember(nombreEjercicio) { EjerciciosPremium.porNombre(nombreEjercicio) }
    val parametros = remember(nombreEjercicio) {
        premium?.let { EjerciciosPremium.parametros(context, it) }
    }

    val contenido = remember(nombreEjercicio) {
        if (premium != null && parametros != null) contenidoPremium(premium, parametros)
        else contenidoDe(nombreEjercicio)
    }

    val segundosPausa = 3
    val totalRepeticiones = parametros?.repeticiones?.coerceAtLeast(1) ?: 5
    // En Premium "segundos" es la duración total: de ahí sale lo que dura cada repetición.
    val segundosSostener = parametros
        ?.let { (it.segundos / it.repeticiones.coerceAtLeast(1) - segundosPausa).coerceAtLeast(2) }
        ?: 5
    val zonaMostrada = premium?.zona ?: zona
    val tipoMostrado = premium?.tipo ?: tipo

    var repeticionActual by remember { mutableIntStateOf(1) }
    var fase by remember { mutableStateOf(FaseEjercicio.SOSTENIENDO) }
    var segundosRestantes by remember { mutableIntStateOf(segundosSostener) }
    var pausadoPorUsuario by rememberSaveable { mutableStateOf(false) }
    var terminado by rememberSaveable { mutableStateOf(false) }
    var mostrarInfo by rememberSaveable { mutableStateOf(false) }
    var segundosTranscurridos by remember { mutableIntStateOf(0) }

    // Diagnósticos del usuario, para mostrar el cartel que le corresponde
    var diagnosticos by remember { mutableStateOf(UserData.diagnosticos) }
    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        FirebaseFirestore.getInstance().collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                val lista = (doc.get("diagnosticos") as? List<*>)?.filterIsInstance<String>()
                if (!lista.isNullOrEmpty()) diagnosticos = lista
            }
    }

    var pasoAnimadoIndex by remember { mutableIntStateOf(0) }

    // ---------- Lectura en voz alta (voz del celular, sin internet) ----------
    var vozActiva by rememberSaveable { mutableStateOf(false) }
    var vozLista by rememberSaveable { mutableStateOf(false) }
    var finLectura by remember { mutableIntStateOf(0) }
    var motorVoz by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        val handler = Handler(Looper.getMainLooper())
        lateinit var motor: TextToSpeech
        motor = TextToSpeech(context.applicationContext) { estado ->
            if (estado == TextToSpeech.SUCCESS) {
                val r = motor.setLanguage(Locale("es", "AR"))
                if (r == TextToSpeech.LANG_MISSING_DATA || r == TextToSpeech.LANG_NOT_SUPPORTED) {
                    motor.setLanguage(Locale("es"))
                }
                motor.setSpeechRate(0.85f) // un poco más lento, más fácil de seguir
                motor.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onDone(utteranceId: String?) {
                        if (utteranceId?.startsWith("paso") == true) handler.post { finLectura++ }
                    }
                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {}
                })
                handler.post { vozLista = true }
            }
        }
        motorVoz = motor
        onDispose {
            motor.stop()
            motor.shutdown()
        }
    }

    // Lee el paso que se está mostrando
    LaunchedEffect(pasoAnimadoIndex, vozActiva, vozLista, pausadoPorUsuario) {
        val motor = motorVoz ?: return@LaunchedEffect
        if (vozActiva && vozLista && !pausadoPorUsuario) {
            val paso = contenido.pasos[pasoAnimadoIndex.coerceIn(0, contenido.pasos.size - 1)]
            motor.speak(
                "Paso ${paso.numero} de ${contenido.pasos.size}. ${paso.detalle}",
                TextToSpeech.QUEUE_FLUSH, null, "paso${paso.numero}"
            )
        } else {
            motor.stop()
        }
    }

    // Cuando termina de leer un paso, espera un momento y pasa al siguiente
    LaunchedEffect(finLectura) {
        if (finLectura > 0 && vozActiva && !pausadoPorUsuario && !reducirAnimaciones) {
            delay(3000)
            pasoAnimadoIndex = (pasoAnimadoIndex + 1) % contenido.pasos.size
        }
    }

    // Con "Reducir animaciones" los pasos no rotan solos: se cambian con los botones.
    // Con la voz activada, avanza la voz (cuando termina de leer cada paso).
    LaunchedEffect(reducirAnimaciones, vozActiva) {
        if (!reducirAnimaciones && !vozActiva) {
            while (true) {
                delay(if (premium != null) 4000 else 1500)
                pasoAnimadoIndex = (pasoAnimadoIndex + 1) % contenido.pasos.size
            }
        }
    }

    LaunchedEffect(pausadoPorUsuario, terminado) {
        while (!pausadoPorUsuario && !terminado) {
            delay(1000)
            segundosTranscurridos += 1
            if (segundosRestantes > 1) {
                segundosRestantes -= 1
            } else {
                when (fase) {
                    FaseEjercicio.SOSTENIENDO -> {
                        if (repeticionActual >= totalRepeticiones) {
                            // Última repetición: no hace falta pausa final
                            terminado = true
                        } else {
                            fase = FaseEjercicio.PAUSA
                            segundosRestantes = segundosPausa
                        }
                    }
                    FaseEjercicio.PAUSA -> {
                        if (repeticionActual < totalRepeticiones) {
                            repeticionActual += 1
                            fase = FaseEjercicio.SOSTENIENDO
                            segundosRestantes = segundosSostener
                        } else {
                            terminado = true
                        }
                    }
                }
            }
        }
    }

    // Al completar todas las repeticiones se cierra solo (antes quedaba esperando)
    LaunchedEffect(terminado) {
        if (terminado) onFinish(repeticionActual, segundosTranscurridos, totalRepeticiones)
    }

    // ---------- Diálogo de información ----------
    if (mostrarInfo) {
        AlertDialog(
            onDismissRequest = { mostrarInfo = false },
            containerColor = Color.White.enTema(),
            shape = RoundedCornerShape(20.dp),
            title = {
                Column {
                    Text(
                        text = "Sobre este ejercicio",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280).enTema()
                    )
                    Text(
                        text = nombreEjercicio,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = Verde
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    BloqueInfo("Para qué sirve", contenido.infoParaQue)
                    BloqueInfo("Cómo hacerlo bien", contenido.infoComoHacerlo)
                    BloqueInfo("Cuánto y cuándo", contenido.infoCuanto)

                    Spacer(Modifier.height(6.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(RojoAvisoClaro, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Warning,
                                contentDescription = null,
                                tint = RojoAviso,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Cuándo parar",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = RojoAviso
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = contenido.infoCuandoParar,
                            fontSize = 13.sp,
                            color = Color(0xFF374151).enTema(),
                            lineHeight = 19.sp
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(VerdeClaro, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Bluetooth,
                                contentDescription = null,
                                tint = Verde,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Qué mide el guante",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Verde
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = contenido.infoQueMide,
                            fontSize = 13.sp,
                            color = Color(0xFF374151).enTema(),
                            lineHeight = 19.sp
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "Esta información es orientativa y no reemplaza la indicación de tu profesional de salud.",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280).enTema(),
                        lineHeight = 17.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { mostrarInfo = false },
                    modifier = Modifier.heightIn(min = 56.dp)
                ) {
                    Text("Entendido", color = Verde, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        )
    }

    val duracionFaseActual = if (fase == FaseEjercicio.SOSTENIENDO) segundosSostener else segundosPausa
    val progresoMovimiento = ((duracionFaseActual - segundosRestantes).toFloat() / duracionFaseActual.toFloat())
        .coerceIn(0f, 1f)

    val colorFase = if (fase == FaseEjercicio.SOSTENIENDO) Verde else AmbarPausa
    val colorFaseClaro = if (fase == FaseEjercicio.SOSTENIENDO) VerdeClaro else AmbarPausaClaro
    val tituloFase = if (fase == FaseEjercicio.SOSTENIENDO) contenido.tituloActivo else contenido.tituloPausa
    val subtituloFase = if (fase == FaseEjercicio.SOSTENIENDO) contenido.subtituloActivo else contenido.subtituloPausa
    val iconoFase = if (fase == FaseEjercicio.SOSTENIENDO) contenido.iconoActivo else Icons.Filled.Pause

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(56.dp)) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Verde)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = nombreEjercicio, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Verde)
                Text(text = "$zonaMostrada · $tipoMostrado", fontSize = 14.sp, color = Color(0xFF6B7280).enTema())
            }
            IconButton(
                onClick = {
                    mostrarInfo = true
                    onInfoClick()
                },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Filled.Info, contentDescription = "Información del ejercicio", tint = Verde)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = contenido.descripcion,
            fontSize = 13.sp,
            color = Color(0xFF6B7280).enTema(),
            lineHeight = 18.sp
        )

        // Cartel según el diagnóstico (Parkinson, artritis o ambas)
        CartelDiagnostico(diagnosticos)

        if (premium != null && parametros != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VerdeClaro, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "${parametros.version} · Nivel ${parametros.nivel} de 5",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Verde
                )
                Text(
                    text = "${parametros.repeticiones} repeticiones · unos ${parametros.segundos} segundos en total",
                    fontSize = 13.sp,
                    color = Color(0xFF374151).enTema()
                )
                if (parametros.nota != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = parametros.nota, fontSize = 13.sp, color = Color(0xFF374151).enTema(), lineHeight = 18.sp)
                }
            }
            if (contenido.advertencia != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RojoAvisoClaro, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = RojoAviso, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = contenido.advertencia, fontSize = 13.sp, color = Color(0xFF374151).enTema(), lineHeight = 18.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BordeGris, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.PlayCircle, contentDescription = null, tint = Verde, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Cómo se hace",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A).enTema(),
                    modifier = Modifier.weight(1f)
                )
                // Botón 🔊: lee los pasos en voz alta
                Button(
                    onClick = {
                        if (!vozLista) {
                            Avisos.mostrar("Tu celular no tiene la voz instalada. Revisá Ajustes → Texto a voz.", largo = true)
                        } else {
                            vozActiva = !vozActiva
                        }
                    },
                    modifier = Modifier.heightIn(min = 48.dp),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (vozActiva) Verde else VerdeClaro,
                        contentColor = if (vozActiva) Color.White else Verde
                    )
                ) {
                    Icon(
                        imageVector = if (vozActiva) Icons.Filled.VolumeOff else Icons.Filled.VolumeUp,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (vozActiva) "Silenciar" else "Escuchar",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedContent(
                targetState = pasoAnimadoIndex,
                transitionSpec = {
                    if (reducirAnimaciones) EnterTransition.None togetherWith ExitTransition.None
                    else fadeIn() togetherWith fadeOut()
                },
                label = "pasoAnimado"
            ) { index ->
                val paso = contenido.pasos[index.coerceIn(0, contenido.pasos.size - 1)]
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(VerdeClaro),
                        contentAlignment = Alignment.Center
                    ) {
                        if (paso.imagenRes != null) {
                            Image(
                                painter = painterResource(id = paso.imagenRes),
                                contentDescription = paso.titulo,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            if (paso.emoji.isNotBlank()) {
                                Text(text = paso.emoji, fontSize = 56.sp)
                            } else {
                                Text(
                                    text = "${paso.numero}",
                                    fontSize = 72.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Verde
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Paso ${paso.numero} de ${contenido.pasos.size}" +
                                if (paso.titulo.isNotBlank()) " · ${paso.titulo}" else "",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A1A).enTema(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = paso.detalle,
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280).enTema(),
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                contenido.pasos.indices.forEach { i ->
                    Box(
                        modifier = Modifier
                            .size(if (i == pasoAnimadoIndex) 8.dp else 6.dp)
                            .background(
                                if (i == pasoAnimadoIndex) Verde else BordeGris,
                                CircleShape
                            )
                    )
                }
            }

            // Botones grandes para cambiar de paso (sin gestos finos)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        pasoAnimadoIndex =
                            (pasoAnimadoIndex - 1 + contenido.pasos.size) % contenido.pasos.size
                    },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 56.dp),
                    shape = RoundedCornerShape(14.dp)
                ) { Text("Paso anterior", color = Verde, fontWeight = FontWeight.SemiBold) }
                OutlinedButton(
                    onClick = {
                        pasoAnimadoIndex = (pasoAnimadoIndex + 1) % contenido.pasos.size
                    },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 56.dp),
                    shape = RoundedCornerShape(14.dp)
                ) { Text("Paso siguiente", color = Verde, fontWeight = FontWeight.SemiBold) }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerdeClaro, RoundedCornerShape(14.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Autorenew, contentDescription = null, tint = Verde, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = if (premium != null) "Hacé $totalRepeticiones repeticiones" else "Repetí de 5 a 10 veces", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A).enTema())
                Text(text = "Cada repetición incluye una pausa de descanso.", fontSize = 12.sp, color = Color(0xFF6B7280).enTema())
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        androidx.compose.material3.HorizontalDivider(color = Color(0xFFECECEC).enTema())

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Repetición",
            fontSize = 15.sp,
            color = Color(0xFF374151).enTema(),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { repeticionActual.toFloat() / totalRepeticiones.toFloat() },
                modifier = Modifier.fillMaxSize(),
                color = Verde,
                trackColor = VerdeClaro,
                strokeWidth = 10.dp,
                strokeCap = StrokeCap.Round
            )
            Text(
                text = "$repeticionActual/$totalRepeticiones",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A).enTema()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .background(colorFaseClaro, RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (fase == FaseEjercicio.SOSTENIENDO) contenido.etiquetaActiva else "En pausa",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorFase
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.AccessTime, contentDescription = null, tint = colorFase, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "$segundosRestantes", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A).enTema())
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "segundos", fontSize = 15.sp, color = Color(0xFF6B7280).enTema())
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---------- Estado actual: tarjeta principal ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(colorFaseClaro)
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(colorFase, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        iconoFase,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (fase == FaseEjercicio.SOSTENIENDO) "Movimiento actual" else "Momento de descansar",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorFase
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tituloFase,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A).enTema()
                    )
                    Text(
                        text = subtituloFase,
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280).enTema()
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.75f)
                ) {
                    Text(
                        text = "${segundosRestantes}s",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorFase
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ---------- Progreso ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BordeGris, RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Progreso del ejercicio",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A).enTema()
                    )
                    Text(
                        text = "Repetición $repeticionActual de $totalRepeticiones",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280).enTema()
                    )
                }

                Text(
                    text = "${(progresoMovimiento * 100).toInt()}%",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorFase
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = { progresoMovimiento },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = colorFase,
                trackColor = Color(0xFFF0F1F2).enTema()
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ---------- Acciones ----------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { pausadoPorUsuario = !pausadoPorUsuario },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Verde)
            ) {
                Icon(
                    imageVector = if (pausadoPorUsuario) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                    contentDescription = null,
                    tint = Verde,
                    modifier = Modifier.size(19.dp)
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = if (pausadoPorUsuario) "Reanudar" else "Pausar",
                    color = Verde,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    onFinish(repeticionActual, segundosTranscurridos, totalRepeticiones)
                },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Verde)
            ) {
                Icon(
                    Icons.Filled.Stop,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(19.dp)
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = "Finalizar",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // ---------- Beneficios ----------
        Text(
            text = "Beneficios del ejercicio",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A).enTema()
        )
        Text(
            text = "Lo que trabajás con este movimiento",
            fontSize = 13.sp,
            color = Color(0xFF6B7280).enTema()
        )

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BordeGris, RoundedCornerShape(22.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            contenido.beneficios.forEachIndexed { index, (_, texto) ->
                Text(
                    text = texto,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp, horizontal = 4.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF374151).enTema(),
                    lineHeight = 19.sp
                )

                if (index < contenido.beneficios.lastIndex) {
                    HorizontalDivider(
                        color = Color(0xFFF0F1F2).enTema()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---------- Consejo ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(VerdeClaro)
                .padding(18.dp)
        ) {
            Text(
                text = "CONSEJO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Verde,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Realizá cada movimiento lentamente y sin forzar. Si sentís dolor, detenete y consultá con tu profesional de salud.",
                fontSize = 13.sp,
                color = Color(0xFF374151).enTema(),
                lineHeight = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun BloqueInfo(titulo: String, texto: String) {
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Text(
            text = titulo,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A).enTema()
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = texto,
            fontSize = 13.sp,
            color = Color(0xFF6B7280).enTema(),
            lineHeight = 19.sp
        )
    }
}

@Composable
private fun BeneficioFila(emoji: String, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(VerdeClaro, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = texto, fontSize = 14.sp, color = Color(0xFF374151).enTema())
    }
}

private fun buildAnnotatedStringConsejo(): androidx.compose.ui.text.AnnotatedString {
    return androidx.compose.ui.text.buildAnnotatedString {
        withStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1B7A4B))) {
            append("Consejo: ")
        }
        append("Realizá los ejercicios lentamente y sin forzar el movimiento. Si sentís dolor, detenete y consultá con tu profesional de salud.")
    }
}

// =====================================================================
// CARTEL SEGÚN EL DIAGNÓSTICO
// =====================================================================
private data class TextoCartel(val titulo: String, val texto: String, val importante: String)

private const val NO_REEMPLAZA =
    "Estos ejercicios son complementarios y no reemplazan el tratamiento médico " +
            "ni la rehabilitación indicada por un profesional."

private val cartelParkinson = TextoCartel(
    titulo = "Ejercicios para el Parkinson",
    texto = "El Parkinson puede afectar la movilidad, el equilibrio y la coordinación de " +
            "diferentes partes del cuerpo, no solo las manos. Por eso, en Pulso Firme incluimos " +
            "ejercicios para manos, brazos, piernas y pies, que buscan mejorar la movilidad, " +
            "reducir la rigidez y favorecer la coordinación de los movimientos.",
    importante = "Realizá los ejercicios de forma lenta, segura y sin forzar el cuerpo. Si sentís " +
            "dolor, mareos o pérdida de equilibrio, detené la actividad y consultá con un " +
            "profesional de salud."
)

private val cartelArtritis = TextoCartel(
    titulo = "La artritis no solo afecta las manos",
    texto = "La artritis también puede afectar otras partes del cuerpo, como las muñecas, " +
            "rodillas, tobillos y pies. Por eso, en Pulso Firme incluimos ejercicios que " +
            "ayudan a mantener la movilidad, reducir la rigidez y mejorar la flexibilidad " +
            "de distintas articulaciones.",
    importante = "Realizá los movimientos suavemente y sin forzar. Si sentís dolor o " +
            "inflamación, suspendé el ejercicio y consultá con un profesional de salud."
)

private val cartelAmbas = TextoCartel(
    titulo = "Ejercicios para Parkinson y artritis",
    texto = "El Parkinson y la artritis pueden afectar la movilidad de diferentes partes del " +
            "cuerpo, no solo las manos. Por eso, en Pulso Firme incluimos ejercicios para manos, " +
            "muñecas, brazos, piernas y pies, que buscan mejorar la movilidad, la coordinación y " +
            "la flexibilidad, además de ayudar a reducir la rigidez.",
    importante = "Realizá los ejercicios de manera suave, respetando tus límites y sin forzar el " +
            "cuerpo. Si sentís dolor, mareos o molestias, detené la actividad y consultá con un " +
            "profesional de salud."
)

@Composable
private fun CartelDiagnostico(diagnosticos: List<String>) {
    val parkinson = diagnosticos.any { it.contains("parkinson", ignoreCase = true) }
    val artritis = diagnosticos.any { it.contains("artritis", ignoreCase = true) }
    val cartel = when {
        parkinson && artritis -> cartelAmbas
        parkinson -> cartelParkinson
        artritis -> cartelArtritis
        else -> return // sin diagnóstico cargado: no se muestra nada
    }

    Spacer(modifier = Modifier.height(14.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(VerdeClaro)
            .border(1.dp, Color(0xFFCDE5D6).enTema(), RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.enTema(), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.AccessibilityNew,
                    contentDescription = null,
                    tint = Verde,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = cartel.titulo,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A).enTema(),
                lineHeight = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = cartel.texto,
            fontSize = 14.sp,
            color = Color(0xFF374151).enTema(),
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFFF7E0).enTema())
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Filled.Warning,
                contentDescription = null,
                tint = Color(0xFF9A6B00).enTema(),
                modifier = Modifier
                    .padding(top = 1.dp)
                    .size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Importante: ") }
                    append(cartel.importante)
                },
                fontSize = 13.sp,
                color = Color(0xFF5C4400).enTema(),
                lineHeight = 19.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = NO_REEMPLAZA,
            fontSize = 12.sp,
            color = Color(0xFF6B7280).enTema(),
            lineHeight = 17.sp
        )
    }
}