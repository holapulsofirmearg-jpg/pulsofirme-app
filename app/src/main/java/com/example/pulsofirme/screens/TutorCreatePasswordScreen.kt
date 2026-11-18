package com.example.pulsofirme.screens

import androidx.compose.runtime.saveable.rememberSaveable

import com.example.pulsofirme.ui.theme.enTema

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.R
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

private val VerdeTitulo: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
private val GrisSubtitulo: Color
    @Composable get() = Color(0xFF6B7280).enTema()
private val GrisEtiqueta: Color
    @Composable get() = Color(0xFF6B7280).enTema()
private val GrisBorde: Color
    @Composable get() = Color(0xFFD8DED9).enTema()
private val GrisFondoLista: Color
    @Composable get() = Color(0xFFF3F5F4).enTema()
@Composable
fun TutorCreatePasswordScreen(
    onBack: () -> Unit,
    onCrearCuenta: (String) -> Unit = {},
    // true mientras se crea la cuenta (bloquea el botón)
    cargando: Boolean = false
) {
    var contrasenia by rememberSaveable { mutableStateOf("") }
    var confirmarContrasenia by rememberSaveable { mutableStateOf("") }
    var verContrasenia by rememberSaveable { mutableStateOf(false) }
    var verConfirmar by rememberSaveable { mutableStateOf(false) }
    var aceptaTerminos by rememberSaveable { mutableStateOf(false) }

    val puedeContinuar = contrasenia.isNotBlank() &&
            contrasenia == confirmarContrasenia &&
            aceptaTerminos

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.enTema())
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
            painter = painterResource(id = R.drawable.logooo),
            contentDescription = "PulsoFirme",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(150.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Crear contraseña",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeTitulo,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Creá una contraseña segura para tu\ncuenta de tutor.",
            fontSize = 15.sp,
            color = GrisSubtitulo,
            textAlign = TextAlign.Center,
            lineHeight = 21.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text("Contraseña", fontSize = 13.sp, color = GrisEtiqueta)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = contrasenia,
            onValueChange = { contrasenia = it },
            placeholder = { Text("Ingresá tu contraseña") },
            leadingIcon = {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = GrisEtiqueta)
            },
            trailingIcon = {
                IconButton(onClick = { verContrasenia = !verContrasenia }) {
                    Icon(
                        imageVector = if (verContrasenia) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null,
                        tint = GrisEtiqueta
                    )
                }
            },
            visualTransformation = if (verContrasenia) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VerdeTitulo,
                unfocusedBorderColor = GrisBorde
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text("Confirmar contraseña", fontSize = 13.sp, color = GrisEtiqueta)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = confirmarContrasenia,
            onValueChange = { confirmarContrasenia = it },
            placeholder = { Text("Volvé a ingresar tu contraseña") },
            leadingIcon = {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = GrisEtiqueta)
            },
            trailingIcon = {
                IconButton(onClick = { verConfirmar = !verConfirmar }) {
                    Icon(
                        imageVector = if (verConfirmar) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null,
                        tint = GrisEtiqueta
                    )
                }
            },
            visualTransformation = if (verConfirmar) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VerdeTitulo,
                unfocusedBorderColor = GrisBorde
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(GrisFondoLista, RoundedCornerShape(14.dp))
                .padding(16.dp)
        ) {
            Text("La contraseña debe tener:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = VerdeTitulo)
            Spacer(modifier = Modifier.height(10.dp))
            RequisitoContrasenia("Al menos 8 caracteres")
            RequisitoContrasenia("Una letra mayúscula")
            RequisitoContrasenia("Una letra minúscula")
            RequisitoContrasenia("Un número")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.clickable { aceptaTerminos = !aceptaTerminos }
        ) {
            Icon(
                imageVector = if (aceptaTerminos) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank,
                contentDescription = null,
                tint = if (aceptaTerminos) VerdeTitulo else GrisBorde,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Acepto los Términos y Condiciones y la Política de Privacidad",
                fontSize = 14.sp,
                color = Color(0xFF333333).enTema(),
                lineHeight = 19.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { if (!cargando) onCrearCuenta(contrasenia) },
            enabled = puedeContinuar && !cargando,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeTitulo,
                disabledContainerColor = Color(0xFFBEE0CC).enTema()
            )
        ) {
            if (cargando) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .size(22.dp)
                        .semantics { contentDescription = "Creando cuenta" }
                )
            } else {
                Text("Crear cuenta", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GrisBorde)
        ) {
            Text("Cancelar", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16324F).enTema())
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun RequisitoContrasenia(texto: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.RadioButtonUnchecked,
            contentDescription = null,
            tint = GrisBorde,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(texto, fontSize = 14.sp, color = Color(0xFF374151).enTema())
    }
}