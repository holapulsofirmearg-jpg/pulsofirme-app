package com.example.pulsofirme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeCumplimiento = Color(0xFF07884A)
private val VerdeClaroCumplimiento = Color(0xFFEAF8F0)
private val GrisCumplimiento = Color(0xFF6B7280)
private val NaranjaCumplimiento = Color(0xFFF59E0B)
private val NaranjaClaroCumplimiento = Color(0xFFFFF4DB)

private data class PacienteCumplimiento(
    val nombre: String,
    val ejercicios: Int
)

@Composable
fun ProfessionalComplianceScreen(
    onBack: () -> Unit,
    onPacienteClick: (String) -> Unit = {}
) {
    val pacientes = listOf(
        PacienteCumplimiento("Juan Pérez", 5),
        PacienteCumplimiento("María Gómez", 4),
        PacienteCumplimiento("Lucía Castro", 4),
        PacienteCumplimiento("Diego Ramírez", 3),
        PacienteCumplimiento("Ana Suárez", 3)
    )

    val promedio = pacientes
        .map { it.ejercicios * 20 }
        .average()
        .toInt()

    Scaffold(containerColor = Color.White) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                }
                Text(
                    "Cumplimiento de pacientes",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10152B)
                )
            }

            Spacer(Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = VerdeClaroCumplimiento)
            ) {
                Row(
                    Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.TrackChanges,
                        contentDescription = null,
                        tint = VerdeCumplimiento,
                        modifier = Modifier.size(42.dp)
                    )
                    Spacer(Modifier.width(15.dp))
                    Column {
                        Text(
                            "$promedio%",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10152B)
                        )
                        Text(
                            "Cumplimiento promedio",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Promedio de tus 5 pacientes esta semana",
                            fontSize = 12.sp,
                            color = GrisCumplimiento
                        )
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            Text(
                "Por paciente",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10152B)
            )

            Spacer(Modifier.height(10.dp))

            pacientes.forEach { paciente ->
                val porcentaje = (paciente.ejercicios * 20).coerceIn(0, 100)
                val objetivoCumplido = porcentaje == 100
                val buenProgreso = porcentaje >= 80
                val estado = when {
                    objetivoCumplido -> "Objetivo cumplido"
                    buenProgreso -> "Muy buen progreso"
                    porcentaje >= 60 -> "En progreso"
                    else -> "Requiere seguimiento"
                }
                val estadoColor =
                    if (porcentaje >= 80) VerdeCumplimiento else NaranjaCumplimiento
                val estadoFondo =
                    if (porcentaje >= 80) VerdeClaroCumplimiento else NaranjaClaroCumplimiento

                Card(
                    onClick = { onPacienteClick(paciente.nombre) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(Modifier.padding(15.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    paciente.nombre,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF10152B)
                                )
                                Text(
                                    "${paciente.ejercicios} / 5 ejercicios",
                                    fontSize = 13.sp,
                                    color = GrisCumplimiento
                                )
                            }
                            Text(
                                "$porcentaje%",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = VerdeCumplimiento
                            )
                            Spacer(Modifier.width(5.dp))
                            Icon(
                                Icons.Filled.ChevronRight,
                                contentDescription = "Ver paciente",
                                tint = GrisCumplimiento
                            )
                        }

                        Spacer(Modifier.height(9.dp))

                        LinearProgressIndicator(
                            progress = { porcentaje / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = VerdeCumplimiento,
                            trackColor = Color(0xFFE5E7EB)
                        )

                        Spacer(Modifier.height(9.dp))

                        Surface(
                            color = estadoFondo,
                            shape = RoundedCornerShape(50)
                        ) {
                            Row(
                                Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (porcentaje >= 80) {
                                    Icon(
                                        Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = estadoColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(5.dp))
                                }
                                Text(
                                    estado,
                                    color = estadoColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VerdeClaroCumplimiento)
            ) {
                Row(
                    Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Lightbulb,
                        contentDescription = null,
                        tint = VerdeCumplimiento
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            "Tip",
                            color = VerdeCumplimiento,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Incentivá la constancia con rutinas personalizadas para cada paciente.",
                            fontSize = 12.sp,
                            color = GrisCumplimiento
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}
