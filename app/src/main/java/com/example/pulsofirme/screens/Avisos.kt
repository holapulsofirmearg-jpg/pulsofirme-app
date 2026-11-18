package com.example.pulsofirme.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.Accesibilidad
import com.example.pulsofirme.ui.theme.ColoresApp
import kotlinx.coroutines.delay

enum class TipoAviso { EXITO, ERROR, INFO }

data class Aviso(
    val id: Long,
    val mensaje: String,
    val tipo: TipoAviso,
    val largo: Boolean
)

/**
 * Avisos dentro de la app (reemplazan a los Toast del sistema).
 * Se pueden llamar desde cualquier lado, también desde los listeners de Firebase:
 *
 *     Avisos.mostrar("Datos guardados")
 *
 * El tipo (éxito, error o información) se deduce del texto si no se indica.
 * Se muestran abajo de la pantalla, con ícono y color, y se cierran solos
 * (o tocándolos). TalkBack los lee en voz alta.
 */
object Avisos {
    var actual by mutableStateOf<Aviso?>(null)
        private set

    fun mostrar(mensaje: String, largo: Boolean = false, tipo: TipoAviso? = null) {
        actual = Aviso(
            id = System.nanoTime(),
            // Se traduce si el usuario eligió inglés (el tipo se deduce del texto original)
            mensaje = tr(mensaje),
            tipo = tipo ?: deducirTipo(mensaje),
            largo = largo || mensaje.length > 70
        )
    }

    fun cerrar() {
        actual = null
    }

    private val palabrasError = listOf(
        "no se pudo", "no pudimos", "no es correct", "no son correct", "incorrect", "error",
        "no hay conexión", "sin conexión", "inválid", "no válid", "no es válid", "falta",
        "completá", "ingresá", "debe", "tenés que", "no coinciden", "demasiados intentos",
        "no se encontró", "no existe", "no tenés", "límite", "rechaz", "deshabilitada",
        "ya tiene una cuenta", "muy débil", "no está", "elegí", "seleccioná", "escribí"
    )

    private val palabrasExito = listOf(
        "guardad", "enviad", "eliminad", "actualizad", "vinculad", "registrad", "creada",
        "creado", "listo", "correctamente", "activad", "copiad", "agregad", "cambiad", "¡"
    )

    private fun deducirTipo(mensaje: String): TipoAviso {
        val t = mensaje.lowercase()
        return when {
            palabrasError.any { it in t } -> TipoAviso.ERROR
            palabrasExito.any { it in t } -> TipoAviso.EXITO
            else -> TipoAviso.INFO
        }
    }
}

/** Se dibuja encima de toda la app (ver MainActivity). */
@Composable
fun BoxScope.AvisoFlotante() {
    val aviso = Avisos.actual
    // Guarda el último aviso para que se vea mientras se va la animación de salida
    var ultimo by remember { mutableStateOf<Aviso?>(null) }
    if (aviso != null) ultimo = aviso

    LaunchedEffect(aviso?.id) {
        if (aviso != null) {
            delay(if (aviso.largo) 5000L else 3200L)
            if (Avisos.actual?.id == aviso.id) Avisos.cerrar()
        }
    }

    val colores = ColoresApp
    val sinAnimacion = Accesibilidad.reducirAnimaciones

    AnimatedVisibility(
        visible = aviso != null,
        enter = if (sinAnimacion) fadeIn(initialAlpha = 1f) else fadeIn() + slideInVertically { it / 2 },
        exit = if (sinAnimacion) fadeOut(targetAlpha = 1f) else fadeOut() + slideOutVertically { it / 2 },
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .navigationBarsPadding()
            .imePadding()
            // Queda arriba del menú inferior
            .padding(start = 16.dp, end = 16.dp, bottom = 92.dp)
    ) {
        val mostrado = ultimo ?: return@AnimatedVisibility

        val (icono, acento) = when (mostrado.tipo) {
            TipoAviso.EXITO -> Icons.Filled.CheckCircle to colores.verde
            TipoAviso.ERROR -> Icons.Filled.ErrorOutline to colores.rojo
            TipoAviso.INFO -> Icons.Filled.Info to colores.azul
        }
        val fondo = if (colores.esOscuro) Color(0xFF2A2A2A) else Color(0xFF1F2937)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .shadow(10.dp, RoundedCornerShape(16.dp))
                .background(fondo, RoundedCornerShape(16.dp))
                .border(1.dp, acento.copy(alpha = 0.55f), RoundedCornerShape(16.dp))
                .clickable(onClickLabel = "Cerrar aviso") { Avisos.cerrar() }
                .heightIn(min = 56.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .semantics { liveRegion = LiveRegionMode.Polite }
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(acento.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = acento, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = mostrado.mensaje,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 20.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}