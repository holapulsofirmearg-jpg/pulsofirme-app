package com.example.pulsofirme.screens

import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.widget.Toast
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pulsofirme.TextosLegales
import com.example.pulsofirme.ErroresFirebase
import com.example.pulsofirme.exportarMisDatos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.Accesibilidad
import com.example.pulsofirme.DificultadAdaptativa

import com.example.pulsofirme.EjerciciosPremium
import com.example.pulsofirme.LimiteTutores
import com.example.pulsofirme.ResultadoBorrado
import com.example.pulsofirme.eliminarCuentaCompleta
import kotlinx.coroutines.launch
import com.example.pulsofirme.alarmas.RecordatorioEjercicio
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun AppNavGraph() {

    val navController: NavHostController = rememberNavController()

    // Datos compartidos por todas las pantallas (ver EstadoNav.kt)
    val estado = rememberEstadoNav(navController)

    with(estado) {

        var consentimientoRol by remember { mutableStateOf<String?>(null) }

        DisposableEffect(Unit) {
            val oyente = FirebaseAuth.AuthStateListener { fa ->
                val usuario = fa.currentUser
                if (usuario == null) {
                    consentimientoRol = null
                } else {
                    db.collection("usuarios").document(usuario.uid).get()
                        .addOnSuccessListener { doc ->
                            // Si el documento todavía no existe es un registro nuevo (ya acepta ahí)
                            if (doc.exists() &&
                                fa.currentUser?.uid == usuario.uid &&
                                doc.getString("consentimientoVersion") != TextosLegales.VERSION
                            ) {
                                consentimientoRol = doc.getString("rol") ?: "paciente"
                            }
                        }
                }
            }
            auth.addAuthStateListener(oyente)
            onDispose { auth.removeAuthStateListener(oyente) }
        }

        consentimientoRol?.let { rolPendiente ->
            val contextoConsentimiento = LocalContext.current
            Dialog(
                onDismissRequest = {},
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false
                )
            ) {
                PantallaConsentimientoPendiente(
                    rol = rolPendiente,
                    onAceptado = { consentimientoRol = null },
                    onRechazar = {
                        RecordatorioEjercicio.cancelar(contextoConsentimiento)
                        DificultadAdaptativa.reiniciar(contextoConsentimiento)
                        auth.signOut()
                        consentimientoRol = null
                        nombreUsuarioActual = "Usuario"
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        NavHost(
            navController = navController,
            startDestination = "splash",
            // "Reducir animaciones" (Ajustes): las pantallas cambian sin transición
            enterTransition = {
                if (Accesibilidad.reducirAnimaciones) EnterTransition.None else fadeIn(tween(700))
            },
            exitTransition = {
                if (Accesibilidad.reducirAnimaciones) ExitTransition.None else fadeOut(tween(700))
            },
            popEnterTransition = {
                if (Accesibilidad.reducirAnimaciones) EnterTransition.None else fadeIn(tween(700))
            },
            popExitTransition = {
                if (Accesibilidad.reducirAnimaciones) ExitTransition.None else fadeOut(tween(700))
            }
        ) {
            // Las rutas están separadas por tipo de usuario, cada grupo en su archivo
            rutasInicio(estado)
            rutasPaciente(estado)
            rutasCuidador(estado)
            rutasProfesional(estado)
        }
    }
}

/**
 * Pide la contraseña y borra la cuenta completa (Firestore + Authentication).
 * Se usa desde Ajustes del paciente y Ajustes del cuidador.
 */
@Composable
internal fun BorrarCuentaConClave(
    onCancelar: () -> Unit,
    onBorrada: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var borrando by remember { mutableStateOf(false) }

    DialogoClaveParaBorrar(
        borrando = borrando,
        onCancelar = { if (!borrando) onCancelar() },
        onConfirmar = { clave ->
            borrando = true
            scope.launch {
                when (val r = eliminarCuentaCompleta(clave)) {
                    is ResultadoBorrado.Ok -> {
                        borrando = false
                        Toast.makeText(
                            context,
                            tr("Tu cuenta y tus datos fueron eliminados"),
                            Toast.LENGTH_LONG
                        ).show()
                        onBorrada()
                    }
                    is ResultadoBorrado.ContrasenaIncorrecta -> {
                        borrando = false
                        Toast.makeText(context, tr("La contraseña no es correcta"), Toast.LENGTH_LONG).show()
                    }
                    is ResultadoBorrado.SinConexion -> {
                        borrando = false
                        Toast.makeText(
                            context,
                            tr("No hay conexión a internet. Probá de nuevo con WiFi o datos."),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is ResultadoBorrado.Fallo -> {
                        borrando = false
                        Toast.makeText(context, tr(r.detalle), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    )
}

/** Pide la contraseña antes de borrar la cuenta (Firebase exige reautenticar). */
@Composable
internal fun DialogoClaveParaBorrar(
    borrando: Boolean,
    onCancelar: () -> Unit,
    onConfirmar: (String) -> Unit
) {
    var clave by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        containerColor = Color.White,
        title = {
            Text(tr("Eliminar mi cuenta"), fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
        },
        text = {
            Column {
                Text(
                    tr("Se borran tus datos y tu cuenta de forma definitiva. " +
                            "Para confirmar, escribí tu contraseña."),
                    fontSize = 14.sp,
                    color = Color(0xFF374151)
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = clave,
                    onValueChange = { clave = it },
                    label = { Text(tr("Contraseña")) },
                    singleLine = true,
                    enabled = !borrando,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirmar(clave) },
                enabled = clave.isNotBlank() && !borrando,
                modifier = Modifier.heightIn(min = 56.dp)
            ) {
                Text(
                    tr(if (borrando) "Eliminando…" else "Eliminar definitivamente"),
                    color = Color(0xFFC0392B),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {


            TextButton(
                onClick = onCancelar,
                enabled = !borrando,
                modifier = Modifier.heightIn(min = 56.dp)
            ) { Text(tr("Cancelar"), color = Color(0xFF6B7280)) }
        }
    )
}