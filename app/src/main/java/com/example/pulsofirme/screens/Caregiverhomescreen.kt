package com.example.pulsofirme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ============================================================
// COLORES
// ============================================================

private val VerdePrincipal = Color(0xFF16834E)
private val VerdeClaroFondo = Color(0xFFE8F5EC) // tarjetas verde muy claro
private val GrisTexto = Color(0xFF6B7280)
private val NegroTexto = Color(0xFF17202A)

// ============================================================
// PANTALLA DE INICIO DEL CUIDADOR
// ============================================================

@Composable
fun CaregiverHomeScreen(
    rutaActual: String,
    nombreCuidador: String = "Cuidador",
    nombrePaciente: String = "Morena Martínez",
    condicion: String = "Parkinson",
    horaEjercicio: String = "Hoy, 09:30 AM",
    adherenciaSemanal: String = "85%",
    tiempoGuante: String = "2h 35m",
    codigoVinculacion: String = "482 913",
    onNavigate: (String) -> Unit,
    onVerProgreso: () -> Unit = {},
    onVerCuantoUso: () -> Unit = {},
    onCopiarCodigo: () -> Unit = {}
) {
    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                containerColor = Color.White
            ) {
                NavigationBarItem(
                    selected = rutaActual == "caregiver_home",
                    onClick = { onNavigate("caregiver_home") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    colors = navItemColors()
                )
                NavigationBarItem(
                    selected = rutaActual == "caregiver_progress",
                    onClick = { onNavigate("caregiver_progress") },
                    icon = { Icon(Icons.Default.ShowChart, contentDescription = "Progreso") },
                    label = { Text("Progreso") },
                    colors = navItemColors()
                )
                NavigationBarItem(
                    selected = rutaActual == "caregiver_alerts",
                    onClick = { onNavigate("caregiver_alerts") },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Alertas") },
                    label = { Text("Alertas") },
                    colors = navItemColors()
                )
                NavigationBarItem(
                    selected = rutaActual == "caregiver_account",
                    onClick = { onNavigate("caregiver_account") },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Cuenta") },
                    label = { Text("Cuenta") },
                    colors = navItemColors()
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            // =================================================
            // HEADER VERDE
            // =================================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VerdePrincipal)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Panel de cuidador",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
                            tint = Color.White
                        )
                    }
                }
                Text(
                    text = "Hola, $nombreCuidador",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {

                Spacer(Modifier.height(16.dp))

                // =================================================
                // TARJETA "ESTÁS VIENDO LOS DATOS DE"
                // =================================================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = VerdeClaroFondo)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = VerdePrincipal
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Estás viendo los datos de",
                                fontSize = 12.sp,
                                color = GrisTexto
                            )
                            Text(
                                text = nombrePaciente,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NegroTexto
                            )
                            Text(
                                text = condicion,
                                fontSize = 13.sp,
                                color = GrisTexto
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // =================================================
                // RESUMEN DE HOY
                // =================================================
                Text(
                    text = "Resumen de hoy",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NegroTexto
                )
                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ResumenCard(
                        modifier = Modifier.weight(1f),
                        icono = Icons.Default.AccessTime,
                        iconoFondo = Color.White,
                        iconoBorde = true,
                        valor = horaEjercicio,
                        etiqueta = "Último ejercicio"
                    )
                    ResumenCard(
                        modifier = Modifier.weight(1f),
                        icono = Icons.Default.Check,
                        iconoFondo = VerdePrincipal,
                        iconoBorde = false,
                        valor = adherenciaSemanal,
                        etiqueta = "Adherencia semanal"
                    )
                }

                Spacer(Modifier.height(12.dp))

                // =================================================
                // CUÁNTO USÓ EL GUANTE
                // =================================================
                AccionCard(
                    icono = Icons.Default.PanTool,
                    titulo = "Cuánto usó el guante",
                    subtitulo = tiempoGuante,
                    subtituloGrande = true,
                    onClick = onVerCuantoUso
                )

                Spacer(Modifier.height(20.dp))

                // =================================================
                // CUESTIONARIO
                // =================================================
                Text(
                    text = "Cuestionario",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NegroTexto
                )
                Spacer(Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(VerdePrincipal, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "No reportó dolor",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = NegroTexto
                            )
                            Text(
                                text = "Dolor asociado a Parkinson — hoy",
                                fontSize = 13.sp,
                                color = VerdePrincipal
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // =================================================
                // ACCESOS RÁPIDOS
                // =================================================
                Text(
                    text = "Accesos rápidos",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NegroTexto
                )
                Spacer(Modifier.height(10.dp))

                AccionCard(
                    icono = Icons.Default.TrendingUp,
                    titulo = "Ver progreso completo",
                    subtitulo = "Historial, gráficos y QuickDASH",
                    onClick = onVerProgreso
                )

                Spacer(Modifier.height(12.dp))

                // Código de vinculación
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = VerdeClaroFondo)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Link,
                                    contentDescription = null,
                                    tint = VerdePrincipal
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Tu código de vinculación",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VerdePrincipal
                                )
                                Text(
                                    text = codigoVinculacion,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NegroTexto
                                )
                            }
                        }
                        IconButton(onClick = onCopiarCodigo) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copiar código",
                                tint = VerdePrincipal
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

// ============================================================
// COMPONENTES REUTILIZABLES
// ============================================================

@Composable
private fun ResumenCard(
    modifier: Modifier = Modifier,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    iconoFondo: Color,
    iconoBorde: Boolean,
    valor: String,
    etiqueta: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = if (iconoBorde) Color.White else iconoFondo,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = if (iconoBorde) VerdePrincipal else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = valor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = NegroTexto
            )
            Text(
                text = etiqueta,
                fontSize = 12.sp,
                color = GrisTexto
            )
        }
    }
}

@Composable
private fun AccionCard(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    subtitulo: String,
    subtituloGrande: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VerdeClaroFondo)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = VerdePrincipal
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = titulo,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NegroTexto
                    )
                    Text(
                        text = subtitulo,
                        fontSize = if (subtituloGrande) 18.sp else 13.sp,
                        fontWeight = if (subtituloGrande) FontWeight.Bold else FontWeight.Normal,
                        color = if (subtituloGrande) VerdePrincipal else GrisTexto
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = GrisTexto
            )
        }
    }
}

@Composable
private fun navItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = VerdePrincipal,
    selectedTextColor = VerdePrincipal,
    indicatorColor = VerdeClaroFondo,
    unselectedIconColor = GrisTexto,
    unselectedTextColor = GrisTexto
)