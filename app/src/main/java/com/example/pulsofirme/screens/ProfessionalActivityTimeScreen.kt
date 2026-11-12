package com.example.pulsofirme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeTiempoProf = Color(0xFF07884A)
private val VerdeClaroTiempoProf = Color(0xFFEAF8F0)
private val GrisTiempoProf = Color(0xFF6B7280)

private data class TiempoPacienteProfesional(
    val nombre: String,
    val minutos: Int
)

@Composable
fun ProfessionalActivityTimeScreen(
    onBack: () -> Unit
) {
    val pacientes = listOf(
        TiempoPacienteProfesional("Juan Pérez", 65),
        TiempoPacienteProfesional("María Gómez", 52),
        TiempoPacienteProfesional("Lucía Castro", 44),
        TiempoPacienteProfesional("Diego Ramírez", 31),
        TiempoPacienteProfesional("Ana Suárez", 16)
    )

    val totalMinutos = pacientes.sumOf { it.minutos }
    val horas = totalMinutos / 60
    val minutos = totalMinutos % 60

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
                        text = "Tiempo de actividad",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10152B)
                    )
                    Text(
                        text = "Actividad semanal de tus 5 pacientes",
                        fontSize = 13.sp,
                        color = GrisTiempoProf
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = VerdeClaroTiempoProf
                )
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = VerdeTiempoProf,
                        modifier = Modifier.size(35.dp)
                    )

                    Spacer(Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Tiempo total esta semana",
                            fontSize = 14.sp,
                            color = GrisTiempoProf
                        )
                        Text(
                            text = "$horas h $minutos min",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = VerdeTiempoProf
                        )
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            pacientes.forEach { paciente ->
                val proporcion =
                    if (totalMinutos == 0) 0f
                    else paciente.minutos.toFloat() / totalMinutos.toFloat()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = paciente.nombre,
                                modifier = Modifier.weight(1f),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF10152B)
                            )

                            Text(
                                text = formatearTiempo(paciente.minutos),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = VerdeTiempoProf
                            )
                        }

                        Spacer(Modifier.height(11.dp))

                        LinearProgressIndicator(
                            progress = { proporcion },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = VerdeTiempoProf,
                            trackColor = Color(0xFFE5E7EB)
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = "Esta semana",
                            fontSize = 12.sp,
                            color = GrisTiempoProf
                        )
                    }
                }
            }
        }
    }
}

private fun formatearTiempo(minutosTotales: Int): String {
    val horas = minutosTotales / 60
    val minutos = minutosTotales % 60

    return if (horas > 0) {
        "$horas h ${minutos.toString().padStart(2, '0')} min"
    } else {
        "$minutos min"
    }
}
