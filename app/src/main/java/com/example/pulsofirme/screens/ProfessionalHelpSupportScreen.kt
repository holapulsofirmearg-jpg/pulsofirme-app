package com.example.pulsofirme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.R

private val VerdeAPS = Color(0xFF07884A)
private val VerdeClaroAPS = Color(0xFFEAF5EE)
private val AzulAPS = Color(0xFF17365D)
private val AzulBotonAPS = Color(0xFF315E96)
private val GrisAPS = Color(0xFF7A8294)
private val BordeAPS = Color(0xFFE0E4EA)

@Composable
fun ProfessionalHelpSupportScreen(
    onBack: () -> Unit,
    onPreguntasFrecuentes: () -> Unit = {},
    onGestionarPacientes: () -> Unit = {},
    onInterpretarProgreso: () -> Unit = {},
    onPremiumProfesional: () -> Unit = {},
    onReportarProblema: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp)
    ) {
        Spacer(Modifier.height(42.dp))

        // CABECERA
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = VerdeAPS
                )
            }

            Spacer(Modifier.width(2.dp))

            Text(
                text = "Ayuda y soporte",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeAPS
            )
        }

        Spacer(Modifier.height(22.dp))

        // AYUDA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(VerdeClaroAPS)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.HeadsetMic,
                    contentDescription = null,
                    tint = VerdeAPS,
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column {
                Text(
                    text = "¿Necesitás ayuda?",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulAPS
                )

                Spacer(Modifier.height(3.dp))

                Text(
                    text = "Nuestro equipo responde en menos de 24 hs.",
                    fontSize = 13.sp,
                    color = GrisAPS
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = "Contacto",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeAPS
        )

        Spacer(Modifier.height(12.dp))

        // CONTACTO + INFORMACIÓN DE PRUEBA
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(17.dp))
                .background(Color.White)
                .then(
                    Modifier
                )
                .padding(16.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(17.dp),
                color = Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(1.dp, BordeAPS)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.iconoinicio),
                            contentDescription = "Logo PulsoFirme",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )

                        Spacer(Modifier.width(13.dp))

                        Column {
                            Text(
                                text = "PulsoFirme",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = AzulAPS
                            )

                            Spacer(Modifier.height(2.dp))

                            Text(
                                text = "holapulsofirmearg@gmail.com",
                                fontSize = 13.sp,
                                color = GrisAPS
                            )
                        }
                    }

                    Spacer(Modifier.height(17.dp))

                    HorizontalDivider(color = BordeAPS)

                    Spacer(Modifier.height(15.dp))

                    Text(
                        text = "Versión de prueba",
                        fontSize = 13.sp,
                        color = GrisAPS
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Esta es una versión de demostración desarrollada por estudiantes de la Escuela EXPERIMENTAL PROA. " +
                                "La cuenta profesional de PulsoFirme permite gestionar pacientes, consultar su progreso y acompañar su seguimiento. " +
                                "PulsoFirme es una herramienta de apoyo y no reemplaza el criterio ni la indicación de un profesional de la salud.",
                        fontSize = 14.sp,
                        color = Color(0xFF262626),
                        lineHeight = 20.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        Text(
            text = "Recursos para profesionales",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeAPS
        )

        Spacer(Modifier.height(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(17.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, BordeAPS)
        ) {
            Column {
                RecursoProfesionalAPS(
                    icono = Icons.Filled.MenuBook,
                    titulo = "Preguntas frecuentes para profesionales",
                    descripcion = "Respondemos las dudas más comunes.",
                    onClick = onPreguntasFrecuentes
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = BordeAPS
                )

                RecursoProfesionalAPS(
                    icono = Icons.Filled.Groups,
                    titulo = "Cómo gestionar pacientes",
                    descripcion = "Vincular, ver y administrar pacientes.",
                    onClick = onGestionarPacientes
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = BordeAPS
                )

                RecursoProfesionalAPS(
                    icono = Icons.Filled.BarChart,
                    titulo = "Cómo interpretar el progreso",
                    descripcion = "Entendé los datos y reportes.",
                    onClick = onInterpretarProgreso
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = BordeAPS
                )

                RecursoProfesionalAPS(
                    icono = Icons.Filled.WorkspacePremium,
                    titulo = "Premium Profesional",
                    descripcion = "Conocé los beneficios de la versión completa.",
                    onClick = onPremiumProfesional
                )
            }
        }

        Spacer(Modifier.height(22.dp))

        Button(
            onClick = onReportarProblema,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(13.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulBotonAPS
            )
        ) {
            Text(
                text = "Reportar un problema",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun RecursoProfesionalAPS(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(VerdeClaroAPS),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = VerdeAPS,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.width(13.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = titulo,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = AzulAPS,
                lineHeight = 19.sp
            )

            Spacer(Modifier.height(3.dp))

            Text(
                text = descripcion,
                fontSize = 12.5.sp,
                color = GrisAPS,
                lineHeight = 17.sp
            )
        }

        Spacer(Modifier.width(8.dp))

        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = GrisAPS
        )
    }
}
