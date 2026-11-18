package com.example.pulsofirme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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

private val VerdeTitulo = Color(0xFF1B7A4B)
private val AzulTexto = Color(0xFF16324F)
private val GrisSubtitulo = Color(0xFF6B7280)
private val VerdeClaro = Color(0xFFDFF3E4)
private val VerdeTextoChip = Color(0xFF1B7A4B)
private val AzulClaro = Color(0xFFE3EEFB)
private val AzulTextoChip = Color(0xFF1D64C7)
private val AmarilloClaro = Color(0xFFFBF1D8)
private val AmarilloTexto = Color(0xFFB5850F)
private val RojoClaro = Color(0xFFFBE4E4)
private val RojoTexto = Color(0xFFC94A4A)
private val GrisDivider = Color(0xFFE5E7EB)

private enum class NivelDificultad(val etiqueta: String, val fondo: Color, val texto: Color) {
    NINGUNA("Ninguna dificultad", VerdeClaro, VerdeTextoChip),
    LEVE("Dificultad leve", AzulClaro, AzulTextoChip),
    MODERADA("Dificultad moderada", AmarilloClaro, AmarilloTexto),
    SEVERA("Dificultad severa", RojoClaro, RojoTexto)
}

private data class PreguntaQuickDash(
    val numero: Int,
    val texto: String,
    val nivel: NivelDificultad,
    // Lo que respondió el paciente, con las mismas palabras de la pregunta
    val respuesta: String
)



@Composable
fun QuickDashDetailCaregiverScreen(
    onBack: () -> Unit,
    onVolverAlResumen: () -> Unit = onBack
) {
    // Datos reales: la última evaluación QuickDASH del paciente vinculado
    val (estado, reintentar) = rememberPacienteDelTutor()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Detalle de la evaluación",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = AzulTexto
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "QuickDASH",
            fontSize = 15.sp,
            color = GrisSubtitulo
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (estado !is EstadoPacienteTutor.Listo) {
            MensajeEstadoTutor(estado, onReintentar = reintentar)
            return@Column
        }

        val paciente = estado.paciente
        val evaluacion = paciente.evaluaciones.firstOrNull()

        if (!paciente.comparteProgreso || evaluacion == null || evaluacion.respuestas.isEmpty()) {
            TarjetaNoCompartido(
                if (!paciente.comparteProgreso)
                    "${paciente.nombre} decidió no compartir su progreso, que incluye las evaluaciones QuickDASH."
                else "No hay respuestas guardadas de una evaluación QuickDASH."
            )
            return@Column
        }

        val nombrePaciente = paciente.nombre
        val fechaEvaluacion = PacienteDelTutor.textoFechaCorta(evaluacion.fechaMs)
        val preguntasQuickDash = PacienteDelTutor.preguntasQuickDash.mapIndexed { i, texto ->
            val respuesta = evaluacion.respuestas.getOrNull(i)
            PreguntaQuickDash(
                numero = i + 1,
                texto = texto,
                nivel = when {
                    respuesta == null || respuesta <= 0 -> NivelDificultad.NINGUNA
                    respuesta == 1 -> NivelDificultad.LEVE
                    respuesta == 2 -> NivelDificultad.MODERADA
                    else -> NivelDificultad.SEVERA
                },
                respuesta = respuesta?.let { PacienteDelTutor.textoRespuesta(i, it) } ?: "Sin respuesta"
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(VerdeClaro, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = nombrePaciente.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString(""),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdeTitulo
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(nombrePaciente, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = AzulTexto)
                Spacer(modifier = Modifier.height(2.dp))
                Text("Paciente vinculado", fontSize = 13.sp, color = GrisSubtitulo)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = GrisSubtitulo,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(fechaEvaluacion, fontSize = 13.sp, color = GrisSubtitulo)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        preguntasQuickDash.forEach { pregunta ->
            FilaPreguntaQuickDash(pregunta)
            HorizontalDivider(color = GrisDivider)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onVolverAlResumen,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeTitulo)
        ) {
            Text("Volver al resumen", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun FilaPreguntaQuickDash(pregunta: PreguntaQuickDash) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "${pregunta.numero}.",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AzulTexto,
            modifier = Modifier.width(26.dp)
        )

        Text(
            text = pregunta.texto,
            fontSize = 15.sp,
            color = AzulTexto,
            lineHeight = 20.sp,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .background(pregunta.nivel.fondo, RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = pregunta.respuesta,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = pregunta.nivel.texto
            )
        }
    }
}