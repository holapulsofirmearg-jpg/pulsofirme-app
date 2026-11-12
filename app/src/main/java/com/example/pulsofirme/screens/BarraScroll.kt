package com.example.pulsofirme.screens

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Dibuja una barra de scroll a la derecha de una pantalla que scrollea.
// IMPORTANTE: se pone ANTES del .verticalScroll(), sino la barra se mueve con el contenido.
fun Modifier.barraDeScroll(
    estado: ScrollState,
    color: Color = Color(0xFF1B7A4B),
    ancho: Dp = 5.dp,
    margen: Dp = 3.dp
): Modifier = this.drawWithContent {
    drawContent()

    if (estado.maxValue > 0 && estado.maxValue != Int.MAX_VALUE) {
        val alturaVisible = size.height
        val alturaTotal = alturaVisible + estado.maxValue
        val alturaBarra = (alturaVisible / alturaTotal) * alturaVisible
        val recorrido = alturaVisible - alturaBarra
        val y = (estado.value.toFloat() / estado.maxValue) * recorrido

        val anchoPx = ancho.toPx()
        val margenPx = margen.toPx()

        drawRoundRect(
            color = color.copy(alpha = 0.45f),
            topLeft = Offset(size.width - anchoPx - margenPx, y),
            size = Size(anchoPx, alturaBarra),
            cornerRadius = CornerRadius(anchoPx / 2, anchoPx / 2)
        )
    }
}