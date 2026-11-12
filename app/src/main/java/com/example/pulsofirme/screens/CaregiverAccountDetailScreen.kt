package com.example.pulsofirme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeTitulo = Color(0xFF1B7A4B)
private val GrisSubtitulo = Color(0xFF6B7280)
private val GrisEtiqueta = Color(0xFF9CA3AF)
private val AzulTexto = Color(0xFF16324F)
private val CelesteFondo = Color(0xFFEAF3F7)
private val GrisDivider = Color(0xFFDCE3E0)

@Composable
fun CaregiverAccountDetailScreen(
    codigoVinculacion: String = "482 913",
    plan: String = "Plan Básico",
    diagnostico: String = "Discapacidad motora",
    manoGuante: String = "Derecha",
    fechaVinculacion: String = "21/09/2025",
    onBack: () -> Unit,
    onEditarDatos: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Mi cuenta de cuidador",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeTitulo
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Acá podés ver la información de tu cuenta\nde cuidador y los datos vinculados.",
            fontSize = 15.sp,
            color = GrisSubtitulo,
            lineHeight = 21.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CelesteFondo, shape = RoundedCornerShape(20.dp))
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            FilaDato(
                icono = Icons.Filled.Link,
                etiqueta = "Código de vinculación",
                valor = codigoVinculacion
            )
            HorizontalDivider(color = GrisDivider)

            FilaDato(
                icono = Icons.Filled.Description,
                etiqueta = "Plan",
                valor = plan
            )
            HorizontalDivider(color = GrisDivider)

            FilaDato(
                icono = Icons.Filled.AddCircle,
                etiqueta = "Diagnóstico",
                valor = diagnostico
            )
            HorizontalDivider(color = GrisDivider)

            FilaDato(
                icono = Icons.Filled.Person,
                etiqueta = "Mano del guante",
                valor = manoGuante
            )
            HorizontalDivider(color = GrisDivider)

            FilaDato(
                icono = Icons.Filled.CalendarMonth,
                etiqueta = "Fecha de vinculación",
                valor = fechaVinculacion,
                conDivider = false
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFDCEFE3), shape = RoundedCornerShape(28.dp))
                .clickable { onEditarDatos() }
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = VerdeTitulo,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Editar datos",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeTitulo
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = VerdeTitulo,
                modifier = Modifier.size(14.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun FilaDato(
    icono: ImageVector,
    etiqueta: String,
    valor: String,
    conDivider: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = Color(0xFF6B7280),
            modifier = Modifier
                .padding(top = 2.dp)
                .size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(text = etiqueta, fontSize = 13.sp, color = GrisEtiqueta)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = valor, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = AzulTexto)
        }
    }
}