package com.example.pulsofirme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.EstadoPacienteTutor
import com.example.pulsofirme.PacienteDelTutor
import com.example.pulsofirme.rememberPacienteDelTutor
import kotlin.math.abs
import kotlin.math.roundToInt

private val VerdeTitulo = Color(0xFF1B7A4B)
private val AzulTexto = Color(0xFF16324F)
private val GrisSubtitulo = Color(0xFF6B7280)
private val VerdeClaroFondo = Color(0xFFEFF6F1)
private val VerdeClaro = Color(0xFFDFF3E4)
private val AzulClaro = Color(0xFFE3EEFB)
private val AzulTextoChip = Color(0xFF1D64C7)
private val AmarilloClaro = Color(0xFFFBF1D8)
private val AmarilloTexto = Color(0xFFB5850F)
private val RojoClaro = Color(0xFFFBE4E4)
private val RojoTexto = Color(0xFFC94A4A)
private val GrisMoradoFondo = Color(0xFFEFEDF4)

data class RespuestaQuickDash(
    val cantidad: Int,
    val titulo: String,
    val subtitulo: String,
    val colorFondo: Color,
    val colorTexto: Color
)

// Cómo cambió entre las dos últimas evaluaciones (más bajo = menos dificultad)
private enum class Tendencia { MEJORO, IGUAL, EMPEORO }

@Composable
fun QuickDashResultCaregiverScreen(
    onBack: () -> Unit,
    onVerDetalle: () -> Unit = {}
) {
    // Datos reales del paciente vinculado (antes era un ejemplo fijo)
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
            text = "Evaluación QuickDASH",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = AzulTexto
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Resultado del paciente",
            fontSize = 15.sp,
            color = GrisSubtitulo
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (estado !is EstadoPacienteTutor.Listo) {
            MensajeEstadoTutor(estado, onReintentar = reintentar)
            return@Column
        }

        val paciente = estado.paciente

        if (!paciente.comparteProgreso) {
            TarjetaNoCompartido(
                "${paciente.nombre} decidió no compartir su progreso, que incluye las evaluaciones QuickDASH."
            )
            return@Column
        }

        val actual = paciente.evaluaciones.getOrNull(0)
        if (actual == null) {
            TarjetaNoCompartido("${paciente.nombre} todavía no hizo ninguna evaluación QuickDASH.")
            return@Column
        }
        val anterior = paciente.evaluaciones.getOrNull(1)

        val puntajeActual = actual.puntaje.roundToInt()
        val puntajeAnterior = anterior?.puntaje?.roundToInt()
        val diferencia = puntajeAnterior?.let { puntajeActual - it }
        // Diferencias menores a 5 puntos se toman como "sin cambios"
        val tendencia = when {
            diferencia == null -> null
            diferencia <= -5 -> Tendencia.MEJORO
            diferencia >= 5 -> Tendencia.EMPEORO
            else -> Tendencia.IGUAL
        }

        // Paciente + fecha
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(VerdeClaro, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = paciente.nombre.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString(""),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdeTitulo
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(paciente.nombre, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = AzulTexto)
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
                Text(
                    PacienteDelTutor.textoFechaCorta(actual.fechaMs),
                    fontSize = 13.sp,
                    color = GrisSubtitulo,
                    textAlign = TextAlign.End
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Resultado principal
        val (icono, colorFondo, colorTexto) = when (tendencia) {
            Tendencia.MEJORO -> Triple(Icons.AutoMirrored.Filled.TrendingDown, VerdeClaro, VerdeTitulo)
            Tendencia.EMPEORO -> Triple(Icons.AutoMirrored.Filled.TrendingUp, RojoClaro, RojoTexto)
            Tendencia.IGUAL -> Triple(Icons.AutoMirrored.Filled.TrendingFlat, AzulClaro, AzulTextoChip)
            null -> Triple(Icons.Filled.Assessment, VerdeClaro, VerdeTitulo)
        }
        val titulo = when (tendencia) {
            Tendencia.MEJORO -> "El paciente mejoró"
            Tendencia.EMPEORO -> "Tiene más dificultad que antes"
            Tendencia.IGUAL -> "Se mantiene estable"
            null -> "Primera evaluación"
        }
        val descripcion = when (tendencia) {
            Tendencia.MEJORO ->
                "Tiene menos dificultad para usar sus manos que en la evaluación anterior."
            Tendencia.EMPEORO ->
                "Tiene más dificultad que en la evaluación anterior. Conviene comentarlo con su profesional."
            Tendencia.IGUAL ->
                "No hubo cambios importantes respecto de la evaluación anterior."
            null ->
                "Obtuvo $puntajeActual de 100 (${PacienteDelTutor.interpretacionQuickDash(actual.puntaje).lowercase()}). " +
                        "Con la próxima evaluación vas a poder ver si mejora."
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(colorFondo, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorTexto,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = titulo,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colorTexto,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = descripcion,
                fontSize = 15.sp,
                color = GrisSubtitulo,
                textAlign = TextAlign.Center,
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "En el QuickDASH, un puntaje más bajo significa menos dificultad.",
                fontSize = 12.sp,
                color = GrisSubtitulo,
                textAlign = TextAlign.Center
            )
        }

        // Resumen de respuestas (solo si la evaluación las tiene guardadas)
        if (actual.respuestas.isNotEmpty()) {
            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Resumen de respuestas",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = AzulTexto
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "De las ${actual.respuestas.size} preguntas del test:",
                fontSize = 14.sp,
                color = GrisSubtitulo
            )

            Spacer(modifier = Modifier.height(14.dp))

            val respuestas = listOf(
                RespuestaQuickDash(
                    actual.respuestas.count { it <= 0 },
                    "Sin dificultad", "Actividades que realiza bien", VerdeClaro, VerdeTitulo
                ),
                RespuestaQuickDash(
                    actual.respuestas.count { it == 1 },
                    "Dificultad leve", "Pequeñas limitaciones", AzulClaro, AzulTextoChip
                ),
                RespuestaQuickDash(
                    actual.respuestas.count { it == 2 },
                    "Dificultad moderada", "Limitación moderada", AmarilloClaro, AmarilloTexto
                ),
                RespuestaQuickDash(
                    actual.respuestas.count { it >= 3 },
                    "Dificultad severa", "Mayor limitación", RojoClaro, RojoTexto
                )
            )

            respuestas.forEach { respuesta ->
                FilaRespuestaQuickDash(respuesta)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Comparación (solo si hay una evaluación anterior)
        if (anterior != null && puntajeAnterior != null && diferencia != null) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Comparación con evaluación anterior",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = AzulTexto
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CajaPuntaje(
                    titulo = "Anterior",
                    puntaje = puntajeAnterior,
                    fecha = PacienteDelTutor.textoFechaCorta(anterior.fechaMs),
                    fondo = GrisMoradoFondo,
                    colorPuntaje = AzulTexto,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier.width(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("→", fontSize = 20.sp, color = GrisSubtitulo)
                }

                CajaPuntaje(
                    titulo = "Actual",
                    puntaje = puntajeActual,
                    fecha = PacienteDelTutor.textoFechaCorta(actual.fechaMs),
                    fondo = colorFondo,
                    colorPuntaje = colorTexto,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = when {
                    diferencia == 0 -> "Mismo puntaje que la vez anterior"
                    diferencia < 0 -> "${abs(diferencia)} puntos menos que la vez anterior"
                    else -> "$diferencia puntos más que la vez anterior"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colorTexto,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (actual.respuestas.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onVerDetalle,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeTitulo)
            ) {
                Text("Ver detalle de la evaluación", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun CajaPuntaje(
    titulo: String,
    puntaje: Int,
    fecha: String,
    fondo: Color,
    colorPuntaje: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(fondo, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = "Evaluación $titulo: $puntaje de 100, $fecha"
            }
    ) {
        Text(titulo, fontSize = 13.sp, color = GrisSubtitulo)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text("$puntaje", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = colorPuntaje)
            Text(" / 100", fontSize = 14.sp, color = GrisSubtitulo)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(fecha, fontSize = 12.sp, color = GrisSubtitulo)
    }
}

@Composable
private fun FilaRespuestaQuickDash(respuesta: RespuestaQuickDash) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(14.dp))
            .padding(vertical = 10.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = "${respuesta.titulo}: ${respuesta.cantidad} preguntas"
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(respuesta.colorFondo, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "${respuesta.cantidad}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = respuesta.colorTexto
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(respuesta.titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AzulTexto)
            Spacer(modifier = Modifier.height(2.dp))
            Text(respuesta.subtitulo, fontSize = 13.sp, color = GrisSubtitulo)
        }
    }
}