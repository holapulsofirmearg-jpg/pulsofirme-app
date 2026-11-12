package com.example.pulsofirme.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeTitulo = Color(0xFF1B7A4B)
private val AzulTexto = Color(0xFF16324F)
private val GrisSubtitulo = Color(0xFF6B7280)
private val CelesteFondo = Color(0xFFEAF3F7)
private val VerdeClaroFondo = Color(0xFFEFF6F1)
private val VerdeIconoFondo = Color(0xFFDFF3E4)
private val RojoBoton = Color(0xFFE05A4E)
private val GrisDivider = Color(0xFFE5E7EB)

@Composable
fun AjustesCaregiverScreen(
    onBack: () -> Unit,
    onCuentaEliminada: () -> Unit = {}
) {
    var sonidos by remember { mutableStateOf(true) }
    var accesibilidadExpandida by remember { mutableStateOf(false) }

    var altoContraste by remember { mutableStateOf(true) }
    var botonesGrandes by remember { mutableStateOf(true) }
    var reducirAnimaciones by remember { mutableStateOf(false) }
    var lecturaPantalla by remember { mutableStateOf(false) }
    var sonidosYVibracion by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Ajustes",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeTitulo,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        FilaAjuste(titulo = "Idioma", valor = "Español", onClick = { /* TODO */ })

        Spacer(modifier = Modifier.height(12.dp))

        FilaAjuste(titulo = "Tema", valor = "Claro", onClick = { /* TODO */ })

        Spacer(modifier = Modifier.height(12.dp))

        FilaAjuste(titulo = "Unidad de medida", valor = "Métrico", onClick = { /* TODO */ })

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CelesteFondo, RoundedCornerShape(14.dp))
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Sonidos",
                fontSize = 16.sp,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = sonidos,
                onCheckedChange = { sonidos = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = VerdeTitulo
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        FilaAjuste(titulo = "Tamaño del texto", valor = "Mediano", onClick = { /* TODO */ })

        Spacer(modifier = Modifier.height(12.dp))

        // ---------- Accesibilidad (acordeón) ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .background(
                    if (accesibilidadExpandida) VerdeClaroFondo else CelesteFondo,
                    RoundedCornerShape(14.dp)
                )
                .clickable { accesibilidadExpandida = !accesibilidadExpandida }
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(VerdeIconoFondo, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Accessibility,
                        contentDescription = null,
                        tint = VerdeTitulo,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("Accesibilidad", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AzulTexto)
                    if (accesibilidadExpandida) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Adaptá la aplicación para usarla de la forma más cómoda para vos.",
                            fontSize = 12.sp,
                            color = GrisSubtitulo,
                            lineHeight = 16.sp
                        )
                    }
                }

                Icon(
                    imageVector = if (accesibilidadExpandida) Icons.Filled.ExpandLess else Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = GrisSubtitulo,
                    modifier = Modifier.size(if (accesibilidadExpandida) 22.dp else 14.dp)
                )
            }

            if (accesibilidadExpandida) {
                Spacer(modifier = Modifier.height(14.dp))

                FilaAccesibilidad(
                    icono = Icons.Filled.Contrast,
                    titulo = "Alto contraste",
                    descripcion = "Aumenta el contraste de colores\npara una mejor visualización.",
                    activo = altoContraste,
                    onCambio = { altoContraste = it }
                )
                HorizontalDivider(color = GrisDivider)

                FilaAccesibilidad(
                    icono = Icons.Filled.PanTool,
                    titulo = "Botones más grandes",
                    descripcion = "Aumenta el tamaño de los botones\npara facilitar su uso.",
                    activo = botonesGrandes,
                    onCambio = { botonesGrandes = it }
                )
                HorizontalDivider(color = GrisDivider)

                FilaAccesibilidad(
                    icono = Icons.Filled.PlayArrow,
                    titulo = "Reducir animaciones",
                    descripcion = "Disminuye los movimientos\ny transiciones de la aplicación.",
                    activo = reducirAnimaciones,
                    onCambio = { reducirAnimaciones = it }
                )
                HorizontalDivider(color = GrisDivider)

                FilaAccesibilidad(
                    icono = Icons.Filled.RecordVoiceOver,
                    titulo = "Lectura de pantalla",
                    descripcion = "Optimiza la aplicación para\nlectores de pantalla.",
                    activo = lecturaPantalla,
                    onCambio = { lecturaPantalla = it }
                )
                HorizontalDivider(color = GrisDivider)

                FilaAccesibilidad(
                    icono = Icons.Filled.Notifications,
                    titulo = "Sonidos y vibración",
                    descripcion = "Recibe una respuesta sonora\ny háptica al realizar acciones.",
                    activo = sonidosYVibracion,
                    onCambio = { sonidosYVibracion = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onCuentaEliminada,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RojoBoton)
        ) {
            Text("Eliminar cuenta", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun FilaAjuste(
    titulo: String,
    valor: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CelesteFondo, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            titulo,
            fontSize = 16.sp,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.weight(1f)
        )
        if (valor.isNotEmpty()) {
            Text(
                valor,
                fontSize = 15.sp,
                color = GrisSubtitulo
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = GrisSubtitulo,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun FilaAccesibilidad(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    activo: Boolean,
    onCambio: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(VerdeIconoFondo, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icono, contentDescription = null, tint = VerdeTitulo, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AzulTexto)
            Spacer(modifier = Modifier.height(2.dp))
            Text(descripcion, fontSize = 12.sp, color = GrisSubtitulo, lineHeight = 16.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = activo,
            onCheckedChange = onCambio,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VerdeTitulo
            )
        )
    }
}