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
import androidx.navigation.NavGraphBuilder

// Pantallas exclusivas del profesional de salud.
// Las rutas usan los datos compartidos de EstadoNav (nombreUsuarioActual, navController, etc.).
fun NavGraphBuilder.rutasProfesional(estado: EstadoNav) {
    with(estado) {


        // =====================================================
        // AYUDA Y SOPORTE - PROFESIONAL
        // =====================================================

        composable("professional_support") {
            if (rolCuidadorActual == "profesional") {

                val context = LocalContext.current

                ProfessionalHelpSupportScreen(
                    onBack = { navController.popBackStack() },
                    onPreguntasFrecuentes = {
                        navController.navigate("dudas_frecuentes")
                    },
                    onGestionarPacientes = {
                        navController.navigate("caregiver_patients")
                    },
                    onInterpretarProgreso = {
                        navController.navigate("caregiver_progress")
                    },
                    onPremiumProfesional = {
                        navController.navigate("professional_premium")
                    },
                    onReportarProblema = {
                        val intentCorreo = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:")
                            putExtra(
                                Intent.EXTRA_EMAIL,
                                arrayOf("holapulsofirmearg@gmail.com")
                            )
                            putExtra(
                                Intent.EXTRA_SUBJECT,
                                "Consulta / problema - PulsoFirme Profesional"
                            )
                        }

                        try {
                            context.startActivity(intentCorreo)
                        } catch (_: Exception) {
                            Toast.makeText(
                                context,
                                "No se encontró una aplicación de correo instalada",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate("caregiver_support") {
                        popUpTo("professional_support") { inclusive = true }
                    }
                }
            }
        }


        // =====================================================
        // EJERCICIOS REALIZADOS - PROFESIONAL
        // =====================================================

        composable("professional_exercises_progress") {
            if (rolCuidadorActual == "profesional") {
                ProfessionalExercisesProgressScreen(
                    onBack = { navController.popBackStack() }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate("caregiver_home") {
                        popUpTo("professional_exercises_progress") {
                            inclusive = true
                        }
                    }
                }
            }
        }


        // =====================================================
        // TIEMPO DE ACTIVIDAD - PROFESIONAL
        // =====================================================

        composable("professional_activity_time") {
            if (rolCuidadorActual == "profesional") {
                ProfessionalActivityTimeScreen(
                    onBack = { navController.popBackStack() }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate("caregiver_home") {
                        popUpTo("professional_activity_time") {
                            inclusive = true
                        }
                    }
                }
            }
        }


        // =====================================================
        // CUMPLIMIENTO - PROFESIONAL
        // =====================================================

        composable("professional_compliance") {
            if (rolCuidadorActual == "profesional") {
                ProfessionalComplianceScreen(
                    onBack = { navController.popBackStack() },
                    onPacienteClick = { nombrePaciente ->
                        pacienteProfesionalSeleccionado = nombrePaciente
                        navController.navigate("professional_patient_detail")
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate("caregiver_home") {
                        popUpTo("professional_compliance") {
                            inclusive = true
                        }
                    }
                }
            }
        }


        // =====================================================
        // EVOLUCIÓN DE PACIENTES - PROFESIONAL
        // =====================================================

        composable("professional_patient_evolution") {
            if (rolCuidadorActual == "profesional") {
                ProfessionalPatientEvolutionScreen(
                    onBack = { navController.popBackStack() },
                    onPacienteClick = { nombrePaciente ->
                        pacienteProfesionalSeleccionado = nombrePaciente
                        navController.navigate("professional_patient_detail")
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate("caregiver_home") {
                        popUpTo("professional_patient_evolution") {
                            inclusive = true
                        }
                    }
                }
            }
        }


        // =====================================================
        // NOTIFICACIONES - PROFESIONAL
        // =====================================================

        composable("professional_notifications") {
            if (rolCuidadorActual == "profesional") {
                ProfessionalNotificationsScreen(
                    onBack = { navController.popBackStack() },
                    onPacienteClick = { nombrePaciente ->
                        pacienteProfesionalSeleccionado = nombrePaciente
                        navController.navigate("professional_patient_detail")
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate("caregiver_home") {
                        popUpTo("professional_notifications") {
                            inclusive = true
                        }
                    }
                }
            }
        }


        // =====================================================
        // PACIENTES - PROFESIONAL
        // =====================================================

        composable("caregiver_patients") {

            // Esta sección es exclusiva del rol profesional.
            if (rolCuidadorActual == "profesional") {

                ProfessionalPatientsScreen(
                    rutaActual = "caregiver_patients",

                    onNavigate = { ruta ->
                        navController.navigate(ruta)
                    },

                    onPacienteClick = { nombrePaciente ->
                        pacienteProfesionalSeleccionado = nombrePaciente
                        navController.navigate("professional_patient_detail")
                    }
                )

            } else {

                // Si un tutor intenta acceder manualmente a esta ruta,
                // vuelve a su panel.
                LaunchedEffect(Unit) {
                    navController.navigate("caregiver_home") {
                        popUpTo("caregiver_patients") {
                            inclusive = true
                        }
                    }
                }
            }
        }


        // =====================================================
        // DETALLE DE PACIENTE - PROFESIONAL
        // =====================================================

        composable("professional_patient_detail") {

            if (rolCuidadorActual == "profesional") {
                ProfessionalPatientDetailScreen(
                    nombrePaciente = pacienteProfesionalSeleccionado,

                    onBack = {
                        navController.popBackStack()
                    },

                    onVerProgresoCompleto = {
                        navController.navigate("caregiver_progress")
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate("caregiver_home") {
                        popUpTo("professional_patient_detail") {
                            inclusive = true
                        }
                    }
                }
            }
        }


        // =====================================================
        // DATOS PROFESIONALES
        // =====================================================

        composable("professional_data") {

            val context = LocalContext.current
            val uid = auth.currentUser?.uid

            var lugarTrabajo by rememberSaveable { mutableStateOf("") }
            var especialidad by rememberSaveable { mutableStateOf("") }
            var matricula by rememberSaveable { mutableStateOf("") }
            var formacionAdicional by rememberSaveable { mutableStateOf("") }

            LaunchedEffect(uid) {
                if (uid != null) {
                    db.collection("usuarios")
                        .document(uid)
                        .get()
                        .addOnSuccessListener { document ->
                            lugarTrabajo = document.getString("lugarTrabajo") ?: ""
                            especialidad = document.getString("especialidad") ?: ""
                            matricula = document.getString("matricula") ?: ""
                            formacionAdicional = document.getString("formacionAdicional") ?: ""
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "No se pudieron cargar los datos profesionales",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }

            ProfessionalDataScreen(
                lugarTrabajoInicial = lugarTrabajo,
                especialidadInicial = especialidad,
                matriculaInicial = matricula,
                formacionAdicionalInicial = formacionAdicional,
                onBack = { navController.popBackStack() },
                onGuardarCambios = { lugar, especialidadNueva, matriculaNueva, formacion ->
                    if (uid == null) {
                        Toast.makeText(
                            context,
                            "No se encontró la sesión del profesional",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val datos = mapOf(
                            "lugarTrabajo" to lugar.trim(),
                            "especialidad" to especialidadNueva.trim(),
                            "matricula" to matriculaNueva.trim(),
                            "formacionAdicional" to formacion.trim()
                        )

                        db.collection("usuarios")
                            .document(uid)
                            .update(datos)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Datos profesionales guardados correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.popBackStack()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "No se pudieron guardar los datos profesionales",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            )
        }


        // =====================================================
        // PREMIUM PROFESIONAL
        // =====================================================

        composable("professional_premium") {
            if (rolCuidadorActual == "profesional") {
                ProfessionalPremiumScreen(
                    onBack = { navController.popBackStack() },
                    onActivarPremium = { plan ->
                        // Próximo paso: conectar pago real.
                        // Mientras tanto, esPremium se activa a mano desde la consola de Firestore.
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate("caregiver_account") {
                        popUpTo("professional_premium") { inclusive = true }
                    }
                }
            }
        }


        // =====================================================
        // EDITAR PERFIL PROFESIONAL
        // =====================================================

        composable("professional_edit_profile") {

            val context = LocalContext.current
            val uid = auth.currentUser?.uid

            LaunchedEffect(uid) {
                if (uid != null) {
                    db.collection("usuarios")
                        .document(uid)
                        .get()
                        .addOnSuccessListener { document ->
                            tutorPerfilNombre = document.getString("nombre") ?: ""
                            tutorPerfilApellido = document.getString("apellido") ?: ""
                            tutorPerfilCorreo =
                                document.getString("correo")
                                    ?: auth.currentUser?.email.orEmpty()
                            tutorPerfilTelefono = document.getString("telefono") ?: ""
                            tutorPerfilFechaNacimiento =
                                document.getString("fechaNacimiento") ?: ""
                            tutorPerfilGenero = document.getString("genero") ?: ""
                            tutorPerfilPais = document.getString("pais") ?: ""
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "No se pudieron cargar los datos del profesional",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }

            ProfessionalEditProfileScreen(
                nombreInicial = tutorPerfilNombre,
                apellidoInicial = tutorPerfilApellido,
                correoInicial = tutorPerfilCorreo,
                telefonoInicial = tutorPerfilTelefono,
                fechaNacimientoInicial = tutorPerfilFechaNacimiento,
                generoInicial = tutorPerfilGenero,
                paisInicial = tutorPerfilPais,

                onBack = {
                    navController.popBackStack()
                },

                onGuardarCambios = {
                        nombre,
                        apellido,
                        correo,
                        telefono,
                        fechaNacimiento,
                        genero,
                        pais ->

                    if (uid == null) {
                        Toast.makeText(
                            context,
                            "No se encontró la sesión del profesional",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val datosActualizados = mapOf(
                            "nombre" to nombre.trim(),
                            "apellido" to apellido.trim(),
                            "correo" to correo.trim(),
                            "telefono" to telefono.trim(),
                            "fechaNacimiento" to fechaNacimiento.trim(),
                            "genero" to genero.trim(),
                            "pais" to pais.trim()
                        )

                        db.collection("usuarios")
                            .document(uid)
                            .update(datosActualizados)
                            .addOnSuccessListener {
                                tutorPerfilNombre = nombre.trim()
                                tutorPerfilApellido = apellido.trim()
                                tutorPerfilCorreo = correo.trim()
                                tutorPerfilTelefono = telefono.trim()
                                tutorPerfilFechaNacimiento = fechaNacimiento.trim()
                                tutorPerfilGenero = genero.trim()
                                tutorPerfilPais = pais.trim()
                                nombreUsuarioActual = nombre.trim()

                                Toast.makeText(
                                    context,
                                    "Datos del profesional actualizados correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()

                                navController.popBackStack()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "No se pudieron guardar los cambios",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                },

                onCambiarContrasena = {
                    navController.navigate("caregiver_change_password")
                }
            )
        }


        // =====================================================
        // PERMISOS DE ACCESO - PROFESIONAL
        // =====================================================

        composable("professional_access_permissions") {
            if (rolCuidadorActual == "profesional") {
                ProfessionalAccessPermissionsScreen(
                    onBack = { navController.popBackStack() },
                    onPacienteClick = { uidPaciente, nombrePaciente ->
                        pacienteProfesionalUidSeleccionado = uidPaciente
                        pacienteProfesionalSeleccionado = nombrePaciente
                        navController.navigate("professional_patient_permissions")
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate("caregiver_account") {
                        popUpTo("professional_access_permissions") { inclusive = true }
                    }
                }
            }
        }


        // =====================================================
        // PERMISOS POR PACIENTE - PROFESIONAL
        // =====================================================

        composable("professional_patient_permissions") {
            if (rolCuidadorActual == "profesional") {
                ProfessionalPatientPermissionsScreen(
                    pacienteUid = pacienteProfesionalUidSeleccionado,
                    nombrePaciente = pacienteProfesionalSeleccionado,
                    onBack = { navController.popBackStack() }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate("caregiver_account") {
                        popUpTo("professional_patient_permissions") { inclusive = true }
                    }
                }
            }
        }
    }
}