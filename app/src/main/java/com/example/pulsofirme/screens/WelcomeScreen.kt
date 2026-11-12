package com.example.pulsofirme.screens

import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.R

private val AzulCabecera = Color(0xFF1565C0)
private val VerdeBotones = Color(0xFF237A68)
private val GrisOscuroCuidador = Color(0xFF394A51)
private val GrisSubtitulo = Color(0xFF555555)
private val GrisBordeIdioma = Color(0xFFD0D0D0)
private val VerdeClaroSeleccionado = Color(0xFFEFF6F1)

private data class Idioma(
    val nombre: String,
    val bandera: String
)

private val idiomasDisponibles = listOf(
    Idioma("Español", "🇪🇸"),
    Idioma("Inglés", "🇺🇸"),
    Idioma("Portugués", "🇧🇷"),
    Idioma("Francés", "🇫🇷"),
    Idioma("Chino", "🇨🇳")
)

@Composable
fun WelcomeScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToCaregiver: () -> Unit
) {
    var idiomaSeleccionado by remember { mutableStateOf(idiomasDisponibles.first()) }
    var mostrarSelectorIdioma by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            // Curva azul superior (cae suave hacia la derecha)
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width, size.height * 0.55f)
                    quadraticTo(
                        size.width * 0.45f, size.height * 1.15f,
                        0f, size.height * 0.85f
                    )
                    close()
                }
                drawPath(
                    path = path,
                    color = AzulCabecera
                )
            }

            // Contenido Principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo de la mano
                Image(
                    painter = painterResource(id = R.drawable.icon2), // Reemplazá ic_logo_heart por tu drawable exacto de la mano
                    contentDescription = "Logo Pulso Firme",
                    modifier = Modifier.size(110.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Título principal
                Text(
                    text = "Bienvenido",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Subtítulo
                Text(
                    text = "Tu bienestar en tus manos",
                    fontSize = 17.sp,
                    color = GrisSubtitulo,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Fila de Registrarse + Acceder como cuidador
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Botón Registrarse
                    Button(
                        onClick = onNavigateToRegister,
                        modifier = Modifier
                            .weight(1f)
                            .height(95.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeBotones),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Color.White, RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = null,
                                    tint = VerdeBotones,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Registrarse",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Botón Acceder como cuidador
                    Button(
                        onClick = onNavigateToCaregiver,
                        modifier = Modifier
                            .weight(1f)
                            .height(95.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GrisOscuroCuidador),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Acceder como\ncuidador",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Botón Iniciar sesión (Ancho completo)
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBotones)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Iniciar sesión",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Selector de Idioma
                OutlinedButton(
                    onClick = { mostrarSelectorIdioma = !mostrarSelectorIdioma },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, GrisBordeIdioma),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Language,
                            contentDescription = null,
                            tint = VerdeBotones,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = idiomaSeleccionado.nombre,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (mostrarSelectorIdioma) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = null,
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (mostrarSelectorIdioma) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(14.dp))
                            .background(
                                androidx.compose.ui.graphics.Color.Transparent
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(14.dp))
                        ) {
                            idiomasDisponibles.forEach { idioma ->
                                val seleccionado = idioma.nombre == idiomaSeleccionado.nombre

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (seleccionado) VerdeClaroSeleccionado else Color.White
                                        )
                                        .clickable {
                                            idiomaSeleccionado = idioma
                                            mostrarSelectorIdioma = false
                                        }
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = idioma.bandera,
                                        fontSize = 20.sp
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = idioma.nombre,
                                        fontSize = 16.sp,
                                        fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                                        color = if (seleccionado) VerdeBotones else Color.Black,
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (seleccionado) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = VerdeBotones,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                if (idioma != idiomasDisponibles.last()) {
                                    HorizontalDivider(color = Color(0xFFEFEFEF))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}