package com.example.pulsofirme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeTitulo = Color(0xFF1B7A4B)
private val AzulTexto = Color(0xFF16324F)
private val CelesteFondo = Color(0xFFF3F8FA)
private val GrisSubtitulo = Color(0xFF6B7280)
private val RojoBoton = Color(0xFFD64545)

@Composable
fun CaregiverSettingsScreen(
    onBack: () -> Unit,
    onCuentaEliminada: () -> Unit = {}
) {
    var sonidosActivados by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        // Botón Volver
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .clip(CircleShape)
                .background(Color(0xFFF3F4F6))
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color(0xFF1F2937),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Título
        Text(
            text = "Ajustes",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeTitulo,
            letterSpacing = (-0.5).sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Opciones de Ajustes
        OpcionAjusteItem(
            titulo = "Idioma",
            valorActual = "Español",
            onClick = { /* TODO */ }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OpcionAjusteItem(
            titulo = "Tema",
            valorActual = "Claro",
            onClick = { /* TODO */ }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OpcionAjusteItem(
            titulo = "Unidad de medida",
            valorActual = "Métrico",
            onClick = { /* TODO */ }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Opción con Switch de Sonidos
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            color = CelesteFondo
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sonidos",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = AzulTexto,
                    modifier = Modifier.weight(1f)
                )

                Switch(
                    checked = sonidosActivados,
                    onCheckedChange = { sonidosActivados = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = VerdeTitulo,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFD1D5DB)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OpcionAjusteItem(
            titulo = "Tamaño del texto",
            valorActual = "Mediano",
            onClick = { /* TODO */ }
        )

        Spacer(modifier = Modifier.weight(1f, fill = false))
        Spacer(modifier = Modifier.height(48.dp))

        // Botón Eliminar cuenta
        Button(
            onClick = onCuentaEliminada,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RojoBoton)
        ) {
            Text(
                text = "Eliminar cuenta",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun OpcionAjusteItem(
    titulo: String,
    valorActual: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = CelesteFondo
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = titulo,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = AzulTexto,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = valorActual,
                fontSize = 15.sp,
                color = GrisSubtitulo
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = GrisSubtitulo,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}