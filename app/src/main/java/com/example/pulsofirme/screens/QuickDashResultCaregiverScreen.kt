package com.example.pulsofirme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeTitulo = Color(0xFF1B7A4B)
private val AzulTexto = Color(0xFF16324F)
private val GrisSubtitulo = Color(0xFF6B7280)
private val VerdeClaroFondo = Color(0xFFEFF6F1)
private val VerdeClaro = Color(0xFFDFF3E4)
private val AzulClaro = Color(0xFFE3EEFB)
private val AzulTextoChip = Color(0xFF1D64C7)
private val AmarilloClaro = Color(0xFFFBF1D8)
private val AmarilloTexto = Color(0xFFB5850F)
private val RojoClaro = Color(0xFFFBE4E4)
private val RojoTexto = Color(0xFFC94A4A)
private val GrisBorde = Color(0xFFE5E7EB)
private val GrisClaroFondo = Color(0xFFF5F5F6)
private val GrisMoradoFondo = Color(0xFFEFEDF4)

data class RespuestaQuickDash(
    val cantidad: Int,
    val titulo: String,
    val subtitulo: String,
    val colorFondo: Color,
    val colorTexto: Color
)

@Composable
fun QuickDashResultCaregiverScreen(
    nombrePaciente: String = "María López",
    fechaEvaluacion: String = "30 de septiembre de 2024",
    puntajeAnterior: Int = 52,
    fechaAnterior: String = "2 de septiembre",
    puntajeActual: Int = 28,
    onBack: () -> Unit,
    onVerDetalle: () -> Unit = {}
) {
    val respuestas = listOf(
        RespuestaQuickDash(6, "Sin dificultad", "Actividades que realiza bien", VerdeClaro, VerdeTitulo),
        RespuestaQuickDash(3, "Dificultad leve", "Pequeñas limitaciones", AzulClaro, AzulTextoChip),
        RespuestaQuickDash(1, "Dificultad moderada", "Limitación moderada", AmarilloClaro, AmarilloTexto),
        RespuestaQuickDash(1, "Dificultad severa", "Mayor limitación", RojoClaro, RojoTexto)
    )

    val diferencia = puntajeActual - puntajeAnterior
    val mejoro = diferencia < 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Evaluación QuickDASH",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = AzulTexto
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Resultado del paciente",
            fontSize = 15.sp,
            color = GrisSubtitulo
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(VerdeClaro, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = nombrePaciente.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString(""),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdeTitulo
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(nombrePaciente, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = AzulTexto)
                Spacer(modifier = Modifier.height(2.dp))
                Text("Paciente vinculado", fontSize = 13.sp, color = GrisSubtitulo)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = GrisSubtitulo,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    fechaEvaluacion,
                    fontSize = 13.sp,
                    color = GrisSubtitulo,
                    textAlign = TextAlign.End
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(VerdeClaro, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = VerdeTitulo,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (mejoro) "El paciente mejoró" else "El paciente empeoró",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeTitulo,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tu paciente mostró una mejora en la funcionalidad de sus manos en comparación con la evaluación anterior.",
                fontSize = 15.sp,
                color = GrisSubtitulo,
                textAlign = TextAlign.Center,
                lineHeight = 21.sp
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Resumen de respuestas",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = AzulTexto
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "De las 11 preguntas del test:",
            fontSize = 14.sp,
            color = GrisSubtitulo
        )

        Spacer(modifier = Modifier.height(14.dp))

        respuestas.forEach { respuesta ->
            FilaRespuestaQuickDash(respuesta)
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Comparación con evaluación anterior",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = AzulTexto
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(GrisMoradoFondo, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text("Evaluación anterior", fontSize = 13.sp, color = GrisSubtitulo)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("$puntajeAnterior", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = AzulTexto)
                    Text(" / 100", fontSize = 14.sp, color = GrisSubtitulo)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(fechaAnterior, fontSize = 12.sp, color = GrisSubtitulo)
            }

            Box(
                modifier = Modifier.width(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("→", fontSize = 20.sp, color = GrisSubtitulo)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(VerdeClaroFondo, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text("Evaluación actual", fontSize = 13.sp, color = GrisSubtitulo)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("$puntajeActual", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = VerdeTitulo)
                    Text(" / 100", fontSize = 14.sp, color = GrisSubtitulo)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(fechaEvaluacion.substringBefore(" de "), fontSize = 12.sp, color = GrisSubtitulo)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(VerdeClaroFondo, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = VerdeTitulo,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "${diferencia} puntos",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdeTitulo
                )
                Text(
                    if (mejoro) "Mejora" else "Empeoró",
                    fontSize = 12.sp,
                    color = VerdeTitulo
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onVerDetalle,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeTitulo)
        ) {
            Text("Ver detalle de la evaluación", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun FilaRespuestaQuickDash(respuesta: RespuestaQuickDash) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(14.dp))
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(respuesta.colorFondo, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "${respuesta.cantidad}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = respuesta.colorTexto
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(respuesta.titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AzulTexto)
            Spacer(modifier = Modifier.height(2.dp))
            Text(respuesta.subtitulo, fontSize = 13.sp, color = GrisSubtitulo)
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(14.dp)
        )
    }
}