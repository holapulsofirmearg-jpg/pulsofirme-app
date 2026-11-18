package com.example.pulsofirme.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.R

private val VerdeSp = Color(0xFF1B7A4B)
private val VerdeOscuroSp = Color(0xFF1B4332)
private val VerdeSuaveSp = Color(0xFFEAF5EC)
private val AzulBotonSp = Color(0xFF2C5F8D)
private val BordeSp = Color(0xFFE5E7EB)
private val GrisSp = Color(0xFF6B7280)
private val NegroSp = Color(0xFF1A1A1A)

private const val CORREO_SOPORTE = "holapulsofirmearg@gmail.com"

private val dudasFrecuentes = listOf(
    "¿Necesito el guante para usar la app?" to
            "No. Podés hacer los ejercicios y llevar tu progreso sin el guante. El guante suma el registro automático del movimiento.",
    "¿Los ejercicios reemplazan la kinesiología?" to
            "No. Son un complemento para sostener la rutina entre sesiones. La indicación siempre la da tu profesional de salud.",
    "¿Para qué sirve el código de vinculación?" to
            "Es el número que le compartís a un cuidador o médico para que pueda seguir tu progreso desde su propia cuenta.",
    "¿Qué pasa si un día no puedo hacer los ejercicios?" to
            "No pasa nada. La racha se corta pero podés retomar cuando quieras. Es mejor descansar que forzar."
)

private val guiaDelGuante = listOf(
    "Cargalo antes de usarlo" to
            "Conectalo hasta que la luz quede fija. Una carga completa alcanza para varias sesiones.",
    "Colocátelo sin apretar" to
            "Los sensores tienen que quedar apoyados sobre los dedos, pero el guante no debe marcar la piel ni molestar.",
    "Vinculalo por Bluetooth" to
            "Entrá a la sección Dispositivo y esperá a que aparezca. Solo hace falta hacerlo la primera vez."
)

@Composable
fun SupportScreen(
    onBack: () -> Unit,
    onDudasFrecuentes: () -> Unit = {}
) {
    val contexto = LocalContext.current
    var abiertoDudas by remember { mutableStateOf(false) }
    var abiertoGuia by remember { mutableStateOf(false) }

    val abrirCorreo: (String) -> Unit = { asunto ->
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$CORREO_SOPORTE")
            putExtra(Intent.EXTRA_SUBJECT, asunto)
        }
        try {
            contexto.startActivity(intent)
        } catch (e: Exception) {
            // Si el dispositivo no tiene app de correo, no hacemos nada
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(48.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = VerdeSp
                )
            }
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Ayuda y soporte",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeOscuroSp
            )
        }

        Spacer(Modifier.height(20.dp))

        // ---------- Banner ----------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(VerdeSuaveSp)
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.SupportAgent,
                    contentDescription = null,
                    tint = VerdeSp,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    "¿Necesitás ayuda?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NegroSp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "Nuestro equipo responde en menos de 24 hs.",
                    fontSize = 14.sp,
                    color = GrisSp,
                    lineHeight = 19.sp
                )
            }
        }

        Spacer(Modifier.height(26.dp))

        // ---------- Contacto ----------
        Text(
            "Contacto",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeOscuroSp
        )

        Spacer(Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, BordeSp, RoundedCornerShape(16.dp))
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logo2),
                    contentDescription = "Logo de Pulso Firme",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        "PulsoFirme",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = NegroSp
                    )
                    Text(
                        text = CORREO_SOPORTE,
                        fontSize = 14.sp,
                        color = GrisSp,
                        modifier = Modifier.clickable { abrirCorreo("Consulta desde la app") }
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Text("Versión de prueba", fontSize = 14.sp, color = GrisSp)

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Esta es una versión de demostración desarrollada por estudiantes de la Escuela EXPERIMENTAL PROA. " +
                        "PulsoFirme está pensado para acompañar ejercicios de manos en Parkinson y artritis, pero no reemplaza la indicación de un profesional de la salud. Si tenés cualquier duda y querés que te ayudemos, escribinos a $CORREO_SOPORTE.",
                fontSize = 15.sp,
                color = Color(0xFF374151),
                lineHeight = 23.sp
            )
        }

        Spacer(Modifier.height(26.dp))

        // ---------- Recursos ----------
        Text("Recursos", fontSize = 15.sp, color = GrisSp)

        Spacer(Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, BordeSp, RoundedCornerShape(16.dp))
        ) {
            FilaDesplegable(
                titulo = "Dudas frecuentes",
                abierto = abiertoDudas,
                onClick = {
                    abiertoDudas = !abiertoDudas
                    if (abiertoDudas) onDudasFrecuentes()
                }
            )

            AnimatedVisibility(visible = abiertoDudas) {
                Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)) {
                    dudasFrecuentes.forEach { (pregunta, respuesta) ->
                        BloqueRecurso(pregunta, respuesta)
                    }
                }
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BordeSp)
            )

            FilaDesplegable(
                titulo = "Guía de uso del guante",
                abierto = abiertoGuia,
                onClick = { abiertoGuia = !abiertoGuia }
            )

            AnimatedVisibility(visible = abiertoGuia) {
                Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)) {
                    guiaDelGuante.forEach { (titulo, detalle) ->
                        BloqueRecurso(titulo, detalle)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { abrirCorreo("Reporte de un problema en la app") },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AzulBotonSp)
        ) {
            Text("Reportar un problema", fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(30.dp))
    }
}

@Composable
private fun FilaDesplegable(titulo: String, abierto: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titulo,
            fontSize = 17.sp,
            color = NegroSp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = if (abierto) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = null,
            tint = Color(0xFF9CA3AF)
        )
    }
}

@Composable
private fun BloqueRecurso(titulo: String, detalle: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = titulo,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = NegroSp,
            lineHeight = 21.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = detalle,
            fontSize = 14.sp,
            color = GrisSp,
            lineHeight = 20.sp
        )
    }
}
