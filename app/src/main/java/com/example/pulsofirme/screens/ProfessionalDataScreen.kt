package com.example.pulsofirme.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeDP = Color(0xFF07884A)
private val VerdeClaroDP = Color(0xFFE8F5EE)
private val GrisDP = Color(0xFF7A8294)

@Composable
fun ProfessionalDataScreen(
    lugarTrabajoInicial: String = "",
    especialidadInicial: String = "",
    matriculaInicial: String = "",
    formacionAdicionalInicial: String = "",
    onBack: () -> Unit,
    onGuardarCambios: (String, String, String, String) -> Unit = { _, _, _, _ -> }
) {
    var lugarTrabajo by remember(lugarTrabajoInicial) { mutableStateOf(lugarTrabajoInicial) }
    var especialidad by remember(especialidadInicial) { mutableStateOf(especialidadInicial) }
    var matricula by remember(matriculaInicial) { mutableStateOf(matriculaInicial) }
    var formacionAdicional by remember(formacionAdicionalInicial) {
        mutableStateOf(formacionAdicionalInicial)
    }

    Scaffold(containerColor = Color.White) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                }

                Text(
                    text = "Datos profesionales",
                    color = VerdeDP,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(VerdeClaroDP, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.MedicalServices,
                    contentDescription = null,
                    tint = VerdeDP,
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Completá tu información profesional\npara que tu perfil esté actualizado.",
                modifier = Modifier.fillMaxWidth(),
                color = GrisDP,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(Modifier.height(30.dp))

            CampoDatoProfesional(
                valor = lugarTrabajo,
                onCambio = { lugarTrabajo = it },
                titulo = "Lugar de trabajo",
                ejemplo = "Ej. Hospital San Martín",
                icono = Icons.Filled.Apartment
            )

            CampoDatoProfesional(
                valor = especialidad,
                onCambio = { especialidad = it },
                titulo = "Especialidad",
                ejemplo = "Ej. Neurología – Rehabilitación",
                icono = Icons.Filled.MedicalServices
            )

            CampoDatoProfesional(
                valor = matricula,
                onCambio = { matricula = it },
                titulo = "Matrícula profesional",
                ejemplo = "Ej. MP 12345",
                icono = Icons.Filled.Badge
            )

            CampoDatoProfesional(
                valor = formacionAdicional,
                onCambio = { formacionAdicional = it },
                titulo = "Formación adicional (opcional)",
                ejemplo = "Ej. Posgrado, certificaciones, cursos, etc.",
                icono = Icons.Filled.School
            )

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = {
                    onGuardarCambios(
                        lugarTrabajo,
                        especialidad,
                        matricula,
                        formacionAdicional
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDDF2E6),
                    contentColor = VerdeDP
                )
            ) {
                Text(
                    "Guardar cambios",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CampoDatoProfesional(
    valor: String,
    onCambio: (String) -> Unit,
    titulo: String,
    ejemplo: String,
    icono: ImageVector
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
        label = { Text(titulo) },
        placeholder = { Text(ejemplo, color = Color(0xFF9AA2B2)) },
        leadingIcon = {
            Icon(icono, contentDescription = null, tint = GrisDP)
        },
        singleLine = titulo != "Formación adicional (opcional)",
        minLines = if (titulo == "Formación adicional (opcional)") 2 else 1,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VerdeDP,
            focusedLabelColor = VerdeDP,
            unfocusedBorderColor = Color(0xFFD6DAD8)
        )
    )
}
