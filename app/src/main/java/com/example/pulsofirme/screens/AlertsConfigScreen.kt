package com.example.pulsofirme.screens

import com.example.pulsofirme.ui.theme.enTema

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeTitulo: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
private val AzulTexto: Color
    @Composable get() = Color(0xFF16324F).enTema()
private val GrisSubtitulo: Color
    @Composable get() = Color(0xFF6B7280).enTema()
private val VerdeIconoFondo: Color
    @Composable get() = Color(0xFFDFF3E4).enTema()
private val GrisBorde: Color
    @Composable get() = Color(0xFFE5E7EB).enTema()
@Composable
fun AlertsConfigScreen(
    onBack: () -> Unit,
    medicacionInicial: Boolean = true,
    ejercicioPendienteInicial: Boolean = true,
    rachaInicial: Boolean = true,
    nuevoCuidadorInicial: Boolean = true,
    onGuardarCambios: (
        medicacion: Boolean,
        ejercicioPendiente: Boolean,
        racha: Boolean,
        nuevoCuidador: Boolean
    ) -> Unit = { _, _, _, _ -> }
) {
    var medicacion by remember(medicacionInicial) { mutableStateOf(medicacionInicial) }
    var ejercicioPendiente by remember(ejercicioPendienteInicial) { mutableStateOf(ejercicioPendienteInicial) }
    var racha by remember(rachaInicial) { mutableStateOf(rachaInicial) }
    var nuevoCuidador by remember(nuevoCuidadorInicial) { mutableStateOf(nuevoCuidadorInicial) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.enTema())
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        IconButton(onClick = onBack, modifier = Modifier.size(56.dp)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = tr("Volver"),
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = tr("Configuración de alertas"),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = AzulTexto
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = tr("Elegí qué alertas querés recibir."),
            fontSize = 15.sp,
            color = GrisSubtitulo,
            lineHeight = 21.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        FilaAlertaConfig(
            icono = Icons.Filled.Medication,
            titulo = "Medicación",
            descripcion = "Avisos sobre tus horarios\nde medicación.",
            activo = medicacion,
            onCambio = { medicacion = it }
        )

        Spacer(modifier = Modifier.height(14.dp))

        FilaAlertaConfig(
            icono = Icons.Filled.Bolt,
            titulo = "Ejercicio pendiente",
            descripcion = "Te recordamos cuando todavía\nno hiciste ejercicios en el día.",
            activo = ejercicioPendiente,
            onCambio = { ejercicioPendiente = it }
        )

        Spacer(modifier = Modifier.height(14.dp))

        FilaAlertaConfig(
            icono = Icons.Filled.EmojiEvents,
            titulo = "Racha",
            descripcion = "Avisos sobre tu racha\nde días consecutivos.",
            activo = racha,
            onCambio = { racha = it }
        )

        Spacer(modifier = Modifier.height(14.dp))

        FilaAlertaConfig(
            icono = Icons.Filled.PersonAdd,
            titulo = "Nuevo cuidador",
            descripcion = "Avisos para vincular un familiar\no profesional a tu cuenta.",
            activo = nuevoCuidador,
            onCambio = { nuevoCuidador = it }
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                onGuardarCambios(medicacion, ejercicioPendiente, racha, nuevoCuidador)
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeTitulo)
        ) {
            Text(tr("Guardar cambios"), fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun FilaAlertaConfig(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    activo: Boolean,
    onCambio: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .border(1.dp, GrisBorde, RoundedCornerShape(16.dp))
            .toggleable(
                value = activo,
                role = Role.Switch,
                onValueChange = onCambio
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(VerdeIconoFondo, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icono, contentDescription = null, tint = VerdeTitulo, modifier = Modifier.size(22.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(tr(titulo), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AzulTexto)
            Spacer(modifier = Modifier.height(2.dp))
            Text(tr(descripcion), fontSize = 13.sp, color = GrisSubtitulo, lineHeight = 18.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = activo,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VerdeTitulo
            )
        )
    }
}