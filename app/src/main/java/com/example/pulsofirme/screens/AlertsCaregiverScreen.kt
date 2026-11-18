package com.example.pulsofirme.screens

import com.example.pulsofirme.ui.theme.enTema

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
import com.example.pulsofirme.EstadoPacienteTutor
import com.example.pulsofirme.PacienteDelTutor
import com.example.pulsofirme.PacienteTutor
import com.example.pulsofirme.rememberPacienteDelTutor
import java.util.Calendar
import kotlin.math.roundToInt

private val VerdeCuidador = Color(0xFF1B7A4B)
private val VerdeClaroCuidador: Color
    @Composable get() = Color(0xFFEFF6F1).enTema()
private val BordeGrisCuidador: Color
    @Composable get() = Color(0xFFE5E7EB).enTema()
private data class AlertaCuidador(
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector,
    val color: Color
)

private val RojoAlerta = Color(0xFFC94A4A)
private val AmbarAlerta = Color(0xFFB5850F)
private val AzulAlerta = Color(0xFF1D64C7)
private val GrisAlerta = Color(0xFF6B7280)

// Alertas reales a partir de los datos del paciente (mismas reglas que los avisos en segundo plano).
// "importante" = algo que conviene revisar; el resto es información.
private data class AlertaCalculada(val alerta: AlertaCuidador, val importante: Boolean)

private fun alertasDe(paciente: PacienteTutor): List<AlertaCalculada> {
    val lista = mutableListOf<AlertaCalculada>()
    val nombre = paciente.nombre

    if (paciente.medicacionPendiente.isNotEmpty()) {
        val horarios = paciente.medicacionPendiente.joinToString(", ")
        lista += AlertaCalculada(
            AlertaCuidador(
                titulo = "No registró su medicación de hoy",
                descripcion = "Pasó más de una hora y no marcó \"Ya la tomé\" en: $horarios.",
                icono = Icons.Filled.Medication,
                color = RojoAlerta
            ),
            importante = true
        )
    }

    if (!paciente.comparteProgreso) {
        lista += AlertaCalculada(
            AlertaCuidador(
                titulo = "$nombre no comparte su progreso",
                descripcion = "Las alertas de ejercicios y evaluaciones aparecen si lo activa en Privacidad y datos.",
                icono = Icons.Filled.Lock,
                color = GrisAlerta
            ),
            importante = false
        )
        return lista
    }

    if (paciente.sensacionHoy?.contains("Dolor", ignoreCase = true) == true) {
        lista += AlertaCalculada(
            AlertaCuidador(
                titulo = "Registró dolor hoy",
                descripcion = "Indicó \"${paciente.sensacionHoy}\". Si el dolor sigue o aumenta, conviene consultar a su profesional.",
                icono = Icons.Filled.Healing,
                color = AmbarAlerta
            ),
            importante = true
        )
    }

    val hoy = PacienteDelTutor.deHoy(paciente.ejercicios)
    val diasSin = PacienteDelTutor.diasSinEjercicio(paciente.ejercicios)
    val hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    when {
        diasSin == null -> lista += AlertaCalculada(
            AlertaCuidador(
                titulo = "Todavía no registró ejercicios",
                descripcion = "Cuando $nombre complete su primer ejercicio, lo vas a ver acá.",
                icono = Icons.Filled.FitnessCenter,
                color = AmbarAlerta
            ),
            importante = true
        )
        diasSin >= 3 -> lista += AlertaCalculada(
            AlertaCuidador(
                titulo = "Lleva $diasSin días sin hacer ejercicio",
                descripcion = "Puede ser un buen momento para acompañarlo/a en una rutina corta.",
                icono = Icons.Filled.FitnessCenter,
                color = AmbarAlerta
            ),
            importante = true
        )
        hoy.isEmpty() && hora >= 18 -> lista += AlertaCalculada(
            AlertaCuidador(
                titulo = "Todavía no hizo ejercicio hoy",
                descripcion = "Un mensajito para recordárselo puede ayudar a mantener la constancia.",
                icono = Icons.Filled.FitnessCenter,
                color = AmbarAlerta
            ),
            importante = true
        )
    }

    val actual = paciente.evaluaciones.getOrNull(0)
    val anterior = paciente.evaluaciones.getOrNull(1)
    if (actual != null && System.currentTimeMillis() - actual.fechaMs <= 7L * 24 * 60 * 60 * 1000) {
        val empeoro = anterior != null && actual.puntaje >= anterior.puntaje + 5
        lista += AlertaCalculada(
            AlertaCuidador(
                titulo = if (empeoro) "Su evaluación QuickDASH empeoró" else "Completó su evaluación QuickDASH",
                descripcion = "${PacienteDelTutor.textoHace(actual.fechaMs)}: ${actual.puntaje.roundToInt()} de 100 " +
                        "(más bajo = menos dificultad)." +
                        if (empeoro) " Conviene comentarlo con su profesional." else "",
                icono = Icons.Filled.Assessment,
                color = if (empeoro) RojoAlerta else AzulAlerta
            ),
            importante = empeoro
        )
    }

    if (hoy.isNotEmpty()) {
        lista += AlertaCalculada(
            AlertaCuidador(
                titulo = if (hoy.size == 1) "Hizo 1 ejercicio hoy" else "Hizo ${hoy.size} ejercicios hoy",
                descripcion = "Último: ${hoy.first().nombre}.",
                icono = Icons.Filled.CheckCircle,
                color = VerdeCuidador
            ),
            importante = false
        )
    }

    return lista
}

@Composable
fun AlertsCaregiverScreen(
    rutaActual: String,
    onNavigate: (String) -> Unit,
    onConfigClick: () -> Unit = {}
) {
    // Alertas reales del paciente vinculado (antes siempre decía "Sin alertas nuevas")
    val (estado, reintentar) = rememberPacienteDelTutor()
    val calculadas = (estado as? EstadoPacienteTutor.Listo)?.paciente?.let { alertasDe(it) }
    val alertasCuidador = calculadas?.map { it.alerta }?.ifEmpty {
        listOf(
            AlertaCuidador(
                titulo = "Sin alertas nuevas",
                descripcion = "No hay nada para revisar por ahora.",
                icono = Icons.Filled.CheckCircle,
                color = VerdeCuidador
            )
        )
    }.orEmpty()
    val importantes = calculadas?.count { it.importante } ?: 0

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White.enTema()
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
                            unselectedIconColor = Color(0xFF9CA3AF).enTema(),
                            unselectedTextColor = Color(0xFF9CA3AF).enTema(),
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
                    color = Color(0xFF1A1A1A).enTema()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Consultá las alertas del usuario asociado",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280).enTema()
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
                            Color(0xFFD5E5DA).enTema(),
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
                                color = Color(0xFF1A1A1A).enTema()
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = when {
                                    estado !is EstadoPacienteTutor.Listo ->
                                        "Acá podés consultar si el usuario tiene alguna alerta."
                                    importantes == 0 -> "Todo en orden: no hay nada urgente para revisar."
                                    importantes == 1 -> "Hay 1 alerta para revisar."
                                    else -> "Hay $importantes alertas para revisar."
                                },
                                fontSize = 13.sp,
                                color = Color(0xFF5A6B60).enTema()
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
                    color = Color(0xFF1A1A1A).enTema()
                )
            }

            if (estado !is EstadoPacienteTutor.Listo) {
                item { MensajeEstadoTutor(estado, onReintentar = reintentar) }
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
                            color = Color(0xFF1A1A1A).enTema()
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = alerta.descripcion,
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280).enTema()
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
                                    Color(0xFFF3F4F6).enTema(),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = null,
                                tint = Color(0xFF6B7280).enTema(),
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
                                color = Color(0xFF1A1A1A).enTema()
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = "Configurá cómo recibir las alertas del usuario.",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280).enTema()
                            )
                        }

                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF).enTema()
                        )
                    }
                }
            }
        }
    }
}