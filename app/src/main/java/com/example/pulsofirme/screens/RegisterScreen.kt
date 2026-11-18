package com.example.pulsofirme.screens

import androidx.compose.runtime.saveable.rememberSaveable

import com.example.pulsofirme.ui.theme.enTema

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.DialogoLegal
import com.example.pulsofirme.R
import com.example.pulsofirme.TextosLegales
import com.example.pulsofirme.UserData
import com.example.pulsofirme.areaTactil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

private val AzulCabecera = Color(0xFF1565C0)
private val VerdeReg: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
private val GrisBorde: Color
    @Composable get() = Color(0xFFE0E0E0).enTema()
private val AzulLink: Color
    @Composable get() = Color(0xFF2196F3).enTema()
private val RojoError: Color
    @Composable get() = Color(0xFFC63927).enTema()
@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onCreateAccount: (String) -> Unit,
    onGoToLogin: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasenia by rememberSaveable { mutableStateOf("") }
    var confirmar by rememberSaveable { mutableStateOf("") }
    var verPass by rememberSaveable { mutableStateOf(false) }
    var verConfirmar by rememberSaveable { mutableStateOf(false) }

    // Consentimientos: separados y destildados por defecto (consentimiento expreso)
    var aceptaTerminos by rememberSaveable { mutableStateOf(false) }
    var consentimientoSalud by rememberSaveable { mutableStateOf(false) }
    var mostrarTerminos by rememberSaveable { mutableStateOf(false) }
    var mostrarPolitica by rememberSaveable { mutableStateOf(false) }

    var cargando by remember { mutableStateOf(false) }
    var mensajeError by rememberSaveable { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    if (mostrarTerminos) {
        DialogoLegal(
            titulo = "Términos y Condiciones",
            texto = TextosLegales.terminos,
            onCerrar = { mostrarTerminos = false }
        )
    }
    if (mostrarPolitica) {
        DialogoLegal(
            titulo = "Política de privacidad",
            texto = TextosLegales.politica,
            conLogo = true,
            onCerrar = { mostrarPolitica = false }
        )
    }

    fun validarYCrearCuenta() {
        mensajeError = ""

        when {
            nombre.isBlank() -> {
                mensajeError = "Ingresá tu nombre completo."
                return
            }
            correo.isBlank() -> {
                mensajeError = "Ingresá tu correo electrónico."
                return
            }
            contrasenia.length < 6 -> {
                mensajeError = "La contraseña debe tener al menos 6 caracteres."
                return
            }
            contrasenia != confirmar -> {
                mensajeError = "Las contraseñas no coinciden."
                return
            }
            !aceptaTerminos -> {
                mensajeError = "Tenés que aceptar los términos y la política de privacidad."
                return
            }
            !consentimientoSalud -> {
                mensajeError = "Para crear tu cuenta necesitamos tu consentimiento para tratar tus datos de salud."
                return
            }
        }

        cargando = true

        scope.launch {
            try {
                val auth = FirebaseAuth.getInstance()

                val resultado = auth
                    .createUserWithEmailAndPassword(correo.trim(), contrasenia)
                    .await()

                val uid = resultado.user?.uid

                if (uid != null) {
                    val datosUsuario = hashMapOf<String, Any>(
                        "nombre" to nombre.trim(),
                        "correo" to correo.trim(),
                        "rol" to "paciente",
                        // Registro del consentimiento (Ley 25.326)
                        "consentimientoTerminos" to true,
                        "consentimientoSalud" to true,
                        "consentimientoVersion" to TextosLegales.VERSION,
                        "consentimientoFecha" to FieldValue.serverTimestamp()
                    )

                    FirebaseFirestore.getInstance()
                        .collection("usuarios")
                        .document(uid)
                        .set(datosUsuario)
                        .await()
                }

                UserData.nombreUsuario = nombre.trim()

                cargando = false
                onCreateAccount(nombre.trim())

            } catch (e: Exception) {
                cargando = false
                mensajeError = when {
                    e.message?.contains("email address is already in use", true) == true ->
                        "Ese correo ya tiene una cuenta registrada."
                    e.message?.contains("badly formatted", true) == true ->
                        "El correo electrónico no es válido."
                    e.message?.contains("network", true) == true ->
                        "Sin conexión. Verificá tu internet e intentá de nuevo."
                    else ->
                        "No se pudo crear la cuenta. Intentá de nuevo."
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.enTema())
            .verticalScroll(rememberScrollState())
    ) {

        // ---------- Cabecera azul con onda ----------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val camino = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(w, 0f)
                    lineTo(w, h * 0.62f)
                    cubicTo(
                        w * 0.80f, h * 0.62f,
                        w * 0.68f, h * 1.02f,
                        w * 0.38f, h * 0.97f
                    )
                    cubicTo(
                        w * 0.20f, h * 0.94f,
                        w * 0.12f, h * 0.86f,
                        0f, h * 0.86f
                    )
                    close()
                }
                drawPath(path = camino, color = AzulCabecera)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 62.dp)
                    .height(48.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Crear cuenta",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                )
            }
        }

        // ---------- Contenido ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(10.dp))

            Image(
                painter = painterResource(id = R.drawable.icon2),
                contentDescription = "PulsoFirme",
                modifier = Modifier.size(120.dp)
            )

            Spacer(Modifier.height(14.dp))

            Text(
                text = "Completa tus datos para crear tu cuenta",
                fontSize = 15.sp,
                color = Color(0xFF5A5A5A).enTema(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(22.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                placeholder = { Text("Nombre completo") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VerdeReg,
                    unfocusedBorderColor = GrisBorde,
                    focusedLeadingIconColor = VerdeReg,
                    unfocusedLeadingIconColor = VerdeReg,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                placeholder = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VerdeReg,
                    unfocusedBorderColor = GrisBorde,
                    focusedLeadingIconColor = VerdeReg,
                    unfocusedLeadingIconColor = VerdeReg,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = contrasenia,
                onValueChange = { contrasenia = it },
                placeholder = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { verPass = !verPass }) {
                        Icon(
                            imageVector = if (verPass) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff,
                            contentDescription = if (verPass) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = VerdeReg
                        )
                    }
                },
                visualTransformation = if (verPass) VisualTransformation.None
                else PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VerdeReg,
                    unfocusedBorderColor = GrisBorde,
                    focusedLeadingIconColor = VerdeReg,
                    unfocusedLeadingIconColor = VerdeReg,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = confirmar,
                onValueChange = { confirmar = it },
                placeholder = { Text("Confirmar contraseña") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { verConfirmar = !verConfirmar }) {
                        Icon(
                            imageVector = if (verConfirmar) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff,
                            contentDescription = if (verConfirmar) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = VerdeReg
                        )
                    }
                },
                visualTransformation = if (verConfirmar) VisualTransformation.None
                else PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VerdeReg,
                    unfocusedBorderColor = GrisBorde,
                    focusedLeadingIconColor = VerdeReg,
                    unfocusedLeadingIconColor = VerdeReg,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // ---------- CONSENTIMIENTO 1: términos y política ----------
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
                    colors = CheckboxDefaults.colors(
                        checkedColor = VerdeReg,
                        uncheckedColor = Color(0xFF9E9E9E).enTema()
                    )
                )
                Column {
                    Text(
                        "Leí y acepto los Términos y Condiciones y la Política de Privacidad.",
                        fontSize = 14.sp,
                        color = Color(0xFF333333).enTema(),
                        lineHeight = 20.sp
                    )
                    Row {
                        Text(
                            "Ver términos",
                            fontSize = 14.sp,
                            color = VerdeReg,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clickable { mostrarTerminos = true }
                                .padding(vertical = 12.dp)
                        )
                        Spacer(Modifier.width(20.dp))
                        Text(
                            "Ver política",
                            fontSize = 14.sp,
                            color = VerdeReg,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clickable { mostrarPolitica = true }
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ---------- CONSENTIMIENTO 2: datos de salud (explícito) ----------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .areaTactil()
                    .toggleable(
                        value = consentimientoSalud,
                        role = Role.Checkbox,
                        onValueChange = { consentimientoSalud = it }
                    ),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = consentimientoSalud,
                    onCheckedChange = null,
                    modifier = Modifier.padding(top = 2.dp, end = 10.dp),
                    colors = CheckboxDefaults.colors(
                        checkedColor = VerdeReg,
                        uncheckedColor = Color(0xFF9E9E9E).enTema()
                    )
                )
                Column {
                    Text(
                        "Doy mi consentimiento expreso para que PulsoFirme trate mis datos de salud " +
                                "(diagnóstico, medicación, ejercicios y evaluaciones) y los guarde en " +
                                "servidores de Google, que pueden estar fuera de la Argentina.",
                        fontSize = 14.sp,
                        color = Color(0xFF333333).enTema(),
                        lineHeight = 20.sp
                    )
                    Text(
                        "Podés retirar este consentimiento y borrar tus datos cuando quieras, " +
                                "desde Privacidad y datos. Compartir con un cuidador o profesional " +
                                "solo ocurre si vos los vinculás.",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280).enTema(),
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            if (mensajeError.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                Text(
                    text = mensajeError,
                    color = RojoError,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = { validarYCrearCuenta() },
                enabled = !cargando && aceptaTerminos && consentimientoSalud,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdeReg,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
            ) {
                if (cargando) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text("Crear cuenta", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Un solo texto: si la letra es grande, baja de renglón en vez de cortarse
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF333333).enTema())) {
                        append("¿Ya tenés cuenta? ")
                    }
                    withStyle(SpanStyle(color = AzulLink, fontWeight = FontWeight.SemiBold)) {
                        append("Iniciá sesión")
                    }
                },
                fontSize = 15.sp,
                lineHeight = 21.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .areaTactil()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(role = Role.Button) { onGoToLogin() }
                    .padding(vertical = 14.dp)
            )

            Spacer(Modifier.height(30.dp))
        }
    }
}