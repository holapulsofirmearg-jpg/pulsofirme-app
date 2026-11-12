package com.example.pulsofirme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeCuidador = Color(0xFF1B7A4B)
private val VerdeClaroCuidador = Color(0xFFEFF6F1)
private val BordeGrisCuidador = Color(0xFFE5E7EB)

private data class EjercicioCuidador(
    val nombre: String,
    val descripcion: String,
    val duracion: String,
    val icono: ImageVector
)

private val ejerciciosCuidador = listOf(
    EjercicioCuidador(
        nombre = "Extensión de dedos",
        descripcion = "Abrir y cerrar los dedos de la mano",
        duracion = "2 min",
        icono = Icons.Filled.PanTool
    ),
    EjercicioCuidador(
        nombre = "Flexión de muñeca",
        descripcion = "Movimientos suaves de la muñeca",
        duracion = "3 min",
        icono = Icons.Filled.Swipe
    ),
    EjercicioCuidador(
        nombre = "Movimiento de dedos",
        descripcion = "Movilización individual de los dedos",
        duracion = "2 min",
        icono = Icons.Filled.TouchApp
    ),
    EjercicioCuidador(
        nombre = "Apertura de mano",
        descripcion = "Abrir la mano lentamente y mantener",
        duracion = "3 min",
        icono = Icons.Filled.BackHand
    )
)

@Composable
fun ExercisesCaregiverScreen(
    rutaActual: String,
    onNavigate: (String) -> Unit,
    onEjercicioClick: (String) -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {

                // MENÚ DEL CUIDADOR: SOLO 4 ÍTEMS
                val itemsMenuCuidador = listOf(
                    Triple(
                        "Inicio",
                        Icons.Filled.Home,
                        "caregiver_home"
                    ),
                    Triple(
                        "Progreso",
                        Icons.Filled.ShowChart,
                        "caregiver_progress"
                    ),
                    Triple(
                        "Alertas",
                        Icons.Filled.Notifications,
                        "caregiver_alerts"
                    ),
                    Triple(
                        "Cuenta",
                        Icons.Filled.Person,
                        "caregiver_account"
                    )
                )

                itemsMenuCuidador.forEach { item ->

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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            item {
                Text(
                    text = "Ejercicios",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Ejercicios disponibles para el usuario",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))

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
                        .padding(16.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    VerdeCuidador,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {

                            Text(
                                text = "Ejercicios del usuario",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1A1A1A)
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "Desde acá podés consultar sus ejercicios.",
                                fontSize = 13.sp,
                                color = Color(0xFF5A6B60)
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Rutina de ejercicios",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
            }

            items(ejerciciosCuidador) { ejercicio ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            BordeGrisCuidador,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            onEjercicioClick(ejercicio.nombre)
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                VerdeClaroCuidador,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = ejercicio.icono,
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
                            text = ejercicio.nombre,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A1A1A)
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = ejercicio.descripcion,
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280)
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = ejercicio.duracion,
                            fontSize = 12.sp,
                            color = VerdeCuidador,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Ver ejercicio",
                        tint = Color(0xFF9CA3AF)
                    )
                }
            }
        }
    }
}