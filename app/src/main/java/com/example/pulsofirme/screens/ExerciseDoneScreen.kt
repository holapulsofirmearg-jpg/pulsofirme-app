package com.example.pulsofirme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeBoton = Color(0xFF1E7A50)
private val VerdeTitulo = Color(0xFF1B7A4B)
private val VerdeSuave = Color(0xFFEFF6F1)
private val BordeGris = Color(0xFFE3E8E5)
private val GrisTexto = Color(0xFF6B7280)

@Composable
fun ExerciseDoneScreen(
    nombre: String,
    ejercicio: String = "Extensión de dedos",
    repeticiones: String = "5 de 5",
    duracion: String = "2 min",
    onRepeat: () -> Unit,
    onHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(70.dp))

        Box(
            modifier = Modifier
                .size(130.dp)
                .background(VerdeSuave, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = VerdeBoton,
                modifier = Modifier.size(76.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (nombre.isBlank()) "¡Muy bien, lo lograste!" else "¡Muy bien $nombre, lo lograste!",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeTitulo,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Completaste el ejercicio. Cada repetición suma a tu movilidad.",
            fontSize = 15.sp,
            color = GrisTexto,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BordeGris, RoundedCornerShape(16.dp))
                .padding(18.dp)
        ) {
            Text(
                text = "Resumen de la sesión",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(16.dp))

            FilaDato(etiqueta = "Ejercicio", valor = ejercicio)
            Spacer(modifier = Modifier.height(12.dp))
            FilaDato(etiqueta = "Repeticiones", valor = repeticiones)
            Spacer(modifier = Modifier.height(12.dp))
            FilaDato(etiqueta = "Duración", valor = duracion)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerdeSuave, RoundedCornerShape(14.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🎉", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Llevás 3 de 4 ejercicios de hoy. ¡Te queda uno para completar el día!",
                fontSize = 14.sp,
                color = Color(0xFF3D5C4A),
                lineHeight = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeBoton)
        ) {
            Text(
                text = "Volver al inicio",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onRepeat,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
        ) {
            Text(
                text = "Repetir ejercicio",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = VerdeBoton
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = etiqueta,
            fontSize = 15.sp,
            color = GrisTexto
        )
        Text(
            text = valor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
    }
}