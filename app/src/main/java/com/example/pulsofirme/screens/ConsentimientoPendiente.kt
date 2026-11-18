package com.example.pulsofirme.screens

import androidx.compose.runtime.saveable.rememberSaveable

import com.example.pulsofirme.ui.theme.enTema

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.DialogoLegal
import com.example.pulsofirme.TextosLegales
import com.example.pulsofirme.areaTactil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

private val VerdeCp: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
/**
 * Pantalla para cuentas que se registraron antes de existir el consentimiento
 * (o cuando se sube TextosLegales.VERSION). Bloquea la app hasta aceptar o salir.
 *
 * rol = "paciente" | "tutor" | "profesional"
 */
@Composable
fun PantallaConsentimientoPendiente(
    rol: String,
    onAceptado: () -> Unit,
    onRechazar: () -> Unit
) {
    val context = LocalContext.current
    val esPaciente = rol == "paciente"

    var aceptaTerminos by rememberSaveable { mutableStateOf(false) }
    var aceptaSegundo by rememberSaveable { mutableStateOf(false) }
    var mostrarTerminos by rememberSaveable { mutableStateOf(false) }
    var mostrarPolitica by rememberSaveable { mutableStateOf(false) }
    var guardando by remember { mutableStateOf(false) }

    if (mostrarTerminos) {
        DialogoLegal(titulo = "Términos y condiciones", texto = TextosLegales.terminos) { mostrarTerminos = false }
    }
    if (mostrarPolitica) {
        DialogoLegal(titulo = "Política de privacidad", texto = TextosLegales.politica) { mostrarPolitica = false }
    }

    fun guardar() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            onRechazar()
            return
        }
        guardando = true

        val campos = hashMapOf<String, Any>(
            "consentimientoTerminos" to true,
            "consentimientoVersion" to TextosLegales.VERSION,
            "consentimientoFecha" to FieldValue.serverTimestamp()
        )
        if (esPaciente) campos["consentimientoSalud"] = true
        else campos["consentimientoConfidencialidad"] = true

        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(uid)
            .update(campos)
            .addOnSuccessListener {
                guardando = false
                onAceptado()
            }
            .addOnFailureListener {
                guardando = false
                Toast.makeText(
                    context,
                    "No se pudo guardar tu aceptación. Revisá tu conexión e intentá de nuevo.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.enTema())
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(56.dp))

        Text(
            text = "Antes de seguir",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeCp
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = "Actualizamos los términos y la política de privacidad de PulsoFirme " +
                    "(Ley 25.326 de Protección de los Datos Personales). " +
                    "Necesitamos tu aceptación para que sigas usando la app.",
            fontSize = 15.sp,
            color = Color(0xFF374151).enTema(),
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .areaTactil()
                .toggleable(value = aceptaTerminos, role = Role.Checkbox, onValueChange = { aceptaTerminos = it }),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = aceptaTerminos,
                onCheckedChange = null,
                modifier = Modifier.padding(top = 2.dp, end = 10.dp),
                colors = CheckboxDefaults.colors(checkedColor = VerdeCp)
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
                color = VerdeCp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .areaTactil()
                    .clickable(role = Role.Button) { mostrarTerminos = true }
                    .padding(end = 20.dp, top = 12.dp)
            )
            Text(
                text = "Leer política",
                fontSize = 14.sp,
                color = VerdeCp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .areaTactil()
                    .clickable(role = Role.Button) { mostrarPolitica = true }
                    .padding(top = 12.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .areaTactil()
                .toggleable(value = aceptaSegundo, role = Role.Checkbox, onValueChange = { aceptaSegundo = it }),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = aceptaSegundo,
                onCheckedChange = null,
                modifier = Modifier.padding(top = 2.dp, end = 10.dp),
                colors = CheckboxDefaults.colors(checkedColor = VerdeCp)
            )
            Text(
                text = when (rol) {
                    "paciente" ->
                        "Doy mi consentimiento expreso para que PulsoFirme trate mis datos de salud " +
                                "(diagnóstico, medicación, ejercicios y evaluaciones) y los guarde en " +
                                "servidores de Google Firebase, que pueden estar fuera de la Argentina."
                    "profesional" ->
                        "Entiendo que voy a acceder a datos de salud de pacientes, que son datos sensibles. " +
                                "Me comprometo a usarlos solo con fines de seguimiento y a respetar el secreto profesional."
                    else ->
                        "Entiendo que voy a ver datos de salud del paciente, que son datos sensibles. " +
                                "Me comprometo a usarlos solo para acompañarlo y a no compartirlos con terceros."
                },
                fontSize = 14.sp,
                color = Color(0xFF333333).enTema(),
                lineHeight = 20.sp
            )
        }

        if (esPaciente) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Podés retirar este consentimiento y borrar tus datos cuando quieras, " +
                        "desde Privacidad y datos.",
                fontSize = 13.sp,
                color = Color(0xFF6B7280).enTema(),
                lineHeight = 18.sp,
                modifier = Modifier.padding(start = 40.dp)
            )
        }

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = { guardar() },
            enabled = aceptaTerminos && aceptaSegundo && !guardando,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeCp)
        ) {
            Text(
                text = if (guardando) "Guardando…" else "Aceptar y continuar",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onRechazar,
            enabled = !guardando,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "No acepto, cerrar sesión",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF16324F).enTema()
            )
        }

        Spacer(Modifier.height(40.dp))
    }
}