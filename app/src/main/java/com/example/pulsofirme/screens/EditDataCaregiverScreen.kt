package com.example.pulsofirme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeCuidador = Color(0xFF1B7A4B)
private val VerdeClaroCuidador = Color(0xFFEFF6F1)
private val GrisTextoCuidador = Color(0xFF84919D)
private val GrisLineaCuidador = Color(0xFFE1E7EA)

@Composable
fun EditDataCaregiverScreen(
    onBack: () -> Unit = {},
    onCambiarContrasena: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 34.dp)
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            // TÍTULO
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Tus datos siendo cuidador",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdeCuidador
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // NOMBRE
            DatoPersonalCuidador(
                icono = Icons.Filled.PersonOutline,
                titulo = "Nombre",
                valor = "Nombre del cuidador"
            )

            // APELLIDO
            DatoPersonalCuidador(
                icono = Icons.Filled.PersonOutline,
                titulo = "Apellido",
                valor = "Apellido del cuidador"
            )

            // CORREO
            DatoPersonalCuidador(
                icono = Icons.Filled.Email,
                titulo = "Correo electrónico",
                valor = "correo@ejemplo.com"
            )

            // TELÉFONO
            DatoPersonalCuidador(
                icono = Icons.Filled.Phone,
                titulo = "Número de teléfono",
                valor = "+54 9 11 1234 5678"
            )

            // FECHA DE NACIMIENTO
            DatoPersonalCuidador(
                icono = Icons.Filled.CalendarMonth,
                titulo = "Fecha de nacimiento",
                valor = "21/09/1995"
            )

            // GÉNERO
            DatoPersonalCuidador(
                icono = Icons.Filled.Wc,
                titulo = "Género",
                valor = "Mujer",
                tieneFlecha = true
            )

            // PAÍS
            DatoPersonalCuidador(
                icono = Icons.Filled.Language,
                titulo = "País",
                valor = "Argentina",
                tieneFlecha = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // GUARDAR CAMBIOS
            Button(
                onClick = {
                    // Acá después podemos guardar los datos
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDDF4EC),
                    contentColor = VerdeCuidador
                )
            ) {
                Text(
                    text = "Guardar cambios",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(34.dp))

            // CAMBIAR CONTRASEÑA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = GrisTextoCuidador,
                    modifier = Modifier.size(30.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "¿Querés cambiar tu contraseña?",
                    fontSize = 16.sp,
                    color = GrisTextoCuidador
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onCambiarContrasena()
                    }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Cambiar contraseña",
                    fontSize = 18.sp,
                    color = VerdeCuidador,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.width(18.dp))

                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = VerdeCuidador,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun DatoPersonalCuidador(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    valor: String,
    tieneFlecha: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = GrisTextoCuidador,
            modifier = Modifier
                .size(30.dp)
                .padding(end = 2.dp)
        )

        Spacer(modifier = Modifier.width(18.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = titulo,
                fontSize = 14.sp,
                color = GrisTextoCuidador
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = valor,
                fontSize = 17.sp,
                color = Color(0xFF245F52)
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(
                color = GrisLineaCuidador,
                thickness = 1.dp
            )
        }

        if (tieneFlecha) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = GrisTextoCuidador,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}