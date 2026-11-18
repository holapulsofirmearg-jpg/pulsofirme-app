package com.example.pulsofirme.screens

import androidx.compose.runtime.saveable.rememberSaveable

import com.example.pulsofirme.ui.theme.enTema

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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdePE: Color
    @Composable get() = Color(0xFF07884A).enTema()
private val VerdeClaroPE: Color
    @Composable get() = Color(0xFFE8F5EE).enTema()
private val GrisPE: Color
    @Composable get() = Color(0xFF7A8294).enTema()
@Composable
fun ProfessionalEditProfileScreen(
    nombreInicial: String = "",
    apellidoInicial: String = "",
    correoInicial: String = "",
    telefonoInicial: String = "",
    fechaNacimientoInicial: String = "",
    generoInicial: String = "",
    paisInicial: String = "",
    onBack: () -> Unit,
    onGuardarCambios: (String, String, String, String, String, String, String) -> Unit =
        { _, _, _, _, _, _, _ -> },
    onCambiarContrasena: () -> Unit = {}
) {
    var nombre by remember(nombreInicial) { mutableStateOf(nombreInicial) }
    var apellido by remember(apellidoInicial) { mutableStateOf(apellidoInicial) }
    var correo by remember(correoInicial) { mutableStateOf(correoInicial) }
    var telefono by remember(telefonoInicial) { mutableStateOf(telefonoInicial) }
    var fechaNacimiento by remember(fechaNacimientoInicial) { mutableStateOf(fechaNacimientoInicial) }
    var genero by remember(generoInicial) { mutableStateOf(generoInicial) }
    var pais by remember(paisInicial) { mutableStateOf(paisInicial) }

    val context = LocalContext.current
    val preferenciasFoto = remember {
        context.getSharedPreferences("professional_profile", android.content.Context.MODE_PRIVATE)
    }

    var fotoPerfilUri by remember {
        mutableStateOf(preferenciasFoto.getString("fotoPerfilUri", null))
    }

    val selectorFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
            }

            fotoPerfilUri = uri.toString()
            preferenciasFoto.edit()
                .putString("fotoPerfilUri", uri.toString())
                .apply()
        }
    }

    var menuGenero by rememberSaveable { mutableStateOf(false) }
    var menuPais by rememberSaveable { mutableStateOf(false) }

    Scaffold(containerColor = Color.White.enTema()) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                }

                Text(
                    text = "Tus datos como profesional",
                    color = VerdePE,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(22.dp))

            Box(
                modifier = Modifier
                    .size(92.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape)
                    .background(VerdeClaroPE)
                    .clickable {
                        selectorFoto.launch(arrayOf("image/*"))
                    },
                contentAlignment = Alignment.Center
            ) {
                if (fotoPerfilUri.isNullOrBlank()) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "Elegir foto de perfil",
                        tint = VerdePE,
                        modifier = Modifier.size(48.dp)
                    )
                } else {
                    AsyncImage(
                        model = fotoPerfilUri,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = if (fotoPerfilUri.isNullOrBlank()) "Agregar foto" else "Cambiar foto",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        selectorFoto.launch(arrayOf("image/*"))
                    }
                    .padding(vertical = 4.dp),
                color = VerdePE,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(28.dp))

            CampoProfesional(
                valor = nombre,
                onValorCambio = { nombre = it },
                etiqueta = "Nombre",
                icono = Icons.Filled.Person
            )
            CampoProfesional(
                valor = apellido,
                onValorCambio = { apellido = it },
                etiqueta = "Apellido",
                icono = Icons.Filled.Person
            )
            CampoProfesional(
                valor = correo,
                onValorCambio = { correo = it },
                etiqueta = "Correo electrónico",
                icono = Icons.Filled.Email,
                keyboardType = KeyboardType.Email
            )
            CampoProfesional(
                valor = telefono,
                onValorCambio = { telefono = it },
                etiqueta = "Número de teléfono",
                icono = Icons.Filled.Phone,
                keyboardType = KeyboardType.Phone
            )
            CampoProfesional(
                valor = fechaNacimiento,
                onValorCambio = { fechaNacimiento = it },
                etiqueta = "Fecha de nacimiento",
                icono = Icons.Filled.CalendarMonth
            )

            Box {
                CampoSeleccionProfesional(
                    texto = genero.ifBlank { "Género" },
                    icono = Icons.Filled.Male,
                    onClick = { menuGenero = true }
                )

                DropdownMenu(
                    expanded = menuGenero,
                    onDismissRequest = { menuGenero = false }
                ) {
                    listOf("Mujer", "Hombre", "No binario", "Prefiero no decirlo").forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                genero = opcion
                                menuGenero = false
                            }
                        )
                    }
                }
            }

            Box {
                CampoSeleccionProfesional(
                    texto = pais.ifBlank { "País" },
                    icono = Icons.Filled.Public,
                    onClick = { menuPais = true }
                )

                DropdownMenu(
                    expanded = menuPais,
                    onDismissRequest = { menuPais = false }
                ) {
                    listOf("Argentina", "Uruguay", "Chile", "Paraguay", "Bolivia", "Otro").forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                pais = opcion
                                menuPais = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    onGuardarCambios(
                        nombre,
                        apellido,
                        correo,
                        telefono,
                        fechaNacimiento,
                        genero,
                        pais
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDDF2E6).enTema(),
                    contentColor = VerdePE
                )
            ) {
                Text(
                    "Guardar cambios",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Lock,
                    contentDescription = null,
                    tint = GrisPE,
                    modifier = Modifier.size(19.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "¿Querés cambiar tu contraseña?",
                    color = GrisPE,
                    fontSize = 14.sp
                )
            }

            Text(
                text = "Cambiar contraseña  ›",
                modifier = Modifier
                    .clickable { onCambiarContrasena() }
                    .padding(vertical = 14.dp),
                color = VerdePE,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun CampoProfesional(
    valor: String,
    onValorCambio: (String) -> Unit,
    etiqueta: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambio,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
        placeholder = { Text(etiqueta, color = Color(0xFF9AA2B2).enTema()) },
        leadingIcon = { Icon(icono, contentDescription = null, tint = GrisPE) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(13.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VerdePE,
            unfocusedBorderColor = Color(0xFFD6DAD8).enTema()
        )
    )
}

@Composable
private fun CampoSeleccionProfesional(
    texto: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(13.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFFD6DAD8).enTema()
        ),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icono, contentDescription = null, tint = GrisPE)
            Spacer(Modifier.width(14.dp))
            Text(
                text = texto,
                modifier = Modifier.weight(1f),
                color = if (texto == "Género" || texto == "País") Color(0xFF9AA2B2).enTema() else Color(0xFF171A24).enTema()
            )
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = GrisPE)
        }
    }
}