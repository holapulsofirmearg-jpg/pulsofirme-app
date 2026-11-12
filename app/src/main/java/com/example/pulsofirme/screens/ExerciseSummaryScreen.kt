package com.example.pulsofirme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Verde = Color(0xFF1B7A4B)
private val VerdeClaro = Color(0xFFEFF6F1)

@Composable
fun ExerciseSummaryScreen(
    nombreEjercicio: String,
    repeticionesCompletadas: Int,
    totalRepeticiones: Int,
    tiempoTotalSegundos: Int,
    onVolverInicio: () -> Unit,
    onVerOtrosEjercicios: () -> Unit
) {
    val minutos = tiempoTotalSegundos / 60
    val segundos = tiempoTotalSegundos % 60
    val tiempoTexto = if (minutos > 0) "${minutos} min ${segundos} seg" else "${segundos} seg"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        Box(
            modifier = Modifier
                .size(130.dp)
                .background(VerdeClaro, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = Verde,
                modifier = Modifier.size(76.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "¡Ejercicio completado!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Verde,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = nombreEjercicio,
            fontSize = 18.sp,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(36.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            TarjetaResumenEjercicio(
                icono = Icons.Filled.Repeat,
                valor = "$repeticionesCompletadas/$totalRepeticiones",
                etiqueta = "Repeticiones",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(14.dp))
            TarjetaResumenEjercicio(
                icono = Icons.Filled.Timer,
                valor = tiempoTexto,
                etiqueta = "Tiempo total",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "¡Buen trabajo! Cada repetición suma para mejorar tu movilidad.",
            fontSize = 17.sp,
            color = Color(0xFF374151),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(44.dp))

        Button(
            onClick = onVolverInicio,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Verde)
        ) {
            Text("Volver al inicio", fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ver otros ejercicios",
            fontSize = 17.sp,
            color = Verde,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(vertical = 10.dp)
                .clickable { onVerOtrosEjercicios() }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun TarjetaResumenEjercicio(
    icono: ImageVector,
    valor: String,
    etiqueta: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(VerdeClaro, RoundedCornerShape(16.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icono, contentDescription = null, tint = Verde, modifier = Modifier.size(30.dp))
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = valor, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
        Text(text = etiqueta, fontSize = 14.sp, color = Color(0xFF6B7280))
    }
}