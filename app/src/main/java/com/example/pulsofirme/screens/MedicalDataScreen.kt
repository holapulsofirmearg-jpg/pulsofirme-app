package com.example.pulsofirme.screens

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
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.UserData

private val VerdeMd = Color(0xFF1B7A4B)
private val VerdeOscuroMd = Color(0xFF1B4332)
private val VerdeSuaveMd = Color(0xFFEAF5EC)
private val BordeMd = Color(0xFFD8DED9)
private val GrisMd = Color(0xFF6B7280)
private val NegroMd = Color(0xFF1A1A1A)

private val diagnosticosDisponibles = listOf(
    "Enfermedad de Parkinson",
    "Artritis",
    "Parkinson y artritis",
    "Otro / sin diagnóstico"
)

private val zonasDisponibles = listOf(
    "Mano derecha",
    "Mano izquierda",
    "Ambas manos",
    "Muñeca derecha",
    "Hombro derecho"
)

// Toma lo que el usuario eligió en "Contanos un poco más" como valor inicial
private fun diagnosticoInicial(): String {
    if (UserData.diagnosticoPrincipal.isNotBlank()) return UserData.diagnosticoPrincipal
    val d = UserData.diagnosticos
    return when {
        d.size > 1 -> "Parkinson y artritis"
        d.size == 1 && d.first().equals("Parkinson", ignoreCase = true) -> "Enfermedad de Parkinson"
        d.size == 1 -> d.first()
        else -> "Enfermedad de Parkinson"
    }
}

private fun zonaInicial(): String {
    if (UserData.zonaAfectada.isNotBlank()) return UserData.zonaAfectada
    return if (UserData.mano.isBlank()) "Mano derecha" else "Mano ${UserData.mano.lowercase()}"
}

@Composable
fun MedicalDataScreen(
    onBack: () -> Unit,
    diagnosticoInicialFirebase: String = "",
    zonaInicialFirebase: String = "",
    profesionalInicialFirebase: String = "",
    notasInicialFirebase: String = "",
    onGuardarCambios: (String, String, String, String) -> Unit = { _, _, _, _ -> }
) {
    var diagnostico by remember(diagnosticoInicialFirebase) {
        mutableStateOf(diagnosticoInicialFirebase.ifBlank { diagnosticoInicial() })
    }
    var zona by remember(zonaInicialFirebase) {
        mutableStateOf(zonaInicialFirebase.ifBlank { zonaInicial() })
    }
    var profesional by remember(profesionalInicialFirebase) {
        mutableStateOf(profesionalInicialFirebase.ifBlank { UserData.profesionalACargo })
    }
    var notas by remember(notasInicialFirebase) {
        mutableStateOf(notasInicialFirebase.ifBlank { UserData.notasCuidador })
    }

    var menuDiagnostico by remember { mutableStateOf(false) }
    var menuZona by remember { mutableStateOf(false) }
    var guardado by remember { mutableStateOf(false) }

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
                    tint = VerdeMd
                )
            }
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Diagnóstico y datos médicos",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeOscuroMd,
                lineHeight = 27.sp
            )
        }

        Spacer(Modifier.height(24.dp))

        // ---------- Diagnóstico principal ----------
        Text("Diagnóstico principal", fontSize = 13.sp, color = GrisMd)
        Spacer(Modifier.height(6.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BordeMd, RoundedCornerShape(12.dp))
                    .clickable { menuDiagnostico = true }
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = diagnostico,
                    fontSize = 15.sp,
                    color = NegroMd,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFF9CA3AF)
                )
            }

            DropdownMenu(
                expanded = menuDiagnostico,
                onDismissRequest = { menuDiagnostico = false },
                modifier = Modifier.background(Color.White)
            ) {
                diagnosticosDisponibles.forEach { d ->
                    DropdownMenuItem(
                        text = { Text(text = d, fontSize = 15.sp) },
                        onClick = {
                            diagnostico = d
                            guardado = false
                            menuDiagnostico = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---------- Zona afectada ----------
        Text("Zona afectada", fontSize = 13.sp, color = GrisMd)
        Spacer(Modifier.height(6.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BordeMd, RoundedCornerShape(12.dp))
                    .clickable { menuZona = true }
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = zona,
                    fontSize = 15.sp,
                    color = NegroMd,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFF9CA3AF)
                )
            }

            DropdownMenu(
                expanded = menuZona,
                onDismissRequest = { menuZona = false },
                modifier = Modifier.background(Color.White)
            ) {
                zonasDisponibles.forEach { z ->
                    DropdownMenuItem(
                        text = { Text(text = z, fontSize = 15.sp) },
                        onClick = {
                            zona = z
                            guardado = false
                            menuZona = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---------- Profesional a cargo ----------
        Text("Profesional a cargo", fontSize = 13.sp, color = GrisMd)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = profesional,
            onValueChange = { profesional = it; guardado = false },
            placeholder = {
                Text("Ej: Dr. Roberto Díaz, Neurología", color = Color(0xFF9AA0A6), fontSize = 15.sp)
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VerdeMd,
                unfocusedBorderColor = BordeMd,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(Modifier.height(16.dp))

        // ---------- Notas para tu cuidador ----------
        Text("Notas para tu cuidador", fontSize = 13.sp, color = GrisMd)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = notas,
            onValueChange = { notas = it; guardado = false },
            placeholder = {
                Text(
                    "Ej: rigidez matutina más marcada. Evitar ejercicios intensos después de las 20:00 hs.",
                    color = Color(0xFF9AA0A6),
                    fontSize = 15.sp,
                    lineHeight = 21.sp
                )
            },
            shape = RoundedCornerShape(12.dp),
            minLines = 4,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VerdeMd,
                unfocusedBorderColor = BordeMd,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(Modifier.height(18.dp))

        // ---------- Aviso de privacidad ----------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(VerdeSuaveMd)
                .padding(16.dp)
        ) {
            Icon(
                Icons.Filled.Lock,
                contentDescription = null,
                tint = VerdeMd,
                modifier = Modifier.size(19.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Esta información solo la puede ver tu cuidador si activaste el permiso correspondiente.",
                fontSize = 14.sp,
                color = Color(0xFF374151),
                lineHeight = 20.sp
            )
        }

        Spacer(Modifier.height(22.dp))

        Button(
            onClick = {
                UserData.diagnosticoPrincipal = diagnostico
                UserData.zonaAfectada = zona
                UserData.profesionalACargo = profesional.trim()
                UserData.notasCuidador = notas.trim()

                onGuardarCambios(
                    diagnostico,
                    zona,
                    profesional.trim(),
                    notas.trim()
                )

                guardado = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeMd)
        ) {
            Text("Guardar cambios", fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }

        if (guardado) {
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(VerdeSuaveMd)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(VerdeMd),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Text("Tus datos médicos se guardaron", fontSize = 14.sp, color = VerdeOscuroMd)
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Estos datos son solo para organizar tu seguimiento dentro de la app. No reemplazan tu historia clínica ni la indicación de tu profesional de salud.",
            fontSize = 12.sp,
            color = GrisMd,
            lineHeight = 17.sp
        )

        Spacer(Modifier.height(30.dp))
    }
}