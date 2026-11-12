package com.example.pulsofirme.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.GuanteConexion
import com.example.pulsofirme.UserData
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.roundToInt

// Nombre del modelo que aparece en "Información del dispositivo"
private const val MODELO_GUANTE = "Movit-v3"

private val Verde = Color(0xFF1B7A4B)
private val VerdeOscuro = Color(0xFF1B4332)
private val VerdeClaro = Color(0xFFEFF6F1)
private val BordeGris = Color(0xFFE5E7EB)
private val GrisTexto = Color(0xFF6B7280)
private val NegroTexto = Color(0xFF1A1A1A)
private val RojoBoton = Color(0xFFB91C1C)
private val AmbarAviso = Color(0xFFBA7517)
private val AmbarAvisoClaro = Color(0xFFFAEEDA)
private val AzulEjeY = Color(0xFF1565C0)

private const val CIERRE_AVISO =
    "Es un acompañamiento del tratamiento, no un reemplazo. PulsoFirme no cura la enfermedad " +
            "ni sustituye la medicación ni las indicaciones de tu médico o kinesiólogo.\n\n" +
            "No dejes ni cambies ningún tratamiento por tu cuenta. Ante cualquier duda o si notás " +
            "que los síntomas empeoran, hablá con el profesional a cargo."

@Composable
fun DeviceScreen(
    rutaActual: String,
    onNavigate: (String) -> Unit
) {
    val contexto = LocalContext.current
    val estado = GuanteConexion.estado
    val conectado = estado == GuanteConexion.Estado.CONECTADO
    val encendido = GuanteConexion.guanteEncendido

    var mostrarAviso by remember { mutableStateOf(false) }

    // Diagnósticos elegidos en el perfil (lista + diagnóstico principal)
    val textoDiagnostico =
        (UserData.diagnosticos + UserData.diagnosticoPrincipal).joinToString(" ").lowercase()
    val tieneParkinson = textoDiagnostico.contains("parkinson")
    val tieneArtritis = textoDiagnostico.contains("artritis")

    val tituloAviso = when {
        tieneParkinson && !tieneArtritis -> "Sobre las vibraciones terapéuticas"
        tieneArtritis && !tieneParkinson -> "Sobre el calor terapéutico"
        else -> "Vibraciones y calor terapéutico"
    }

    val textoAviso = when {
        tieneParkinson && !tieneArtritis ->
            "Las vibraciones terapéuticas del guante ayudan a que la mano se sienta más estable " +
                    "y a reducir la sensación de temblor mientras lo usás.\n\n" +
                    CIERRE_AVISO
        tieneArtritis && !tieneParkinson ->
            "El calor terapéutico del guante ayuda a aliviar la rigidez y el dolor en las " +
                    "articulaciones de la mano, y a que te muevas con más comodidad.\n\n" +
                    CIERRE_AVISO
        else ->
            "Las vibraciones terapéuticas ayudan a que la mano se sienta más estable frente al " +
                    "temblor, y el calor terapéutico alivia la rigidez y el dolor en las articulaciones.\n\n" +
                    CIERRE_AVISO
    }

    val xSuave by animateFloatAsState(
        targetValue = GuanteConexion.anguloX,
        animationSpec = tween(durationMillis = 120),
        label = "xSuave"
    )
    val ySuave by animateFloatAsState(
        targetValue = GuanteConexion.anguloY,
        animationSpec = tween(durationMillis = 120),
        label = "ySuave"
    )

    if (mostrarAviso) {
        AlertDialog(
            onDismissRequest = { mostrarAviso = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            icon = {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = Verde,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = tituloAviso,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdeOscuro
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = textoAviso,
                        fontSize = 14.sp,
                        color = Color(0xFF374151),
                        lineHeight = 21.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarAviso = false }) {
                    Text(
                        "Entendido",
                        color = Verde,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
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
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // ---------- Header ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Dispositivo", fontSize = 15.sp, color = GrisTexto)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Tu guante inteligente",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = VerdeOscuro
                    )
                }
                IconButton(onClick = { mostrarAviso = true }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Información del tratamiento",
                        tint = VerdeOscuro
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ---------- Estado de la conexión ----------
            val colorPunto = when {
                conectado && encendido -> Verde
                conectado -> AmbarAviso
                estado == GuanteConexion.Estado.CONECTANDO -> AmbarAviso
                estado == GuanteConexion.Estado.ERROR -> RojoBoton
                else -> Color(0xFF9CA3AF)
            }
            val textoEstado = when {
                conectado && encendido -> "Encendido"
                conectado -> "Apagado"
                estado == GuanteConexion.Estado.CONECTANDO -> "Conectando..."
                estado == GuanteConexion.Estado.ERROR -> "Sin conexión"
                else -> "Desconectado"
            }
            val subtituloEstado = when {
                conectado && encendido -> "PulsoFirme Glove, recibiendo datos"
                conectado -> "El guante dejó de enviar datos"
                estado == GuanteConexion.Estado.CONECTANDO -> "Buscando el guante en la red..."
                else -> "Conectate a la red ${GuanteConexion.RED_WIFI}"
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFFE2F0E7), RoundedCornerShape(16.dp))
                    .background(if (encendido) Color(0xFFF6FAF7) else Color.White)
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(VerdeClaro),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Power,
                        contentDescription = null,
                        tint = if (encendido) Verde else Color(0xFF9CA3AF),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = textoEstado,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = NegroTexto
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = subtituloEstado, fontSize = 13.sp, color = GrisTexto)
                }

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(colorPunto)
                )
            }

            if (conectado && !encendido) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AmbarAvisoClaro)
                        .padding(14.dp)
                ) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = null,
                        tint = AmbarAviso,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Dejaron de llegar datos. Puede que hayas apagado el guante o que te alejaste demasiado. Se reanuda solo al volver la señal.",
                        fontSize = 13.sp,
                        color = Color(0xFF374151),
                        lineHeight = 19.sp
                    )
                }
            }

            if (estado == GuanteConexion.Estado.ERROR && GuanteConexion.mensajeError.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AmbarAvisoClaro)
                        .padding(14.dp)
                ) {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = null,
                        tint = AmbarAviso,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = GuanteConexion.mensajeError,
                        fontSize = 13.sp,
                        color = Color(0xFF374151),
                        lineHeight = 19.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ---------- Inclinación de la mano (ejes X e Y) ----------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BordeGris, RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.ScreenRotation,
                        contentDescription = null,
                        tint = Verde,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Inclinación de la mano",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NegroTexto
                        )
                        Text(
                            text = "Ejes X e Y · en tiempo real",
                            fontSize = 13.sp,
                            color = GrisTexto
                        )
                    }
                    if (encendido) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(VerdeClaro)
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "EN VIVO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Verde,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(180.dp)) {
                        val centro = Offset(size.width / 2, size.height / 2)
                        val radio = size.minDimension / 2

                        drawCircle(
                            color = if (encendido) VerdeClaro else Color(0xFFF3F3F3),
                            radius = radio,
                            center = centro
                        )
                        drawCircle(
                            color = if (encendido) Color(0xFFC9E6D4) else Color(0xFFE5E5E5),
                            radius = radio * 0.6f,
                            center = centro,
                            style = Stroke(width = 2f)
                        )
                        drawCircle(
                            color = if (encendido) Color(0xFFC9E6D4) else Color(0xFFE5E5E5),
                            radius = radio * 0.25f,
                            center = centro,
                            style = Stroke(width = 2f)
                        )

                        val colorCruz = if (encendido) Color(0xFFB6DCC5) else Color(0xFFE0E0E0)
                        drawLine(
                            color = colorCruz,
                            start = Offset(centro.x - radio, centro.y),
                            end = Offset(centro.x + radio, centro.y),
                            strokeWidth = 2f
                        )
                        drawLine(
                            color = colorCruz,
                            start = Offset(centro.x, centro.y - radio),
                            end = Offset(centro.x, centro.y + radio),
                            strokeWidth = 2f
                        )

                        if (encendido) {
                            val limite = 90f
                            val maxima = radio * 0.85f
                            var despX = (ySuave / limite) * maxima
                            var despY = (-xSuave / limite) * maxima

                            // La pelotita choca contra la orilla y nunca se sale del círculo
                            val distancia = hypot(despX, despY)
                            if (distancia > maxima) {
                                val factor = maxima / distancia
                                despX *= factor
                                despY *= factor
                            }

                            drawCircle(
                                color = Verde,
                                radius = 16f,
                                center = Offset(centro.x + despX, centro.y + despY)
                            )
                        } else {
                            drawCircle(
                                color = Color(0xFFC7C7C7),
                                radius = 14f,
                                center = centro
                            )
                        }
                    }
                }

                Text(
                    text = GuanteConexion.descripcionInclinacion(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (encendido) VerdeOscuro else GrisTexto,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    TarjetaEje(
                        etiqueta = "Eje X",
                        detalle = "Izquierda/ derecha",
                        valor = if (encendido) "${xSuave.roundToInt()}°" else "--",
                        color = Verde,
                        activo = encendido,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    TarjetaEje(
                        etiqueta = "Eje Y",
                        detalle = "Arriba / bajo",
                        valor = if (encendido) "${ySuave.roundToInt()}°" else "--",
                        color = AzulEjeY,
                        activo = encendido,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    MiniDato(
                        titulo = "Máx. eje X",
                        valor = if (GuanteConexion.lecturasRecibidas > 0)
                            "${abs(GuanteConexion.maximoX).roundToInt()}°" else "--",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    MiniDato(
                        titulo = "Máx. eje Y",
                        valor = if (GuanteConexion.lecturasRecibidas > 0)
                            "${abs(GuanteConexion.maximoY).roundToInt()}°" else "--",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    MiniDato(
                        titulo = "Lecturas",
                        valor = if (GuanteConexion.lecturasRecibidas > 0)
                            "${GuanteConexion.lecturasRecibidas}" else "--",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ---------- Velocidad de vibración ----------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BordeGris, RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Vibration,
                        contentDescription = null,
                        tint = Verde,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Velocidad de vibración",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NegroTexto
                        )
                        Text(
                            text = "Se aplica a los dos motores a la vez",
                            fontSize = 13.sp,
                            color = GrisTexto
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    GuanteConexion.Velocidad.entries.forEachIndexed { indice, v ->
                        if (indice > 0) Spacer(modifier = Modifier.width(10.dp))

                        val elegida = GuanteConexion.velocidad == v
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (elegida) Verde else Color.White)
                                .border(
                                    width = if (elegida) 0.dp else 1.dp,
                                    color = if (elegida) Verde else BordeGris,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable(enabled = conectado) {
                                    GuanteConexion.cambiarVelocidad(v)
                                }
                                .padding(vertical = 16.dp, horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Barritas que crecen según la intensidad
                            Row(verticalAlignment = Alignment.Bottom) {
                                val niveles = indice + 1
                                for (i in 1..3) {
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 2.dp)
                                            .width(5.dp)
                                            .height((7 + i * 5).dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(
                                                when {
                                                    i > niveles && elegida -> Color.White.copy(alpha = 0.3f)
                                                    i > niveles -> Color(0xFFE0E0E0)
                                                    elegida -> Color.White
                                                    else -> Verde
                                                }
                                            )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = v.etiqueta,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (elegida) Color.White else NegroTexto
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = v.detalle,
                                fontSize = 10.sp,
                                color = if (elegida) Color.White.copy(alpha = 0.85f) else GrisTexto,
                                textAlign = TextAlign.Center,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (conectado) VerdeClaro else Color(0xFFF5F5F5))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (conectado) Icons.Filled.CheckCircle else Icons.Filled.Info,
                        contentDescription = null,
                        tint = if (conectado) Verde else GrisTexto,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (conectado)
                            "Vibrando en ${GuanteConexion.velocidad.etiqueta.lowercase()}. El cambio se aplica al instante."
                        else
                            "Conectá el guante para poder cambiar la velocidad.",
                        fontSize = 13.sp,
                        color = Color(0xFF374151),
                        lineHeight = 19.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ---------- Información del dispositivo ----------
            Text(
                text = "Información del dispositivo",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = GrisTexto
            )

            Spacer(modifier = Modifier.height(16.dp))

            FilaInfoTexto(etiqueta = "Modelo", valor = MODELO_GUANTE, valorEnNegrita = true)
            Spacer(modifier = Modifier.height(14.dp))
            FilaInfoTexto(etiqueta = "Sensor", valor = "MPU6050")
            Spacer(modifier = Modifier.height(14.dp))
            FilaInfoTexto(etiqueta = "Red del guante", valor = GuanteConexion.RED_WIFI)
            Spacer(modifier = Modifier.height(14.dp))
            FilaInfoTexto(
                etiqueta = "Dirección",
                valor = "${GuanteConexion.IP_GUANTE}:${GuanteConexion.PUERTO}"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ---------- Botones ----------
            Button(
                onClick = { GuanteConexion.conectar(contexto) },
                enabled = estado != GuanteConexion.Estado.CONECTANDO && !conectado,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Verde,
                    disabledContainerColor = Color(0xFFDCDCDC),
                    disabledContentColor = Color(0xFF8A8A8A)
                )
            ) {
                Text(
                    text = when {
                        conectado -> "Guante conectado"
                        estado == GuanteConexion.Estado.CONECTANDO -> "Conectando..."
                        else -> "Conectar guante"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { GuanteConexion.desconectar(contexto) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BordeGris),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RojoBoton)
            ) {
                Text("Desvincular dispositivo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Para conectar el guante, entrá a los ajustes de WiFi del celular y elegí la red ${GuanteConexion.RED_WIFI}. Si Android avisa que la red no tiene internet, aceptá seguir conectado.",
                fontSize = 12.sp,
                color = GrisTexto,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TarjetaEje(
    etiqueta: String,
    detalle: String,
    valor: String,
    color: Color,
    activo: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, BordeGris, RoundedCornerShape(14.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(if (activo) color else Color(0xFFC7C7C7))
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = etiqueta,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = GrisTexto
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = valor,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = if (activo) color else Color(0xFFB5B5B5)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = detalle,
            fontSize = 11.sp,
            color = GrisTexto,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MiniDato(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(VerdeClaro)
            .padding(12.dp)
    ) {
        Text(text = titulo, fontSize = 11.sp, color = GrisTexto)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = valor, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NegroTexto)
    }
}

@Composable
private fun FilaInfoTexto(
    etiqueta: String,
    valor: String,
    valorEnNegrita: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = etiqueta, fontSize = 15.sp, color = GrisTexto)
        Text(
            text = valor,
            fontSize = 15.sp,
            fontWeight = if (valorEnNegrita) FontWeight.Bold else FontWeight.SemiBold,
            color = NegroTexto
        )
    }
}