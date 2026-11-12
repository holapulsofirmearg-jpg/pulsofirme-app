package com.example.pulsofirme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.semantics.Role
import com.example.pulsofirme.EjercicioPremium
import com.example.pulsofirme.EjerciciosPremium
import com.example.pulsofirme.UserData

private val Verde = Color(0xFF1B7A4B)
private val VerdeClaro = Color(0xFFEFF6F1)
private val BordeGris = Color(0xFFE5E7EB)

@Composable
fun ExercisesScreen(
    rutaActual: String,
    onNavigate: (String) -> Unit,
    onEjercicioClick: (String) -> Unit,
    // Se llama al tocar un ejercicio Premium sin tener Premium: lleva a la pantalla de planes
    onPremiumClick: () -> Unit = {}
) {
    val diagnosticosUsuario = UserData.diagnosticos
    val mostrarSelectorCompleto = diagnosticosUsuario.size != 1
    val filtrosDiagnostico = if (mostrarSelectorCompleto) listOf("Todos", "Parkinson", "Artritis") else diagnosticosUsuario
    // "Premium" va siempre al final, como un cartelito más
    val filtros = filtrosDiagnostico + "Premium"

    var filtroSeleccionado by remember { mutableStateOf(filtrosDiagnostico.firstOrNull() ?: "Todos") }

    val enPremium = filtroSeleccionado == "Premium"
    val mostrarParkinson = !enPremium && (filtroSeleccionado == "Parkinson" || filtroSeleccionado == "Todos")
    val mostrarArtritis = !enPremium && (filtroSeleccionado == "Artritis" || filtroSeleccionado == "Todos")

    // Dentro de Premium: botón Parkinson o Artritis.
    // Arranca en el diagnóstico del usuario (si tiene los dos, en Parkinson).
    var premiumDiagnostico by remember {
        mutableStateOf(
            if (!mostrarSelectorCompleto && diagnosticosUsuario.first().equals("Artritis", ignoreCase = true))
                "Artritis" else "Parkinson"
        )
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                itemsMenuInferior.forEach { item ->
                    NavigationBarItem(
                        selected = rutaActual == item.ruta,
                        onClick = { onNavigate(item.ruta) },
                        icon = { Icon(item.icono, contentDescription = item.label) },
                        label = { Text(item.label, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Verde,
                            selectedTextColor = Verde,
                            unselectedIconColor = Color(0xFF9CA3AF),
                            unselectedTextColor = Color(0xFF9CA3AF),
                            indicatorColor = VerdeClaro
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Ejercicios", fontSize = 13.sp, color = Color(0xFF6B7280))
            Text(
                text = "Movete a tu ritmo",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (filtros.size > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    filtros.forEach { filtro ->
                        val seleccionado = filtroSeleccionado == filtro
                        val esPremium = filtro == "Premium"
                        Row(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    when {
                                        seleccionado -> Verde
                                        esPremium -> VerdeClaro
                                        else -> Color.White
                                    }
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (seleccionado) Verde else BordeGris,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable(role = Role.Tab) { filtroSeleccionado = filtro }
                                .heightIn(min = 56.dp)
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (esPremium) {
                                Icon(
                                    Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = if (seleccionado) Color.White else Verde,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = filtro,
                                color = when {
                                    seleccionado -> Color.White
                                    esPremium -> Verde
                                    else -> Color(0xFF374151)
                                },
                                fontSize = 14.sp,
                                fontWeight = if (seleccionado || esPremium) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // PARKINSON
            if (mostrarParkinson) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VerdeClaro, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = Verde, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Los ejercicios de Parkinson son orientativos. Consultá a tu neurólogo o kinesiólogo antes de empezar una rutina.",
                        fontSize = 13.sp,
                        color = Color(0xFF374151),
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                CartelTratamiento(
                    numero = "TRATAMIENTO 1",
                    condicion = "Extensión de dedos",
                    lineas = listOf(
                        Icons.Filled.PanTool to "Movilidad de los dedos",
                        Icons.Filled.FitnessCenter to "Apertura controlada de la mano"
                    ),
                    onClick = { onEjercicioClick("Extensión de dedos") }
                )

                Spacer(modifier = Modifier.height(20.dp))

                CartelTratamiento(
                    numero = "TRATAMIENTO 2",
                    condicion = "Toques de dedos",
                    lineas = listOf(
                        Icons.Filled.PanTool to "Coordinación de los dedos",
                        Icons.Filled.FitnessCenter to "Destreza y control fino"
                    ),
                    onClick = { onEjercicioClick("Toques de dedos") }
                )

                Spacer(modifier = Modifier.height(20.dp))

                CartelTratamiento(
                    numero = "TRATAMIENTO 3",
                    condicion = "Pronación y supinación",
                    lineas = listOf(
                        Icons.Filled.PanTool to "Movilidad del antebrazo",
                        Icons.Filled.FitnessCenter to "Control del movimiento"
                    ),
                    onClick = { onEjercicioClick("Pronación y supinación") }
                )

                Spacer(modifier = Modifier.height(20.dp))

                CartelTratamiento(
                    numero = "TRATAMIENTO 4",
                    condicion = "Abrir y cerrar la mano",
                    lineas = listOf(
                        Icons.Filled.PanTool to "Amplitud de movimiento",
                        Icons.Filled.FitnessCenter to "Coordinación de mano y dedos"
                    ),
                    onClick = { onEjercicioClick("Abrir y cerrar la mano") }
                )

                Spacer(modifier = Modifier.height(20.dp))

                CartelTratamiento(
                    numero = "TRATAMIENTO 5",
                    condicion = "Golpeteo alternado de dedos",
                    lineas = listOf(
                        Icons.Filled.PanTool to "Movimiento alternado de los dedos",
                        Icons.Filled.FitnessCenter to "Coordinación y ritmo"
                    ),
                    onClick = { onEjercicioClick("Golpeteo alternado de dedos") }
                )

                Spacer(modifier = Modifier.height(22.dp))
            }

            // ARTRITIS
            if (mostrarArtritis) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VerdeClaro, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = Verde, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Los ejercicios de artritis son orientativos. Consultá a tu reumatólogo o kinesiólogo antes de empezar una rutina.",
                        fontSize = 13.sp,
                        color = Color(0xFF374151),
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                CartelTratamiento(
                    numero = "TRATAMIENTO 1",
                    condicion = "Artritis",
                    lineas = listOf(
                        Icons.Filled.MedicalServices to "Control del dolor y la inflamación",
                        Icons.Filled.FitnessCenter to "Movilidad articular"
                    ),
                    onClick = { onEjercicioClick("Movilidad de muñecas") }
                )

                Spacer(modifier = Modifier.height(20.dp))

                CartelTratamiento(
                    numero = "TRATAMIENTO 2",
                    condicion = "Puño y extensión",
                    lineas = listOf(
                        Icons.Filled.PanTool to "Fortalecimiento de agarre",
                        Icons.Filled.FitnessCenter to "Movilidad de dedos"
                    ),
                    onClick = { onEjercicioClick("Puño y extensión") }
                )

                Spacer(modifier = Modifier.height(20.dp))

                CartelTratamiento(
                    numero = "TRATAMIENTO 3",
                    condicion = "Rodar una pelota blanda",
                    lineas = listOf(
                        Icons.Filled.SportsBaseball to "Movilidad de palma y muñeca",
                        Icons.Filled.FitnessCenter to "Evolución y coordinación"
                    ),
                    onClick = { onEjercicioClick("Rodar una pelota blanda") }
                )

                Spacer(modifier = Modifier.height(20.dp))

                CartelTratamiento(
                    numero = "TRATAMIENTO 4",
                    condicion = "Oposición del pulgar",
                    lineas = listOf(
                        Icons.Filled.PanTool to "Movilidad de pulgar y dedos",
                        Icons.Filled.FitnessCenter to "Coordinación fina de la mano"
                    ),
                    onClick = { onEjercicioClick("Oposición del pulgar") }
                )

                Spacer(modifier = Modifier.height(20.dp))

                CartelTratamiento(
                    numero = "TRATAMIENTO 5",
                    condicion = "Deslizamiento de dedos",
                    lineas = listOf(
                        Icons.Filled.PanTool to "Movilidad suave de los dedos",
                        Icons.Filled.FitnessCenter to "Apertura y cierre de la mano"
                    ),
                    onClick = { onEjercicioClick("Deslizamiento de dedos") }
                )

            }

            // PREMIUM
            if (enPremium) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VerdeClaro, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Verde, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (UserData.esPremium)
                            "Incluidos en tu plan. Se ajustan a tu nivel y a si estás on u off."
                        else
                            "Ejercicios exclusivos de Premium Guiada. Tocá uno para ver cómo activarlo.",
                        fontSize = 13.sp,
                        color = Color(0xFF374151),
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    BotonDiagnostico(
                        texto = "Parkinson",
                        icono = Icons.Filled.PanTool,
                        seleccionado = premiumDiagnostico == "Parkinson",
                        onClick = { premiumDiagnostico = "Parkinson" },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    BotonDiagnostico(
                        texto = "Artritis",
                        icono = Icons.Filled.MedicalServices,
                        seleccionado = premiumDiagnostico == "Artritis",
                        onClick = { premiumDiagnostico = "Artritis" },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Cartel informativo según el diagnóstico elegido
                Spacer(modifier = Modifier.height(18.dp))
                if (premiumDiagnostico == "Artritis") CartelArtritis() else CartelParkinson()

                PremiumSeccion(
                    ejercicios = EjerciciosPremium.seleccion(premiumDiagnostico),
                    onEjercicioClick = onEjercicioClick,
                    onPremiumClick = onPremiumClick
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun CartelTratamiento(
    numero: String,
    condicion: String,
    lineas: List<Pair<ImageVector, String>>,
    onClick: () -> Unit,
    bloqueado: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BordeGris, RoundedCornerShape(16.dp))
            .clickable(role = Role.Button) { onClick() }
            .padding(20.dp)
    ) {
        Text(
            text = numero,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF6B7280),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = condicion,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Verde
        )

        Spacer(modifier = Modifier.height(18.dp))

        lineas.forEach { (icono, texto) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(VerdeClaro, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icono, contentDescription = null, tint = Verde, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = texto,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (bloqueado) Color(0xFF6B7280) else Verde)
                .clickable(role = Role.Button) { onClick() }
                .heightIn(min = 56.dp)
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (bloqueado) {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = if (bloqueado) "DISPONIBLE CON PREMIUM" else "APRETÁ AQUÍ",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp
            )
            if (!bloqueado) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/** Sección "Ejercicios Premium": los que no tiene el plan gratis quedan bloqueados. */
@Composable
private fun PremiumSeccion(
    ejercicios: List<EjercicioPremium>,
    onEjercicioClick: (String) -> Unit,
    onPremiumClick: () -> Unit
) {
    if (ejercicios.isEmpty()) return

    Spacer(modifier = Modifier.height(6.dp))

    ejercicios.forEachIndexed { i, e ->
        val bloqueado = EjerciciosPremium.bloqueado(e)
        Spacer(modifier = Modifier.height(16.dp))
        CartelTratamiento(
            numero = "PREMIUM ${i + 1}",
            condicion = e.nombre,
            lineas = listOf(
                Icons.Filled.PanTool to e.zona,
                Icons.Filled.FitnessCenter to e.tipo
            ),
            bloqueado = bloqueado,
            onClick = {
                if (bloqueado) onPremiumClick() else onEjercicioClick(e.nombre)
            }
        )
    }
}

/** Botón grande para elegir Parkinson o Artritis dentro de Premium. */
@Composable
private fun BotonDiagnostico(
    texto: String,
    icono: ImageVector,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (seleccionado) Verde else Color.White)
            .border(
                width = if (seleccionado) 2.dp else 1.dp,
                color = if (seleccionado) Verde else BordeGris,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(role = Role.Tab) { onClick() }
            .heightIn(min = 64.dp)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icono,
            contentDescription = null,
            tint = if (seleccionado) Color.White else Verde,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = texto,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = if (seleccionado) Color.White else Color(0xFF1A1A1A)
        )
    }
}

/** Cartel informativo de la sección Premium (título, texto y aviso "Importante"). */
@Composable
private fun CartelInfo(titulo: String, texto: String, importante: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(VerdeClaro)
            .border(1.dp, Color(0xFFCDE5D6), RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.AccessibilityNew,
                    contentDescription = null,
                    tint = Verde,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = titulo,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                lineHeight = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = texto,
            fontSize = 14.sp,
            color = Color(0xFF374151),
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFFF7E0))
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Filled.Warning,
                contentDescription = null,
                tint = Color(0xFF9A6B00),
                modifier = Modifier
                    .padding(top = 1.dp)
                    .size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Importante: ") }
                    append(importante)
                },
                fontSize = 13.sp,
                color = Color(0xFF5C4400),
                lineHeight = 19.sp
            )
        }
    }
}

/** Explica por qué en artritis hay ejercicios de muñecas, tobillos y pies. */
@Composable
private fun CartelArtritis() = CartelInfo(
    titulo = "La artritis no solo afecta las manos",
    texto = "La artritis también puede afectar otras partes del cuerpo, como las muñecas, " +
            "rodillas, tobillos y pies. Por eso, en Pulso Firme incluimos ejercicios que " +
            "ayudan a mantener la movilidad, reducir la rigidez y mejorar la flexibilidad " +
            "de distintas articulaciones.",
    importante = "Realizá los movimientos suavemente y sin forzar. Si sentís dolor o " +
            "inflamación, suspendé el ejercicio y consultá con un profesional de salud."
)

/** Explica por qué en Parkinson hay ejercicios de manos, brazos, piernas y pies. */
@Composable
private fun CartelParkinson() = CartelInfo(
    titulo = "Ejercicios para el Parkinson",
    texto = "El Parkinson puede afectar la movilidad, el equilibrio y la coordinación de " +
            "diferentes partes del cuerpo, no solo las manos. Por eso, en Pulso Firme incluimos " +
            "ejercicios para manos, brazos, piernas y pies, que buscan mejorar la movilidad, " +
            "reducir la rigidez y favorecer la coordinación de los movimientos.",
    importante = "Realizá los ejercicios de forma lenta, segura y sin forzar el cuerpo. Si " +
            "sentís dolor, mareos o pérdida de equilibrio, detené la actividad y consultá con " +
            "un profesional de salud.\n\nEstos ejercicios son complementarios y no reemplazan " +
            "el tratamiento médico ni la rehabilitación indicada por un profesional."
)