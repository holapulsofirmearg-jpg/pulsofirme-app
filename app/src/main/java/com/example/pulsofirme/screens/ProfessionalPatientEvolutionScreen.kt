package com.example.pulsofirme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeEvolucion = Color(0xFF07884A)
private val VerdeClaroEvolucion = Color(0xFFEAF8F0)
private val AzulClaroEvolucion = Color(0xFFEAF6FF)
private val GrisEvolucion = Color(0xFF6B7280)

private data class PacienteEvolucion(
    val nombre: String,
    val inicio: Int,
    val actual: Int,
    val estado: String
)

@Composable
fun ProfessionalPatientEvolutionScreen(
    onBack: () -> Unit,
    onPacienteClick: (String) -> Unit = {}
) {
    val pacientes = listOf(
        PacienteEvolucion("Juan Pérez", 60, 90, "En mejora"),
        PacienteEvolucion("María Gómez", 55, 85, "En mejora"),
        PacienteEvolucion("Lucía Castro", 50, 80, "En mejora"),
        PacienteEvolucion("Diego Ramírez", 45, 75, "En mejora"),
        PacienteEvolucion("Ana Suárez", 50, 50, "Sin cambios")
    )

    val pacientesEnMejora = pacientes.count { it.actual > it.inicio }

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
                    text = "Evolución de pacientes",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10152B)
                )
            }

            Spacer(Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = VerdeClaroEvolucion)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.TrendingUp,
                        contentDescription = null,
                        tint = VerdeEvolucion,
                        modifier = Modifier.size(42.dp)
                    )
                    Spacer(Modifier.width(15.dp))
                    Column {
                        Text(
                            text = "$pacientesEnMejora / ${pacientes.size}",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10152B)
                        )
                        Text(
                            text = "Pacientes en mejora",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Evolución registrada esta semana",
                            fontSize = 12.sp,
                            color = GrisEvolucion
                        )
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            Text(
                text = "Por paciente",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10152B)
            )

            Spacer(Modifier.height(10.dp))

            pacientes.forEach { paciente ->
                val diferencia = paciente.actual - paciente.inicio
                val mejora = diferencia > 0

                Card(
                    onClick = { onPacienteClick(paciente.nombre) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(15.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = paciente.nombre,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF10152B)
                                )
                                Text(
                                    text = "Precisión y control de movimiento",
                                    fontSize = 12.sp,
                                    color = GrisEvolucion
                                )
                            }

                            Surface(
                                color = if (mejora) VerdeClaroEvolucion else Color(0xFFF3F4F6),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = if (mejora) "+$diferencia% Mejora" else "Sin cambios",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    color = if (mejora) VerdeEvolucion else GrisEvolucion,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = "Ver paciente",
                                tint = GrisEvolucion
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${paciente.inicio}%",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GrisEvolucion
                                )
                                Text("Inicio", fontSize = 11.sp, color = GrisEvolucion)
                            }

                            LinearProgressIndicator(
                                progress = { paciente.actual / 100f },
                                modifier = Modifier
                                    .weight(2f)
                                    .height(8.dp),
                                color = VerdeEvolucion,
                                trackColor = Color(0xFFE5E7EB)
                            )

                            Spacer(Modifier.width(14.dp))

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${paciente.actual}%",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (mejora) VerdeEvolucion else GrisEvolucion
                                )
                                Text("Actual", fontSize = 11.sp, color = GrisEvolucion)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AzulClaroEvolucion)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = Color(0xFF2583C5)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Indicador de evolución",
                            color = Color(0xFF2583C5),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Compara el valor inicial con el estado actual de cada paciente.",
                            fontSize = 12.sp,
                            color = GrisEvolucion
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}