package com.example.pulsofirme.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.R

private val VerdeBoton = Color(0xFF2E9E63)
private val VerdeTitulo = Color(0xFF1B7A4B)
private val AzulCabecera = Color(0xFF1565C0)
private val AzulTexto = Color(0xFF1565C0)

@Composable
fun CongratsScreen(
    nombre: String,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    val estadoScroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(estadoScroll)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val ancho = size.width
                val alto = size.height
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(ancho, 0f)
                    lineTo(ancho, alto * 0.62f)
                    cubicTo(
                        ancho * 0.62f, alto * 1.08f,
                        ancho * 0.35f, alto * 0.60f,
                        0f, alto * 0.95f
                    )
                    close()
                }
                drawPath(path = path, color = AzulCabecera)
            }

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 10.dp, top = 62.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "¡Ya estás listo!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 68.dp)
            )
        }

        Image(
            painter = painterResource(id = R.drawable.pulgar),
            contentDescription = "Pulgar arriba",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(190.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (nombre.isBlank()) "¡Felicitaciones!" else "¡Felicitaciones $nombre!",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeTitulo,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {

            Text(
                text = buildAnnotatedString {
                    append("Comienza hoy con la versión gratuita de ")
                    withStyle(SpanStyle(color = AzulTexto, fontWeight = FontWeight.Bold)) {
                        append("Pulso Firme")
                    }
                    append(".")
                },
                fontSize = 15.sp,
                color = Color(0xFF3D3D3D),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = buildAnnotatedString {
                    append("Cuando estés listo para dar el siguiente paso, podrás acceder a la ")
                    withStyle(SpanStyle(color = VerdeTitulo, fontWeight = FontWeight.Bold)) {
                        append("Versión Premium Guiada")
                    }
                    append(", diseñada para ofrecerte una experiencia más completa con:")
                },
                fontSize = 15.sp,
                color = Color(0xFF3D3D3D),
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            BeneficioItem(emoji = "🏋", texto = "Ejercicios exclusivos.")
            BeneficioItem(emoji = "📋", texto = "Planes personalizados según tu progreso.")
            BeneficioItem(emoji = "🏆", texto = "Desafíos, logros y recompensas.")
            BeneficioItem(emoji = "🔄", texto = "Contenido actualizado periódicamente.")

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "¡Mejora tus resultados con una experiencia de entrenamiento más completa!",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeTitulo,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(26.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeBoton)
            ) {
                Text(
                    text = "¡Adelante!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
private fun BeneficioItem(emoji: String, texto: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(Color(0xFFEFF6F1), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 19.sp)
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = texto,
            fontSize = 15.sp,
            color = Color(0xFF3D3D3D)
        )
    }
}