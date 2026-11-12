package com.example.pulsofirme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeEvolucion = Color(0xFF1B7A4B)
private val VerdeClaroEvolucion = Color(0xFFEFF6F1)
private val BordeEvolucion = Color(0xFFE5E7EB)
private val GrisEvolucion = Color(0xFF6B7280)

@Composable
fun PatientEvolutionCaregiverScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(34.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }

            Text(
                text = "Evolución del paciente",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerdeClaroEvolucion, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    tint = VerdeEvolucion
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "Juan Pérez",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Seguimiento de movilidad de la mano",
                    fontSize = 13.sp,
                    color = GrisEvolucion
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Resumen de evolución",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        TarjetaEvolucion(
            titulo = "Movilidad",
            valor = "En mejora",
            descripcion = "Se observa una mejora en la movilidad de la mano derecha.",
            icono = {
                Icon(
                    Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = VerdeEvolucion
                )
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        TarjetaEvolucion(
            titulo = "Ejercicios completados",
            valor = "3 de 5",
            descripcion = "Ejercicios realizados durante la jornada de hoy.",
            icono = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = VerdeEvolucion
                )
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        TarjetaEvolucion(
            titulo = "Tiempo de actividad",
            valor = "28 min",
            descripcion = "Tiempo total dedicado a ejercicios de rehabilitación.",
            icono = {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color(0xFF1565C0)
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun TarjetaEvolucion(
    titulo: String,
    valor: String,
    descripcion: String,
    icono: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BordeEvolucion, RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(VerdeClaroEvolucion, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            icono()
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                fontSize = 14.sp,
                color = GrisEvolucion
            )

            Text(
                text = valor,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = descripcion,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = GrisEvolucion
            )
        }
    }
}
