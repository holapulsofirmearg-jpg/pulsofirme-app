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

private data class AlertaCuidador(
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector,
    val color: Color
)

private val alertasCuidador = listOf(
    AlertaCuidador(
        titulo = "Sin alertas nuevas",
        descripcion = "No hay alertas pendientes del usuario.",
        icono = Icons.Filled.CheckCircle,
        color = VerdeCuidador
    )
)

@Composable
fun AlertsCaregiverScreen(
    rutaActual: String,
    onNavigate: (String) -> Unit,
    onConfigClick: () -> Unit = {}
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Text(
                    text = "Alertas",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Consultá las alertas del usuario asociado",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
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
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    VerdeCuidador,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Estado de alertas",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1A1A1A)
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = "Acá podés consultar si el usuario tiene alguna alerta.",
                                fontSize = 13.sp,
                                color = Color(0xFF5A6B60)
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Alertas recientes",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
            }

            items(alertasCuidador) { alerta ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            BordeGrisCuidador,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                VerdeClaroCuidador,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = alerta.icono,
                            contentDescription = null,
                            tint = alerta.color,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = alerta.titulo,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A1A1A)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = alerta.descripcion,
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(2.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            BordeGrisCuidador,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            onConfigClick()
                        }
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    Color(0xFFF3F4F6),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = null,
                                tint = Color(0xFF6B7280),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Configuración de alertas",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1A1A1A)
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = "Configurá cómo recibir las alertas del usuario.",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280)
                            )
                        }

                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF)
                        )
                    }
                }
            }
        }
    }
}