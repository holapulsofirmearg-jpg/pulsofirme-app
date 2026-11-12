package com.example.pulsofirme

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

/** Cómo se sintió el paciente (viene de "Cómo te sentís hoy" en el Inicio). */
enum class Sensacion(val valor: Int, val texto: String) {
    BIEN(1, "bien"),
    REGULAR(0, "regular"),
    CON_MOLESTIAS(-1, "con molestias")
}

/** Estado de la medicación (Parkinson): "on" = hace efecto, "off" = se está pasando. */
enum class EstadoMedicacion(val texto: String) {
    ON("On"),
    OFF("Off")
}

data class ResultadoDificultad(
    val nivel: Int,                                   // 1..5
    val etiqueta: String,                             // "Suave", "Normal", etc.
    val factor: Float,                                // multiplicador de repeticiones / duración
    val motivo: String,                               // explicación para el paciente
    val estadoMedicacion: EstadoMedicacion? = null    // null = no respondió hoy
) {
    val enOff: Boolean get() = estadoMedicacion == EstadoMedicacion.OFF
    val variante: String get() = if (enOff) "Versión suave (off)" else "Versión normal (on)"
}

/**
 * Dificultad adaptativa (Premium): sube o baja según el último QuickDASH
 * y cómo se sintió el paciente. Además guarda si hoy está "on" u "off".
 */
object DificultadAdaptativa {

    private const val PREFS = "pulsofirme_adaptativa"
    private const val KEY_SENSACIONES = "sensaciones"          // "2026-10-13:1,2026-10-12:0"
    private const val KEY_MED_ESTADO = "medicacion_estado"
    private const val KEY_MED_FECHA = "medicacion_fecha"
    private const val MAX_DIAS = 3

    private val etiquetas = mapOf(
        1 to "Muy suave",
        2 to "Suave",
        3 to "Normal",
        4 to "Desafiante",
        5 to "Avanzado"
    )

    private val factores = mapOf(
        1 to 0.6f,
        2 to 0.8f,
        3 to 1.0f,
        4 to 1.2f,
        5 to 1.4f
    )

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun hoy(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    // ---------------- SENSACIONES ----------------

    private fun leerSensaciones(context: Context): List<Pair<String, Int>> {
        val csv = prefs(context).getString(KEY_SENSACIONES, "") ?: ""
        return csv.split(",").mapNotNull { item ->
            val partes = item.split(":")
            if (partes.size == 2) partes[1].toIntOrNull()?.let { partes[0] to it } else null
        }
    }

    /** Guarda cómo se sintió hoy (si toca varias veces el mismo día, queda la última). */
    fun registrarSensacion(context: Context, sensacion: Sensacion) {
        val mapa = leerSensaciones(context).toMap().toMutableMap()
        mapa[hoy()] = sensacion.valor
        val ultimos = mapa.toList().sortedBy { it.first }.takeLast(MAX_DIAS)
        prefs(context).edit()
            .putString(KEY_SENSACIONES, ultimos.joinToString(",") { "${it.first}:${it.second}" })
            .apply()
    }

    /** Traduce el texto de las opciones del Inicio a una sensación. */
    fun sensacionDesdeTexto(texto: String): Sensacion = when {
        texto.contains("baja", ignoreCase = true) -> Sensacion.BIEN
        texto.contains("dolor", ignoreCase = true) -> Sensacion.CON_MOLESTIAS
        else -> Sensacion.REGULAR
    }

    fun registrarSensacionDesdeTexto(context: Context, texto: String) =
        registrarSensacion(context, sensacionDesdeTexto(texto))

    // ---------------- ON / OFF ----------------

    /** El estado vale solo para el día en que se respondió. */
    fun estadoMedicacionHoy(context: Context): EstadoMedicacion? {
        val p = prefs(context)
        if (p.getString(KEY_MED_FECHA, "") != hoy()) return null
        return when (p.getString(KEY_MED_ESTADO, "")) {
            "ON" -> EstadoMedicacion.ON
            "OFF" -> EstadoMedicacion.OFF
            else -> null
        }
    }

    fun registrarEstadoMedicacion(context: Context, estado: EstadoMedicacion) {
        prefs(context).edit()
            .putString(KEY_MED_ESTADO, estado.name)
            .putString(KEY_MED_FECHA, hoy())
            .apply()
    }

    /** Llamar al cerrar sesión o eliminar la cuenta. */
    fun reiniciar(context: Context) {
        prefs(context).edit().clear().apply()
    }

    // ---------------- CÁLCULO ----------------

    private fun textoQuickDash(puntaje: Float): String = when {
        puntaje < 15f -> "sin dificultad o muy leve"
        puntaje < 40f -> "dificultad leve"
        puntaje < 60f -> "dificultad moderada"
        else -> "dificultad importante"
    }

    /** Cálculo puro: no mira si es Premium. */
    fun calcular(
        context: Context,
        puntajeQuickDash: Float? = ProgresoData.puntajeQuickDash
    ): ResultadoDificultad {
        val base = when {
            puntajeQuickDash == null -> 3
            puntajeQuickDash < 15f -> 4
            puntajeQuickDash < 40f -> 3
            puntajeQuickDash < 60f -> 2
            else -> 1
        }

        val sensaciones = leerSensaciones(context).map { it.second }
        val ajuste = if (sensaciones.isEmpty()) 0 else sensaciones.average().roundToInt()

        var nivel = (base + ajuste).coerceIn(1, 5)

        // Si lo último que dijo fue "con molestias", no subimos de nivel normal
        if (sensaciones.lastOrNull() == Sensacion.CON_MOLESTIAS.valor) nivel = minOf(nivel, 3)

        val partes = mutableListOf<String>()
        partes += if (puntajeQuickDash != null) {
            "Tu último QuickDASH fue ${String.format(Locale.US, "%.0f", puntajeQuickDash)} (${textoQuickDash(puntajeQuickDash)})"
        } else {
            "Todavía no hiciste el QuickDASH"
        }
        val ultima = Sensacion.values().firstOrNull { it.valor == sensaciones.lastOrNull() }
        if (ultima != null) partes += "te sentiste ${ultima.texto}"
        val motivo = partes.joinToString(" y ") + ", así que tus rutinas están en nivel $nivel."

        return ResultadoDificultad(
            nivel = nivel,
            etiqueta = etiquetas.getValue(nivel),
            factor = factores.getValue(nivel),
            motivo = motivo,
            estadoMedicacion = estadoMedicacionHoy(context)
        )
    }

    /**
     * Lo que tienen que usar los ejercicios: con Premium aplica el cálculo;
     * sin Premium siempre devuelve nivel normal y versión on.
     */
    fun vigente(context: Context): ResultadoDificultad {
        return if (UserData.esPremium) {
            calcular(context)
        } else {
            ResultadoDificultad(3, etiquetas.getValue(3), 1.0f, "Plan gratuito: rutinas en nivel normal.", null)
        }
    }

    /** Ajusta una cantidad base (repeticiones, segundos) según el nivel. Nunca baja de 1. */
    fun escalar(valorBase: Int, resultado: ResultadoDificultad): Int =
        (valorBase * resultado.factor).roundToInt().coerceAtLeast(1)
}

/**
 * Pregunta "¿Cómo está tu medicación ahora?" (on / off).
 * Se muestra antes de entrenar. No guarda sola: usá onElegir para llamar a
 * DificultadAdaptativa.registrarEstadoMedicacion(...).
 */
@Composable
fun TarjetaOnOff(
    estado: EstadoMedicacion?,
    onElegir: (EstadoMedicacion) -> Unit
) {
    val verde = Color(0xFF1B7A4B)
    val borde = Color(0xFFE5E7EB)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borde, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "¿Cómo está tu medicación ahora?",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A1A)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Así elegimos la versión de tus rutinas para este momento.",
            fontSize = 13.sp,
            color = Color(0xFF6B7280),
            lineHeight = 18.sp
        )

        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            listOf(
                EstadoMedicacion.ON to "Está haciendo efecto",
                EstadoMedicacion.OFF to "Se está pasando el efecto"
            ).forEachIndexed { i, (opcion, detalle) ->
                val elegido = estado == opcion
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .areaTactil()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (elegido) verde else Color.White)
                        .border(
                            width = if (elegido) 2.dp else 1.dp,
                            color = if (elegido) verde else borde,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .selectable(
                            selected = elegido,
                            role = Role.RadioButton,
                            onClick = { onElegir(opcion) }
                        )
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = opcion.texto,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (elegido) Color.White else verde
                    )
                    Text(
                        text = detalle,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp,
                        color = if (elegido) Color.White else Color(0xFF6B7280)
                    )
                }
                if (i == 0) Spacer(Modifier.width(10.dp))
            }
        }

        if (estado != null) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = if (estado == EstadoMedicacion.OFF)
                    "Hoy vas a entrenar con la versión suave."
                else
                    "Hoy vas a entrenar con la versión normal.",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = verde
            )
        }
    }
}