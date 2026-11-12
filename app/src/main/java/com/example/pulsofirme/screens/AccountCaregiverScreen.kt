package com.example.pulsofirme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeCuidador = Color(0xFF1B7A4B)
private val VerdeClaroCuidador = Color(0xFFEFF6F1)

@Composable
fun AccountCaregiverScreen(
    rutaActual: String,
    onNavigate: (String) -> Unit,
    onEditarDatos: () -> Unit = {},
    onPrivacidad: () -> Unit = {},
    onAyuda: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    onProbarComoProfesional: () -> Unit = {},
    rolUsuario: String = "tutor"
) {
    var nombreTutor by remember { mutableStateOf("") }
    var nombrePaciente by remember { mutableStateOf("") }
    val usuario = FirebaseAuth.getInstance().currentUser
    val desde = remember(usuario?.uid) {
        usuario?.metadata?.creationTimestamp?.let {
            java.text.SimpleDateFormat("d 'de' MMMM 'de' yyyy", java.util.Locale("es", "AR")).format(java.util.Date(it))
        }.orEmpty()
    }

    LaunchedEffect(usuario?.uid) {
        val uid = usuario?.uid ?: return@LaunchedEffect
        val db = FirebaseFirestore.getInstance()
        db.collection("usuarios").document(uid).get().addOnSuccessListener { yo ->
            nombreTutor = listOfNotNull(yo.getString("nombre"), yo.getString("apellido"))
                .filter { it.isNotBlank() }.joinToString(" ")
            val pacUid = yo.getString("pacienteVinculadoUid").orEmpty()
            if (pacUid.isNotBlank() && rolUsuario != "profesional") {
                db.collection("usuarios").document(pacUid).get().addOnSuccessListener { pac ->
                    nombrePaciente = listOfNotNull(pac.getString("nombre"), pac.getString("apellido"))
                        .filter { it.isNotBlank() }.joinToString(" ")
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {

                val itemsMenuCuidador = listOf(
                    Triple(
                        "Inicio",
                        Icons.Filled.Home,
                        "caregiver_home"
                    ),
                    Triple(
                        "Progreso",
                        Icons.Filled.ShowChart,
                        "caregiver_progress"
                    ),
                    Triple(
                        "Alertas",
                        Icons.Filled.Notifications,
                        "caregiver_alerts"
                    ),
                    Triple(
                        "Cuenta",
                        Icons.Filled.Person,
                        "caregiver_account"
                    )
                )

                val itemsMenuProfesional = listOf(
                    Triple("Inicio", Icons.Filled.Home, "caregiver_home"),
                    Triple("Pacientes", Icons.Filled.Groups, "caregiver_patients"),
                    Triple("Progreso", Icons.Filled.ShowChart, "caregiver_progress"),
                    Triple("Cuenta", Icons.Filled.Person, "caregiver_account")
                )

                val itemsMenu = if (rolUsuario == "profesional") {
                    itemsMenuProfesional
                } else {
                    itemsMenuCuidador
                }

                itemsMenu.forEach { item ->

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
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = VerdeCuidador,
                            selectedTextColor = VerdeCuidador,
                            unselectedIconColor = Color(0xFF9CA3AF),
                            unselectedTextColor = Color(0xFF9CA3AF),
                            indicatorColor = VerdeClaroCuidador
                        )
                    )
                }
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 34.dp),
            contentPadding = PaddingValues(
                top = 18.dp,
                bottom = 20.dp
            )
        ) {

            // CUENTA
            item {

                Text(
                    text = "Cuenta",
                    fontSize = 17.sp,
                    color = Color(0xFF9CA3AF),
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(12.dp))

                // PERFIL DEL CUIDADOR
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .background(
                                VerdeCuidador,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = nombreTutor.firstOrNull()?.uppercase() ?: "C",
                            color = Color.White,
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column {

                        Text(
                            text = nombreTutor.ifBlank { "Cuidador" },
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = VerdeCuidador
                        )

                        Spacer(modifier = Modifier.height(1.dp))

                        Text(
                            text = if (desde.isBlank()) "Cuidador" else "Cuidador, desde el $desde",
                            fontSize = 15.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))
            }

            // PERFIL
            item {

                Text(
                    text = "Perfil",
                    fontSize = 18.sp,
                    color = Color(0xFF9CA3AF)
                )

                Spacer(modifier = Modifier.height(12.dp))

                FilaCuentaCuidador(
                    titulo = "Editar datos personales",
                    onClick = onEditarDatos
                )

                Spacer(modifier = Modifier.height(18.dp))

                FilaCuentaCuidador(
                    titulo = "Mi cuenta de cuidador",
                    onClick = {
                        onNavigate("caregiver_account_detail")
                    }
                )

                Spacer(modifier = Modifier.height(34.dp))
            }

            // PACIENTE VINCULADO
            item {

                Text(
                    text = "Paciente vinculado",
                    fontSize = 18.sp,
                    color = Color(0xFF9CA3AF)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            VerdeClaroCuidador,
                            RoundedCornerShape(16.dp)
                        )
                        .heightIn(min = 64.dp)
                        .clickable(role = Role.Button) {
                            onNavigate("caregiver_patient")
                        }
                        .padding(
                            horizontal = 16.dp,
                            vertical = 14.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                Color.White,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = VerdeCuidador,
                            modifier = Modifier.size(25.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Paciente vinculado",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF163B2D)
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = nombrePaciente.ifBlank { "Paciente" },
                            fontSize = 13.sp,
                            color = Color(0xFF8A9A92)
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Cambiar paciente",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VerdeCuidador,
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .clickable(role = Role.Button) {
                            onNavigate("caregiver_change_patient")
                        }
                        .padding(
                            horizontal = 4.dp,
                            vertical = 12.dp
                        )
                )

                Spacer(modifier = Modifier.height(34.dp))
            }

            // PREFERENCIAS
            item {

                Text(
                    text = "Preferencias",
                    fontSize = 18.sp,
                    color = Color(0xFF9CA3AF)
                )

                Spacer(modifier = Modifier.height(12.dp))

                FilaCuentaCuidador(
                    titulo = "Privacidad y datos",
                    onClick = onPrivacidad
                )

                Spacer(modifier = Modifier.height(18.dp))

                FilaCuentaCuidador(
                    titulo = "Ayuda y soporte",
                    onClick = onAyuda
                )

                Spacer(modifier = Modifier.height(30.dp))
            }

            // AJUSTES
            item {

                Text(
                    text = "Ajustes",
                    fontSize = 16.sp,
                    color = Color(0xFF1A1A1A),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .clickable(role = Role.Button) {
                            onNavigate("caregiver_settings")
                        }
                        .padding(vertical = 14.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Probar como profesional",
                    fontSize = 16.sp,
                    color = VerdeCuidador,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .clickable(role = Role.Button) {
                            onProbarComoProfesional()
                        }
                        .padding(vertical = 14.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Cerrar sesión",
                    fontSize = 16.sp,
                    color = Color(0xFFD64545),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .clickable(role = Role.Button) {
                            onCerrarSesion()
                        }
                        .padding(vertical = 14.dp)
                )
            }
        }
    }
}

@Composable
private fun FilaCuentaCuidador(
    titulo: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(role = Role.Button) {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = titulo,
            fontSize = 16.sp,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(22.dp)
        )
    }
}