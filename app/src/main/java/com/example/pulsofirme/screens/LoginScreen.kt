package com.example.pulsofirme.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.R

// Colores del diseño
val PrimaryBlue = Color(0xFF007A9E)
val SecondaryGreen = Color(0xFF1B8057)
val BorderGreen = Color(0xFF43A047)
val LightGrayBorder = Color(0xFFE0E0E0)
val DarkText = Color(0xFF1A1A1A)
val SubtitleText = Color(0xFF666666)
private val GoogleBlue = Color(0xFF4285F4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onBackClick: () -> Unit = {},
    onIngresar: (String, String) -> Unit = { _, _ -> },
    onForgotPasswordClick: (String) -> Unit = {},
    onCrearCuenta: () -> Unit = {},
    onCuidador: () -> Unit = {},

    onGoogleClick: () -> Unit = {},
    onAppleClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 0. Flecha para volver arriba a la izquierda
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver atrás",
                        tint = DarkText
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 1. Logo principal
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logooo),
                    contentDescription = "Logo Pulso Firme",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Títulos
            Text(
                text = "Bienvenido",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tu bienestar en tus manos",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = SubtitleText
            )

            Text(
                text = "Inicia sesión para continuar",
                fontSize = 14.sp,
                color = SubtitleText
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 3. Campo de Correo Electrónico
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Correo electrónico", color = Color.Gray) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = null,
                        tint = PrimaryBlue
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = LightGrayBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Campo de Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Contraseña", color = Color.Gray) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = PrimaryBlue
                    )
                },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null, tint = PrimaryBlue)
                    }
                },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = LightGrayBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Link "Olvidaste tu contraseña"
            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = PrimaryBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(role = Role.Button) { onForgotPasswordClick(email) }
                        .padding(vertical = 12.dp, horizontal = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = { onIngresar(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(
                    text = "Iniciar sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            // Un solo texto: si la letra es grande, baja de renglón en vez de cortarse
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = SubtitleText)) { append("¿No tenés cuenta? ") }
                    withStyle(SpanStyle(color = PrimaryBlue, fontWeight = FontWeight.Bold)) { append("Crear cuenta") }
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

            Spacer(modifier = Modifier.height(20.dp))


            OutlinedButton(
                onClick = { onCuidador() },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderGreen),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = BorderGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ingresar como cuidador",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BorderGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = LightGrayBorder)
                Text(
                    text = " o continúa con ",
                    fontSize = 12.sp,
                    color = SubtitleText
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = LightGrayBorder)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 9. Redes Sociales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { onGoogleClick() },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 52.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LightGrayBorder)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "G",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoogleBlue
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Google", color = DarkText, fontWeight = FontWeight.Medium)
                    }
                }

                OutlinedButton(
                    onClick = { onAppleClick() },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 52.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LightGrayBorder)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconoManzana(modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Apple", color = DarkText, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
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