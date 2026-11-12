package com.example.pulsofirme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.R

val DarkGreenBg = Color(0xFF075E5B)
val HighlightGreen = Color(0xFF42D6A4)
val ButtonDarkGreen = Color(0xFF042B2A)
val DotsInactive = Color(0x66FFFFFF)

@Composable
fun WelcomeCaregiverScreen(
    onBack: () -> Unit = {},
    onComenzar: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkGreenBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botón Volver
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(
                    onClick = { onBack() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver atrás",
                        tint = Color.White
                    )
                }
            }

            // Contenido del medio: se centra, y si no entra
            // (celular chico o letra grande) se desliza.
            // El botón "Comenzar" queda siempre visible abajo.
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(22.dp, Alignment.CenterVertically)
            ) {
                // Título
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "¡Bienvenido",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "cuidador!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = HighlightGreen,
                        textAlign = TextAlign.Center
                    )
                }

                // Imagen limpia (sin fondo adicional)
                Image(
                    painter = painterResource(id = R.drawable.segunda),
                    contentDescription = "Ilustración Cuidador",
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .heightIn(max = 260.dp),
                    contentScale = ContentScale.Fit
                )

                // Texto descriptivo
                Text(
                    text = "Estás acá para hacer la diferencia. Como cuidador podés acompañar, ayudar y cuidar a quienes más lo necesitan.",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                // Puntos de paginación
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(HighlightGreen)
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(DotsInactive)
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(DotsInactive)
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(DotsInactive)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón Comenzar
            Button(
                onClick = { onComenzar() },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonDarkGreen)
            ) {
                Text(
                    text = "Comenzar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}