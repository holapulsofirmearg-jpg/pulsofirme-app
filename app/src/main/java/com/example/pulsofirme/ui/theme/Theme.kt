package com.example.pulsofirme.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import android.content.Context
import androidx.compose.runtime.ReadOnlyComposable
import kotlin.math.abs
import com.example.pulsofirme.Accesibilidad

// ---------- Modo elegido por el usuario ----------
enum class ModoTema(val etiqueta: String) {
    CLARO("Claro"),
    OSCURO("Oscuro"),
    AUTOMATICO("Automático")
}

object TemaApp {
    var modo by mutableStateOf(ModoTema.CLARO)
        private set

    private const val PREFS = "tema_app"
    private const val CLAVE_MODO = "modo"

    /** Lee el tema elegido (se llama al abrir la app). */
    fun cargar(context: Context) {
        val guardado = context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(CLAVE_MODO, null)
        modo = ModoTema.values().firstOrNull { it.name == guardado } ?: ModoTema.CLARO
    }

    /** Cambia el tema y lo guarda en el celular. */
    fun cambiar(context: Context, nuevo: ModoTema) {
        modo = nuevo
        context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(CLAVE_MODO, nuevo.name).apply()
    }
}

/**
 * Adapta un color pensado para modo claro al modo oscuro.
 * En modo claro (y en alto contraste) devuelve el mismo color.
 *
 * - Blanco (fondos)            -> fondo oscuro
 * - Grises muy claros          -> paneles y bordes oscuros
 * - Tonos pastel (verde, etc.) -> el mismo tono, oscuro
 * - Textos oscuros             -> el mismo tono, claro
 * - Grises medios (subtítulos) -> gris claro legible
 * - Colores de marca           -> un poco más claros para que se lean sobre negro
 */
@Composable
@ReadOnlyComposable
fun Color.enTema(): Color =
    if (LocalColoresPF.current.esOscuro) colorParaOscuro(this) else this

fun colorParaOscuro(color: Color): Color {
    if (color == Color.Transparent || color.alpha == 0f) return color

    val r = color.red
    val g = color.green
    val b = color.blue
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val l = (max + min) / 2f
    val d = max - min
    val s = if (d == 0f) 0f else d / (1f - abs(2f * l - 1f))
    val h = when {
        d == 0f -> 0f
        max == r -> 60f * (((g - b) / d) % 6f)
        max == g -> 60f * (((b - r) / d) + 2f)
        else -> 60f * (((r - g) / d) + 4f)
    }.let { if (it < 0f) it + 360f else it }

    val resultado = when {
        // Blanco o casi blanco: fondo de pantalla
        l >= 0.97f && s < 0.25f -> FondoOscuro
        // Grises muy claros: paneles y bordes
        l >= 0.80f && s < 0.12f -> if (l >= 0.95f) SuperficieOscura else Color(0xFF333333)
        // Pasteles (fondos de tarjetas): mismo tono, oscuro
        l >= 0.80f -> Color.hsl(h, minOf(s, 0.30f), if (l >= 0.90f) 0.16f else 0.24f)
        // Textos casi negros o muy oscuros: mismo tono, claro
        l <= 0.22f -> if (s < 0.15f) TextoOscuro else Color.hsl(h, minOf(s, 0.45f), 0.86f)
        // Grises medios (subtítulos, íconos apagados)
        s < 0.15f -> if (l < 0.55f) Color(0xFFA0A6B0) else Color(0xFF7C828C)
        // Colores de marca: un poco más claros
        else -> Color.hsl(h, minOf(s, 0.60f), l.coerceIn(0.40f, 0.70f))
    }
    return resultado.copy(alpha = color.alpha)
}

// ---------- Colores propios de PulsoFirme, accesibles desde cualquier pantalla ----------
data class ColoresPF(
    val verde: Color,
    val verdeOscuro: Color,
    val verdeClaro: Color,
    val fondo: Color,
    val superficie: Color,
    val texto: Color,
    val textoSecundario: Color,
    val borde: Color,
    val tarjeta: Color,
    val azul: Color,
    val rojo: Color,
    val ambar: Color,
    val esOscuro: Boolean
)

private val coloresClaros = ColoresPF(
    verde = VerdePF,
    verdeOscuro = VerdeOscuroPF,
    verdeClaro = VerdeClaroPF,
    fondo = FondoClaro,
    superficie = SuperficieClara,
    texto = TextoClaro,
    textoSecundario = TextoSecundarioClaro,
    borde = BordeClaro,
    tarjeta = TarjetaClara,
    azul = AzulPF,
    rojo = RojoPF,
    ambar = AmbarPF,
    esOscuro = false
)

private val coloresOscuros = ColoresPF(
    verde = VerdeOscuroModo,
    verdeOscuro = VerdeOscuroModo,
    verdeClaro = TarjetaOscura,
    fondo = FondoOscuro,
    superficie = SuperficieOscura,
    texto = TextoOscuro,
    textoSecundario = TextoSecundarioOscuro,
    borde = BordeOscuro,
    tarjeta = TarjetaOscura,
    azul = Color(0xFF64B5F6),
    rojo = Color(0xFFE57373),
    ambar = Color(0xFFE0A73A),
    esOscuro = true
)

// Alto contraste (Ajustes > Accesibilidad): negro sobre blanco y verde bien oscuro
private val coloresAltoContraste = coloresClaros.copy(
    verde = Color(0xFF0B4F2E),
    verdeOscuro = Color(0xFF063A21),
    texto = Color.Black,
    textoSecundario = Color(0xFF1F2937),
    borde = Color(0xFF4B5563),
    rojo = Color(0xFF8B1A10)
)

val LocalColoresPF = staticCompositionLocalOf { coloresClaros }

// Atajo para usar en las pantallas: ColoresPF.texto, ColoresPF.fondo, etc.
val ColoresApp: ColoresPF
    @Composable
    get() = LocalColoresPF.current

private val EsquemaOscuro = darkColorScheme(
    primary = VerdeOscuroModo,
    secondary = VerdeOscuroModo,
    background = FondoOscuro,
    surface = SuperficieOscura,
    onBackground = TextoOscuro,
    onSurface = TextoOscuro
)

private val EsquemaAltoContraste = lightColorScheme(
    primary = Color(0xFF0B4F2E),
    secondary = Color(0xFF0B4F2E),
    background = Color.White,
    surface = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    outline = Color(0xFF4B5563)
)

private val EsquemaClaro = lightColorScheme(
    primary = VerdePF,
    secondary = VerdePF,
    background = FondoClaro,
    surface = SuperficieClara,
    onBackground = TextoClaro,
    onSurface = TextoClaro
)

@Composable
fun PulsoFirmeTheme(
    content: @Composable () -> Unit
) {
    val oscuro = when (TemaApp.modo) {
        ModoTema.CLARO -> false
        ModoTema.OSCURO -> true
        ModoTema.AUTOMATICO -> isSystemInDarkTheme()
    }

    val contraste = Accesibilidad.altoContraste && !oscuro
    val colores = when {
        contraste -> coloresAltoContraste
        oscuro -> coloresOscuros
        else -> coloresClaros
    }

    CompositionLocalProvider(LocalColoresPF provides colores) {
        MaterialTheme(
            colorScheme = when {
                contraste -> EsquemaAltoContraste
                oscuro -> EsquemaOscuro
                else -> EsquemaClaro
            },
            typography = Typography,
            content = content
        )
    }
}