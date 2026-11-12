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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.ProgresoData
import com.example.pulsofirme.UserData

private val VerdeAl = Color(0xFF1B7A4B)
private val VerdeOscuroAl = Color(0xFF1B4332)
private val VerdeSuaveAl = Color(0xFFEAF5EC)
private val GrisAl = Color(0xFF6B7280)
private val NegroAl = Color(0xFF1A1A1A)
private val AmarilloAl = Color(0xFFE0A73A)

@Composable
fun AlertsScreen(
    rutaActual: String,
    onNavigate: (String) -> Unit,
    onConfigClick: () -> Unit = {},
    guanteConectado: Boolean = false
) {
    val horarios = UserData.horariosMedicacion
    val hechos = ProgresoData.ejerciciosCompletados

    val textoMedicacion = if (horarios.isEmpty()) {
        "Todavía no configuraste tus horarios"
    } else {
        "Es hora de tu dosis de las ${horarios.first()} hs"
    }

    val textoEjercicio = if (hechos == 0) {
        "Todavía no hiciste ejercicios hoy"
    } else {
        "Llevás $hechos/$hechos ejercicios de hoy"
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                itemsMenuInferior.forEach { item ->
                    NavigationBarItem(
                        selected = rutaActual == item.ruta,
                        onClick = { onNavigate(item.ruta) },
                        icon = { Icon(item.icono, contentDescription = item.label) },
                        label = { Text(item.label, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = VerdeAl,
                            selectedTextColor = VerdeAl,
                            unselectedIconColor = Color(0xFF9CA3AF),
                            unselectedTextColor = Color(0xFF9CA3AF),
                            indicatorColor = VerdeSuaveAl
                        )
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text("Alertas", fontSize = 15.sp, color = GrisAl)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Al día con tu rutina",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = VerdeOscuroAl
                    )
                }
                IconButton(onClick = onConfigClick) {
                    Icon(Icons.Filled.Settings, contentDescription = "Configuración", tint = GrisAl)
                }
            }

            Spacer(Modifier.height(20.dp))

            // ---------- HOY ----------
            Text("Hoy", fontSize = 14.sp, color = GrisAl)
            Spacer(Modifier.height(10.dp))

            ItemAlerta(
                icono = Icons.Filled.Medication,
                colorIcono = AmarilloAl,
                titulo = "Medicación",
                detalle = textoMedicacion,
                destacada = true,
                puntoVerde = true
            )

            Spacer(Modifier.height(10.dp))

            ItemAlerta(
                icono = Icons.Filled.Bolt,
                colorIcono = VerdeAl,
                titulo = "Ejercicio pendiente",
                detalle = textoEjercicio,
                destacada = true,
                puntoVerde = true
            )

            Spacer(Modifier.height(10.dp))

            ItemAlerta(
                icono = Icons.Filled.EmojiEvents,
                colorIcono = VerdeAl,
                titulo = "Nueva racha",
                detalle = "Vas por el primer día",
                destacada = false,
                puntoVerde = false
            )

            Spacer(Modifier.height(10.dp))

            ItemAlerta(
                icono = Icons.Filled.PersonAdd,
                colorIcono = VerdeAl,
                titulo = "Todavía no tenés un cuidador",
                detalle = "Vinculá a un familiar o profesional para que siga tu progreso",
                destacada = false,
                puntoVerde = false
            )

            // ---------- ESTA SEMANA ----------
            if (guanteConectado) {
                Spacer(Modifier.height(24.dp))

                Text("Esta semana", fontSize = 14.sp, color = GrisAl)
                Spacer(Modifier.height(10.dp))

                ItemAlerta(
                    icono = Icons.Filled.Bluetooth,
                    colorIcono = VerdeAl,
                    titulo = "Guante emparejado",
                    detalle = "Se conectó correctamente a tu mano derecha",
                    destacada = false,
                    puntoVerde = false
                )
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
private fun ItemAlerta(
    icono: ImageVector,
    colorIcono: Color,
    titulo: String,
    detalle: String,
    destacada: Boolean,
    puntoVerde: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (destacada) VerdeSuaveAl else Color.Transparent)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(if (destacada) Color.White else VerdeSuaveAl),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(19.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(titulo, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = NegroAl)
            Spacer(Modifier.height(2.dp))
            Text(detalle, fontSize = 13.sp, color = GrisAl, lineHeight = 18.sp)
        }
        if (puntoVerde) {
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(VerdeAl)
            )
        }
    }
}