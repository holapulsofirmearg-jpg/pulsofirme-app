package com.example.pulsofirme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

private val VerdeCuidador = Color(0xFF1B7A4B)
private val VerdeClaroCuidador = Color(0xFFEFF6F1)
private val BordeGrisCuidador = Color(0xFFE5E7EB)

@Composable
fun ProgressCaregiverScreen(
    rutaActual: String,
    onNavigate: (String) -> Unit,
    onQuickDashClick: () -> Unit,
    rolUsuario: String = "tutor"
) {
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {

                val itemsMenuCuidador = listOf(
                    Triple("Inicio", Icons.Filled.Home, "caregiver_home"),
                    Triple("Progreso", Icons.Filled.ShowChart, "caregiver_progress"),
                    Triple("Alertas", Icons.Filled.Notifications, "caregiver_alerts"),
                    Triple("Cuenta", Icons.Filled.Person, "caregiver_account")
                )

                val itemsMenuProfesional = listOf(
                    Triple("Inicio", Icons.Filled.Home, "caregiver_home"),
                    Triple("Pacientes", Icons.Filled.Groups, "caregiver_patients"),
                    Triple("Progreso", Icons.Filled.ShowChart, "caregiver_progress"),
                    Triple("Cuenta", Icons.Filled.Person, "caregiver_account")
                )

                val itemsMenu = if (rolUsuario == "profesional") {
                    itemsMenuProfesional
                } else {
                    itemsMenuCuidador
                }

                itemsMenu.forEach { item ->

                    NavigationBarItem(
                        selected = rutaActual == item.third,
                        onClick = {
                            onNavigate(item.third)
                        },
                        icon = {
                            Icon(
                                imageVector = item.second,
                                contentDescription = item.first
                            )
                        },
                        label = {
                            Text(
                                text = item.first,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = VerdeCuidador,
                            selectedTextColor = VerdeCuidador,
                            unselectedIconColor = Color(0xFF9CA3AF),
                            unselectedTextColor = Color(0xFF9CA3AF),
                            indicatorColor = VerdeClaroCuidador
                        )
                    )
                }
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(
                top = 20.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Text(
                    text = "Progreso",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Consultá el progreso del usuario asociado",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
            }

            // Días de racha
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            VerdeCuidador,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                Color.White.copy(alpha = 0.18f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalFireDepartment,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Racha actual",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "0 días",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "Días consecutivos realizando ejercicios",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Ejercicios realizados
            item {
                Text(
                    text = "Ejercicios realizados",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            BordeGrisCuidador,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                VerdeClaroCuidador,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = VerdeCuidador,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Ejercicios completados",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "Todavía no hay ejercicios registrados",
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280)
                        )
                    }

                    Text(
                        text = "—",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = VerdeCuidador
                    )
                }
            }

            // Ejercicios por día
            item {
                Text(
                    text = "Ejercicios por día",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            BordeGrisCuidador,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Actividad semanal",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val dias = listOf(
                        "Lun", "Mar", "Mié", "Jue",
                        "Vie", "Sáb", "Dom"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        dias.forEach { dia ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(
                                            Color(0xFFF3F4F6),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "0",
                                        fontSize = 11.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }

                                Spacer(modifier = Modifier.height(5.dp))

                                Text(
                                    text = dia,
                                    fontSize = 11.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }
                    }
                }
            }

            // Evaluación Kisdash
            item {
                Text(
                    text = "Evaluación de Quickdash",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            VerdeClaroCuidador,
                            RoundedCornerShape(16.dp)
                        )
                        .border(
                            1.dp,
                            Color(0xFFD5E5DA),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(18.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    Color.White,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Assessment,
                                contentDescription = null,
                                tint = VerdeCuidador,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Evaluación semanal",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = "El usuario puede realizarla semanalmente.",
                                fontSize = 13.sp,
                                color = Color(0xFF5A6B60)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Última evaluación",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "Sin evaluaciones realizadas",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onQuickDashClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdeCuidador
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Ver evaluación",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}