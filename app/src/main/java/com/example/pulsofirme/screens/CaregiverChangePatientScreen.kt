package com.example.pulsofirme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeTitulo = Color(0xFF1B7A4B)
private val AzulTexto = Color(0xFF16324F)
private val GrisSubtitulo = Color(0xFF6B7280)
private val VerdeFondo = Color(0xFFEFF6F1)
private val AmarilloFondo = Color(0xFFFCF0D9)
private val AmarilloTexto = Color(0xFF8A6D1D)
private val GrisBorde = Color(0xFFD8DED9)

@Composable
fun CaregiverChangePatientScreen(
    nombrePacienteActual: String = "Nombre del paciente",
    fechaVinculacion: String = "21 de septiembre de 2026",
    onBack: () -> Unit,
    onVincularNuevoPaciente: (String) -> Unit = {}
) {
    var codigoNuevoPaciente by remember { mutableStateOf("") }

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
                text = "Cambiar paciente",
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
                    .background(Color.White, CircleShape),
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
                Text("Paciente actual", fontSize = 13.sp, color = GrisSubtitulo)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    nombrePacienteActual,
                    fontSize = 18.sp,
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

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Vincular nuevo paciente",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AzulTexto
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Para vincularte con otro paciente, primero se reemplazará el vínculo actual.",
            fontSize = 15.sp,
            color = GrisSubtitulo,
            lineHeight = 21.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("Código del nuevo paciente", fontSize = 13.sp, color = GrisSubtitulo)
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = codigoNuevoPaciente,
            onValueChange = { codigoNuevoPaciente = it },
            placeholder = { Text("Ingresá el código de vinculación") },
            leadingIcon = {
                Icon(Icons.Filled.Link, contentDescription = null, tint = GrisSubtitulo)
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VerdeTitulo,
                unfocusedBorderColor = GrisBorde
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AmarilloFondo, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFFF5C451), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.WarningAmber,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Al cambiar de paciente dejarás de ver la información del paciente actual.",
                fontSize = 14.sp,
                color = AmarilloTexto,
                lineHeight = 19.sp,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onVincularNuevoPaciente(codigoNuevoPaciente) },
            enabled = codigoNuevoPaciente.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeTitulo,
                disabledContainerColor = Color(0xFFBEE0CC)
            )
        ) {
            Text("Vincular nuevo paciente", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GrisBorde)
        ) {
            Text("Cancelar", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = AzulTexto)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}