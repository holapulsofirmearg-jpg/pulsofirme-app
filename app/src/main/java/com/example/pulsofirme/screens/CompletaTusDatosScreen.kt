package com.example.pulsofirme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.R

private val VerdePrincipal = Color(0xFF006C3B)
private val VerdeClaroChip = Color(0xFFE8F5E9)
private val VerdeTextoChip = Color(0xFF1B5E20)
private val GrisTexto = Color(0xFF6E6E6E)
private val GrisBorde = Color(0xFFE0E0E0)
private val GrisFondoChips = Color(0xFFF8F9FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletaTusDatosScreen(
    onBack: () -> Unit = {},
    onGuardarYContinuar: () -> Unit = {}
) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var relacionSeleccionada by remember { mutableStateOf("") }
    var expandedDropdown by remember { mutableStateOf(false) }

    val opcionesRelacion = listOf("Familiar", "Amigo/a", "Cuidador/a", "Otro")

    Scaffold(
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = painterResource(id = R.drawable.logooo),
                contentDescription = "Logo Pulso Firme",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(90.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Completá tus datos",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = VerdePrincipal,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Necesitamos algunos datos para conocerte\ny mantener tu información actualizada.",
                fontSize = 13.sp,
                color = GrisTexto,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Insignia: Paso único
            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .background(VerdeClaroChip, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(VerdePrincipal, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Paso único",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = VerdeTextoChip
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Campo Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                placeholder = { Text("Nombre completo", color = GrisTexto) },
                leadingIcon = {
                    Icon(Icons.Outlined.Person, contentDescription = null, tint = GrisTexto)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = GrisBorde,
                    focusedBorderColor = VerdePrincipal
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo Teléfono
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                placeholder = { Text("Teléfono", color = GrisTexto) },
                leadingIcon = {
                    Icon(Icons.Outlined.Call, contentDescription = null, tint = GrisTexto)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = GrisBorde,
                    focusedBorderColor = VerdePrincipal
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo Fecha de Nacimiento
            OutlinedTextField(
                value = fechaNacimiento,
                onValueChange = { fechaNacimiento = it },
                placeholder = { Text("Fecha de nacimiento", color = GrisTexto) },
                leadingIcon = {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = GrisTexto)
                },
                trailingIcon = {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = GrisTexto)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = GrisBorde,
                    focusedBorderColor = VerdePrincipal
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Menu Desplegable Relación
            ExposedDropdownMenuBox(
                expanded = expandedDropdown,
                onExpandedChange = { expandedDropdown = !expandedDropdown }
            ) {
                OutlinedTextField(
                    value = if (relacionSeleccionada.isEmpty()) "Selecciona una opción" else relacionSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Relación con la persona que cuidás", color = Color.Black, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Outlined.People, contentDescription = null, tint = GrisTexto)
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = GrisBorde,
                        focusedBorderColor = VerdePrincipal
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedDropdown,
                    onDismissRequest = { expandedDropdown = false }
                ) {
                    opcionesRelacion.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                relacionSeleccionada = opcion
                                expandedDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chips / Botones Rápidos de Selección
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OpciónChip("Familiar", Icons.Outlined.People, relacionSeleccionada == "Familiar", Modifier.weight(1f)) {
                    relacionSeleccionada = "Familiar"
                }
                OpciónChip("Amigo/a", Icons.Outlined.Person, relacionSeleccionada == "Amigo/a", Modifier.weight(1f)) {
                    relacionSeleccionada = "Amigo/a"
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OpciónChip("Cuidador/a", Icons.Outlined.FavoriteBorder, relacionSeleccionada == "Cuidador/a", Modifier.weight(1f)) {
                    relacionSeleccionada = "Cuidador/a"
                }
                OpciónChip("Otro", Icons.Filled.MoreHoriz, relacionSeleccionada == "Otro", Modifier.weight(1f)) {
                    relacionSeleccionada = "Otro"
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Botón Guardar y Continuar
            Button(
                onClick = onGuardarYContinuar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Guardar y continuar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OpciónChip(
    texto: String,
    icono: ImageVector,
    seleccionado: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colorBorde = if (seleccionado) VerdePrincipal else GrisBorde
    val colorTexto = if (seleccionado) VerdePrincipal else Color.Black
    val colorIcono = if (seleccionado) VerdePrincipal else GrisTexto

    Surface(
        modifier = modifier
            .height(44.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, colorBorde),
        color = GrisFondoChips
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = colorIcono,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = texto,
                fontSize = 12.sp,
                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                color = colorTexto
            )
        }
    }
}