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
import com.example.pulsofirme.Accesibilidad

// ---------- Modo elegido por el usuario ----------
enum class ModoTema(val etiqueta: String) {
    CLARO("Claro"),
    OSCURO("Oscuro"),
    AUTOMATICO("Automático")
}

object TemaApp {
    var modo by mutableStateOf(ModoTema.CLARO)
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