package com.example.pulsofirme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeEjProf = Color(0xFF07884A)
private val VerdeClaroEjProf = Color(0xFFEAF8F0)
private val GrisEjProf = Color(0xFF6B7280)

private data class PacienteEjercicios(
    val nombre: String,
    val realizados: Int
)

@Composable
fun ProfessionalExercisesProgressScreen(
    onBack: () -> Unit
) {
    val pacientes = listOf(
        PacienteEjercicios("Juan Pérez", 5),
        PacienteEjercicios("María Gómez", 4),
        PacienteEjercicios("Lucía Castro", 3),
        PacienteEjercicios("Diego Ramírez", 2),
        PacienteEjercicios("Ana Suárez", 1)
    )

    Scaffold(containerColor = Color.White) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 22.dp, vertical = 18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                }
                Spacer(Modifier.width(4.dp))
                Column {
                    Text(
                        "Ejercicios realizados",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10152B)
                    )
                    Text(
                        "Progreso semanal de tus 5 pacientes",
                        fontSize = 13.sp,
                        color = GrisEjProf
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = VerdeClaroEjProf)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.FitnessCenter,
                        contentDescription = null,
                        tint = VerdeEjProf,
                        modifier = Modifier.size(34.dp)
                    )
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(
                            "Objetivo semanal",
                            fontSize = 14.sp,
                            color = GrisEjProf
                        )
                        Text(
                            "5 ejercicios = 100%",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            color = VerdeEjProf
                        )
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            pacientes.forEach { paciente ->
                val porcentaje = (paciente.realizados * 20).coerceIn(0, 100)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    paciente.nombre,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF10152B)
                                )
                                Text(
                                    "${paciente.realizados} de 5 ejercicios",
                                    fontSize = 13.sp,
                                    color = GrisEjProf
                                )
                            }

                            if (porcentaje == 100) {
                                Icon(
                                    Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = VerdeEjProf
                                )
                                Spacer(Modifier.width(5.dp))
                            }

                            Text(
                                "$porcentaje%",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = VerdeEjProf
                            )
                        }

                        Spacer(Modifier.height(11.dp))

                        LinearProgressIndicator(
                            progress = { porcentaje / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = VerdeEjProf,
                            trackColor = Color(0xFFE5E7EB)
                        )
                    }
                }
            }
        }
    }
}
