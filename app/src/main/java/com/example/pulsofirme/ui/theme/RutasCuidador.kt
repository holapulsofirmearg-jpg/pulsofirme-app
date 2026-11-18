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

// Pantallas del tutor (y las que comparte con el profesional).
// Las rutas usan los datos compartidos de EstadoNav (nombreUsuarioActual, navController, etc.).
fun NavGraphBuilder.rutasCuidador(estado: EstadoNav) {
    with(estado) {


        // =====================================================
        // HOME CUIDADOR / TUTOR
        // =====================================================

        composable("caregiver_home") {

            if (rolCuidadorActual == "profesional") {
                HomeProfessionalScreen(
                    nombreProfesional = nombreUsuarioActual,
                    matricula = profesionalMatricula,
                    lugarTrabajo = profesionalLugarTrabajo,
                    especialidad = profesionalEspecialidad,
                    rutaActual = "caregiver_home",
                    onNavigate = { ruta -> navController.navigate(ruta) },
                    onVerPacientes = { navController.navigate("caregiver_patients") },
                    onEjerciciosRealizados = { navController.navigate("professional_exercises_progress") },
                    onTiempoActividad = { navController.navigate("professional_activity_time") },
                    onCumplimiento = { navController.navigate("professional_compliance") },
                    onEvolucionPacientes = { navController.navigate("professional_patient_evolution") },
                    onNotificaciones = {
                        navController.navigate("professional_notifications")
                    },
                    onPulsiBot = {
                        rolPulsiBotActual = "profesional"
                        navController.navigate("pulsibot")
                    }
                )
            } else {
                HomeCaregiverScreen(
                    nombreUsuario = nombreUsuarioActual,
                    rutaActual = "caregiver_home",
                    onNavigate = { ruta ->
                        if (ruta == "pulsibot") {
                            rolPulsiBotActual = rolCuidadorActual
                        }
                        navController.navigate(ruta)
                    },
                    onEjercicioClick = { navController.navigate("caregiver_exercises") },
                    onSensacionClick = {},
                    onEvolucionClick = { navController.navigate("caregiver_patient_evolution") },
                    rolUsuario = rolCuidadorActual
                )
            }
        }


        // =====================================================
        // EVOLUCIÓN DEL PACIENTE - CUIDADOR / TUTOR
        // =====================================================

        composable("caregiver_patient_evolution") {
            PatientEvolutionCaregiverScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }


        // =====================================================
        // EJERCICIOS CUIDADOR
        // =====================================================

        composable("caregiver_exercises") {
            ExercisesCaregiverScreen(

                rutaActual =
                    "caregiver_exercises",

                onNavigate = { ruta ->
                    navController.navigate(ruta)
                },

                onEjercicioClick = { nombre ->

                    nombreEjercicioActual = nombre

                    navController.navigate(
                        "exercise_in_progress"
                    )
                }
            )
        }


        // =====================================================
        // PROGRESO CUIDADOR
        // =====================================================

        composable("caregiver_progress") {

            if (rolCuidadorActual == "profesional") {

                ProfessionalProgressScreen(
                    rutaActual = "caregiver_progress",

                    onNavigate = { ruta ->
                        navController.navigate(ruta)
                    },

                    onPacienteClick = { nombrePaciente ->
                        pacienteProfesionalSeleccionado = nombrePaciente
                        navController.navigate("professional_patient_detail")
                    }
                )

            } else {

                ProgressCaregiverScreen(
                    rutaActual = "caregiver_progress",

                    onNavigate = { ruta ->
                        navController.navigate(ruta)
                    },

                    onQuickDashClick = {
                        navController.navigate(
                            "caregiver_quickdash_result"
                        )
                    },

                    rolUsuario = rolCuidadorActual
                )
            }
        }


        // =====================================================
        // RESULTADO EVALUACIÓN QUICKDASH CUIDADOR
        // =====================================================

        composable("caregiver_quickdash_result") {
            QuickDashResultCaregiverScreen(

                onBack = {
                    navController.popBackStack()
                },

                onVerDetalle = {
                    navController.navigate("caregiver_quickdash_detail")
                }
            )
        }


        // =====================================================
        // DETALLE EVALUACIÓN QUICKDASH CUIDADOR
        // =====================================================

        composable("caregiver_quickdash_detail") {
            QuickDashDetailCaregiverScreen(

                onBack = {
                    navController.popBackStack()
                },

                onVolverAlResumen = {
                    navController.popBackStack()
                }
            )
        }


        // =====================================================
        // ALERTAS CUIDADOR
        // =====================================================

        composable("caregiver_alerts") {
            AlertsCaregiverScreen(

                rutaActual =
                    "caregiver_alerts",

                onNavigate = { ruta ->
                    navController.navigate(ruta)
                },

                onConfigClick = {
                    navController.navigate("caregiver_alerts_config")
                }
            )
        }


        // =====================================================
        // CONFIGURACIÓN DE ALERTAS CUIDADOR
        // =====================================================

        composable("caregiver_alerts_config") {
            AlertsConfigCaregiverScreen(

                onBack = {
                    navController.popBackStack()
                },

                onGuardarCambios = { _, _, _, _, _ ->
                    navController.popBackStack()
                }
            )
        }


        // =====================================================
        // CUENTA CUIDADOR
        // =====================================================

        composable("caregiver_account") {

            val context = LocalContext.current

            if (rolCuidadorActual == "profesional") {
                AccountProfessionalScreen(
                    nombreProfesional = nombreUsuarioActual,
                    matricula = profesionalMatricula,
                    lugarTrabajo = profesionalLugarTrabajo,
                    especialidad = profesionalEspecialidad,
                    rutaActual = "caregiver_account",
                    onNavigate = { ruta -> navController.navigate(ruta) },
                    onEditarDatos = { navController.navigate("professional_edit_profile") },
                    onDatosProfesionales = {
                        navController.navigate("professional_data")
                    },
                    onGestionarPlan = {
                        navController.navigate("professional_premium")
                    },
                    onPrivacidad = { navController.navigate("caregiver_privacy") },
                    onAyuda = { navController.navigate("professional_support") },
                    onCambiarContrasena = { navController.navigate("caregiver_change_password") },
                    onCerrarSesion = {
                        DificultadAdaptativa.reiniciar(context)
                        auth.signOut()
                        nombreUsuarioActual = "Usuario"
                        rolCuidadorActual = "tutor"
                        profesionalMatricula = ""
                        profesionalLugarTrabajo = ""
                        profesionalEspecialidad = ""
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            } else {
                AccountCaregiverScreen(
                    rutaActual = "caregiver_account",
                    onNavigate = { ruta -> navController.navigate(ruta) },
                    onEditarDatos = { navController.navigate("caregiver_edit_profile") },
                    onPrivacidad = { navController.navigate("caregiver_privacy") },
                    onAyuda = { navController.navigate("caregiver_support") },
                    onCerrarSesion = {
                        // Antes no cerraba la sesión de Firebase: seguía abierta
                        DificultadAdaptativa.reiniciar(context)
                        auth.signOut()
                        nombreUsuarioActual = "Usuario"
                        rolCuidadorActual = "tutor"
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onProbarComoProfesional = {
                        rolCuidadorActual = "profesional"
                        navController.navigate("caregiver_home") {
                            popUpTo("caregiver_home") { inclusive = true }
                        }
                    },
                    rolUsuario = rolCuidadorActual
                )
            }
        }


        // =====================================================
        // EDITAR PERFIL CUIDADOR
        // =====================================================

        composable("caregiver_edit_profile") {

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
                            Avisos.mostrar("No se pudieron cargar los datos del tutor")
                        }
                }
            }

            CaregiverEditProfileScreen(
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
                        Avisos.mostrar("No se encontró la sesión del tutor")
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

                                Avisos.mostrar("Datos del tutor actualizados correctamente")

                                navController.popBackStack()
                            }
                            .addOnFailureListener {
                                Avisos.mostrar("No se pudieron guardar los cambios")
                            }
                    }
                },

                onCambiarContrasena = {
                    navController.navigate(
                        "caregiver_change_password"
                    )
                }
            )
        }


        // =====================================================
        // CAMBIAR CONTRASEÑA CUIDADOR
        // =====================================================

        composable("caregiver_change_password") {
            CaregiverChangePasswordScreen(

                onBack = {
                    navController.popBackStack()
                },

                onCambiarContrasena = { _, _ ->
                    navController.popBackStack()
                }
            )
        }


        // =====================================================
        // DETALLE CUENTA CUIDADOR
        // =====================================================

        composable("caregiver_account_detail") {
            CaregiverAccountDetailScreen(

                onBack = {
                    navController.popBackStack()
                },

                onEditarDatos = {
                    navController.navigate(
                        "caregiver_edit_profile"
                    )
                }
            )
        }


        // =====================================================
        // CAMBIAR PACIENTE (TUTOR)
        // Canjea el código de otro paciente y pasa el vínculo.
        // =====================================================

        composable("caregiver_change_patient") {

            val context = LocalContext.current

            CaregiverChangePatientScreen(

                onBack = {
                    navController.popBackStack()
                },

                onVincularNuevoPaciente = { codigoIngresado ->

                    val tutorUid = auth.currentUser?.uid
                    val codigo = codigoIngresado.trim()

                    if (tutorUid == null) {
                        Avisos.mostrar("No se encontró la sesión del tutor")
                    } else if (codigo.length != 6 || !codigo.all { it.isDigit() }) {
                        Avisos.mostrar("Ingresá el código de 6 dígitos del paciente")
                    } else {
                        val tutorRef = db.collection("usuarios").document(tutorUid)

                        tutorRef.get()
                            .addOnSuccessListener { tutorDocument ->

                                val pacienteActualUid =
                                    tutorDocument.getString("pacienteVinculadoUid").orEmpty()

                                db.collection("codigos")
                                    .document(codigo)
                                    .get()
                                    .addOnSuccessListener { codigoDoc ->

                                        val nuevoPacienteUid =
                                            codigoDoc.getString("uid").orEmpty()

                                        when {
                                            !codigoDoc.exists() || nuevoPacienteUid.isBlank() -> {
                                                Avisos.mostrar("No existe un paciente con ese código", largo = true)
                                            }

                                            nuevoPacienteUid == pacienteActualUid -> {
                                                Avisos.mostrar("Ese paciente ya está vinculado")
                                            }

                                            else -> {
                                                // El límite de tutores del nuevo paciente lo valida
                                                // Firestore (reglas); desde acá no se puede leer su documento.
                                                val batch = db.batch()

                                                batch.update(
                                                    tutorRef,
                                                    mapOf(
                                                        "pacienteVinculadoUid" to nuevoPacienteUid,
                                                        "codigoVinculacion" to codigo,
                                                        "fechaVinculacion" to
                                                                com.google.firebase.firestore.FieldValue.serverTimestamp()
                                                    )
                                                )

                                                if (pacienteActualUid.isNotBlank()) {
                                                    batch.update(
                                                        db.collection("usuarios")
                                                            .document(pacienteActualUid),
                                                        "tutoresVinculados",
                                                        com.google.firebase.firestore.FieldValue.arrayRemove(
                                                            tutorUid
                                                        )
                                                    )
                                                }

                                                batch.update(
                                                    db.collection("usuarios")
                                                        .document(nuevoPacienteUid),
                                                    "tutoresVinculados",
                                                    com.google.firebase.firestore.FieldValue.arrayUnion(
                                                        tutorUid
                                                    )
                                                )

                                                batch.commit()
                                                    .addOnSuccessListener {
                                                        Avisos.mostrar("Paciente vinculado correctamente")

                                                        navController.popBackStack()
                                                    }
                                                    .addOnFailureListener { error ->
                                                        val denegado =
                                                            (error as? com.google.firebase.firestore
                                                            .FirebaseFirestoreException)?.code ==
                                                                    com.google.firebase.firestore
                                                                        .FirebaseFirestoreException.Code
                                                                        .PERMISSION_DENIED
                                                        Avisos.mostrar(if (denegado) "No se pudo vincular: ese paciente ya alcanzó " +
                                                                trf("el máximo de tutores ({0} en el plan gratis, ", LimiteTutores.MAX_GRATIS) +
                                                                trf("{0} con Premium).", LimiteTutores.MAX_PREMIUM)
                                                        else "No se pudo cambiar de paciente", largo = true)
                                                    }
                                            }
                                        }
                                    }
                                    .addOnFailureListener {
                                        Avisos.mostrar("No se pudo verificar el código", largo = true)
                                    }
                            }
                            .addOnFailureListener {
                                Avisos.mostrar("No se pudieron cargar los datos del vínculo")
                            }
                    }
                }
            )
        }


        // =====================================================
        // PACIENTE VINCULADO (TUTOR) - DATOS REALES
        // =====================================================

        composable("caregiver_patient") {

            val tutorUid = auth.currentUser?.uid

            var pacienteNombre by rememberSaveable { mutableStateOf("") }
            var pacienteFechaVinculo by rememberSaveable { mutableStateOf("") }
            var pacienteFechaNacimiento by rememberSaveable { mutableStateOf("") }
            var pacienteGenero by rememberSaveable { mutableStateOf("") }
            var pacientePais by rememberSaveable { mutableStateOf("") }
            var pacienteComparteProgreso by remember { mutableStateOf<Boolean?>(null) }
            var pacienteComparteMedicacion by remember { mutableStateOf<Boolean?>(null) }
            var pacienteComparteUbicacion by remember { mutableStateOf<Boolean?>(null) }

            LaunchedEffect(tutorUid) {
                if (tutorUid != null) {
                    db.collection("usuarios")
                        .document(tutorUid)
                        .get()
                        .addOnSuccessListener { tutorDocument ->

                            val fechaVinculo =
                                tutorDocument.getTimestamp("fechaVinculacion")?.toDate()

                            if (fechaVinculo != null) {
                                pacienteFechaVinculo =
                                    java.text.SimpleDateFormat(
                                        "d 'de' MMMM 'de' yyyy",
                                        java.util.Locale("es", "AR")
                                    ).format(fechaVinculo)
                            }

                            val pacienteUid =
                                tutorDocument.getString("pacienteVinculadoUid").orEmpty()

                            if (pacienteUid.isNotBlank()) {
                                db.collection("usuarios")
                                    .document(pacienteUid)
                                    .get()
                                    .addOnSuccessListener { pacienteDocument ->
                                        pacienteNombre =
                                            listOfNotNull(
                                                pacienteDocument.getString("nombre"),
                                                pacienteDocument.getString("apellido")
                                            )
                                                .filter { it.isNotBlank() }
                                                .joinToString(" ")

                                        pacienteFechaNacimiento =
                                            pacienteDocument.getString("fechaNacimiento") ?: ""
                                        pacienteGenero =
                                            pacienteDocument.getString("genero") ?: ""
                                        pacientePais =
                                            pacienteDocument.getString("pais") ?: ""

                                        // Permisos reales (mismos valores por defecto que Privacidad y datos)
                                        pacienteComparteProgreso =
                                            pacienteDocument.getBoolean("permisosTutor.progreso") ?: true
                                        pacienteComparteMedicacion =
                                            pacienteDocument.getBoolean("permisosTutor.medicacion") ?: true
                                        pacienteComparteUbicacion =
                                            pacienteDocument.getBoolean("permisosTutor.ubicacion") ?: false
                                    }
                            }
                        }
                }
            }

            CaregiverPatientScreen(
                nombrePaciente = pacienteNombre.ifBlank { "Paciente" },
                fechaVinculacion = pacienteFechaVinculo.ifBlank { "-" },
                fechaNacimiento = pacienteFechaNacimiento.ifBlank { "-" },
                genero = pacienteGenero.ifBlank { "-" },
                pais = pacientePais.ifBlank { "-" },
                comparteProgreso = pacienteComparteProgreso,
                comparteMedicacion = pacienteComparteMedicacion,
                comparteUbicacion = pacienteComparteUbicacion,

                onBack = {
                    navController.popBackStack()
                },

                onCambiarPaciente = {
                    navController.navigate(
                        "caregiver_change_patient"
                    )
                }
            )
        }


        // =====================================================
        // PRIVACIDAD CUIDADOR
        // =====================================================

        composable("caregiver_privacy") {

            if (rolCuidadorActual == "profesional") {

                val context = LocalContext.current
                val uid = auth.currentUser?.uid

                var datosPdfProfesional by remember {
                    mutableStateOf(com.example.pulsofirme.ProfessionalPdfData())
                }

                LaunchedEffect(uid) {
                    if (uid != null) {
                        db.collection("usuarios")
                            .document(uid)
                            .get()
                            .addOnSuccessListener { document ->
                                datosPdfProfesional = com.example.pulsofirme.ProfessionalPdfData(
                                    nombre = document.getString("nombre") ?: "",
                                    apellido = document.getString("apellido") ?: "",
                                    correo = document.getString("correo")
                                        ?: auth.currentUser?.email.orEmpty(),
                                    telefono = document.getString("telefono") ?: "",
                                    fechaNacimiento = document.getString("fechaNacimiento") ?: "",
                                    genero = document.getString("genero") ?: "",
                                    pais = document.getString("pais") ?: "",
                                    lugarTrabajo = document.getString("lugarTrabajo") ?: "",
                                    especialidad = document.getString("especialidad") ?: "",
                                    matricula = document.getString("matricula") ?: "",
                                    formacionAdicional = document.getString("formacionAdicional") ?: ""
                                )
                            }
                            .addOnFailureListener {
                                Avisos.mostrar("No se pudieron cargar los datos para el PDF")
                            }
                    }
                }

                ProfessionalPrivacyScreen(
                    onBack = {
                        navController.popBackStack()
                    },

                    datosProfesional = datosPdfProfesional,

                    onGestionarPacientes = {
                        navController.navigate("caregiver_patients")
                    },

                    onPermisosAcceso = {
                        navController.navigate("professional_access_permissions")
                    },

                    onCuentaEliminada = {
                        nombreUsuarioActual = "Usuario"
                        rolCuidadorActual = "tutor"
                        profesionalMatricula = ""
                        profesionalLugarTrabajo = ""
                        profesionalEspecialidad = ""
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )

            } else {

                val context = LocalContext.current
                val tutorUid = auth.currentUser?.uid
                val scope = rememberCoroutineScope()
                var pedirClaveBorrado by rememberSaveable { mutableStateOf(false) }
                var borrando by remember { mutableStateOf(false) }

                if (pedirClaveBorrado) {
                    DialogoClaveParaBorrar(
                        borrando = borrando,
                        onCancelar = { if (!borrando) pedirClaveBorrado = false },
                        onConfirmar = { clave ->
                            borrando = true
                            scope.launch {
                                when (val r = eliminarCuentaCompleta(clave)) {
                                    is ResultadoBorrado.Ok -> {
                                        borrando = false
                                        pedirClaveBorrado = false
                                        DificultadAdaptativa.reiniciar(context)
                                        nombreUsuarioActual = "Usuario"
                                        rolCuidadorActual = "tutor"
                                        Avisos.mostrar("Tu cuenta y tus datos fueron eliminados", largo = true)
                                        navController.navigate("login") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                    is ResultadoBorrado.ContrasenaIncorrecta -> {
                                        borrando = false
                                        Avisos.mostrar("La contraseña no es correcta", largo = true)
                                    }
                                    is ResultadoBorrado.SinConexion -> {
                                        borrando = false
                                        Avisos.mostrar("Sin conexión. Probá de nuevo con internet.", largo = true)
                                    }
                                    is ResultadoBorrado.Fallo -> {
                                        borrando = false
                                        Avisos.mostrar(r.detalle, largo = true)
                                    }
                                }
                            }
                        }
                    )
                }

                CaregiverPrivacyScreen(
                    onBack = {
                        navController.popBackStack()
                    },

                    onDescargarDatos = {
                        scope.launch {
                            val archivo = exportarMisDatos(context)
                            Avisos.mostrar(if (archivo != null) trf("Guardado en Descargas: {0}", archivo)
                            else "No se pudieron exportar tus datos", largo = true)
                        }
                    },

                    onDesvincularPaciente = {
                        if (tutorUid == null) {
                            Avisos.mostrar("No se encontró la sesión del tutor")
                        } else {
                            val tutorRef = db.collection("usuarios").document(tutorUid)

                            tutorRef.get()
                                .addOnSuccessListener { tutorDocument ->
                                    val pacienteUid =
                                        tutorDocument.getString("pacienteVinculadoUid").orEmpty()

                                    if (pacienteUid.isBlank()) {
                                        Avisos.mostrar("No hay un paciente vinculado")
                                    } else {
                                        val pacienteRef =
                                            db.collection("usuarios").document(pacienteUid)

                                        val batch = db.batch()

                                        batch.update(
                                            tutorRef,
                                            mapOf(
                                                "pacienteVinculadoUid" to
                                                        com.google.firebase.firestore.FieldValue.delete(),
                                                "codigoVinculacion" to
                                                        com.google.firebase.firestore.FieldValue.delete(),
                                                "fechaVinculacion" to
                                                        com.google.firebase.firestore.FieldValue.delete()
                                            )
                                        )

                                        batch.update(
                                            pacienteRef,
                                            "tutoresVinculados",
                                            com.google.firebase.firestore.FieldValue.arrayRemove(tutorUid)
                                        )

                                        batch.commit()
                                            .addOnSuccessListener {
                                                Avisos.mostrar("Paciente desvinculado correctamente")

                                                auth.signOut()
                                                nombreUsuarioActual = "Usuario"
                                                rolCuidadorActual = "tutor"

                                                navController.navigate("tutor_login") {
                                                    popUpTo(0) { inclusive = true }
                                                }
                                            }
                                            .addOnFailureListener {
                                                Avisos.mostrar("No se pudo desvincular al paciente", largo = true)
                                            }
                                    }
                                }
                                .addOnFailureListener {
                                    Avisos.mostrar("No se pudieron cargar los datos del vínculo")
                                }
                        }
                    },

                    onEliminarDatos = {
                        pedirClaveBorrado = true
                    }
                )
            }
        }


        // =====================================================
        // PREGUNTAS FRECUENTES CUIDADOR
        // =====================================================

        composable("caregiver_faq") {
            DudasFrecuentesCaregiverScreen(

                rutaActual =
                    "caregiver_account",

                onNavigate = { ruta ->
                    navController.navigate(ruta)
                },

                onBack = {
                    navController.popBackStack()
                }
            )
        }


        // =====================================================
        // SOPORTE CUIDADOR
        // =====================================================

        composable("caregiver_support") {
            val context = LocalContext.current

            CaregiverSupportScreen(

                onBack = {
                    navController.popBackStack()
                },

                onDudasFrecuentes = {
                    navController.navigate(
                        "caregiver_faq"
                    )
                },

                onReportarProblema = {
                    val intentCorreo = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf("holapulsofirmearg@gmail.com"))
                        putExtra(Intent.EXTRA_SUBJECT, "Reporte de problema - PulsoFirme Tutor")
                    }
                    try {
                        context.startActivity(intentCorreo)
                    } catch (_: Exception) {
                        Avisos.mostrar("No se encontró una aplicación de correo. Escribinos a holapulsofirmearg@gmail.com", largo = true)
                    }
                }
            )
        }


        // =====================================================
        // AJUSTES CUIDADOR
        // =====================================================

        composable("caregiver_settings") {
            val context = LocalContext.current
            var pedirClaveBorrado by rememberSaveable { mutableStateOf(false) }

            // Antes solo volvía al login y la cuenta seguía existiendo.
            // Ahora pide la contraseña y borra la cuenta y los datos de verdad.
            if (pedirClaveBorrado) {
                BorrarCuentaConClave(
                    onCancelar = { pedirClaveBorrado = false },
                    onBorrada = {
                        pedirClaveBorrado = false
                        DificultadAdaptativa.reiniciar(context)
                        nombreUsuarioActual = "Usuario"
                        rolCuidadorActual = "tutor"
                        profesionalMatricula = ""
                        profesionalLugarTrabajo = ""
                        profesionalEspecialidad = ""
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            AjustesCaregiverScreen(

                onBack = {
                    navController.popBackStack()
                },

                onEliminarCuenta = {
                    pedirClaveBorrado = true
                }
            )
        }

    }
}