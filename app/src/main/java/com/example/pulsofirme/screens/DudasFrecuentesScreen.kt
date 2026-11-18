package com.example.pulsofirme.screens

import androidx.compose.runtime.saveable.rememberSaveable

import com.example.pulsofirme.ui.theme.enTema

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdePr: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
private val VerdeOscuroPr: Color
    @Composable get() = Color(0xFF1B4332).enTema()
private val VerdeSuavePr: Color
    @Composable get() = Color(0xFFEAF5EC).enTema()
private val GrisPr: Color
    @Composable get() = Color(0xFF6B7280).enTema()
private val NegroPr: Color
    @Composable get() = Color(0xFF1A1A1A).enTema()
data class PreguntaFrecuente(
    val pregunta: String,
    val respuesta: String
)

private val listaPreguntas = listOf(
    PreguntaFrecuente(
        pregunta = "¿Cómo funciona el seguimiento de ejercicios?",
        respuesta = "La app utiliza los sensores del dispositivo para medir la precisión y rango de movimiento durante cada rutina, guardando tu progreso automáticamente."
    ),
    PreguntaFrecuente(
        pregunta = "¿Puedo vincular a mi médico o cuidador?",
        respuesta = "Sí, desde la pantalla de Cuenta podés vincular a tu cuidador para que reciba tus alertas e informes de progreso."
    ),
    PreguntaFrecuente(
        pregunta = "¿Qué hago si se desconecta el dispositivo?",
        respuesta = "Asegurate de tener el Bluetooth activado en tu teléfono. Podés reintentar la conexión desde la pestaña 'Dispositivo'."
    ),
    PreguntaFrecuente(
        pregunta = "¿Mis datos médicos están protegidos?",
        respuesta = "Sí, toda tu información está cifrada y almacenada de forma segura cumpliendo con las normativas de privacidad."
    ),
    PreguntaFrecuente(
        pregunta = "¿Cómo exporto el informe para mi consulta?",
        respuesta = "Desde la sección de Ajustes o Datos podés generar un reporte con la información relevante para tu profesional de salud."
    )
)

@Composable
fun DudasFrecuentesScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.enTema())
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(48.dp))

        // Encabezado
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = VerdeOscuroPr
                )
            }
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Dudas frecuentes",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeOscuroPr
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Encontrá respuestas rápidas a las consultas más comunes sobre el uso de PulsoFirme.",
            fontSize = 15.sp,
            color = GrisPr,
            lineHeight = 21.sp
        )

        Spacer(Modifier.height(24.dp))

        listaPreguntas.forEach { item ->
            ItemPregunta(item = item)
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(30.dp))
    }
}

@Composable
private fun ItemPregunta(item: PreguntaFrecuente) {
    var expandido by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (expandido) VerdeSuavePr else Color(0xFFF9FAFB).enTema())
            .clickable { expandido = !expandido }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.pregunta,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (expandido) VerdeOscuroPr else NegroPr,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expandido) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = if (expandido) VerdePr else GrisPr
            )
        }

        AnimatedVisibility(visible = expandido) {
            Column {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = item.respuesta,
                    fontSize = 14.sp,
                    color = NegroPr,
                    lineHeight = 20.sp
                )
            }
        }
    }
}