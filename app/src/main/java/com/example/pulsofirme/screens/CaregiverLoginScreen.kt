package com.example.pulsofirme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.R
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

private val VerdeTitulo = Color(0xFF1B7A4B)
private val GrisSubtitulo = Color(0xFF6B7280)
private val GrisEtiqueta = Color(0xFF6B7280)
private val CampoFondo = Color(0xFFEAF3EF)
private val GrisDivider = Color(0xFFD8DED9)

@Composable
fun CaregiverLoginScreen(
    onBack: () -> Unit,
    onIniciarSesion: (String, String) -> Unit,
    onForgotPasswordClick: (String) -> Unit,
    onCrearCuenta: () -> Unit,
    // true mientras se verifica el inicio de sesión (bloquea el botón)
    cargando: Boolean = false
) {
    var correo by remember { mutableStateOf("") }
    var contrasenia by remember { mutableStateOf("") }
    var verPass by remember { mutableStateOf(false) }
    var errorFormulario by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id = R.drawable.doctora1),
            contentDescription = "Profesional",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(160.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Acceder como\nprofesional",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeTitulo,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Ingresá con tu cuenta para continuar\nen la plataforma.",
            fontSize = 15.sp,
            color = GrisSubtitulo,
            textAlign = TextAlign.Center,
            lineHeight = 21.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Mail, contentDescription = null, tint = GrisEtiqueta, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Correo electrónico", fontSize = 14.sp, color = GrisEtiqueta)
        }

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            placeholder = { Text("Ingresá tu correo") },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = CampoFondo,
                unfocusedContainerColor = CampoFondo
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Lock, contentDescription = null, tint = GrisEtiqueta, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Contraseña", fontSize = 14.sp, color = GrisEtiqueta)
        }

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = contrasenia,
            onValueChange = { contrasenia = it },
            placeholder = { Text("Ingresá tu contraseña") },
            trailingIcon = {
                IconButton(onClick = { verPass = !verPass }) {
                    Icon(
                        imageVector = if (verPass) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null,
                        tint = GrisEtiqueta
                    )
                }
            },
            visualTransformation = if (verPass) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = CampoFondo,
                unfocusedContainerColor = CampoFondo
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "¿Olvidaste tu contraseña?",
            fontSize = 14.sp,
            color = VerdeTitulo,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable(role = Role.Button) {
                    if (correo.isBlank()) {
                        errorFormulario = "Ingresá tu correo para recuperar la contraseña."
                    } else {
                        errorFormulario = ""
                        onForgotPasswordClick(correo.trim())
                    }
                }
                .padding(vertical = 12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (errorFormulario.isNotBlank()) {
            Text(
                text = errorFormulario,
                color = Color(0xFFB3261E),
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = {
                val correoLimpio = correo.trim().lowercase()
                errorFormulario = when {
                    correoLimpio.isBlank() || !correoLimpio.contains("@") -> "Ingresá un correo electrónico válido."
                    contrasenia.isBlank() -> "Ingresá tu contraseña."
                    else -> ""
                }

                if (errorFormulario.isBlank() && !cargando) {
                    onIniciarSesion(correoLimpio, contrasenia)
                }
            },
            enabled = !cargando,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeTitulo,
                disabledContainerColor = VerdeTitulo.copy(alpha = 0.6f)
            )
        ) {
            if (cargando) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .size(22.dp)
                        .semantics { contentDescription = "Iniciando sesión" }
                )
            } else {
                Text("Iniciar sesión", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = GrisDivider)
            Text(text = "  o  ", fontSize = 13.sp, color = GrisSubtitulo)
            HorizontalDivider(modifier = Modifier.weight(1f), color = GrisDivider)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Un solo texto: si la letra es grande, baja de renglón en vez de cortarse
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = GrisSubtitulo)) {
                    append("¿No tenés una cuenta? ")
                }
                withStyle(SpanStyle(color = VerdeTitulo, fontWeight = FontWeight.Bold)) {
                    append("Crear cuenta")
                }
            },
            fontSize = 15.sp,
            lineHeight = 21.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(role = Role.Button) { onCrearCuenta() }
                .padding(vertical = 14.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}