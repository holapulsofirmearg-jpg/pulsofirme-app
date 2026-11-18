package com.example.pulsofirme.screens

import androidx.compose.runtime.saveable.rememberSaveable

import com.example.pulsofirme.ui.theme.enTema

import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.R
import com.example.pulsofirme.Accesibilidad
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

private val VerdeTutor: Color
    @Composable get() = Color(0xFF237A68).enTema()
private val VerdeTexto: Color
    @Composable get() = Color(0xFF1B7A6E).enTema()
private val GrisTexto: Color
    @Composable get() = Color(0xFF6E6E6E).enTema()
private val GrisBorde: Color
    @Composable get() = Color(0xFFD8D8D8).enTema()
@Composable
fun TutorLoginScreen(
    onBack: () -> Unit = {},
    onIngresar: (email: String, password: String, codigo: String) -> Unit = { _, _, _ -> },
    onOlvidasteContrasena: (String) -> Unit = {},
    onCrearCuenta: () -> Unit = {},
    // true mientras se verifica el inicio de sesión (bloquea el botón)
    cargando: Boolean = false
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var codigo by rememberSaveable { mutableStateOf("") }
    var mostrarAccesibilidad by rememberSaveable { mutableStateOf(false) }

    // Opciones de accesibilidad antes de iniciar sesión (tamaño de letra y contraste).
    // Usa las mismas preferencias que Ajustes, así que quedan guardadas para toda la app.
    if (mostrarAccesibilidad) {
        DialogoAccesibilidadRapida(onCerrar = { mostrarAccesibilidad = false })
    }

    Scaffold(
        containerColor = Color.White.enTema()
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

            // Logo
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
                text = "Acceder como tutor",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeTexto,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "La información de quienes cuidas\nestá siempre protegida",
                fontSize = 14.sp,
                color = GrisTexto,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Correo electrónico del tutor") },
                leadingIcon = {
                    Icon(Icons.Filled.Email, contentDescription = null, tint = VerdeTutor)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = GrisBorde,
                    focusedBorderColor = VerdeTutor
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Campo contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Contraseña del tutor") },
                leadingIcon = {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = VerdeTutor)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null,
                            tint = GrisTexto
                        )
                    }
                },
                visualTransformation = if (passwordVisible)
                    androidx.compose.ui.text.input.VisualTransformation.None
                else
                    androidx.compose.ui.text.input.PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = GrisBorde,
                    focusedBorderColor = VerdeTutor
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "¿Olvidaste tu contraseña?",
                fontSize = 13.sp,
                color = VerdeTutor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickableText { onOlvidasteContrasena(email.trim()) }
                    .padding(vertical = 12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Campo código de vinculación
            OutlinedTextField(
                value = codigo,
                onValueChange = { codigo = it },
                placeholder = { Text("Código de vinculación del paciente") },
                leadingIcon = {
                    Icon(Icons.Filled.PanTool, contentDescription = null, tint = VerdeTutor)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = GrisBorde,
                    focusedBorderColor = VerdeTutor
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Ingresá el código de vinculación de 6 dígitos que te dio el paciente.",
                fontSize = 12.sp,
                color = GrisTexto,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Botón Ingresar
            Button(
                onClick = { if (!cargando) onIngresar(email, password, codigo) },
                enabled = !cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdeTutor,
                    disabledContainerColor = VerdeTutor.copy(alpha = 0.6f)
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
                    Text(
                        text = "Ingresar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Un solo texto: si la letra es grande, baja de renglón en vez de cortarse
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = GrisTexto)) { append("¿No tenés cuenta? ") }
                    withStyle(SpanStyle(color = VerdeTutor, fontWeight = FontWeight.Bold)) { append("Crear cuenta") }
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

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickableText { mostrarAccesibilidad = true }
                    .heightIn(min = 48.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Accessibility,
                    contentDescription = null,
                    tint = GrisTexto,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Accesibilidad",
                    fontSize = 13.sp,
                    color = GrisTexto
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Extensión chiquita para hacer clickeable un Text sin usar Modifier.clickable directo
// (evita import repetido de foundation.clickable en cada Text)
private fun Modifier.clickableText(onClick: () -> Unit): Modifier =
    this.then(Modifier.clickable(onClick = onClick))

@Composable
private fun DialogoAccesibilidadRapida(onCerrar: () -> Unit) {
    val contexto = LocalContext.current
    AlertDialog(
        onDismissRequest = onCerrar,
        containerColor = Color.White.enTema(),
        shape = RoundedCornerShape(20.dp),
        title = {
            Text("Accesibilidad", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = VerdeTutor)
        },
        text = {
            Column {
                Text("Tamaño de la letra", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A).enTema())
                Spacer(modifier = Modifier.height(6.dp))
                Accesibilidad.niveles.forEach { nivel ->
                    val elegido = Accesibilidad.tamanoLetra == nivel
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .selectable(
                                selected = elegido,
                                role = Role.RadioButton,
                                onClick = { Accesibilidad.cambiarTamanoLetra(contexto, nivel) }
                            )
                            .heightIn(min = 48.dp)
                    ) {
                        RadioButton(
                            selected = elegido,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = VerdeTutor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(nivel, fontSize = 16.sp, color = Color(0xFF1A1A1A).enTema())
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .toggleable(
                            value = Accesibilidad.altoContraste,
                            role = Role.Switch,
                            onValueChange = { Accesibilidad.cambiarAltoContraste(contexto, it) }
                        )
                        .heightIn(min = 48.dp)
                ) {
                    Text("Alto contraste", fontSize = 16.sp, color = Color(0xFF1A1A1A).enTema(), modifier = Modifier.weight(1f))
                    Switch(
                        checked = Accesibilidad.altoContraste,
                        onCheckedChange = null,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = VerdeTutor)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onCerrar, modifier = Modifier.heightIn(min = 48.dp)) {
                Text("Listo", color = VerdeTutor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    )
}