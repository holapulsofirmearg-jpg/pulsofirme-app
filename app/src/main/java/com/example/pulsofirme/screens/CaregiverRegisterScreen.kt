package com.example.pulsofirme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.semantics.Role
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
import com.example.pulsofirme.DialogoLegal
import com.example.pulsofirme.R
import com.example.pulsofirme.TextosLegales
import com.example.pulsofirme.areaTactil

private val VerdeTitulo = Color(0xFF1B7A4B)
private val GrisSubtitulo = Color(0xFF6B7280)
private val GrisEtiqueta = Color(0xFF6B7280)
private val CampoFondo = Color(0xFFEAF3EF)
private val AzulLink = Color(0xFF2196F3)

private val profesiones = listOf("Médico/a", "Kinesiólogo/a", "Terapista ocupacional", "Enfermero/a", "Otro")

@Composable
fun CaregiverRegisterScreen(
    onBack: () -> Unit,
    onCreateAccount: (String, String, String, String, String, String) -> Unit,
    onGoToLogin: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasenia by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var verPass by remember { mutableStateOf(false) }
    var verConfirmar by remember { mutableStateOf(false) }
    var profesion by remember { mutableStateOf("") }
    var menuProfesionAbierto by remember { mutableStateOf(false) }
    var matricula by remember { mutableStateOf("") }
    var aceptaTerminos by remember { mutableStateOf(false) }
    var aceptaConfidencialidad by remember { mutableStateOf(false) }
    var mostrarTerminos by remember { mutableStateOf(false) }
    var mostrarPolitica by remember { mutableStateOf(false) }
    var errorFormulario by remember { mutableStateOf("") }

    if (mostrarTerminos) {
        DialogoLegal(titulo = "Términos y condiciones", texto = TextosLegales.terminos) { mostrarTerminos = false }
    }
    if (mostrarPolitica) {
        DialogoLegal(titulo = "Política de privacidad", texto = TextosLegales.politica) { mostrarPolitica = false }
    }

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
            painter = painterResource(id = R.drawable.agenda),
            contentDescription = "Crear cuenta profesional",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(180.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Crear cuenta\nprofesional",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeTitulo,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Completá tus datos para crear tu cuenta.",
            fontSize = 15.sp,
            color = GrisSubtitulo,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        CampoConEtiqueta("Nombre", Icons.Filled.Person, nombre, { nombre = it }, "Ingresá tu nombre")

        Spacer(modifier = Modifier.height(16.dp))

        CampoConEtiqueta("Apellido", Icons.Filled.Person, apellido, { apellido = it }, "Ingresá tu apellido")

        Spacer(modifier = Modifier.height(16.dp))

        CampoConEtiqueta("Correo electrónico", Icons.Filled.Email, correo, { correo = it }, "Ingresá tu correo")

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Lock, contentDescription = null, tint = GrisEtiqueta, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Contraseña", fontSize = 14.sp, color = GrisEtiqueta)
        }
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = contrasenia,
            onValueChange = { contrasenia = it },
            placeholder = { Text("Creá una contraseña") },
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

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Lock, contentDescription = null, tint = GrisEtiqueta, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Confirmar contraseña", fontSize = 14.sp, color = GrisEtiqueta)
        }
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = confirmar,
            onValueChange = { confirmar = it },
            placeholder = { Text("Repetí tu contraseña") },
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
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = CampoFondo,
                unfocusedContainerColor = CampoFondo
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Work, contentDescription = null, tint = GrisEtiqueta, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Profesión", fontSize = 14.sp, color = GrisEtiqueta)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = profesion,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Seleccioná tu profesión") },
                trailingIcon = {
                    IconButton(onClick = { menuProfesionAbierto = true }) {
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = GrisEtiqueta)
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = CampoFondo,
                    unfocusedContainerColor = CampoFondo
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { menuProfesionAbierto = true }
            )

            DropdownMenu(
                expanded = menuProfesionAbierto,
                onDismissRequest = { menuProfesionAbierto = false },
                modifier = Modifier.background(Color.White)
            ) {
                profesiones.forEach { p ->
                    DropdownMenuItem(
                        text = { Text(p) },
                        onClick = {
                            profesion = p
                            menuProfesionAbierto = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        CampoConEtiqueta("Matrícula profesional", Icons.Filled.Badge, matricula, { matricula = it }, "Ingresá tu número de matrícula")

        Spacer(modifier = Modifier.height(20.dp))

        // --- Consentimiento 1: términos y política ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .areaTactil()
                .toggleable(
                    value = aceptaTerminos,
                    role = Role.Checkbox,
                    onValueChange = { aceptaTerminos = it }
                ),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = aceptaTerminos,
                onCheckedChange = null,
                modifier = Modifier.padding(top = 2.dp, end = 10.dp),
                colors = CheckboxDefaults.colors(checkedColor = VerdeTitulo)
            )
            Text(
                text = "Acepto los términos y condiciones y la política de privacidad.",
                fontSize = 14.sp,
                color = Color(0xFF333333),
                lineHeight = 20.sp
            )
        }
        Row(modifier = Modifier.padding(start = 40.dp)) {
            Text(
                text = "Leer términos",
                fontSize = 14.sp,
                color = VerdeTitulo,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .areaTactil()
                    .clickable(role = Role.Button) { mostrarTerminos = true }
                    .padding(end = 20.dp, top = 12.dp)
            )
            Text(
                text = "Leer política",
                fontSize = 14.sp,
                color = VerdeTitulo,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .areaTactil()
                    .clickable(role = Role.Button) { mostrarPolitica = true }
                    .padding(top = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- Consentimiento 2: confidencialidad de los datos de salud de pacientes ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .areaTactil()
                .toggleable(
                    value = aceptaConfidencialidad,
                    role = Role.Checkbox,
                    onValueChange = { aceptaConfidencialidad = it }
                ),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = aceptaConfidencialidad,
                onCheckedChange = null,
                modifier = Modifier.padding(top = 2.dp, end = 10.dp),
                colors = CheckboxDefaults.colors(checkedColor = VerdeTitulo)
            )
            Text(
                text = "Entiendo que voy a acceder a datos de salud de pacientes, que son datos sensibles. " +
                        "Me comprometo a usarlos solo con fines de seguimiento y a respetar el secreto profesional.",
                fontSize = 14.sp,
                color = Color(0xFF333333),
                lineHeight = 20.sp
            )
        }

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
                val nombreLimpio = nombre.trim()
                val apellidoLimpio = apellido.trim()
                val correoLimpio = correo.trim().lowercase()
                val profesionLimpia = profesion.trim()
                val matriculaLimpia = matricula.trim()

                errorFormulario = when {
                    nombreLimpio.isBlank() -> "Ingresá tu nombre."
                    apellidoLimpio.isBlank() -> "Ingresá tu apellido."
                    correoLimpio.isBlank() || !correoLimpio.contains("@") -> "Ingresá un correo electrónico válido."
                    contrasenia.length < 6 -> "La contraseña debe tener al menos 6 caracteres."
                    contrasenia != confirmar -> "Las contraseñas no coinciden."
                    profesionLimpia.isBlank() -> "Seleccioná tu profesión."
                    matriculaLimpia.isBlank() -> "Ingresá tu matrícula profesional."
                    !aceptaTerminos -> "Tenés que aceptar los términos y condiciones."
                    !aceptaConfidencialidad -> "Tenés que aceptar el compromiso de confidencialidad."
                    else -> ""
                }

                if (errorFormulario.isBlank()) {
                    onCreateAccount(
                        nombreLimpio,
                        apellidoLimpio,
                        correoLimpio,
                        contrasenia,
                        profesionLimpia,
                        matriculaLimpia
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeTitulo)
        ) {
            Text("Crear cuenta", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("¿Ya tenés una cuenta? ", fontSize = 14.sp, color = GrisSubtitulo)
            Text(
                text = "Iniciar sesión",
                fontSize = 14.sp,
                color = VerdeTitulo,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .areaTactil()
                    .clickable(role = Role.Button) { onGoToLogin() }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun CampoConEtiqueta(
    etiqueta: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    valor: String,
    onValorCambia: (String) -> Unit,
    placeholder: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = GrisEtiqueta, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(etiqueta, fontSize = 14.sp, color = GrisEtiqueta)
    }
    Spacer(modifier = Modifier.height(6.dp))
    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambia,
        placeholder = { Text(placeholder) },
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
}