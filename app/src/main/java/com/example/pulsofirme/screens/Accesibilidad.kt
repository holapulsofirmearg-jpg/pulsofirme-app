package com.example.pulsofirme

import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.geometry.Rect
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Preferencias de accesibilidad de PulsoFirme.
 * Se guardan en el celular y se aplican en toda la app al instante.
 * Por defecto: texto grande y botones grandes (pensado para temblor / artritis).
 */
object Accesibilidad {

    private const val PREFS = "pulsofirme_accesibilidad"
    private const val KEY_TAMANO = "tamano_letra_v2"
    private const val KEY_BOTONES = "botones_grandes"
    private const val KEY_ANIMACIONES = "reducir_animaciones"
    private const val KEY_VIBRACION = "sonidos_vibracion"
    private const val KEY_CONTRASTE = "alto_contraste"

    const val NORMAL = "Normal"
    const val GRANDE = "Grande"
    const val MUY_GRANDE = "Muy grande"

    val niveles = listOf(NORMAL, GRANDE, MUY_GRANDE)

    var tamanoLetra by mutableStateOf(GRANDE)
        private set

    var botonesGrandes by mutableStateOf(true)
        private set

    var reducirAnimaciones by mutableStateOf(false)
        private set

    var sonidosYVibracion by mutableStateOf(true)
        private set

    var altoContraste by mutableStateOf(false)
        private set

    private var iniciado = false

    /** Llamar una vez al arrancar (MainActivity.onCreate). */
    fun init(context: Context) {
        if (iniciado) return
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val guardado = prefs.getString(KEY_TAMANO, GRANDE) ?: GRANDE
        tamanoLetra = if (guardado in niveles) guardado else GRANDE
        botonesGrandes = prefs.getBoolean(KEY_BOTONES, true)
        reducirAnimaciones = prefs.getBoolean(KEY_ANIMACIONES, false)
        sonidosYVibracion = prefs.getBoolean(KEY_VIBRACION, true)
        altoContraste = prefs.getBoolean(KEY_CONTRASTE, false)
        iniciado = true
    }

    private fun guardarBool(context: Context, clave: String, valor: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(clave, valor).apply()
    }

    fun cambiarTamanoLetra(context: Context, nivel: String) {
        if (nivel !in niveles) return
        tamanoLetra = nivel
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_TAMANO, nivel).apply()
    }

    fun cambiarBotonesGrandes(context: Context, activado: Boolean) {
        botonesGrandes = activado
        guardarBool(context, KEY_BOTONES, activado)
    }

    fun cambiarReducirAnimaciones(context: Context, activado: Boolean) {
        reducirAnimaciones = activado
        guardarBool(context, KEY_ANIMACIONES, activado)
    }

    fun cambiarSonidosYVibracion(context: Context, activado: Boolean) {
        sonidosYVibracion = activado
        guardarBool(context, KEY_VIBRACION, activado)
    }

    fun cambiarAltoContraste(context: Context, activado: Boolean) {
        altoContraste = activado
        guardarBool(context, KEY_CONTRASTE, activado)
    }

    fun multiplicador(): Float = when (tamanoLetra) {
        NORMAL -> 1.00f
        MUY_GRANDE -> 1.50f
        else -> 1.25f
    }

    /** Alto mínimo de cualquier cosa tocable. */
    val altoMinimo: Dp
        get() = if (botonesGrandes) 56.dp else 48.dp
}

/**
 * Para filas o cajas con .clickable / .selectable / .toggleable:
 * nunca quedan más chicas que el mínimo táctil.
 * Ejemplo: Modifier.fillMaxWidth().areaTactil().clickable { ... }
 */
fun Modifier.areaTactil(): Modifier = this.heightIn(min = Accesibilidad.altoMinimo)

/** Como animateContentSize(), pero se apaga si el usuario eligió "Reducir animaciones". */
@Composable
fun Modifier.animarTamano(): Modifier =
    if (Accesibilidad.reducirAnimaciones) this else this.animateContentSize()

/**
 * Alto contraste: oscurece textos y colores medios y limpia los fondos claros
 * en TODA la app (también las pantallas con colores fijos).
 * Matriz: salida = FACTOR * (entrada - PIVOTE) + PIVOTE, en escala 0..255.
 * Se aplica una sola vez en MainActivity, sobre el contenido completo.
 */
private const val FACTOR_CONTRASTE = 1.35f
private const val PIVOTE_CONTRASTE = 170f

private val matrizAltoContraste: ColorMatrix by lazy {
    val t = PIVOTE_CONTRASTE * (1f - FACTOR_CONTRASTE)
    ColorMatrix(
        floatArrayOf(
            FACTOR_CONTRASTE, 0f, 0f, 0f, t,
            0f, FACTOR_CONTRASTE, 0f, 0f, t,
            0f, 0f, FACTOR_CONTRASTE, 0f, t,
            0f, 0f, 0f, 1f, 0f
        )
    )
}

fun Modifier.filtroAltoContraste(): Modifier =
    if (!Accesibilidad.altoContraste) this
    else this.drawWithContent {
        val pintura = Paint().apply { colorFilter = ColorFilter.colorMatrix(matrizAltoContraste) }
        drawIntoCanvas { canvas ->
            canvas.saveLayer(Rect(0f, 0f, size.width, size.height), pintura)
            drawContent()
            canvas.restore()
        }
    }