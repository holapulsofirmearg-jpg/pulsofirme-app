package com.example.pulsofirme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.DificultadAdaptativa
import com.example.pulsofirme.ProgresoData
import com.example.pulsofirme.UserData
import com.example.pulsofirme.areaTactil
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

private val AzulQd = Color(0xFF1565C0)
private val AzulOscuroQd = Color(0xFF0D3B66)
private val AzulSuaveQd = Color(0xFFE7F0FB)
private val BordeQd = Color(0xFFE5E7EB)
private val GrisQd = Color(0xFF6B7280)
private val NegroQd = Color(0xFF1A1A1A)

private val opcionesDificultad = listOf(
    "Ninguna dificultad",
    "Dificultad leve",
    "Dificultad moderada",
    "Dificultad severa",
    "No lo puedo ejecutar"
)

private val opcionesInterferencia = listOf(
    "Nada",
    "Un poco",
    "Moderadamente",
    "Bastante",
    "Muchísimo"
)

private val opcionesLimitacion = listOf(
    "Nada limitado",
    "Un poco limitado",
    "Moderadamente limitado",
    "Muy limitado",
    "No pude hacerlo"
)

private val opcionesSintoma = listOf(
    "Ninguno",
    "Leve",
    "Moderado",
    "Severo",
    "Muy severo"
)

private val opcionesSueno = listOf(
    "Ninguna dificultad",
    "Dificultad leve",
    "Dificultad moderada",
    "Dificultad severa",
    "No pude dormir"
)

private data class PreguntaQd(val texto: String, val opciones: List<String>)

private val preguntas = listOf(
    PreguntaQd("Abrir un frasco nuevo o muy apretado", opcionesDificultad),
    PreguntaQd("Hacer tareas pesadas de la casa (lavar paredes o pisos)", opcionesDificultad),
    PreguntaQd("Cargar una bolsa del super o un bolso", opcionesDificultad),
    PreguntaQd("Lavarte la espalda", opcionesDificultad),
    PreguntaQd("Cortar la comida con cuchillo", opcionesDificultad),
    PreguntaQd("Hacer actividades recreativas que exigen fuerza o impacto en el brazo, hombro o mano", opcionesDificultad),
    PreguntaQd("¿Cuánto interfirió el problema de tu brazo, hombro o mano en tus actividades con familia, amigos o vecinos?", opcionesInterferencia),
    PreguntaQd("¿Cuánto te limitó en tu trabajo o en tus actividades de todos los días?", opcionesLimitacion),
    PreguntaQd("Dolor en el brazo, hombro o mano", opcionesSintoma),
    PreguntaQd("Hormigueo o sensación de agujas en el brazo, hombro o mano", opcionesSintoma),
    PreguntaQd("Dificultad para dormir por el dolor en el brazo, hombro o mano", opcionesSueno)
)

// Puntaje QuickDASH: 0 = sin dificultad, 100 = dificultad máxima
private fun calcularPuntaje(respuestas: List<Int?>): Float {
    val contestadas = respuestas.filterNotNull()
    if (contestadas.isEmpty()) return 0f
    val promedio = contestadas.map { it + 1 }.average()
    return ((promedio - 1) * 25).toFloat()
}

private fun interpretacion(puntaje: Float): String = when {
    puntaje < 15f -> "Sin dificultad o muy leve"
    puntaje < 40f -> "Dificultad leve"
    puntaje < 60f -> "Dificultad moderada"
    else -> "Dificultad importante"
}

// Nivel de un grupo de respuestas (promedio de 1 a 5)
private fun nivelDe(respuestas: List<Int?>): String {
    val contestadas = respuestas.filterNotNull()
    if (contestadas.isEmpty()) return "—"
    val promedio = contestadas.map { it + 1 }.average()
    return when {
        promedio < 1.5 -> "Ninguno"
        promedio < 2.5 -> "Leve"
        promedio < 3.5 -> "Moderado"
        promedio < 4.5 -> "Severo"
        else -> "Muy severo"
    }
}

private fun unDecimal(valor: Float): String = String.format(Locale.US, "%.1f", valor)

@Composable
fun QuickDashScreen(
    onBack: () -> Unit,
    onFinish: () -> Unit = {},
    onResultadoGuardado: (Float, List<Int>) -> Unit = { _, _ -> }
) {
    val contexto = LocalContext.current
    val respuestas = remember { mutableStateListOf<Int?>(*arrayOfNulls<Int?>(preguntas.size)) }
    var indice by remember { mutableIntStateOf(0) }
    var mostrarResultado by remember { mutableStateOf(false) }
    var mostrarCompartir by remember { mutableStateOf(false) }

    // Se capturan al entrar, antes de guardar la evaluación nueva
    val puntajeAnterior = remember { ProgresoData.puntajeQuickDash }
    val fechaAnterior = remember { ProgresoData.fechaQuickDash }

    val manoTexto = if (UserData.mano.isBlank()) "mano derecha" else "mano ${UserData.mano.lowercase()}"

    if (mostrarCompartir) {
        AlertDialog(
            onDismissRequest = { mostrarCompartir = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    "Todavía no tenés cuidador",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuroQd
                )
            },
            text = {
                Text(
                    text = "Para compartir tus resultados, primero alguien tiene que vincularse con tu código desde su propia cuenta. Lo encontrás en Cuenta, en la sección Cuidadores.",
                    fontSize = 15.sp,
                    color = GrisQd,
                    lineHeight = 21.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { mostrarCompartir = false }) {
                    Text("Entendido", color = AzulQd, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        )
    }

    // ---------------- PANTALLA DE RESULTADO ----------------
    if (mostrarResultado) {
        val puntaje = calcularPuntaje(respuestas)
        val adaptativa = remember(puntaje) { DificultadAdaptativa.calcular(contexto, puntaje) }

        LaunchedEffect(Unit) {
            ProgresoData.puntajeQuickDash = puntaje
            ProgresoData.fechaQuickDash = System.currentTimeMillis()

            onResultadoGuardado(
                puntaje,
                respuestas.mapNotNull { it }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(48.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onFinish,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = AzulQd
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(92.dp)
                        .clip(CircleShape)
                        .background(AzulSuaveQd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = AzulQd,
                        modifier = Modifier.size(46.dp)
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Text(
                text = "Evaluación completada",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = AzulOscuroQd,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "QuickDASH, $manoTexto",
                fontSize = 15.sp,
                color = GrisQd,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // ---------- Tarjeta del puntaje ----------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(AzulSuaveQd)
                    .padding(vertical = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = unDecimal(puntaje),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuroQd
                )
                Spacer(Modifier.height(4.dp))
                Text("Puntaje sobre 100", fontSize = 14.sp, color = GrisQd)
                Spacer(Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(22.dp))
                        .background(AzulQd)
                        .padding(horizontal = 22.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = interpretacion(puntaje),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ---------- Comparación con la evaluación anterior ----------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, BordeQd, RoundedCornerShape(14.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (puntajeAnterior == null) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = AzulQd,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(14.dp))
                    Text(
                        text = "Es tu primera evaluación. Repetila en unas semanas para comparar.",
                        fontSize = 14.sp,
                        color = Color(0xFF374151),
                        lineHeight = 20.sp
                    )
                } else {
                    val diferencia = puntaje - puntajeAnterior
                    val mejoro = diferencia < 0
                    val dias = if (fechaAnterior == null) 0
                    else ((System.currentTimeMillis() - fechaAnterior) / 86_400_000L).toInt()
                    val textoDias = when (dias) {
                        0 -> "hoy mismo"
                        1 -> "ayer"
                        else -> "hace $dias días"
                    }

                    Icon(
                        imageVector = if (mejoro) Icons.Filled.TrendingDown else Icons.Filled.TrendingUp,
                        contentDescription = null,
                        tint = AzulQd,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(14.dp))
                    Text(
                        text = if (abs(diferencia) < 0.5f)
                            "Sin cambios respecto a tu evaluación de $textoDias."
                        else if (mejoro)
                            "Mejoró ${unDecimal(abs(diferencia))} puntos respecto a tu evaluación de $textoDias."
                        else
                            "Subió ${unDecimal(abs(diferencia))} puntos respecto a tu evaluación de $textoDias.",
                        fontSize = 14.sp,
                        color = Color(0xFF374151),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ---------- Dificultad adaptativa (Premium) ----------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, BordeQd, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Tune,
                        contentDescription = null,
                        tint = AzulQd,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Dificultad de tus rutinas",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulOscuroQd
                    )
                }

                Spacer(Modifier.height(10.dp))

                if (UserData.esPremium) {
                    Text(
                        text = "Nivel ${adaptativa.nivel} de 5 · ${adaptativa.etiqueta}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NegroQd
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = adaptativa.motivo,
                        fontSize = 14.sp,
                        color = Color(0xFF374151),
                        lineHeight = 20.sp
                    )
                } else {
                    Text(
                        text = "Con Premium Guiada, tus rutinas suben o bajan solas según este resultado y cómo te sentiste en cada encuesta.",
                        fontSize = 14.sp,
                        color = Color(0xFF374151),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ---------- Detalle por grupo ----------
            FilaDetalle("Actividades diarias", nivelDe(respuestas.take(8)))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BordeQd)
            )
            FilaDetalle("Síntomas (dolor, hormigueo, sueño)", nivelDe(respuestas.drop(8)))

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onFinish,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 58.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulQd)
            ) {
                Text("Volver a Progreso", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { mostrarCompartir = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 58.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BordeQd)
            ) {
                Text(
                    "Compartir con mi cuidador",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AzulOscuroQd
                )
            }

            Spacer(Modifier.height(18.dp))

            Text(
                text = "Este resultado es orientativo y no reemplaza la evaluación de tu profesional de salud.",
                fontSize = 12.sp,
                color = GrisQd,
                lineHeight = 17.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(30.dp))
        }
        return
    }

    // ---------------- PANTALLA DE PREGUNTAS ----------------
    val pregunta = preguntas[indice]
    val respuestaActual = respuestas[indice]
    val porcentaje = ((indice + 1).toFloat() / preguntas.size.toFloat())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
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
                    tint = AzulQd
                )
            }
            Spacer(Modifier.width(4.dp))
            Column {
                Text(
                    text = "Evaluación QuickDASH",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuroQd
                )
                Text(
                    text = manoTexto.replaceFirstChar { it.uppercase() },
                    fontSize = 14.sp,
                    color = GrisQd
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
                color = GrisQd
            )
            Text(
                text = "${(porcentaje * 100).roundToInt()}%",
                fontSize = 14.sp,
                color = GrisQd
            )
        }

        Spacer(Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { porcentaje },
            modifier = Modifier
                .fillMaxWidth()
                .height(9.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = AzulQd,
            trackColor = Color(0xFFF0F1F3)
        )

        Spacer(Modifier.height(22.dp))

        Text(
            text = "Evaluá tu capacidad para hacer estas actividades durante la última semana. Elegí la opción que más se ajuste a tu situación.",
            fontSize = 15.sp,
            color = GrisQd,
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(22.dp))

        Text(
            text = "${indice + 1}. ${pregunta.texto}",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = NegroQd,
            lineHeight = 26.sp
        )

        Spacer(Modifier.height(18.dp))

        pregunta.opciones.forEachIndexed { i, opcion ->
            val elegida = respuestaActual == i
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .areaTactil()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (elegida) AzulSuaveQd else Color.White)
                    .border(
                        width = if (elegida) 2.dp else 1.dp,
                        color = if (elegida) AzulQd else BordeQd,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .selectable(
                        selected = elegida,
                        role = Role.RadioButton,
                        onClick = { respuestas[indice] = i }
                    )
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = opcion,
                    fontSize = 16.sp,
                    color = if (elegida) AzulOscuroQd else Color(0xFF374151),
                    fontWeight = if (elegida) FontWeight.SemiBold else FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )
                if (elegida) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Elegida",
                        tint = AzulQd,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                if (indice < preguntas.size - 1) {
                    indice += 1
                } else {
                    mostrarResultado = true
                }
            },
            enabled = respuestaActual != null,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 58.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulQd,
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFDCDCDC),
                disabledContentColor = Color(0xFF8A8A8A)
            )
        ) {
            Text(
                text = if (indice < preguntas.size - 1) "Siguiente" else "Ver resultado",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(30.dp))
    }
}

@Composable
private fun FilaDetalle(titulo: String, nivel: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titulo,
            fontSize = 15.sp,
            color = GrisQd,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = nivel,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = NegroQd
        )
    }
}