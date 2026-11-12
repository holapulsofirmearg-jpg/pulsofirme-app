package com.example.pulsofirme.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.UserData

private val VerdeEd = Color(0xFF1B7A4B)
private val VerdeOscuroEd = Color(0xFF1B4332)
private val VerdeSuaveEd = Color(0xFFEAF5EC)
private val BordeEd = Color(0xFFD8DED9)
private val GrisEd = Color(0xFF6B7280)
private val NegroEd = Color(0xFF1A1A1A)

private val paisesEd = listOf(
    "Argentina", "Bolivia", "Brasil", "Chile", "Colombia", "Ecuador",
    "España", "México", "Paraguay", "Perú", "Uruguay", "Venezuela"
)

private val generosEd = listOf("Mujer", "Hombre", "No binario", "Prefiero no decir")

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    nombreInicial: String = "",
    correoInicial: String = "",
    fechaInicial: String = "",
    paisInicial: String = "",
    telefonoInicial: String = "",
    direccionInicial: String = "",
    generoInicial: String = "",
    onGuardarCambios: (String, String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _ -> }
) {
    val contexto = LocalContext.current

    var nombre by remember(nombreInicial) { mutableStateOf(nombreInicial.ifBlank { UserData.nombreUsuario }) }
    var correo by remember(correoInicial) { mutableStateOf(correoInicial.ifBlank { UserData.correo }) }
    var fecha by remember(fechaInicial) { mutableStateOf(fechaInicial.ifBlank { UserData.fechaNacimiento }) }
    var pais by remember(paisInicial) { mutableStateOf(paisInicial.ifBlank { UserData.pais }) }
    var telefono by remember(telefonoInicial) { mutableStateOf(telefonoInicial.ifBlank { UserData.telefono }) }
    var direccion by remember(direccionInicial) { mutableStateOf(direccionInicial.ifBlank { UserData.direccion }) }
    var genero by remember(generoInicial) { mutableStateOf(generoInicial.ifBlank { UserData.genero }) }
    var fotoUri by remember { mutableStateOf(UserData.fotoPerfil) }

    var menuPaises by remember { mutableStateOf(false) }
    var guardado by remember { mutableStateOf(false) }

    // ---------- Selector de imagen de la galería ----------
    val selectorFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            fotoUri = uri.toString()
            UserData.fotoPerfil = uri.toString()
            guardado = false
        }
    }

    // ---------- Bitmap de la foto elegida ----------
    val bitmapFoto: Bitmap? = remember(fotoUri) {
        if (fotoUri.isBlank()) {
            null
        } else {
            try {
                val uri = Uri.parse(fotoUri)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val fuente = ImageDecoder.createSource(contexto.contentResolver, uri)
                    ImageDecoder.decodeBitmap(fuente) { decoder, _, _ ->
                        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                        decoder.isMutableRequired = false
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(contexto.contentResolver, uri)
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    // ---------- Iniciales para cuando no hay foto ----------
    val iniciales = remember(nombre) {
        nombre.trim()
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(48.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = VerdeEd
                )
            }
            Spacer(Modifier.width(4.dp))
            Column {
                Text("Perfil", fontSize = 13.sp, color = GrisEd)
                Text(
                    text = "Tus datos personales",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdeOscuroEd
                )
            }
        }

        Spacer(Modifier.height(22.dp))

        // ---------- FOTO DE PERFIL ----------
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .clickable { selectorFoto.launch("image/*") }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(VerdeSuaveEd)
                        .border(3.dp, VerdeEd, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (bitmapFoto != null) {
                        Image(
                            bitmap = bitmapFoto.asImageBitmap(),
                            contentDescription = "Foto de perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else if (iniciales.isNotBlank()) {
                        Text(
                            text = iniciales,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = VerdeEd
                        )
                    } else {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = null,
                            tint = VerdeEd,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(VerdeEd)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Cambiar foto",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = if (fotoUri.isBlank()) "Tocá para elegir una foto"
                else "Tocá para cambiar la foto",
                fontSize = 13.sp,
                color = GrisEd
            )

            if (fotoUri.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Quitar foto",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VerdeEd,
                    modifier = Modifier.clickable {
                        fotoUri = ""
                        UserData.fotoPerfil = ""
                        guardado = false
                    }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        CampoEditable(
            valor = nombre,
            onCambio = { nombre = it; guardado = false },
            etiqueta = "Nombre completo",
            icono = Icons.Filled.Person
        )

        Spacer(Modifier.height(14.dp))

        CampoEditable(
            valor = correo,
            onCambio = { correo = it; guardado = false },
            etiqueta = "Correo electrónico",
            icono = Icons.Filled.Email
        )

        Spacer(Modifier.height(14.dp))

        CampoEditable(
            valor = fecha,
            onCambio = { fecha = it; guardado = false },
            etiqueta = "Fecha de nacimiento",
            icono = Icons.Filled.DateRange
        )

        Spacer(Modifier.height(14.dp))

        // ---------- País (desplegable) ----------
        Text("País", fontSize = 13.sp, color = GrisEd)
        Spacer(Modifier.height(6.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .border(1.dp, BordeEd, RoundedCornerShape(12.dp))
                    .clickable { menuPaises = true }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Public,
                    contentDescription = null,
                    tint = VerdeEd,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = if (pais.isBlank()) "Sin completar" else pais,
                    fontSize = 15.sp,
                    color = if (pais.isBlank()) Color(0xFF9AA0A6) else NegroEd,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = GrisEd
                )
            }

            DropdownMenu(
                expanded = menuPaises,
                onDismissRequest = { menuPaises = false },
                modifier = Modifier.background(Color.White)
            ) {
                paisesEd.forEach { p ->
                    DropdownMenuItem(
                        text = { Text(text = p, fontSize = 15.sp) },
                        onClick = {
                            pais = p
                            guardado = false
                            menuPaises = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---------- Género ----------
        Text("Género", fontSize = 13.sp, color = GrisEd)
        Spacer(Modifier.height(8.dp))
        generosEd.forEach { g ->
            val seleccionado = genero == g
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(bottom = 8.dp)
                    .border(
                        width = if (seleccionado) 2.dp else 1.dp,
                        color = if (seleccionado) VerdeEd else BordeEd,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { genero = g; guardado = false },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = g,
                    fontSize = 15.sp,
                    color = if (seleccionado) VerdeEd else Color(0xFF3D3D3D)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        CampoEditable(
            valor = telefono,
            onCambio = { telefono = it; guardado = false },
            etiqueta = "Teléfono de contacto",
            icono = Icons.Filled.Phone
        )

        Spacer(Modifier.height(14.dp))

        CampoEditable(
            valor = direccion,
            onCambio = { direccion = it; guardado = false },
            etiqueta = "Dirección de hogar (opcional)",
            icono = Icons.Filled.Home
        )

        Spacer(Modifier.height(26.dp))

        // ---------- Datos que no se editan acá ----------
        Text(
            "Datos de tu tratamiento",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeOscuroEd
        )

        Spacer(Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(VerdeSuaveEd)
                .padding(18.dp)
        ) {
            FilaSoloLectura(
                "Diagnóstico",
                if (UserData.diagnosticos.isEmpty()) "Sin completar"
                else UserData.diagnosticos.joinToString(" y ")
            )
            Spacer(Modifier.height(14.dp))
            FilaSoloLectura(
                "Mano del guante",
                if (UserData.mano.isBlank()) "Derecha" else UserData.mano
            )
            Spacer(Modifier.height(14.dp))
            FilaSoloLectura("Código de vinculación", UserData.codigoVinculacion)
            Spacer(Modifier.height(14.dp))
            FilaSoloLectura("Plan", if (UserData.esPremium) "Premium Guiada" else "Versión gratuita")
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = "El diagnóstico y la mano se cambian desde \"Diagnóstico y datos médicos\".",
            fontSize = 12.sp,
            color = GrisEd,
            lineHeight = 17.sp
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                UserData.nombreUsuario = nombre.trim()
                UserData.correo = correo.trim()
                UserData.fechaNacimiento = fecha.trim()
                UserData.pais = pais
                UserData.telefono = telefono.trim()
                UserData.direccion = direccion.trim()
                UserData.genero = genero
                UserData.fotoPerfil = fotoUri
                onGuardarCambios(
                    nombre.trim(),
                    correo.trim(),
                    fecha.trim(),
                    pais,
                    telefono.trim(),
                    direccion.trim(),
                    genero
                )
                guardado = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeEd)
        ) {
            Text("Guardar cambios", fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }

        if (guardado) {
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(VerdeSuaveEd)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(VerdeEd),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Text("Tus datos se guardaron", fontSize = 14.sp, color = VerdeOscuroEd)
            }
        }

        Spacer(Modifier.height(30.dp))
    }
}

@Composable
private fun CampoEditable(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector
) {
    Column {
        Text(etiqueta, fontSize = 13.sp, color = GrisEd)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = onCambio,
            placeholder = { Text("Sin completar", color = Color(0xFF9AA0A6), fontSize = 15.sp) },
            leadingIcon = {
                Icon(icono, contentDescription = null, tint = VerdeEd, modifier = Modifier.size(22.dp))
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VerdeEd,
                unfocusedBorderColor = BordeEd,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

@Composable
private fun FilaSoloLectura(titulo: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = titulo, fontSize = 14.sp, color = GrisEd, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(12.dp))
        Text(
            text = valor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = NegroEd
        )
    }
}