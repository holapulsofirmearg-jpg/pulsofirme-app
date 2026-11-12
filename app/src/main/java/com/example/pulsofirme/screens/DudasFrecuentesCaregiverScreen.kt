package com.example.pulsofirme.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.screens.ItemMenuCuidadorCompartido

private val VerdeTitulo = Color(0xFF1B7A4B)
private val GrisSubtitulo = Color(0xFF6B7280)
private val AzulTexto = Color(0xFF16324F)
private val CelesteFondo = Color(0xFFF3F8FA)
private val CelesteFondoActivo = Color(0xFFE5F1F5)
private val VerdeIconoFondo = Color(0xFFE2F4E6)
private val BordeActivo = Color(0xFF1B7A4B).copy(alpha = 0.3f)

private data class Pregunta(
    val icono: ImageVector,
    val titulo: String,
    val respuesta: String
)

private val preguntasFrecuentes = listOf(
    Pregunta(
        icono = Icons.Filled.Person,
        titulo = "¿Cómo sé si el paciente completó sus ejercicios del día?",
        respuesta = "En la pestaña Progreso podés ver el registro en tiempo real de las rutinas realizadas, la duración y los indicadores detectados por la app o el guante."
    ),
    Pregunta(
        icono = Icons.Filled.Notifications,
        titulo = "¿Puedo vincular a mi médico o cuidador?",
        respuesta = "Sí, podés compartir el acceso a la cuenta con otros profesionales de salud o familiares desde la sección Cuenta."
    ),
    Pregunta(
        icono = Icons.Filled.CalendarMonth,
        titulo = "¿Dónde puedo ver el historial de actividades del paciente?",
        respuesta = "En la sección Progreso vas a encontrar un resumen completo de las rutinas, la duración y la evolución a lo largo del tiempo."
    ),
    Pregunta(
        icono = Icons.Filled.ChatBubble,
        titulo = "¿Puedo recibir notificaciones importantes como cuidador?",
        respuesta = "Sí, vas a recibir avisos sobre la actividad del paciente, recordatorios y resultados relevantes en el ícono de la campana."
    ),
    Pregunta(
        icono = Icons.Filled.Help,
        titulo = "¿Cómo puedo contactar al soporte de Pulso Firme?",
        respuesta = "Podés hacerlo desde la sección Cuenta, donde vas a encontrar el botón de ayuda o soporte."
    )
)

@Composable
fun DudasFrecuentesCaregiverScreen(
    rutaActual: String,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                ItemMenuCuidadorCompartido.items.forEach { item ->
                    NavigationBarItem(
                        selected = rutaActual == item.ruta,
                        onClick = { onNavigate(item.ruta) },
                        icon = { Icon(item.icono, contentDescription = item.label) },
                        label = { Text(item.label, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = VerdeTitulo,
                            selectedTextColor = VerdeTitulo,
                            unselectedIconColor = Color(0xFF9CA3AF),
                            unselectedTextColor = Color(0xFF9CA3AF),
                            indicatorColor = VerdeIconoFondo
                        )
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFFF3F4F6))
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color(0xFF1F2937),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Dudas frecuentes",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeTitulo,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Encontrá respuestas rápidas a las consultas más comunes sobre el uso de Pulso Firme como cuidador.",
                fontSize = 14.sp,
                color = GrisSubtitulo,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            preguntasFrecuentes.forEach { pregunta ->
                TarjetaPreguntaMejorada(pregunta)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TarjetaPreguntaMejorada(pregunta: Pregunta) {
    var expandido by remember { mutableStateOf(false) }

    val rotacionFlecha by animateFloatAsState(
        targetValue = if (expandido) 180f else 0f,
        animationSpec = tween(durationMillis = 250),
        label = "RotacionFlecha"
    )

    val fondoColor by animateColorAsState(
        targetValue = if (expandido) CelesteFondoActivo else CelesteFondo,
        animationSpec = tween(durationMillis = 200),
        label = "FondoColor"
    )

    val bordeColor by animateColorAsState(
        targetValue = if (expandido) BordeActivo else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "BordeColor"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.5.dp, bordeColor, RoundedCornerShape(20.dp))
            .clickable { expandido = !expandido },
        color = fondoColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .animateContentSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(VerdeIconoFondo, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = pregunta.icono,
                        contentDescription = null,
                        tint = VerdeTitulo,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = pregunta.titulo,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AzulTexto,
                    lineHeight = 20.sp,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = if (expandido) "Contraer" else "Expandir",
                    tint = Color(0xFF6B7280),
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotacionFlecha)
                )
            }

            AnimatedVisibility(
                visible = expandido,
                enter = fadeIn(animationSpec = tween(200)),
                exit = fadeOut(animationSpec = tween(150))
            ) {
                Column {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        thickness = 1.dp,
                        color = Color(0xFFD1E3EA)
                    )
                    Text(
                        text = pregunta.respuesta,
                        fontSize = 13.5.sp,
                        color = Color(0xFF374151),
                        lineHeight = 19.5.sp
                    )
                }
            }
        }
    }
}