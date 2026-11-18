package com.example.pulsofirme.screens

import androidx.compose.runtime.saveable.rememberSaveable

import com.example.pulsofirme.ui.theme.enTema

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.R

// Verde (Defecto / Rigidez u otras)
private val VerdeEn: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
private val VerdeOscuroEn: Color
    @Composable get() = Color(0xFF1B4332).enTema()
private val VerdeSuaveEn: Color
    @Composable get() = Color(0xFFEAF5EC).enTema()
// Ámbar / Miel (Para Hinchazón)
private val AmarilloEn = Color(0xFFC99700)      // Miel / ámbar seco
private val AmarilloOscuroEn = Color(0xFF4D3800) // Bronce oscuro
private val AmarilloSuaveEn = Color(0xFFFFF8E7)  // Amarillo pastel muy claro

// Azul (Exclusivo para Dolor)
private val AzulEn = Color(0xFF1D68C8)        // Azul principal
private val AzulOscuroEn = Color(0xFF0F3B7A)  // Azul noche para títulos
private val AzulSuaveEn = Color(0xFFEBF3FC)   // Azul pastel muy suave

private val BordeEn: Color
    @Composable get() = Color(0xFFE5E7EB).enTema()
private val GrisEn: Color
    @Composable get() = Color(0xFF6B7280).enTema()
private val NegroEn: Color
    @Composable get() = Color(0xFF1A1A1A).enTema()
private data class PreguntaEncuesta(
    val titulo: String,
    val ayuda: String,
    val opciones: List<String>
)

private fun preguntasPorSintoma(sensacion: String): List<PreguntaEncuesta> {
    return when {
        sensacion.contains("Rigidez", ignoreCase = true) -> listOf(
            PreguntaEncuesta(
                titulo = "¿Cuánta rigidez sentís en las manos?",
                ayuda = "La sensación de que cuesta abrir o mover los dedos.",
                opciones = listOf("Ninguna", "Baja", "Media", "Alta")
            ),
            PreguntaEncuesta(
                titulo = "¿En qué momento del día fue más notoria?",
                ayuda = "El descanso influye directamente en la rigidez.",
                opciones = listOf("Al despertar", "Por la tarde", "Por la noche", "Todo el día")
            ),
            PreguntaEncuesta(
                titulo = "¿Con cuánta energía te sentís ahora?",
                ayuda = "Para ajustar la duración recomendada.",
                opciones = listOf("Con mucha energía", "Normal", "Un poco cansado/a", "Muy cansado/a")
            )
        )
        sensacion.contains("Dolor", ignoreCase = true) -> listOf(
            PreguntaEncuesta(
                titulo = "¿Tuviste dolor en manos o muñecas?",
                ayuda = "Contá también las molestias al mover o presionar la zona.",
                opciones = listOf("Sin dolor", "Leve", "Moderado", "Fuerte")
            ),
            PreguntaEncuesta(
                titulo = "¿El dolor te impide sostener objetos con normalidad?",
                ayuda = "Evaluamos si requerís un movimiento asistido o suave.",
                opciones = listOf("Para nada", "Un poco", "Bastante", "Mucho")
            ),
            PreguntaEncuesta(
                titulo = "¿Cómo dormiste anoche?",
                ayuda = "El dolor suele influir en la calidad del sueño.",
                opciones = listOf("Muy bien", "Bien", "Regular", "Mal")
            )
        )
        sensacion.contains("Hinchaz", ignoreCase = true) -> listOf(
            PreguntaEncuesta(
                titulo = "¿Sentiste las manos hinchadas hoy?",
                ayuda = "Por ejemplo, si te costó sacarte un anillo o cerrar el puño.",
                opciones = listOf("Nada hinchadas", "Un poco", "Bastante", "Mucho")
            ),
            PreguntaEncuesta(
                titulo = "¿Presentás sensación de calor o enrojecimiento?",
                ayuda = "Indica si la zona requiere un enfoque de movilidad más lento.",
                opciones = listOf("No", "Leve", "Moderado", "Bastante")
            ),
            PreguntaEncuesta(
                titulo = "¿En qué momento del día notaste más la hinchazón?",
                ayuda = "Para ajustar la recomendación de horario de tus ejercicios.",
                opciones = listOf("Al despertar", "Por la tarde", "Por la noche", "Durante todo el día")
            )
        )
        else -> listOf(
            PreguntaEncuesta(
                titulo = "¿Cómo te sentís en general hoy?",
                ayuda = "Evaluación rápida para tu rutina diaria.",
                opciones = listOf("Muy bien", "Bien", "Regular", "Con molestias")
            ),
            PreguntaEncuesta(
                titulo = "¿Con cuánta energía te encontrás?",
                ayuda = "Para definir el nivel de la sesión.",
                opciones = listOf("Mucha energía", "Normal", "Cansado/a", "Sin energía")
            )
        )
    }
}

private fun ejercicioRecomendadoDinamico(sensacion: String, respuestas: List<Int?>): Pair<String, String> {
    val r0 = respuestas.getOrNull(0) ?: 0

    return when {
        sensacion.contains("Dolor", ignoreCase = true) && r0 >= 2 ->
            "Movilidad de muñecas" to "Como marcaste un dolor moderado o alto, lo ideal es trabajar primero un movimiento suave y controlled de muñeca."

        sensacion.contains("Rigidez", ignoreCase = true) && r0 >= 1 ->
            "Extensión de dedos" to "Para reducir la rigidez que indicaste, abrí y cerrá la mano de manera pausada con este ejercicio."

        sensacion.contains("Hinchaz", ignoreCase = true) && r0 >= 2 ->
            "Movilidad de muñecas" to "Con la hinchazón que presentás hoy, se recomienda evitar la sobrecarga y realizar movimientos de bajo impacto."

        else ->
            "Extensión de dedos" to "Tu nivel actual permite realizar una sesión estándar para mantener la movilidad activa de tus manos."
    }
}

@Composable
fun SurveyScreen(
    sensacionInicial: String,
    onBack: () -> Unit,
    onEmpezarEjercicio: (String) -> Unit,
    onSaltear: () -> Unit
) {
    val colorPrincipal = when {
        sensacionInicial.contains("Dolor", ignoreCase = true) -> AzulEn
        sensacionInicial.contains("Hinchaz", ignoreCase = true) -> AmarilloEn
        else -> VerdeEn
    }

    val colorOscuro = when {
        sensacionInicial.contains("Dolor", ignoreCase = true) -> AzulOscuroEn
        sensacionInicial.contains("Hinchaz", ignoreCase = true) -> AmarilloOscuroEn
        else -> VerdeOscuroEn
    }

    val colorSuave = when {
        sensacionInicial.contains("Dolor", ignoreCase = true) -> AzulSuaveEn
        sensacionInicial.contains("Hinchaz", ignoreCase = true) -> AmarilloSuaveEn
        else -> VerdeSuaveEn
    }

    val preguntas = remember(sensacionInicial) { preguntasPorSintoma(sensacionInicial) }

    val respuestas = remember(sensacionInicial) {
        mutableStateListOf<Int?>().apply {
            repeat(preguntas.size) { add(null) }
        }
    }

    var indice by remember { mutableIntStateOf(0) }
    var mostrarResultado by rememberSaveable { mutableStateOf(false) }

    if (mostrarResultado) {
        val (ejercicio, motivo) = ejercicioRecomendadoDinamico(sensacionInicial, respuestas)

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.enTema())
        ) {
            val minHeight = maxHeight

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .heightIn(min = minHeight),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(48.dp))

                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .clip(CircleShape)
                        .background(colorSuave),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pulgar4),
                        contentDescription = "Pulgar arriba",
                        modifier = Modifier.size(52.dp)
                    )
                }

                val imagenPulgar = when {
                    sensacionInicial.contains("Dolor", ignoreCase = true) -> R.drawable.pulgar3 // Botón 3 (Dolor)
                    sensacionInicial.contains("Hinchaz", ignoreCase = true) -> R.drawable.pulgar4 // Botón 2 (Hinchazón)
                    else -> R.drawable.pulgar3 // Botón 1 o por defecto (Rigidez)
                }
                Spacer(Modifier.height(20.dp))

                Text(
                    text = "¡Gracias por completarlo!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorOscuro,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Analizamos tu nivel de ${sensacionInicial.lowercase()} para definir la actividad recomendada.",
                    fontSize = 15.sp,
                    color = GrisEn,
                    textAlign = TextAlign.Center,
                    lineHeight = 21.sp
                )

                Spacer(Modifier.height(26.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(colorPrincipal)
                        .padding(22.dp)
                ) {
                    Text(
                        "Ejercicio recomendado",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = ejercicio,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = motivo,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { onEmpezarEjercicio(ejercicio) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorPrincipal)
                ) {
                    Icon(
                        Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Empezar el ejercicio", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(12.dp))

                TextButton(onClick = onSaltear) {
                    Text("Ahora no, volver al inicio", fontSize = 15.sp, color = GrisEn)
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Esta recomendación es orientativa y no reemplaza la indicación de tu profesional de salud.",
                    fontSize = 12.sp,
                    color = GrisEn,
                    lineHeight = 17.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))
            }
        }
        return
    }

    val pregunta = preguntas[indice]
    val respuestaActual = respuestas[indice]
    val porcentaje = (indice + 1).toFloat() / preguntas.size.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.enTema())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(48.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                if (indice > 0) indice -= 1 else onBack()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = colorPrincipal
                )
            }
            Spacer(Modifier.width(4.dp))
            Column {
                Text("Registro diario", fontSize = 13.sp, color = GrisEn)
                Text(
                    text = sensacionInicial,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorOscuro
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Pregunta ${indice + 1} de ${preguntas.size}",
                fontSize = 14.sp,
                color = GrisEn
            )
            Text(text = "${(porcentaje * 100).toInt()}%", fontSize = 14.sp, color = GrisEn)
        }

        Spacer(Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { porcentaje },
            modifier = Modifier
                .fillMaxWidth()
                .height(9.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = colorPrincipal,
            trackColor = Color(0xFFF0F1F3).enTema()
        )

        Spacer(Modifier.height(26.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = pregunta.titulo,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = NegroEn,
                lineHeight = 28.sp
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = pregunta.ayuda,
                fontSize = 14.sp,
                color = GrisEn,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(20.dp))

            Image(
                painter = painterResource(id = R.drawable.pulgar3),
                contentDescription = "Pulgar arriba",
                modifier = Modifier.size(92.dp)
            )
            pregunta.opciones.forEachIndexed { i, opcion ->
                val elegida = respuestaActual == i
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (elegida) colorSuave else Color.White.enTema())
                        .border(
                            width = if (elegida) 2.dp else 1.dp,
                            color = if (elegida) colorPrincipal else BordeEn,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { respuestas[indice] = i }
                        .padding(horizontal = 18.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = opcion,
                        fontSize = 16.sp,
                        color = if (elegida) colorOscuro else Color(0xFF374151).enTema(),
                        fontWeight = if (elegida) FontWeight.SemiBold else FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                    if (elegida) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = colorPrincipal,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (indice < preguntas.size - 1) indice += 1
                else mostrarResultado = true
            },
            enabled = respuestaActual != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorPrincipal,
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFDCDCDC).enTema(),
                disabledContentColor = Color(0xFF8A8A8A).enTema()
            )
        ) {
            Text(
                text = if (indice < preguntas.size - 1) "Siguiente" else "Ver recomendación",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = onSaltear,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Completar más tarde", fontSize = 14.sp, color = GrisEn)
        }

        Spacer(Modifier.height(24.dp))
    }
}