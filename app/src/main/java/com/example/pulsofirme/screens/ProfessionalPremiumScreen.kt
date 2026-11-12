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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.R

private val VerdePremiumPro = Color(0xFF0A8F50)
private val VerdeOscuroPremiumPro = Color(0xFF18513B)
private val VerdeClaroPremiumPro = Color(0xFFEAF6EF)
private val DoradoPremiumPro = Color(0xFFC89418)
private val DoradoClaroPremiumPro = Color(0xFFFFF6E2)
private val GrisPremiumPro = Color(0xFF6B7280)

@Composable
fun ProfessionalPremiumScreen(
    onBack: () -> Unit,
    onActivarPremium: (String) -> Unit = {}
) {
    var planSeleccionado by remember { mutableStateOf("anual") }
    var mostrarConfirmacionPremium by remember { mutableStateOf(false) }

    Scaffold(containerColor = Color.White) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VerdePremiumPro)
                    .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 22.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White
                            )
                        }
                        Text(
                            "Premium Profesional",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .background(DoradoPremiumPro, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.WorkspacePremium,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(Modifier.width(14.dp))

                        Text(
                            "Llevá tu práctica\nal siguiente nivel",
                            color = Color.White,
                            fontSize = 27.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        // IMPORTANTE:
                        // Guardá chicaprofesional.png en:
                        // app/src/main/res/drawable/chicaprofesional.png
                        Image(
                            painter = painterResource(id = R.drawable.chicaprofesional),
                            contentDescription = "Profesional de la salud",
                            modifier = Modifier.size(115.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "La suscripción Premium Profesional te brinda herramientas avanzadas para acompañar mejor a tus pacientes y optimizar tu trabajo.",
                        color = Color.White,
                        fontSize = 15.sp,
                        lineHeight = 21.sp
                    )
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 22.dp)
            ) {
                Text(
                    "¿Por qué elegir Premium Profesional?",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdeOscuroPremiumPro
                )
                Text(
                    "Accedé a funciones exclusivas diseñadas para profesionales de la salud.",
                    fontSize = 14.sp,
                    color = GrisPremiumPro
                )

                Spacer(Modifier.height(18.dp))

                BeneficioPremiumPro(
                    Icons.Filled.Groups,
                    "Vinculá más de un paciente",
                    "Ampliá tu plan y gestioná todos tus pacientes en un solo lugar, de forma simple y organizada."
                )
                BeneficioPremiumPro(
                    Icons.Filled.PictureAsPdf,
                    "Exportá reportes en PDF",
                    "Generá informes completos de cada paciente con su progreso, sesiones y evolución para compartir."
                )
                BeneficioPremiumPro(
                    Icons.Filled.BarChart,
                    "Panel comparativo entre pacientes",
                    "Visualizá y compará el progreso de todos tus pacientes en una sola pantalla."
                )
                BeneficioPremiumPro(
                    Icons.Filled.Tune,
                    "Herramientas avanzadas de seguimiento",
                    "Accedé a estadísticas detalladas, filtros y análisis para mejorar el seguimiento clínico."
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    "Elegí tu plan",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdeOscuroPremiumPro
                )

                Spacer(Modifier.height(12.dp))

                PlanPremiumPro(
                    titulo = "Anual",
                    detalle = "por mes · $142.800 al año, en un solo pago",
                    precio = "$11.900",
                    seleccionado = planSeleccionado == "anual",
                    etiqueta = "2 meses gratis",
                    onClick = { planSeleccionado = "anual" }
                )

                Spacer(Modifier.height(10.dp))

                PlanPremiumPro(
                    titulo = "Mensual",
                    detalle = "por mes · cancelás cuando quieras",
                    precio = "$14.900",
                    seleccionado = planSeleccionado == "mensual",
                    onClick = { planSeleccionado = "mensual" }
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    "Precios en pesos argentinos.",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 12.sp,
                    color = GrisPremiumPro
                )

                Spacer(Modifier.height(18.dp))

                Button(
                    onClick = {
                        mostrarConfirmacionPremium = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DoradoPremiumPro)
                ) {
                    Text(
                        "Activar Premium Profesional",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    "Podés cancelar en cualquier momento desde Ajustes.\nLa suscripción Premium Profesional es independiente de la suscripción de los pacientes.",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = GrisPremiumPro
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (mostrarConfirmacionPremium) {
        AlertDialog(
            onDismissRequest = {
                mostrarConfirmacionPremium = false
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.WorkspacePremium,
                    contentDescription = null,
                    tint = DoradoPremiumPro,
                    modifier = Modifier.size(38.dp)
                )
            },
            title = {
                Text(
                    text = "¡Ya sos Premium Profesional!",
                    fontWeight = FontWeight.Bold,
                    color = VerdeOscuroPremiumPro
                )
            },
            text = {
                Text(
                    text = "Se activó tu plan Premium Profesional. Ya podés acceder a todas las funciones avanzadas de PulsoFirme."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacionPremium = false
                        onActivarPremium(planSeleccionado)
                    }
                ) {
                    Text(
                        text = "Entendido",
                        color = VerdePremiumPro,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }
}

@Composable
private fun BeneficioPremiumPro(
    icono: ImageVector,
    titulo: String,
    descripcion: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5FAF7))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(VerdeClaroPremiumPro, RoundedCornerShape(13.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = VerdePremiumPro)
            }

            Spacer(Modifier.width(13.dp))

            Column {
                Text(
                    titulo,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF151515)
                )
                Text(
                    descripcion,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = GrisPremiumPro
                )
            }
        }
    }
}

@Composable
private fun PlanPremiumPro(
    titulo: String,
    detalle: String,
    precio: String,
    seleccionado: Boolean,
    etiqueta: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) DoradoClaroPremiumPro else Color.White
        ),
        border = CardDefaults.outlinedCardBorder(
            enabled = true
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (seleccionado) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(DoradoPremiumPro, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.White, CircleShape)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    if (etiqueta != null) {
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            color = DoradoPremiumPro,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                etiqueta,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    detalle,
                    fontSize = 12.sp,
                    color = GrisPremiumPro
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    precio,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (seleccionado) DoradoPremiumPro else Color(0xFF151515)
                )
                Text("por mes", fontSize = 11.sp, color = GrisPremiumPro)
            }
        }
    }
}
