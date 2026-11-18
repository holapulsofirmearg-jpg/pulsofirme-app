package com.example.pulsofirme.screens

import com.example.pulsofirme.ui.theme.enTema

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeCuidador: Color
    @Composable get() = Color(0xFF007953).enTema()
private val VerdeIconoFondo: Color
    @Composable get() = Color(0xFFE8F5E9).enTema()
private val CardFondo: Color
    @Composable get() = Color(0xFFF3F8F9).enTema()
private val TextoSubtitulo: Color
    @Composable get() = Color(0xFF718096).enTema()
@Composable
fun CaregiverDownloadDataScreen(
    onBack: () -> Unit,
    onSolicitarDescarga: () -> Unit = {}
) {
    Scaffold(
        containerColor = Color.White.enTema()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Botón atrás
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Título
            Text(
                text = "Descargar mis datos",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeCuidador
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bajada principal
            Text(
                text = "Obtené una copia de la información de tu cuenta.",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF4A5568).enTema()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Texto secundario
            Text(
                text = "Podés solicitar una copia de los datos personales asociados a tu cuenta de cuidador.",
                fontSize = 14.sp,
                color = TextoSubtitulo,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Tarjeta de elementos incluidos
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardFondo),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Se incluirá:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = VerdeCuidador
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    ItemIncluido(
                        icono = Icons.Filled.Person,
                        texto = "Datos personales del cuidador"
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = Color(0xFFE2E8F0).enTema()
                    )

                    ItemIncluido(
                        icono = Icons.Filled.Description,
                        texto = "Información de la cuenta"
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = Color(0xFFE2E8F0).enTema()
                    )

                    ItemIncluido(
                        icono = Icons.Filled.Link,
                        texto = "Datos de vinculación con el paciente"
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = Color(0xFFE2E8F0).enTema()
                    )

                    ItemIncluido(
                        icono = Icons.Filled.Security,
                        texto = "Preferencias y permisos de privacidad"
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Botón Solicitar descarga
            Button(
                onClick = onSolicitarDescarga,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdeCuidador,
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.FileDownload,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Solicitar descarga",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Nota al pie
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = TextoSubtitulo,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Te enviaremos un archivo con tus datos cuando esté listo.",
                    fontSize = 13.sp,
                    color = TextoSubtitulo,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ItemIncluido(
    icono: ImageVector,
    texto: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(VerdeIconoFondo, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = VerdeCuidador,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = texto,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2D3748).enTema()
        )
    }
}