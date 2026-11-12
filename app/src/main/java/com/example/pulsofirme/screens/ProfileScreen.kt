package com.example.pulsofirme.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.R
import com.example.pulsofirme.UserData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val VerdeBoton = Color(0xFF2E9E63)
private val VerdeTexto = Color(0xFF1B7A4B)
private val AzulCabecera = Color(0xFF1565C0)

private val paises = listOf(
    "Argentina", "Bolivia", "Brasil", "Chile", "Colombia", "Ecuador",
    "España", "México", "Paraguay", "Perú", "Uruguay", "Venezuela"
)

private val generos = listOf("Mujer", "Hombre", "No binario", "Prefiero no decir")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    nombre: String,
    onBack: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit,
    onLater: () -> Unit
) {
    var fechaNacimiento by remember { mutableStateOf("") }
    var pais by remember { mutableStateOf("") }
    var menuPaises by remember { mutableStateOf(false) }
    var generoElegido by remember { mutableStateOf("Prefiero no decir") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var mostrarCalendario by remember { mutableStateOf(false) }

    val estadoScroll = rememberScrollState()
    val datePickerState = rememberDatePickerState()
    val interactionSourceFecha = remember { MutableInteractionSource() }
    val presionado by interactionSourceFecha.collectIsPressedAsState()

    LaunchedEffect(presionado) {
        if (presionado) mostrarCalendario = true
    }

    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formato = SimpleDateFormat("dd/MM/yyyy", Locale("es", "AR"))
                        fechaNacimiento = formato.format(Date(millis))
                    }
                    mostrarCalendario = false
                }) {
                    Text("Aceptar", color = VerdeTexto)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) {
                    Text("Cancelar", color = Color(0xFF6B7280))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(estadoScroll)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val ancho = size.width
                val alto = size.height
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(ancho, 0f)
                    lineTo(ancho, alto * 0.62f)
                    cubicTo(
                        ancho * 0.62f, alto * 1.08f,
                        ancho * 0.35f, alto * 0.60f,
                        0f, alto * 0.95f
                    )
                    close()
                }
                drawPath(path = path, color = AzulCabecera)
            }

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 10.dp, top = 62.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "Completá tu perfil",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 68.dp)
            )
        }

        Image(
            painter = painterResource(id = R.drawable.icon2),
            contentDescription = "Logo PulsoFirme",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(110.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = if (nombre.isBlank()) "¡Hola!" else "Hola, $nombre",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "¡Completa estos datos para tener una mejor experiencia!",
            fontSize = 14.sp,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(22.dp))

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {

            OutlinedTextField(
                value = fechaNacimiento,
                onValueChange = { },
                readOnly = true,
                placeholder = { Text("Fecha de nacimiento", color = Color(0xFF9AA0A6), fontSize = 15.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = VerdeTexto,
                        modifier = Modifier.size(22.dp)
                    )
                },
                interactionSource = interactionSourceFecha,
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VerdeTexto,
                    unfocusedBorderColor = Color(0xFFD8DED9),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .border(1.dp, Color(0xFFD8DED9), RoundedCornerShape(12.dp))
                        .clickable { menuPaises = true }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🌐", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (pais.isBlank()) "País" else pais,
                        fontSize = 15.sp,
                        color = if (pais.isBlank()) Color(0xFF9AA0A6) else Color(0xFF1A1A1A),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = Color(0xFF6B7280)
                    )
                }

                DropdownMenu(
                    expanded = menuPaises,
                    onDismissRequest = { menuPaises = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    paises.forEach { p ->
                        DropdownMenuItem(
                            text = { Text(text = p, fontSize = 15.sp) },
                            onClick = {
                                pais = p
                                menuPaises = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Género",
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(8.dp))

            generos.forEach { g ->
                val seleccionado = generoElegido == g
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .padding(bottom = 8.dp)
                        .border(
                            width = if (seleccionado) 2.dp else 1.dp,
                            color = if (seleccionado) VerdeTexto else Color(0xFFD8DED9),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { generoElegido = g },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = g,
                        fontSize = 15.sp,
                        color = if (seleccionado) VerdeTexto else Color(0xFF3D3D3D)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            CampoPerfil(
                valor = telefono,
                onCambio = { telefono = it },
                placeholder = "Teléfono de contacto",
                icono = Icons.Default.Phone
            )

            Spacer(modifier = Modifier.height(12.dp))

            CampoPerfil(
                valor = direccion,
                onCambio = { direccion = it },
                placeholder = "Dirección de hogar",
                icono = Icons.Default.Home,
                textoDerecha = "Opcional"
            )

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = {
                    UserData.genero = generoElegido
                    onSave(
                        fechaNacimiento,
                        pais,
                        generoElegido,
                        telefono,
                        direccion
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeBoton)
            ) {
                Text(
                    text = "Guardar y continuar",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Completar más tarde",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.clickable { onLater() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CampoPerfil(
    valor: String,
    onCambio: (String) -> Unit,
    placeholder: String,
    icono: ImageVector,
    textoDerecha: String? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        placeholder = {
            Text(text = placeholder, color = Color(0xFF9AA0A6), fontSize = 15.sp)
        },
        leadingIcon = {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = VerdeTexto,
                modifier = Modifier.size(22.dp)
            )
        },
        trailingIcon = {
            if (textoDerecha != null) {
                Text(
                    text = textoDerecha,
                    fontSize = 13.sp,
                    color = Color(0xFF9AA0A6),
                    modifier = Modifier.padding(end = 14.dp)
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VerdeTexto,
            unfocusedBorderColor = Color(0xFFD8DED9),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}