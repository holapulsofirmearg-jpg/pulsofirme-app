package com.example.pulsofirme.screens

import com.example.pulsofirme.ui.theme.enTema

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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeTitulo: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
private val AzulTexto: Color
    @Composable get() = Color(0xFF16324F).enTema()
private val GrisSubtitulo: Color
    @Composable get() = Color(0xFF6B7280).enTema()
private val VerdeFondo: Color
    @Composable get() = Color(0xFFEFF6F1).enTema()
private val VerdeIconoFondo: Color
    @Composable get() = Color(0xFFDFF3E4).enTema()
private val GrisFondoLista: Color
    @Composable get() = Color(0xFFF7F8F8).enTema()
private val VerdeAutorizadoFondo: Color
    @Composable get() = Color(0xFFDCEFE3).enTema()
private val GrisDivider: Color
    @Composable get() = Color(0xFFE5E7EB).enTema()
private val GrisBorde: Color
    @Composable get() = Color(0xFFD8DED9).enTema()
@Composable
fun CaregiverPatientScreen(
    nombrePaciente: String = "Nombre del paciente",
    fechaVinculacion: String = "21 de septiembre de 2026",
    fechaNacimiento: String = "21/09/1995",
    genero: String = "Mujer",
    pais: String = "Argentina",
    // Permisos reales que dio el paciente (null = todavía cargando)
    comparteProgreso: Boolean? = null,
    comparteMedicacion: Boolean? = null,
    comparteUbicacion: Boolean? = null,
    onBack: () -> Unit,
    onCambiarPaciente: () -> Unit = {}
) {
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
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Paciente vinculado",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeTitulo
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerdeFondo, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White.enTema(), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = VerdeTitulo,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    nombrePaciente,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdeTitulo
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "Vinculado desde el $fechaVinculacion",
                    fontSize = 13.sp,
                    color = GrisSubtitulo,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = "Información del paciente",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = AzulTexto
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(GrisFondoLista, RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp)
        ) {
            FilaDatoPaciente(
                icono = Icons.Filled.CalendarMonth,
                etiqueta = "Fecha de nacimiento",
                valor = fechaNacimiento
            )
            HorizontalDivider(color = GrisDivider)

            FilaDatoPaciente(
                icono = Icons.Filled.Female,
                etiqueta = "Género",
                valor = genero
            )
            HorizontalDivider(color = GrisDivider)

            FilaDatoPaciente(
                icono = Icons.Filled.Public,
                etiqueta = "País",
                valor = pais,
                conDivider = false
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = "Datos que podés ver",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = AzulTexto
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(GrisFondoLista, RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp)
        ) {
            FilaPermisoAutorizado(
                icono = Icons.AutoMirrored.Filled.TrendingUp,
                titulo = "Progreso del tratamiento",
                descripcion = "Podés ver el progreso y estadísticas\ndel paciente.",
                autorizado = comparteProgreso
            )
            HorizontalDivider(color = GrisDivider)

            FilaPermisoAutorizado(
                icono = Icons.Filled.Notifications,
                titulo = "Alertas de medicación",
                descripcion = "Recibís avisos relacionados\ncon las tomas registradas.",
                autorizado = comparteMedicacion
            )
            HorizontalDivider(color = GrisDivider)

            FilaPermisoAutorizado(
                icono = Icons.Filled.LocationOn,
                titulo = "Ubicación",
                descripcion = "Solo podés verla si el paciente\nautorizó compartirla.",
                autorizado = comparteUbicacion,
                conDivider = false
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onCambiarPaciente,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GrisBorde)
        ) {
            Text("Cambiar paciente", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = VerdeTitulo)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun FilaDatoPaciente(
    icono: ImageVector,
    etiqueta: String,
    valor: String,
    conDivider: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(VerdeIconoFondo, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icono, contentDescription = null, tint = VerdeTitulo, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(etiqueta, fontSize = 13.sp, color = GrisSubtitulo)
            Spacer(modifier = Modifier.height(2.dp))
            Text(valor, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = AzulTexto)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color(0xFF9CA3AF).enTema(),
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun FilaPermisoAutorizado(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    autorizado: Boolean?,
    conDivider: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(VerdeIconoFondo, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icono, contentDescription = null, tint = VerdeTitulo, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AzulTexto)
            Spacer(modifier = Modifier.height(2.dp))
            Text(descripcion, fontSize = 13.sp, color = GrisSubtitulo, lineHeight = 17.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        // Antes decía siempre "Autorizado"; ahora muestra lo que eligió el paciente
        val (texto, fondo, color) = when (autorizado) {
            true -> Triple("Autorizado", VerdeAutorizadoFondo, VerdeTitulo)
            false -> Triple("No autorizado", Color(0xFFF1F2F4).enTema(), Color(0xFF6B7280).enTema())
            null -> Triple("…", Color(0xFFF1F2F4).enTema(), Color(0xFF6B7280).enTema())
        }
        Box(
            modifier = Modifier
                .background(fondo, RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(texto, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}