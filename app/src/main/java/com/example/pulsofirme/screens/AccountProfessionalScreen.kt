package com.example.pulsofirme.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeCP = Color(0xFF07884A)
private val VerdeClaroCP = Color(0xFFEAF8F0)
private val GrisCP = Color(0xFF7A8294)

@Composable
fun AccountProfessionalScreen(
    nombreProfesional: String,
    matricula: String = "MP 12345",
    lugarTrabajo: String = "Hospital San Martín",
    especialidad: String = "Neurología – Rehabilitación",
    rutaActual: String = "caregiver_account",
    onNavigate: (String) -> Unit,
    onEditarDatos: () -> Unit = {},
    onDatosProfesionales: () -> Unit = {},
    onGestionarPlan: () -> Unit = {},
    onPrivacidad: () -> Unit = {},
    onAyuda: () -> Unit = {},
    onCambiarContrasena: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    Scaffold(
        containerColor = Color.White,

        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
                val menu = listOf(
                    Triple(
                        "Inicio",
                        Icons.Filled.Home,
                        "caregiver_home"
                    ),
                    Triple(
                        "Pacientes",
                        Icons.Filled.Groups,
                        "caregiver_patients"
                    ),
                    Triple(
                        "Progreso",
                        Icons.Filled.ShowChart,
                        "caregiver_progress"
                    ),
                    Triple(
                        "Cuenta",
                        Icons.Filled.Person,
                        "caregiver_account"
                    )
                )

                menu.forEach { item ->

                    NavigationBarItem(
                        selected = rutaActual == item.third,

                        onClick = {
                            onNavigate(item.third)
                        },

                        icon = {
                            Icon(
                                imageVector = item.second,
                                contentDescription = item.first
                            )
                        },

                        label = {
                            Text(
                                text = item.first,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        },

                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = VerdeCP,
                            selectedTextColor = VerdeCP,
                            unselectedIconColor = GrisCP,
                            unselectedTextColor = GrisCP,
                            indicatorColor = VerdeClaroCP
                        )
                    )
                }
            }
        }
    ) { padding ->

        // =====================================================
        // CONTENIDO CON SCROLL
        // =====================================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 26.dp,
                    vertical = 20.dp
                )
        ) {

            Text(
                text = "Cuenta",
                fontSize = 16.sp,
                color = GrisCP
            )

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            // =================================================
            // PERFIL PROFESIONAL
            // =================================================

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .background(
                            VerdeCP,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = inicialesProfesionalCuenta(
                            nombreProfesional
                        ),
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.width(18.dp)
                )

                Column {

                    Text(
                        text = nombreProfesional,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = VerdeCP
                    )

                    Text(
                        text = "Profesional de salud",
                        fontSize = 14.sp,
                        color = GrisCP
                    )

                    Text(
                        text = "Matrícula: $matricula",
                        fontSize = 14.sp,
                        color = GrisCP
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            // =================================================
            // PERFIL
            // =================================================

            TituloCP("Perfil")

            OpcionCP(
                texto = "Editar datos personales",
                onClick = onEditarDatos
            )

            OpcionCP(
                texto = "Datos profesionales",
                onClick = onDatosProfesionales
            )

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            // =================================================
            // INFORMACIÓN PROFESIONAL
            // =================================================

            TituloCP(
                "Información profesional"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = VerdeClaroCP
                )
            ) {

                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Filled.Apartment,
                        contentDescription = null,
                        tint = VerdeCP,
                        modifier = Modifier.size(30.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(14.dp)
                    )

                    Column {

                        Text(
                            text = lugarTrabajo,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = especialidad,
                            fontSize = 13.sp,
                            color = GrisCP
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            // =================================================
            // PLAN
            // =================================================

            TituloCP("Plan")

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onGestionarPlan()
                    },

                shape = RoundedCornerShape(14.dp),

                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF7FAF8)
                )
            ) {

                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Filled.Groups,
                        contentDescription = null,
                        tint = VerdeCP
                    )

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Plan actual",
                            fontSize = 13.sp,
                            color = GrisCP
                        )

                        Text(
                            text = "Profesional · 5 pacientes",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = GrisCP
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            // =================================================
            // PREFERENCIAS
            // =================================================

            TituloCP("Preferencias")

            OpcionCP(
                texto = "Privacidad y datos",
                onClick = onPrivacidad
            )

            OpcionCP(
                texto = "Ayuda y soporte",
                onClick = onAyuda
            )

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            // =================================================
            // AJUSTES
            // =================================================

            TituloCP("Ajustes")

            OpcionCP(
                texto = "Cambiar contraseña",
                onClick = onCambiarContrasena
            )

            OpcionCP(
                texto = "Cerrar sesión",
                onClick = onCerrarSesion,
                color = Color(0xFFB42318)
            )

            // Espacio final para que Cerrar sesión
            // no quede pegado al menú inferior.
            Spacer(
                modifier = Modifier.height(30.dp)
            )
        }
    }
}


// =============================================================
// TÍTULO DE SECCIÓN
// =============================================================

@Composable
private fun TituloCP(
    texto: String
) {

    Text(
        text = texto,
        fontSize = 16.sp,
        color = GrisCP
    )

    Spacer(
        modifier = Modifier.height(7.dp)
    )
}


// =============================================================
// OPCIÓN
// =============================================================

@Composable
private fun OpcionCP(
    texto: String,
    onClick: () -> Unit,
    color: Color = Color(0xFF171A24)
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(vertical = 10.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = texto,
            modifier = Modifier.weight(1f),
            fontSize = 15.sp,
            color = color
        )

        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = GrisCP
        )
    }
}


// =============================================================
// INICIALES DEL PROFESIONAL
// =============================================================

private fun inicialesProfesionalCuenta(
    nombre: String
): String {

    return nombre
        .split(" ")
        .filter {
            it.isNotBlank()
        }
        .take(2)
        .joinToString("") {
            it.first()
                .uppercaseChar()
                .toString()
        }
}