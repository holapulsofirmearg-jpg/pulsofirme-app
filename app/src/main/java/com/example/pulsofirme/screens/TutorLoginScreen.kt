package com.example.pulsofirme.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Path
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

private val VerdeTutor = Color(0xFF237A68)
private val VerdeTexto = Color(0xFF1B7A6E)
private val GrisTexto = Color(0xFF6E6E6E)
private val GrisBorde = Color(0xFFD8D8D8)

@Composable
fun TutorLoginScreen(
    onBack: () -> Unit = {},
    onIngresar: (email: String, password: String, codigo: String) -> Unit = { _, _, _ -> },
    onOlvidasteContrasena: (String) -> Unit = {},
    onCrearCuenta: () -> Unit = {},
    onGoogle: () -> Unit = {},
    onApple: () -> Unit = {},
    onAccesibilidad: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var codigo by remember { mutableStateOf("") }

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
                onClick = { onIngresar(email, password, codigo) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeTutor)
            ) {
                Text(
                    text = "Ingresar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Divider "o continúa con"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(modifier = Modifier.weight(1f), color = GrisBorde)
                Text(
                    text = "  o continúa con  ",
                    fontSize = 12.sp,
                    color = GrisTexto
                )
                Divider(modifier = Modifier.weight(1f), color = GrisBorde)
            }

            Spacer(modifier = Modifier.height(14.dp))

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

            // Google / Apple
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onGoogle,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 52.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GrisBorde)
                ) {
                    Text(text = "G", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Google", fontSize = 14.sp, color = Color.Black)
                }
                OutlinedButton(
                    onClick = onApple,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 52.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GrisBorde)
                ) {
                    IconoManzana(modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Apple", fontSize = 14.sp, color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickableText(onAccesibilidad)
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

@Composable
private fun IconoManzana(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val cuerpo = Path().apply {
            moveTo(w * 0.5f, h * 0.30f)
            cubicTo(
                w * 0.15f, h * 0.15f,
                -w * 0.05f, h * 0.55f,
                w * 0.20f, h * 0.85f
            )
            cubicTo(
                w * 0.32f, h * 1.0f,
                w * 0.42f, h * 0.95f,
                w * 0.5f, h * 0.95f
            )
            cubicTo(
                w * 0.58f, h * 0.95f,
                w * 0.68f, h * 1.0f,
                w * 0.80f, h * 0.85f
            )
            cubicTo(
                w * 1.05f, h * 0.55f,
                w * 0.85f, h * 0.15f,
                w * 0.5f, h * 0.30f
            )
            close()
        }
        drawPath(path = cuerpo, color = Color.Black)

        val hoja = Path().apply {
            moveTo(w * 0.55f, h * 0.05f)
            cubicTo(
                w * 0.55f, h * 0.20f,
                w * 0.70f, h * 0.25f,
                w * 0.72f, h * 0.12f
            )
            cubicTo(
                w * 0.65f, h * 0.0f,
                w * 0.55f, h * 0.0f,
                w * 0.55f, h * 0.05f
            )
            close()
        }
        drawPath(path = hoja, color = Color.Black)
    }
}

// Extensión chiquita para hacer clickeable un Text sin usar Modifier.clickable directo
// (evita import repetido de foundation.clickable en cada Text)
private fun Modifier.clickableText(onClick: () -> Unit): Modifier =
    this.then(Modifier.clickable(onClick = onClick))