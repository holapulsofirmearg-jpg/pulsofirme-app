package com.example.pulsofirme.screens

import androidx.compose.runtime.saveable.rememberSaveable

import com.example.pulsofirme.ui.theme.enTema

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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.DialogoLegal
import com.example.pulsofirme.R
import com.example.pulsofirme.TextosLegales
import com.example.pulsofirme.areaTactil

private val VerdeTitulo: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
private val GrisSubtitulo: Color
    @Composable get() = Color(0xFF6B7280).enTema()
private val GrisEtiqueta: Color
    @Composable get() = Color(0xFF6B7280).enTema()
private val CampoFondo: Color
    @Composable get() = Color(0xFFEAF3EF).enTema()
private val GrisBorde: Color
    @Composable get() = Color(0xFFD8DED9).enTema()
@Composable
fun TutorRegisterScreen(
    onBack: () -> Unit,
    onCrearCuenta: (String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _ -> }
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var apellido by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var fechaNacimiento by rememberSaveable { mutableStateOf("") }
    var codigoVinculacion by rememberSaveable { mutableStateOf("") }

    // Consentimiento expreso: separado y destildado por defecto (Ley 25.326)
    var aceptaTerminos by rememberSaveable { mutableStateOf(false) }
    var aceptaConfidencialidad by rememberSaveable { mutableStateOf(false) }
    var mostrarTerminos by rememberSaveable { mutableStateOf(false) }
    var mostrarPolitica by rememberSaveable { mutableStateOf(false) }

    if (mostrarTerminos) {
        DialogoLegal(titulo = "Términos y condiciones", texto = TextosLegales.terminos) { mostrarTerminos = false }
    }
    if (mostrarPolitica) {
        DialogoLegal(titulo = "Política de privacidad", texto = TextosLegales.politica) { mostrarPolitica = false }
    }

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
            text = "Crear cuenta de tutor",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeTitulo,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Completá tus datos para crear una cuenta de tutor\ny poder acompañar al paciente.",
            fontSize = 15.sp,
            color = GrisSubtitulo,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 21.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        CampoConEtiqueta("Nombre", Icons.Filled.Person, nombre, { nombre = it }, "Ingresá tu nombre")

        Spacer(modifier = Modifier.height(16.dp))

        CampoConEtiqueta("Apellido", Icons.Filled.Person, apellido, { apellido = it }, "Ingresá tu apellido")

        Spacer(modifier = Modifier.height(16.dp))

        CampoConEtiqueta("Correo electrónico", Icons.Filled.Email, correo, { correo = it }, "correo@ejemplo.com")

        Spacer(modifier = Modifier.height(16.dp))

        CampoConEtiqueta("Número de teléfono", Icons.Filled.Phone, telefono, { telefono = it }, "+54 9 11 1234 5678")

        Spacer(modifier = Modifier.height(16.dp))

        CampoConEtiqueta("Fecha de nacimiento", Icons.Filled.CalendarMonth, fechaNacimiento, { fechaNacimiento = it }, "DD/MM/AAAA")

        Spacer(modifier = Modifier.height(16.dp))

        CampoConEtiqueta("Código de vinculación del paciente", Icons.Filled.Link, codigoVinculacion, { codigoVinculacion = it }, "Ingresá el código de 6 dígitos")

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "El paciente te proporcionará este código\npara vincular sus datos con tu cuenta.",
            fontSize = 13.sp,
            color = GrisSubtitulo,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.fillMaxWidth()
        )

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
                color = Color(0xFF333333).enTema(),
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

        // --- Consentimiento 2: compromiso sobre los datos de salud del paciente ---
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
                text = "Entiendo que voy a ver datos de salud del paciente, que son datos sensibles. " +
                        "Me comprometo a usarlos solo para acompañarlo y a no compartirlos con terceros.",
                fontSize = 14.sp,
                color = Color(0xFF333333).enTema(),
                lineHeight = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onCrearCuenta(nombre, apellido, correo, telefono, fechaNacimiento, codigoVinculacion)
            },
            enabled = aceptaTerminos && aceptaConfidencialidad,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeTitulo)
        ) {
            Text("Continuar", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            shape = RoundedCornerShape(28.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GrisBorde)
        ) {
            Text("Cancelar", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16324F).enTema())
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun CampoConEtiqueta(
    etiqueta: String,
    icono: ImageVector,
    valor: String,
    onValorCambia: (String) -> Unit,
    placeholder: String
) {
    Text(etiqueta, fontSize = 13.sp, color = GrisEtiqueta)
    Spacer(modifier = Modifier.height(6.dp))
    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambia,
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(icono, contentDescription = null, tint = VerdeTitulo)
        },
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