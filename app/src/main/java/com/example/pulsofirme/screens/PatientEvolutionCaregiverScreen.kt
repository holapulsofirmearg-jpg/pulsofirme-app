package com.example.pulsofirme.screens

import com.example.pulsofirme.ui.theme.enTema

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.EstadoPacienteTutor
import com.example.pulsofirme.PacienteDelTutor
import com.example.pulsofirme.rememberPacienteDelTutor
import kotlin.math.roundToInt

private val VerdeEvolucion: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
private val VerdeClaroEvolucion: Color
    @Composable get() = Color(0xFFEFF6F1).enTema()
private val BordeEvolucion: Color
    @Composable get() = Color(0xFFE5E7EB).enTema()
private val GrisEvolucion: Color
    @Composable get() = Color(0xFF6B7280).enTema()
@Composable
fun PatientEvolutionCaregiverScreen(
    onBack: () -> Unit
) {
    // Datos reales del paciente vinculado (antes eran valores fijos)
    val (estado, reintentar) = rememberPacienteDelTutor()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.enTema())
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(34.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }

            Text(
                text = "Evolución del paciente",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A).enTema()
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (estado !is EstadoPacienteTutor.Listo) {
            MensajeEstadoTutor(estado, onReintentar = reintentar)
            return@Column
        }

        val paciente = estado.paciente

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerdeClaroEvolucion, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.enTema(), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    tint = VerdeEvolucion
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = paciente.nombre,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Seguimiento de movilidad de la mano",
                    fontSize = 13.sp,
                    color = GrisEvolucion
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (!paciente.comparteProgreso) {
            TarjetaNoCompartido(
                "${paciente.nombre} decidió no compartir su progreso. " +
                        "Puede activarlo desde Cuenta → Privacidad y datos."
            )
            return@Column
        }

        Text(
            text = "Resumen de evolución",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Movilidad: sale de comparar las dos últimas evaluaciones QuickDASH
        // (más bajo = menos dificultad). Diferencias chicas se toman como "estable".
        val actual = paciente.evaluaciones.getOrNull(0)
        val anterior = paciente.evaluaciones.getOrNull(1)
        val (valorMovilidad, textoMovilidad) = when {
            actual == null -> "Sin datos" to
                    "Todavía no hizo ninguna evaluación QuickDASH."
            anterior == null -> PacienteDelTutor.interpretacionQuickDash(actual.puntaje) to
                    "Primera evaluación: ${actual.puntaje.roundToInt()} / 100. " +
                    "Con la próxima vas a poder ver si mejora."
            actual.puntaje <= anterior.puntaje - 5 -> "En mejora" to
                    "Bajó de ${anterior.puntaje.roundToInt()} a ${actual.puntaje.roundToInt()} puntos " +
                    "en el QuickDASH: tiene menos dificultad que antes."
            actual.puntaje >= anterior.puntaje + 5 -> "Necesita atención" to
                    "Subió de ${anterior.puntaje.roundToInt()} a ${actual.puntaje.roundToInt()} puntos " +
                    "en el QuickDASH: tiene más dificultad que antes. Conviene consultarlo con su profesional."
            else -> "Estable" to
                    "Pasó de ${anterior.puntaje.roundToInt()} a ${actual.puntaje.roundToInt()} puntos " +
                    "en el QuickDASH: sin cambios importantes."
        }

        TarjetaEvolucion(
            titulo = "Movilidad",
            valor = valorMovilidad,
            descripcion = textoMovilidad,
            icono = {
                Icon(
                    Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = VerdeEvolucion
                )
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        val hoy = PacienteDelTutor.deHoy(paciente.ejercicios)
        TarjetaEvolucion(
            titulo = "Ejercicios de hoy",
            valor = if (hoy.size == 1) "1 ejercicio" else "${hoy.size} ejercicios",
            descripcion = if (hoy.isEmpty()) "Todavía no registró ejercicios hoy."
            else "Último: ${hoy.first().nombre}.",
            icono = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = VerdeEvolucion
                )
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        val semana = PacienteDelTutor.deUltimosDias(paciente.ejercicios, 7)
        TarjetaEvolucion(
            titulo = "Tiempo de actividad",
            valor = PacienteDelTutor.textoMinutos(semana.sumOf { it.segundos }),
            descripcion = "Tiempo total de ejercicios en los últimos 7 días " +
                    "(${if (semana.size == 1) "1 ejercicio" else "${semana.size} ejercicios"}).",
            icono = {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color(0xFF1565C0).enTema()
                )
            }
        )

        Spacer(modifier = Modifier.height(44.dp))
    }
}

@Composable
private fun TarjetaEvolucion(
    titulo: String,
    valor: String,
    descripcion: String,
    icono: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BordeEvolucion, RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(VerdeClaroEvolucion, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            icono()
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                fontSize = 14.sp,
                color = GrisEvolucion
            )

            Text(
                text = valor,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A).enTema()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = descripcion,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = GrisEvolucion
            )
        }
    }
}