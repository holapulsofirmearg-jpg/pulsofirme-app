package com.example.pulsofirme.screens

import androidx.compose.runtime.saveable.rememberSaveable

import com.example.pulsofirme.ui.theme.enTema

import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.R

private val VerdeTitulo: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
private val GrisSubtitulo: Color
    @Composable get() = Color(0xFF6B7280).enTema()
private val VerdeClaroFondo: Color
    @Composable get() = Color(0xFFEFF6F1).enTema()
private val GrisBorde: Color
    @Composable get() = Color(0xFFE5E7EB).enTema()
private val AzulBoton: Color
    @Composable get() = Color(0xFF2C5282).enTema()
private data class PasoGuante(
    val titulo: String,
    val descripcion: String
)

private val pasosGuiaGuante = listOf(
    PasoGuante(
        titulo = "Cargalo antes de usarlo",
        descripcion = "Conectalo hasta que la luz quede fija. Una carga completa alcanza para varias sesiones."
    ),
    PasoGuante(
        titulo = "Colocátelo sin apretar",
        descripcion = "Los sensores tienen que quedar apoyados sobre los dedos, pero el guante no debe marcar la piel ni molestar."
    ),
    PasoGuante(
        titulo = "Vinculalo por Bluetooth",
        descripcion = "Entrá a la sección Dispositivo y esperá a que aparezca. Solo hace falta hacerlo la primera vez."
    )
)

@Composable
fun CaregiverSupportScreen(
    onBack: () -> Unit,
    onDudasFrecuentes: () -> Unit,
    onGuiaGuante: () -> Unit = {}, // <-- Parámetro agregado como lambda
    onReportarProblema: () -> Unit = {}
) {
    var guiaGuanteExpandida by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.enTema())
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = VerdeTitulo
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Ayuda y soporte",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeTitulo
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---------- ¿Necesitás ayuda? ----------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerdeClaroFondo, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.White.enTema(), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.SupportAgent,
                    contentDescription = null,
                    tint = VerdeTitulo,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "¿Necesitás ayuda?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A).enTema()
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Nuestro equipo responde en menos de 24 hs.",
                    fontSize = 13.sp,
                    color = GrisSubtitulo,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Contacto",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeTitulo
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GrisBorde, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.pulsofirme),
                    contentDescription = "Logo PulsoFirme",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "PulsoFirme",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A).enTema()
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "holapulsofirmearg@gmail.com",
                        fontSize = 13.sp,
                        color = GrisSubtitulo
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Versión de prueba",
                fontSize = 13.sp,
                color = GrisSubtitulo
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Esta es una versión de demostración desarrollada por estudiantes de la Escuela EXPERIMENTAL PROA. PulsoFirme está pensado para acompañar ejercicios de manos en Parkinson y artritis, pero no reemplaza la indicación de un profesional de la salud. Si tenés cualquier duda y querés que te ayudemos, escribinos a holapulsofirmearg@gmail.com.",
                fontSize = 14.sp,
                color = Color(0xFF1A1A1A).enTema(),
                lineHeight = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Recursos",
            fontSize = 15.sp,
            color = GrisSubtitulo
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GrisBorde, RoundedCornerShape(14.dp))
        ) {
            FilaRecurso(
                titulo = "Dudas frecuentes",
                onClick = onDudasFrecuentes
            )

            HorizontalDivider(color = GrisBorde)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .clickable {
                        guiaGuanteExpandida = !guiaGuanteExpandida
                        onGuiaGuante() // Invoca la lambda al interactuar
                    }
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Guía de uso del guante",
                        fontSize = 16.sp,
                        color = Color(0xFF1A1A1A).enTema(),
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Filled.ExpandMore,
                        contentDescription = if (guiaGuanteExpandida) "Contraer" else "Expandir",
                        tint = Color(0xFF9CA3AF).enTema()
                    )
                }

                if (guiaGuanteExpandida) {
                    Spacer(modifier = Modifier.height(16.dp))

                    pasosGuiaGuante.forEachIndexed { index, paso ->
                        Text(
                            text = paso.titulo,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A).enTema()
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = paso.descripcion,
                            fontSize = 14.sp,
                            color = GrisSubtitulo,
                            lineHeight = 20.sp
                        )

                        if (index != pasosGuiaGuante.lastIndex) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onReportarProblema,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AzulBoton)
        ) {
            Text(
                text = "Reportar un problema",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun FilaRecurso(
    titulo: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titulo,
            fontSize = 16.sp,
            color = Color(0xFF1A1A1A).enTema(),
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Filled.ExpandMore,
            contentDescription = null,
            tint = Color(0xFF9CA3AF).enTema()
        )
    }
}