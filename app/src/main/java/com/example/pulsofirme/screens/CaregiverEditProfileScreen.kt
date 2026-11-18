package com.example.pulsofirme.screens

import com.example.pulsofirme.ui.theme.enTema

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val VerdeTitulo: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
private val GrisEtiqueta: Color
    @Composable get() = Color(0xFF9CA3AF).enTema()
private val GrisBorde: Color
    @Composable get() = Color(0xFFD8DED9).enTema()
private val VerdeBotonClaro: Color
    @Composable get() = Color(0xFFDCEFE3).enTema()
private val VerdeTextoBoton: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
private val VerdeFondoCirculo: Color
    @Composable get() = Color(0xFFE3F0EC).enTema()
@Composable
fun CaregiverEditProfileScreen(
    nombreInicial: String = "Nombre del cuidador",
    apellidoInicial: String = "Apellido del cuidador",
    correoInicial: String = "correo@ejemplo.com",
    telefonoInicial: String = "+54 9 11 1234 5678",
    fechaNacimientoInicial: String = "21/09/1995",
    generoInicial: String = "Mujer",
    paisInicial: String = "Argentina",
    onBack: () -> Unit,
    onGuardarCambios: (String, String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _ -> },
    onCambiarContrasena: () -> Unit = {}
) {
    var nombre by remember { mutableStateOf(nombreInicial) }
    var apellido by remember { mutableStateOf(apellidoInicial) }
    var correo by remember { mutableStateOf(correoInicial) }
    var telefono by remember { mutableStateOf(telefonoInicial) }
    var fechaNacimiento by remember { mutableStateOf(fechaNacimientoInicial) }
    var genero by remember { mutableStateOf(generoInicial) }
    var pais by remember { mutableStateOf(paisInicial) }
    var fotoPerfilUri by remember { mutableStateOf<Uri?>(null) }

    val selectorDeFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            fotoPerfilUri = uri
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.enTema())
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Tus datos siendo cuidador",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeTitulo
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(96.dp)
                .clip(CircleShape)
                .background(VerdeFondoCirculo)
                .clickable {
                    selectorDeFoto.launch(
                        androidx.activity.result.PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (fotoPerfilUri != null) {
                AsyncImage(
                    model = fotoPerfilUri,
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Foto de perfil",
                    tint = VerdeTitulo,
                    modifier = Modifier.size(44.dp)
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(VerdeTitulo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = "Cambiar foto",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        CampoEditable(
            icono = Icons.Filled.Person,
            etiqueta = "Nombre",
            valor = nombre,
            onValorCambia = { nombre = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CampoEditable(
            icono = Icons.Filled.Person,
            etiqueta = "Apellido",
            valor = apellido,
            onValorCambia = { apellido = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CampoEditable(
            icono = Icons.Filled.Email,
            etiqueta = "Correo electrónico",
            valor = correo,
            onValorCambia = { correo = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CampoEditable(
            icono = Icons.Filled.Phone,
            etiqueta = "Número de teléfono",
            valor = telefono,
            onValorCambia = { telefono = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CampoEditable(
            icono = Icons.Filled.CalendarMonth,
            etiqueta = "Fecha de nacimiento",
            valor = fechaNacimiento,
            onValorCambia = { fechaNacimiento = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CampoEditable(
            icono = Icons.Filled.Male,
            etiqueta = "Género",
            valor = genero,
            onValorCambia = { genero = it },
            conFlecha = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        CampoEditable(
            icono = Icons.Filled.Public,
            etiqueta = "País",
            valor = pais,
            onValorCambia = { pais = it },
            conFlecha = true
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                onGuardarCambios(nombre, apellido, correo, telefono, fechaNacimiento, genero, pais)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeBotonClaro,
                contentColor = VerdeTextoBoton
            )
        ) {
            Text("Guardar cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Lock, contentDescription = null, tint = GrisEtiqueta, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("¿Querés cambiar tu contraseña?", fontSize = 15.sp, color = GrisEtiqueta)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onCambiarContrasena() }
                .padding(vertical = 6.dp)
        ) {
            Text(
                "Cambiar contraseña",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = VerdeTitulo
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = VerdeTitulo,
                modifier = Modifier.size(14.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun CampoEditable(
    icono: ImageVector,
    etiqueta: String,
    valor: String,
    onValorCambia: (String) -> Unit,
    conFlecha: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambia,
        label = { Text(text = etiqueta, color = GrisEtiqueta) },
        leadingIcon = {
            Icon(imageVector = icono, contentDescription = null, tint = Color(0xFF6B7280).enTema())
        },
        trailingIcon = {
            if (conFlecha) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color(0xFF9CA3AF).enTema(),
                    modifier = Modifier.size(14.dp)
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = VerdeTitulo
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VerdeTitulo,
            unfocusedBorderColor = GrisBorde,
            focusedContainerColor = Color.White,

            unfocusedContainerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    )
}