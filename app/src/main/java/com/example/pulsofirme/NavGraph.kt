package com.example.pulsofirme.screens

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
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var nombreEjercicioActual by remember { mutableStateOf("") }
    var zonaEjercicioActual by remember { mutableStateOf("") }
    var tipoEjercicioActual by remember { mutableStateOf("") }

    var repeticionesCompletadas by remember { mutableStateOf(0) }
    var totalRepeticiones by remember { mutableStateOf(0) }
    var tiempoTotalSegundos by remember { mutableStateOf(0) }

    var sensacionInicial by remember { mutableStateOf("") }

    var nombreUsuarioActual by remember {
        mutableStateOf("Usuario")
    }

    var rolCuidadorActual by remember {
        mutableStateOf("tutor")
    }

    // Datos reales del perfil profesional cargados desde Firestore
    var profesionalMatricula by remember { mutableStateOf("") }
    var profesionalLugarTrabajo by remember { mutableStateOf("") }
    var profesionalEspecialidad by remember { mutableStateOf("") }

    // Rol con el que se abrió PulsiBot.
    var rolPulsiBotActual by remember {
        mutableStateOf("paciente")
    }

    var pacienteProfesionalSeleccionado by remember {
        mutableStateOf("")
    }

    // UID del paciente elegido en la pantalla de permisos del profesional
    var pacienteProfesionalUidSeleccionado by remember {
        mutableStateOf("")
    }

    // Datos temporales del registro de tutor
    var tutorNombre by remember { mutableStateOf("") }
    var tutorApellido by remember { mutableStateOf("") }
    var tutorCorreo by remember { mutableStateOf("") }
    var tutorTelefono by remember { mutableStateOf("") }
    var tutorFechaNacimiento by remember { mutableStateOf("") }
    var tutorCodigoVinculacion by remember { mutableStateOf("") }

    // Datos del perfil del tutor
    var tutorPerfilNombre by remember { mutableStateOf("") }
    var tutorPerfilApellido by remember { mutableStateOf("") }
    var tutorPerfilCorreo by remember { mutableStateOf("") }
    var tutorPerfilTelefono by remember { mutableStateOf("") }
    var tutorPerfilFechaNacimiento by remember { mutableStateOf("") }
    var tutorPerfilGenero by remember { mutableStateOf("") }
    var tutorPerfilPais by remember { mutableStateOf("") }

    var editNombre by remember { mutableStateOf("") }
    var editCorreo by remember { mutableStateOf("") }
    var editFecha by remember { mutableStateOf("") }
    var editPais by remember { mutableStateOf("") }
    var editTelefono by remember { mutableStateOf("") }
    var editDireccion by remember { mutableStateOf("") }
    var editGenero by remember { mutableStateOf("") }

    var medicalDiagnostico by remember { mutableStateOf("") }
    var medicalZona by remember { mutableStateOf("") }
    var medicalProfesional by remember { mutableStateOf("") }
    var medicalNotas by remember { mutableStateOf("") }

    var alertaMedicacion by remember { mutableStateOf(true) }
    var alertaEjercicioPendiente by remember { mutableStateOf(true) }
    var alertaRacha by remember { mutableStateOf(true) }
    var alertaNuevoCuidador by remember { mutableStateOf(true) }
    var codigoVinculacionPaciente by remember { mutableStateOf("") }
    var cantidadTutoresVinculados by remember { mutableStateOf(0) }

    // ---------------------------------------------------------
    // Cuentas viejas: si no aceptaron la versión vigente de los términos
    // y la política, se les pide al entrar (bloquea la app hasta aceptar).
    // ---------------------------------------------------------
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

        // =====================================================
        // SPLASH
        // =====================================================

        composable("splash") {

            val context = LocalContext.current

            SplashScreen(
                onContinue = {

                    val uid = auth.currentUser?.uid

                    if (uid == null) {
                        navController.navigate("onboarding") {
                            popUpTo("splash") {
                                inclusive = true
                            }
                        }
                    } else {
                        db.collection("usuarios")
                            .document(uid)
                            .get()
                            .addOnSuccessListener { document ->

                                val nombre =
                                    document.getString("nombre") ?: "Usuario"

                                val rol =
                                    document.getString("rol") ?: "paciente"

                                nombreUsuarioActual = nombre

                                if (rol != "paciente") {
                                    rolCuidadorActual = rol
                                }

                                val destino =
                                    if (rol == "paciente") {
                                        "home"
                                    } else {
                                        "caregiver_home"
                                    }

                                navController.navigate(destino) {
                                    popUpTo("splash") {
                                        inclusive = true
                                    }
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "No se pudieron cargar los datos de la sesión",
                                    Toast.LENGTH_SHORT
                                ).show()

                                navController.navigate("login") {
                                    popUpTo("splash") {
                                        inclusive = true
                                    }
                                }
                            }
                    }
                }
            )
        }


        // =====================================================
        // ONBOARDING
        // =====================================================

        composable("onboarding") {
            OnboardingScreen(
                onFinish = {
                    navController.navigate("welcome")
                }
            )
        }


        // =====================================================
        // WELCOME
        // =====================================================

        composable("welcome") {
            WelcomeScreen(
                onNavigateToRegister = {
                    navController.navigate("register")
                },

                onNavigateToLogin = {
                    navController.navigate("login")
                },

                onNavigateToCaregiver = {
                    navController.navigate("welcome_caregiver")
                }
            )
        }


        // =====================================================
        // BIENVENIDO NUEVAMENTE
        // =====================================================

        composable("welcome_back") {
            WelcomeBackScreen(
                nombre = nombreUsuarioActual,

                onBack = {
                    navController.popBackStack()
                },

                onIrAMiPanel = {
                    navController.navigate("home") {
                        popUpTo("splash") {
                            inclusive = true
                        }
                    }
                }
            )
        }


        // =====================================================
        // LOGIN PACIENTE
        // =====================================================

        composable("login") {

            val context = LocalContext.current

            // true mientras se espera a Firebase: bloquea el botón y evita el doble envío
            var cargando by remember { mutableStateOf(false) }

            LoginScreen(

                cargando = cargando,

                onBackClick = {
                    navController.popBackStack()
                },

                onIngresar = { email, password ->

                    if (cargando) {
                        // Ya se está verificando: ignoramos el segundo toque
                    } else if (email.isBlank() || password.isBlank()) {

                        cargando = false
                        Toast.makeText(
                            context,
                            "Completá el correo y la contraseña",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        cargando = true

                        auth.signInWithEmailAndPassword(
                            email.trim(),
                            password
                        )
                            .addOnSuccessListener {

                                val uid = auth.currentUser?.uid

                                if (uid != null) {

                                    db.collection("usuarios")
                                        .document(uid)
                                        .get()
                                        .addOnSuccessListener { document ->

                                            val nombre =
                                                document.getString("nombre")
                                                    ?: "Usuario"

                                            val rol =
                                                document.getString("rol")
                                                    ?: "paciente"

                                            nombreUsuarioActual = nombre

                                            if (rol == "paciente") {

                                                navController.navigate(
                                                    "welcome_back"
                                                ) {
                                                    popUpTo("login") {
                                                        inclusive = true
                                                    }
                                                }

                                            } else {

                                                navController.navigate(
                                                    "caregiver_home"
                                                ) {
                                                    popUpTo("login") {
                                                        inclusive = true
                                                    }
                                                }
                                            }
                                        }
                                        .addOnFailureListener { error ->

                                            auth.signOut()
                                            cargando = false
                                            Toast.makeText(
                                                context,
                                                ErroresFirebase.datos(error, "No se pudieron cargar tus datos. Intentá de nuevo."),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            }
                            .addOnFailureListener { error ->

                                cargando = false
                                Toast.makeText(
                                    context,
                                    ErroresFirebase.login(error),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                },

                onForgotPasswordClick = { email ->

                    if (email.isBlank()) {

                        cargando = false
                        Toast.makeText(
                            context,
                            "Ingresá tu correo electrónico primero",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        // Enviar el correo de recuperación en español
                        auth.setLanguageCode("es")

                        auth.sendPasswordResetEmail(email.trim())
                            .addOnSuccessListener {

                                cargando = false
                                Toast.makeText(
                                    context,
                                    "Te enviamos un correo para restablecer tu contraseña",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            .addOnFailureListener { error ->

                                cargando = false
                                Toast.makeText(
                                    context,
                                    ErroresFirebase.recuperacion(error),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                },

                onCrearCuenta = {
                    navController.navigate("register")
                },

                onCuidador = {
                    navController.navigate("tutor_login")
                }
            )
        }

        // =====================================================
        // REGISTRO PACIENTE
        // =====================================================

        composable("register") {
            RegisterScreen(
                onBack = {
                    navController.popBackStack()
                },

                onCreateAccount = { nombre ->
                    nombreUsuarioActual = nombre
                    navController.navigate("completa_tus_datos")
                },

                onGoToLogin = {
                    navController.navigate("login")
                }
            )
        }


        // =====================================================
        // COMPLETAR DATOS
        // =====================================================

        composable("completa_tus_datos") {

            val context = LocalContext.current

            ProfileScreen(
                nombre = nombreUsuarioActual,

                onBack = {
                    navController.popBackStack()
                },

                onSave = { fechaNacimiento, pais, genero, telefono, direccion ->

                    val uid = auth.currentUser?.uid

                    if (uid == null) {
                        Toast.makeText(
                            context,
                            "No se encontró la sesión del usuario",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val datosPerfil = mapOf(
                            "fechaNacimiento" to fechaNacimiento,
                            "pais" to pais,
                            "genero" to genero,
                            "telefono" to telefono,
                            "direccion" to direccion
                        )

                        db.collection("usuarios")
                            .document(uid)
                            .update(datosPerfil)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Datos guardados correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()

                                navController.navigate("about_you")
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "No se pudieron guardar los datos",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                },

                onLater = {
                    navController.navigate("about_you")
                }
            )
        }


        // =====================================================
        // SOBRE VOS
        // =====================================================

        composable("about_you") {

            val context = LocalContext.current

            AboutYouScreen(
                onBack = {
                    navController.popBackStack()
                },

                onContinue = { diagnosticos, mano ->

                    val uid = auth.currentUser?.uid

                    if (uid == null) {
                        Toast.makeText(
                            context,
                            "No se encontró la sesión del usuario",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val datosSobreVos = mapOf(
                            "diagnosticos" to diagnosticos,
                            "mano" to mano
                        )

                        db.collection("usuarios")
                            .document(uid)
                            .update(datosSobreVos)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Datos guardados correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()

                                navController.navigate("congrats") {
                                    popUpTo("about_you") {
                                        inclusive = true
                                    }
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "No se pudieron guardar los datos",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            )
        }


        // =====================================================
        // FELICITACIONES
        // =====================================================

        composable("congrats") {
            CongratsScreen(
                nombre = nombreUsuarioActual,

                onBack = {
                    navController.popBackStack()
                },

                onContinue = {
                    navController.navigate("home") {
                        popUpTo("splash") {
                            inclusive = true
                        }
                    }
                }
            )
        }


        // =====================================================
        // LOGIN TUTOR
        // =====================================================

        composable("tutor_login") {

            val context = LocalContext.current

            // true mientras se espera a Firebase: bloquea el botón y evita el doble envío
            var cargando by remember { mutableStateOf(false) }

            TutorLoginScreen(

                cargando = cargando,

                onBack = {
                    navController.popBackStack()
                },

                onIngresar = { email, password, codigo ->

                    if (cargando) {
                        // Ya se está verificando: ignoramos el segundo toque
                    } else if (email.isBlank() || password.isBlank() || codigo.isBlank()) {
                        cargando = false
                        Toast.makeText(
                            context,
                            "Completá el correo, la contraseña y el código de vinculación",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        cargando = true

                        auth.signInWithEmailAndPassword(
                            email.trim(),
                            password
                        )
                            .addOnSuccessListener {

                                val tutorUid = auth.currentUser?.uid

                                if (tutorUid == null) {
                                    cargando = false
                                    Toast.makeText(
                                        context,
                                        "No se encontró la sesión del tutor",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    db.collection("usuarios")
                                        .document(tutorUid)
                                        .get()
                                        .addOnSuccessListener { tutorDocument ->

                                            val rol =
                                                tutorDocument.getString("rol") ?: ""

                                            val nombre =
                                                tutorDocument.getString("nombre") ?: "Tutor"

                                            val pacienteUid =
                                                tutorDocument.getString(
                                                    "pacienteVinculadoUid"
                                                ).orEmpty()

                                            when {
                                                rol != "tutor" -> {
                                                    auth.signOut()
                                                    cargando = false
                                                    Toast.makeText(
                                                        context,
                                                        "Esta cuenta no corresponde a un tutor",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }

                                                pacienteUid.isBlank() -> {
                                                    auth.signOut()
                                                    cargando = false
                                                    Toast.makeText(
                                                        context,
                                                        "Esta cuenta todavía no está vinculada a un paciente",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }

                                                else -> {
                                                    db.collection("usuarios")
                                                        .document(pacienteUid)
                                                        .get()
                                                        .addOnSuccessListener { pacienteDocument ->

                                                            val codigoPaciente =
                                                                pacienteDocument.getString(
                                                                    "codigoVinculacion"
                                                                ).orEmpty()

                                                            if (
                                                                codigoPaciente !=
                                                                codigo.trim()
                                                            ) {
                                                                auth.signOut()
                                                                cargando = false
                                                                Toast.makeText(
                                                                    context,
                                                                    "El código no corresponde al paciente vinculado",
                                                                    Toast.LENGTH_LONG
                                                                ).show()
                                                            } else {
                                                                nombreUsuarioActual =
                                                                    nombre
                                                                rolCuidadorActual =
                                                                    "tutor"

                                                                cargando = false
                                                                Toast.makeText(
                                                                    context,
                                                                    "Sesión iniciada correctamente",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()

                                                                navController.navigate(
                                                                    "caregiver_home"
                                                                ) {
                                                                    popUpTo(
                                                                        "tutor_login"
                                                                    ) {
                                                                        inclusive = true
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        .addOnFailureListener { error ->
                                                            auth.signOut()
                                                            cargando = false
                                                            Toast.makeText(
                                                                context,
                                                                ErroresFirebase.datos(error, "No se pudo verificar el paciente vinculado"),
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                }
                                            }
                                        }
                                        .addOnFailureListener { error ->
                                            auth.signOut()
                                            cargando = false
                                            Toast.makeText(
                                                context,
                                                ErroresFirebase.datos(error, "No se pudieron cargar los datos del tutor"),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            }
                            .addOnFailureListener { error ->
                                cargando = false
                                Toast.makeText(
                                    context,
                                    ErroresFirebase.login(error),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                },

                onOlvidasteContrasena = { email ->

                    if (email.isBlank()) {
                        cargando = false
                        Toast.makeText(
                            context,
                            "Ingresá tu correo electrónico primero",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        auth.setLanguageCode("es")

                        auth.sendPasswordResetEmail(email.trim())
                            .addOnSuccessListener {
                                cargando = false
                                Toast.makeText(
                                    context,
                                    "Te enviamos un correo para restablecer tu contraseña",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            .addOnFailureListener { error ->
                                cargando = false
                                Toast.makeText(
                                    context,
                                    ErroresFirebase.recuperacion(error),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                },

                onCrearCuenta = {
                    navController.navigate("tutor_register")
                }
            )
        }


        // =====================================================
        // CREAR CUENTA TUTOR - DATOS
        // =====================================================

        composable("tutor_register") {
            TutorRegisterScreen(

                onBack = {
                    navController.popBackStack()
                },

                onCrearCuenta = {
                        nombre,
                        apellido,
                        correo,
                        telefono,
                        fechaNacimiento,
                        codigoVinculacion ->

                    tutorNombre = nombre.trim()
                    tutorApellido = apellido.trim()
                    tutorCorreo = correo.trim()
                    tutorTelefono = telefono.trim()
                    tutorFechaNacimiento = fechaNacimiento.trim()
                    tutorCodigoVinculacion = codigoVinculacion.trim()

                    navController.navigate("tutor_create_password")
                }
            )
        }


        // =====================================================
        // CREAR CONTRASEÑA TUTOR
        // =====================================================

        composable("tutor_create_password") {

            val context = LocalContext.current

            // true mientras se espera a Firebase: bloquea el botón y evita el doble envío
            var cargando by remember { mutableStateOf(false) }

            TutorCreatePasswordScreen(

                cargando = cargando,

                onBack = {
                    navController.popBackStack()
                },

                onCrearCuenta = { contrasenia ->

                    if (cargando) {
                        // Ya se está creando la cuenta: ignoramos el segundo toque
                    } else if (tutorNombre.isBlank() ||
                        tutorApellido.isBlank() ||
                        tutorCorreo.isBlank() ||
                        tutorCodigoVinculacion.isBlank() ||
                        contrasenia.isBlank()
                    ) {
                        cargando = false
                        Toast.makeText(
                            context,
                            "Completá los datos obligatorios",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {

                        cargando = true

                        // Buscamos al PACIENTE dueño del código en la colección "codigos".
                        db.collection("codigos")
                            .document(tutorCodigoVinculacion.trim())
                            .get()
                            .addOnSuccessListener { codigoDoc ->

                                if (!codigoDoc.exists()) {
                                    cargando = false
                                    Toast.makeText(
                                        context,
                                        "El código de vinculación no pertenece a ningún paciente",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {

                                    val pacienteUid =
                                        codigoDoc.getString("uid").orEmpty()

                                    auth.createUserWithEmailAndPassword(
                                        tutorCorreo,
                                        contrasenia
                                    )
                                        .addOnSuccessListener { resultado ->

                                            val tutorUid = resultado.user?.uid

                                            if (tutorUid == null) {
                                                cargando = false
                                                Toast.makeText(
                                                    context,
                                                    "No se pudo crear la cuenta",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {

                                                val datosTutor = hashMapOf<String, Any>(
                                                    "nombre" to tutorNombre,
                                                    "apellido" to tutorApellido,
                                                    "correo" to tutorCorreo,
                                                    "telefono" to tutorTelefono,
                                                    "fechaNacimiento" to tutorFechaNacimiento,
                                                    "codigoVinculacion" to tutorCodigoVinculacion.trim(),
                                                    "pacienteVinculadoUid" to pacienteUid,
                                                    "rol" to "tutor",
                                                    // Consentimiento (Ley 25.326): la pantalla de registro
                                                    // no deja continuar sin aceptar ambas casillas.
                                                    "consentimientoTerminos" to true,
                                                    "consentimientoConfidencialidad" to true,
                                                    "consentimientoVersion" to
                                                            com.example.pulsofirme.TextosLegales.VERSION,
                                                    "consentimientoFecha" to
                                                            com.google.firebase.firestore.FieldValue.serverTimestamp(),
                                                    "fechaVinculacion" to
                                                            com.google.firebase.firestore.FieldValue.serverTimestamp()
                                                )

                                                val tutorRef =
                                                    db.collection("usuarios")
                                                        .document(tutorUid)

                                                val pacienteRef =
                                                    db.collection("usuarios")
                                                        .document(pacienteUid)

                                                // El límite de tutores (1 gratis / 3 Premium) lo valida
                                                // Firestore (reglas): un tutor nuevo todavía no puede leer
                                                // al paciente, así que no se puede chequear desde acá.
                                                val batch = db.batch()

                                                batch.set(tutorRef, datosTutor)

                                                batch.update(
                                                    pacienteRef,
                                                    "tutoresVinculados",
                                                    com.google.firebase.firestore.FieldValue.arrayUnion(
                                                        tutorUid
                                                    )
                                                )

                                                batch.commit()
                                                    .addOnSuccessListener {

                                                        nombreUsuarioActual =
                                                            tutorNombre
                                                        rolCuidadorActual =
                                                            "tutor"

                                                        cargando = false
                                                        Toast.makeText(
                                                            context,
                                                            "Tutor vinculado correctamente con el paciente",
                                                            Toast.LENGTH_SHORT
                                                        ).show()

                                                        navController.navigate(
                                                            "caregiver_home"
                                                        ) {
                                                            popUpTo(
                                                                "welcome_caregiver"
                                                            ) {
                                                                inclusive = true
                                                            }
                                                        }
                                                    }
                                                    .addOnFailureListener { error ->

                                                        // Evita dejar una cuenta de Authentication
                                                        // sin sus datos/vínculo en Firestore.
                                                        resultado.user?.delete()

                                                        val denegado =
                                                            (error as? com.google.firebase.firestore
                                                            .FirebaseFirestoreException)?.code ==
                                                                    com.google.firebase.firestore
                                                                        .FirebaseFirestoreException.Code
                                                                        .PERMISSION_DENIED

                                                        cargando = false
                                                        Toast.makeText(
                                                            context,
                                                            if (denegado) "No se pudo vincular: el paciente ya alcanzó " +
                                                                    "el máximo de tutores (${LimiteTutores.MAX_GRATIS} en el plan gratis, " +
                                                                    "${LimiteTutores.MAX_PREMIUM} con Premium) o el código no es válido."
                                                            else ErroresFirebase.datos(error, "No se pudo completar la vinculación"),
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                            }
                                        }
                                        .addOnFailureListener { error ->
                                            cargando = false
                                            Toast.makeText(
                                                context,
                                                ErroresFirebase.registro(error),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                }
                            }
                            .addOnFailureListener { error ->
                                cargando = false
                                Toast.makeText(
                                    context,
                                    ErroresFirebase.datos(error, "No se pudo verificar el código del paciente"),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                }
            )
        }


        // =====================================================
        // WELCOME CUIDADOR
        // =====================================================

        composable("welcome_caregiver") {
            WelcomeCaregiverScreen(

                onBack = {
                    navController.popBackStack()
                },

                onComenzar = {
                    navController.navigate("caregiver_role")
                }
            )
        }


        // =====================================================
        // ELEGIR ROL CUIDADOR
        // =====================================================

        composable("caregiver_role") {
            CaregiverRoleScreen(

                onBack = {
                    navController.popBackStack()
                },

                onNext = { rol ->

                    rolCuidadorActual = rol

                    navController.navigate("caregiver_step2")
                }
            )
        }


        // =====================================================
        // LOGIN CUIDADOR
        // =====================================================

        composable("caregiver_login") {
            val context = LocalContext.current

            // true mientras se espera a Firebase: bloquea el botón y evita el doble envío
            var cargando by remember { mutableStateOf(false) }

            CaregiverLoginScreen(

                cargando = cargando,
                onBack = {
                    navController.popBackStack()
                },

                onIniciarSesion = { correo, contrasenia ->
                    if (cargando) return@CaregiverLoginScreen
                    cargando = true

                    auth.signInWithEmailAndPassword(correo.trim(), contrasenia)
                        .addOnSuccessListener { resultado ->
                            val uid = resultado.user?.uid

                            if (uid == null) {
                                auth.signOut()
                                cargando = false
                                Toast.makeText(
                                    context,
                                    "No se pudo obtener la sesión del profesional",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                db.collection("usuarios")
                                    .document(uid)
                                    .get()
                                    .addOnSuccessListener { document ->
                                        val rol = document.getString("rol") ?: ""

                                        if (rol != "profesional") {
                                            auth.signOut()
                                            cargando = false
                                            Toast.makeText(
                                                context,
                                                "Esta cuenta no corresponde a un profesional",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            nombreUsuarioActual =
                                                document.getString("nombre") ?: "Profesional"
                                            rolCuidadorActual = "profesional"
                                            profesionalMatricula =
                                                document.getString("matricula") ?: ""
                                            profesionalLugarTrabajo =
                                                document.getString("lugarTrabajo") ?: ""
                                            profesionalEspecialidad =
                                                document.getString("especialidad")
                                                    ?: document.getString("profesion")
                                                            ?: ""

                                            navController.navigate("caregiver_home") {
                                                popUpTo("welcome_caregiver") {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    }
                                    .addOnFailureListener { error ->
                                        auth.signOut()
                                        cargando = false
                                        Toast.makeText(
                                            context,
                                            ErroresFirebase.datos(error, "No se pudieron verificar los datos del profesional"),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            }
                        }
                        .addOnFailureListener { error ->
                            cargando = false
                            Toast.makeText(
                                context,
                                ErroresFirebase.login(error),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                },

                onForgotPasswordClick = { correo ->
                    auth.sendPasswordResetEmail(correo.trim())
                        .addOnSuccessListener {
                            cargando = false
                            Toast.makeText(
                                context,
                                "Te enviamos un correo para restablecer tu contraseña",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        .addOnFailureListener { error ->
                            cargando = false
                            Toast.makeText(
                                context,
                                ErroresFirebase.recuperacion(error),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                },

                onCrearCuenta = {
                    navController.navigate("caregiver_register")
                }
            )
        }


        // =====================================================
        // REGISTRO CUIDADOR
        // =====================================================

        composable("caregiver_register") {
            val context = LocalContext.current

            // true mientras se espera a Firebase: bloquea el botón y evita el doble envío
            var cargando by remember { mutableStateOf(false) }

            CaregiverRegisterScreen(

                cargando = cargando,
                onBack = {
                    navController.popBackStack()
                },

                onCreateAccount = { nombre, apellido, correo, contrasenia, profesion, matricula ->
                    if (cargando) return@CaregiverRegisterScreen
                    cargando = true

                    auth.createUserWithEmailAndPassword(correo.trim(), contrasenia)
                        .addOnSuccessListener { resultado ->
                            val uid = resultado.user?.uid

                            if (uid == null) {
                                cargando = false
                                Toast.makeText(
                                    context,
                                    "No se pudo crear la cuenta profesional",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                val datosProfesional = hashMapOf<String, Any>(
                                    "nombre" to nombre.trim(),
                                    "apellido" to apellido.trim(),
                                    "correo" to correo.trim(),
                                    "profesion" to profesion.trim(),
                                    "matricula" to matricula.trim(),
                                    "rol" to "profesional",
                                    // Consentimiento (Ley 25.326): la pantalla de registro
                                    // no deja crear la cuenta sin aceptar ambas casillas.
                                    "consentimientoTerminos" to true,
                                    "consentimientoConfidencialidad" to true,
                                    "consentimientoVersion" to com.example.pulsofirme.TextosLegales.VERSION,
                                    "consentimientoFecha" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
                                    "lugarTrabajo" to "",
                                    "especialidad" to profesion.trim(),
                                    "formacionAdicional" to "",
                                    "pacientesVinculados" to emptyList<String>(),
                                    "fechaRegistro" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                                )

                                db.collection("usuarios")
                                    .document(uid)
                                    .set(datosProfesional)
                                    .addOnSuccessListener {
                                        nombreUsuarioActual = nombre.trim()
                                        rolCuidadorActual = "profesional"
                                        profesionalMatricula = matricula.trim()
                                        profesionalLugarTrabajo = ""
                                        profesionalEspecialidad = profesion.trim()

                                        cargando = false
                                        Toast.makeText(
                                            context,
                                            "Cuenta profesional creada correctamente",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        navController.navigate("caregiver_home") {
                                            popUpTo("welcome_caregiver") {
                                                inclusive = true
                                            }
                                        }
                                    }
                                    .addOnFailureListener { error ->
                                        resultado.user?.delete()
                                        cargando = false
                                        Toast.makeText(
                                            context,
                                            ErroresFirebase.datos(error, "No se pudieron guardar los datos del profesional"),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            }
                        }
                        .addOnFailureListener { error ->
                            cargando = false
                            Toast.makeText(
                                context,
                                ErroresFirebase.registro(error),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                },

                onGoToLogin = {
                    navController.navigate("caregiver_login") {
                        popUpTo("caregiver_register") {
                            inclusive = true
                        }
                    }
                }
            )
        }


        // =====================================================
        // PASO 2 CUIDADOR
        // =====================================================

        composable("caregiver_step2") {
            CaregiverStep2Screen(

                onBack = {
                    navController.popBackStack()
                },

                onComenzar = {
                    navController.navigate("caregiver_step3")
                }
            )
        }


        // =====================================================
        // PASO 3 CUIDADOR
        // =====================================================

        composable("caregiver_step3") {
            CaregiverStep3Screen(

                onBack = {
                    navController.popBackStack()
                },

                onComenzar = {
                    navController.navigate("caregiver_step4")
                }
            )
        }


        // =====================================================
        // PASO 4 CUIDADOR
        // =====================================================

        composable("caregiver_step4") {
            CaregiverStep4Screen(

                onBack = {
                    navController.popBackStack()
                },

                onComenzar = {

                    if (rolCuidadorActual == "profesional") {

                        navController.navigate("caregiver_login")

                    } else {

                        navController.navigate("tutor_login")
                    }
                }
            )
        }


        // =====================================================
        // HOME PACIENTE
        // =====================================================

        composable("home") {

            val context = LocalContext.current

            // Sincroniza el recordatorio diario de ejercicio con la preferencia
            // guardada en Firestore (por defecto está activado).
            LaunchedEffect(Unit) {
                val uid = auth.currentUser?.uid

                if (uid != null) {
                    db.collection("usuarios")
                        .document(uid)
                        .get()
                        .addOnSuccessListener { document ->
                            val activo =
                                document.getBoolean("alertaEjercicioPendiente") ?: true

                            RecordatorioEjercicio.setAvisarRacha(
                                context,
                                document.getBoolean("alertaRacha") ?: true
                            )

                            if (activo) {
                                RecordatorioEjercicio.programar(context)
                            } else {
                                RecordatorioEjercicio.cancelar(context)
                            }
                        }
                }
            }

            HomeScreen(

                nombreUsuario = nombreUsuarioActual,

                rutaActual = "home",

                onNavigate = { ruta ->
                    if (ruta == "pulsibot") {
                        rolPulsiBotActual = "paciente"
                    }
                    navController.navigate(ruta)
                },

                onEjercicioClick = {
                    navController.navigate("exercises")
                },

                // Ejercicio recomendado del Inicio: se abre directo
                onEmpezarEjercicio = { nombre ->
                    nombreEjercicioActual = nombre
                    val premium = EjerciciosPremium.porNombre(nombre)
                    zonaEjercicioActual = premium?.zona.orEmpty()
                    tipoEjercicioActual = premium?.tipo.orEmpty()

                    if (premium != null && EjerciciosPremium.bloqueado(premium)) {
                        navController.navigate("premium")
                    } else {
                        navController.navigate("exercise_in_progress")
                    }
                },

                onSensacionClick = { sensacion ->

                    sensacionInicial = sensacion

                    navController.navigate("survey")
                }
            )
        }


        // =====================================================
        // PULSIBOT
        // =====================================================

        composable("pulsibot") {
            PulsiBotScreen(
                nombreUsuario = nombreUsuarioActual,
                rolUsuario = rolPulsiBotActual,

                onBack = {
                    navController.popBackStack()
                },

                onEmpezarEjercicio = {
                    if (rolPulsiBotActual == "paciente") {
                        navController.navigate("exercises")
                    }
                },

                onVerProgreso = {
                    if (rolPulsiBotActual == "paciente") {
                        navController.navigate("progress")
                    } else {
                        navController.navigate("caregiver_progress")
                    }
                },

                onComoConectarGuante = {
                    if (rolPulsiBotActual == "paciente") {
                        navController.navigate("device")
                    }
                }
            )
        }


        // =====================================================
        // EJERCICIOS
        // =====================================================

        composable("exercises") {
            ExercisesScreen(

                rutaActual = "exercises",

                onNavigate = { ruta ->
                    navController.navigate(ruta)
                },

                onEjercicioClick = { nombre ->

                    nombreEjercicioActual = nombre

                    // Los Premium traen su zona y tipo; los gratis las limpian
                    val premium = EjerciciosPremium.porNombre(nombre)
                    zonaEjercicioActual = premium?.zona.orEmpty()
                    tipoEjercicioActual = premium?.tipo.orEmpty()

                    if (premium != null && EjerciciosPremium.bloqueado(premium)) {
                        navController.navigate("premium")
                    } else {
                        navController.navigate("exercise_in_progress")
                    }
                },

                onPremiumClick = {
                    navController.navigate("premium")
                }
            )
        }


        // =====================================================
        // PROGRESO
        // =====================================================

        composable("progress") {
            ProgressScreen(

                rutaActual = "progress",

                onNavigate = { ruta ->
                    navController.navigate(ruta)
                },

                onQuickDashClick = {
                    navController.navigate("quick_dash")
                }
            )
        }


        // =====================================================
        // ALERTAS
        // =====================================================

        composable("alerts") {
            AlertsScreen(

                rutaActual = "alerts",

                onNavigate = { ruta ->
                    navController.navigate(ruta)
                },

                onConfigClick = {
                    navController.navigate("alerts_config")
                },

                guanteConectado = false
            )
        }


        // =====================================================
        // CONFIGURACIÓN DE ALERTAS PACIENTE
        // =====================================================

        composable("alerts_config") {

            val context = LocalContext.current
            val uid = auth.currentUser?.uid

            LaunchedEffect(uid) {
                if (uid != null) {
                    db.collection("usuarios")
                        .document(uid)
                        .get()
                        .addOnSuccessListener { document ->
                            alertaMedicacion =
                                document.getBoolean("alertaMedicacion") ?: true
                            alertaEjercicioPendiente =
                                document.getBoolean("alertaEjercicioPendiente") ?: true
                            alertaRacha =
                                document.getBoolean("alertaRacha") ?: true
                            alertaNuevoCuidador =
                                document.getBoolean("alertaNuevoCuidador") ?: true
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "No se pudieron cargar las preferencias de alertas",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }

            AlertsConfigScreen(
                onBack = {
                    navController.popBackStack()
                },

                medicacionInicial = alertaMedicacion,
                ejercicioPendienteInicial = alertaEjercicioPendiente,
                rachaInicial = alertaRacha,
                nuevoCuidadorInicial = alertaNuevoCuidador,

                onGuardarCambios = {
                        medicacion,
                        ejercicioPendiente,
                        racha,
                        nuevoCuidador ->

                    if (uid == null) {
                        Toast.makeText(
                            context,
                            "No se encontró la sesión del usuario",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val preferenciasAlertas = mapOf(
                            "alertaMedicacion" to medicacion,
                            "alertaEjercicioPendiente" to ejercicioPendiente,
                            "alertaRacha" to racha,
                            "alertaNuevoCuidador" to nuevoCuidador
                        )

                        db.collection("usuarios")
                            .document(uid)
                            .update(preferenciasAlertas)
                            .addOnSuccessListener {
                                alertaMedicacion = medicacion
                                alertaEjercicioPendiente = ejercicioPendiente
                                alertaRacha = racha
                                alertaNuevoCuidador = nuevoCuidador

                                // Aviso de racha (usa el mismo recordatorio diario)
                                RecordatorioEjercicio.setAvisarRacha(context, racha)

                                // Recordatorio diario de ejercicio pendiente
                                if (ejercicioPendiente) {
                                    RecordatorioEjercicio.programar(context)
                                } else {
                                    RecordatorioEjercicio.cancelar(context)
                                }

                                Toast.makeText(
                                    context,
                                    "Preferencias de alertas guardadas",
                                    Toast.LENGTH_SHORT
                                ).show()

                                navController.popBackStack()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "No se pudieron guardar las preferencias",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            )
        }


        // =====================================================
        // CUENTA PACIENTE
        // =====================================================

        composable("account") {

            val context = LocalContext.current
            val uid = auth.currentUser?.uid

            LaunchedEffect(uid) {
                if (uid != null) {
                    val pacienteRef = db.collection("usuarios").document(uid)

                    pacienteRef.get()
                        .addOnSuccessListener { document ->
                            val codigoExistente =
                                document.getString("codigoVinculacion").orEmpty()

                            val tutores =
                                (document.get("tutoresVinculados") as? List<*>)?.size ?: 0

                            cantidadTutoresVinculados = tutores

                            if (codigoExistente.isNotBlank()) {
                                codigoVinculacionPaciente = codigoExistente

                                // Cuentas creadas antes de este cambio: registra el código
                                db.collection("codigos")
                                    .document(codigoExistente)
                                    .set(mapOf("uid" to uid))
                            } else {
                                fun intentarGenerarCodigo(intentosRestantes: Int) {
                                    if (intentosRestantes <= 0) {
                                        Toast.makeText(
                                            context,
                                            "No se pudo generar el código de vinculación",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return
                                    }

                                    val nuevoCodigo =
                                        (100000..999999).random().toString()

                                    val codigoRef =
                                        db.collection("codigos").document(nuevoCodigo)

                                    codigoRef.get()
                                        .addOnSuccessListener { coincidencia ->
                                            if (coincidencia.exists()) {
                                                intentarGenerarCodigo(intentosRestantes - 1)
                                            } else {
                                                codigoRef.set(mapOf("uid" to uid))
                                                    .addOnSuccessListener {
                                                        pacienteRef.update(
                                                            mapOf(
                                                                "codigoVinculacion" to nuevoCodigo,
                                                                "tutoresVinculados" to emptyList<String>()
                                                            )
                                                        )
                                                            .addOnSuccessListener {
                                                                codigoVinculacionPaciente = nuevoCodigo
                                                                cantidadTutoresVinculados = 0
                                                            }
                                                            .addOnFailureListener {
                                                                Toast.makeText(
                                                                    context,
                                                                    "No se pudo guardar el código de vinculación",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                    }
                                                    .addOnFailureListener {
                                                        Toast.makeText(
                                                            context,
                                                            "No se pudo reservar el código",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                            }
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                context,
                                                "No se pudo verificar el código de vinculación",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }

                                intentarGenerarCodigo(10)
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "No se pudieron cargar los datos de la cuenta",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }

            AccountScreen(

                rutaActual = "account",
                codigoVinculacion = codigoVinculacionPaciente,
                cantidadTutoresVinculados = cantidadTutoresVinculados,

                onNavigate = { ruta ->
                    navController.navigate(ruta)
                },

                onEditarDatos = {
                    navController.navigate("edit_profile")
                },

                onDiagnostico = {
                    navController.navigate("medical_data")
                },

                onPrivacidad = {
                    navController.navigate("privacy")
                },

                onAyuda = {
                    navController.navigate("support")
                },

                onAjustes = {
                    navController.navigate("ajustes")
                },

                onDudas = {
                    navController.navigate("dudas_frecuentes")
                },

                onPremium = {
                    navController.navigate("premium")
                },

                onCerrarSesion = {
                    // Sin sesión no tiene sentido recordar ejercicios
                    RecordatorioEjercicio.cancelar(context)

                    // El nivel y el on/off de hoy son de la persona, no del celular
                    DificultadAdaptativa.reiniciar(context)

                    auth.signOut()
                    nombreUsuarioActual = "Usuario"
                    codigoVinculacionPaciente = ""
                    cantidadTutoresVinculados = 0

                    navController.navigate("login") {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }


        // =====================================================
        // EJERCICIO EN PROGRESO
        // =====================================================

        composable("exercise_in_progress") {

            val context = LocalContext.current

            ExerciseInProgressScreen(

                nombreEjercicio = nombreEjercicioActual,

                zona = zonaEjercicioActual,

                tipo = tipoEjercicioActual,

                onBack = {
                    navController.popBackStack()
                },

                onInfoClick = {
                    // TODO
                },

                onFinish = { repeticiones, segundos, objetivo ->

                    repeticionesCompletadas = repeticiones
                    totalRepeticiones = objetivo
                    tiempoTotalSegundos = segundos

                    // Ya hizo ejercicio hoy: el recordatorio de las 18:00 no avisa
                    RecordatorioEjercicio.marcarHechoHoy(context)

                    // Guardamos la actividad real del paciente para que el profesional
                    // pueda verla en Home, progreso y notificaciones.
                    val uidPaciente = auth.currentUser?.uid
                    if (uidPaciente != null) {
                        val actividad = hashMapOf<String, Any>(
                            "ejercicio" to nombreEjercicioActual,
                            "zona" to zonaEjercicioActual,
                            "tipo" to tipoEjercicioActual,
                            "repeticiones" to repeticiones.toLong(),
                            "segundos" to segundos.toLong(),
                            "fecha" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                        )
                        db.collection("usuarios")
                            .document(uidPaciente)
                            .collection("ejerciciosRealizados")
                            .add(actividad)
                    }

                    navController.navigate("exercise_done") {
                        popUpTo("exercise_in_progress") {
                            inclusive = true
                        }
                    }
                }
            )
        }


        // =====================================================
        // EJERCICIO TERMINADO
        // =====================================================

        composable("exercise_done") {
            ExerciseDoneScreen(

                nombre = nombreUsuarioActual,

                ejercicio = nombreEjercicioActual,

                repeticiones =
                    "$repeticionesCompletadas/$totalRepeticiones",

                duracion =
                    if (tiempoTotalSegundos < 60) "$tiempoTotalSegundos s"
                    else "${tiempoTotalSegundos / 60} min",

                onRepeat = {
                    navController.popBackStack(
                        "exercises",
                        inclusive = false
                    )
                },

                onHome = {
                    navController.navigate("home") {
                        popUpTo("home") {
                            inclusive = true
                        }
                    }
                }
            )
        }


        // =====================================================
        // RESUMEN EJERCICIO
        // =====================================================

        composable("exercise_summary") {
            ExerciseSummaryScreen(

                nombreEjercicio = nombreEjercicioActual,

                repeticionesCompletadas =
                    repeticionesCompletadas,

                totalRepeticiones =
                    totalRepeticiones,

                tiempoTotalSegundos =
                    tiempoTotalSegundos,

                onVolverInicio = {
                    navController.navigate("home") {
                        popUpTo("home") {
                            inclusive = true
                        }
                    }
                },

                onVerOtrosEjercicios = {
                    navController.navigate("exercises") {
                        popUpTo("home") {
                            inclusive = false
                        }
                    }
                }
            )
        }


        // =====================================================
        // ENCUESTA
        // =====================================================

        composable("survey") {
            SurveyScreen(

                sensacionInicial = sensacionInicial,

                onBack = {
                    navController.popBackStack()
                },

                onEmpezarEjercicio = { nombre ->

                    nombreEjercicioActual = nombre
                    zonaEjercicioActual = ""
                    tipoEjercicioActual = ""

                    navController.navigate(
                        "exercise_in_progress"
                    )
                },

                onSaltear = {
                    navController.navigate("exercises")
                }
            )
        }


        // =====================================================
        // QUICK DASH
        // =====================================================

        composable("quick_dash") {

            val context = LocalContext.current
            val uid = auth.currentUser?.uid

            QuickDashScreen(
                onBack = {
                    navController.popBackStack()
                },

                onFinish = {
                    navController.popBackStack()
                },

                onResultadoGuardado = { puntaje, respuestas ->

                    if (uid == null) {
                        Toast.makeText(
                            context,
                            "No se encontró la sesión del usuario",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val evaluacion = hashMapOf(
                            "puntaje" to puntaje.toDouble(),
                            "respuestas" to respuestas,
                            "fecha" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                        )

                        db.collection("usuarios")
                            .document(uid)
                            .collection("quickdash")
                            .add(evaluacion)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Evaluación QuickDASH guardada",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "No se pudo guardar la evaluación",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            )
        }


        // =====================================================
        // EDITAR PERFIL
        // =====================================================

        composable("edit_profile") {

            val context = LocalContext.current
            val uid = auth.currentUser?.uid

            LaunchedEffect(uid) {
                if (uid != null) {
                    db.collection("usuarios")
                        .document(uid)
                        .get()
                        .addOnSuccessListener { document ->
                            editNombre = document.getString("nombre") ?: ""
                            editCorreo = document.getString("correo") ?: auth.currentUser?.email.orEmpty()
                            editFecha = document.getString("fechaNacimiento") ?: ""
                            editPais = document.getString("pais") ?: ""
                            editTelefono = document.getString("telefono") ?: ""
                            editDireccion = document.getString("direccion") ?: ""
                            editGenero = document.getString("genero") ?: ""
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "No se pudieron cargar tus datos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }

            EditProfileScreen(
                onBack = {
                    navController.popBackStack()
                },
                nombreInicial = editNombre,
                correoInicial = editCorreo,
                fechaInicial = editFecha,
                paisInicial = editPais,
                telefonoInicial = editTelefono,
                direccionInicial = editDireccion,
                generoInicial = editGenero,
                onGuardarCambios = { nombre, correo, fecha, pais, telefono, direccion, genero ->

                    if (uid == null) {
                        Toast.makeText(
                            context,
                            "No se encontró la sesión del usuario",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val datosActualizados = mapOf(
                            "nombre" to nombre,
                            "correo" to correo,
                            "fechaNacimiento" to fecha,
                            "pais" to pais,
                            "telefono" to telefono,
                            "direccion" to direccion,
                            "genero" to genero
                        )

                        db.collection("usuarios")
                            .document(uid)
                            .update(datosActualizados)
                            .addOnSuccessListener {
                                nombreUsuarioActual = nombre

                                Toast.makeText(
                                    context,
                                    "Datos actualizados correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "No se pudieron guardar los cambios",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            )
        }


        // =====================================================
        // DATOS MÉDICOS
        // =====================================================

        composable("medical_data") {

            val context = LocalContext.current
            val uid = auth.currentUser?.uid

            LaunchedEffect(uid) {
                if (uid != null) {
                    db.collection("usuarios")
                        .document(uid)
                        .get()
                        .addOnSuccessListener { document ->

                            val diagnosticos = document.get("diagnosticos") as? List<*>
                            val diagnosticoGuardado = document.getString("diagnosticoPrincipal")

                            medicalDiagnostico = diagnosticoGuardado ?: when {
                                diagnosticos != null && diagnosticos.size > 1 ->
                                    "Parkinson y artritis"

                                diagnosticos != null &&
                                        diagnosticos.size == 1 &&
                                        diagnosticos.firstOrNull()?.toString()
                                            ?.equals("Parkinson", ignoreCase = true) == true ->
                                    "Enfermedad de Parkinson"

                                diagnosticos != null && diagnosticos.size == 1 ->
                                    diagnosticos.firstOrNull()?.toString().orEmpty()

                                else -> ""
                            }

                            medicalZona =
                                document.getString("zonaAfectada")
                                    ?: document.getString("mano")
                                        ?.takeIf { it.isNotBlank() }
                                        ?.let { "Mano ${it.lowercase()}" }
                                            ?: ""

                            medicalProfesional =
                                document.getString("profesionalACargo") ?: ""

                            medicalNotas =
                                document.getString("notasCuidador") ?: ""
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "No se pudieron cargar los datos médicos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }

            MedicalDataScreen(
                onBack = {
                    navController.popBackStack()
                },
                diagnosticoInicialFirebase = medicalDiagnostico,
                zonaInicialFirebase = medicalZona,
                profesionalInicialFirebase = medicalProfesional,
                notasInicialFirebase = medicalNotas,

                onGuardarCambios = { diagnostico, zona, profesional, notas ->

                    if (uid == null) {
                        Toast.makeText(
                            context,
                            "No se encontró la sesión del usuario",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {

                        val datosMedicos = mapOf(
                            "diagnosticoPrincipal" to diagnostico,
                            "zonaAfectada" to zona,
                            "profesionalACargo" to profesional,
                            "notasCuidador" to notas
                        )

                        db.collection("usuarios")
                            .document(uid)
                            .update(datosMedicos)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Datos médicos actualizados correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "No se pudieron guardar los datos médicos",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            )
        }


        // =====================================================
        // MEDICACION
        // =====================================================

        composable("medication") {
            MedicationScreen(
                onBack = {
                    navController.popBackStack()
                },
                onSave = { _, _, _ ->
                    navController.popBackStack()
                },
                onDelete = {
                    navController.popBackStack()
                }
            )
        }


        // =====================================================
        // DISPOSITIVO
        // =====================================================

        composable("device") {
            DeviceScreen(

                rutaActual = "device",

                onNavigate = { ruta ->
                    navController.navigate(ruta)
                }
            )
        }


        // =====================================================
        // NOTIFICACIONES
        // =====================================================

        composable("notifications") {
            NotificationsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }


        // =====================================================
        // PRIVACIDAD
        // =====================================================

        composable("privacy") {

            val context = LocalContext.current

            PrivacyScreen(

                onBack = {
                    navController.popBackStack()
                },

                onDatosEliminados = {
                    // La cuenta ya no existe: afuera recordatorios y datos locales
                    RecordatorioEjercicio.cancelar(context)
                    DificultadAdaptativa.reiniciar(context)
                    nombreUsuarioActual = "Usuario"
                    codigoVinculacionPaciente = ""
                    cantidadTutoresVinculados = 0

                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }


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
        // SOPORTE
        // =====================================================

        composable("support") {
            SupportScreen(

                onBack = {
                    navController.popBackStack()
                },

                onDudasFrecuentes = {
                    navController.navigate(
                        "dudas_frecuentes"
                    )
                }
            )
        }


        // =====================================================
        // DUDAS FRECUENTES
        // =====================================================

        composable("dudas_frecuentes") {
            DudasFrecuentesScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }


        // =====================================================
        // AJUSTES
        // =====================================================

        composable("ajustes") {

            val context = LocalContext.current
            val scopeAjustes = rememberCoroutineScope()
            var pedirClaveBorrado by remember { mutableStateOf(false) }

            // Antes solo volvía al login y la cuenta seguía existiendo.
            // Ahora pide la contraseña y borra la cuenta y los datos de verdad.
            if (pedirClaveBorrado) {
                BorrarCuentaConClave(
                    onCancelar = { pedirClaveBorrado = false },
                    onBorrada = {
                        pedirClaveBorrado = false
                        // También se borra lo guardado en este celular
                        com.example.pulsofirme.UserData.borrarTodo()
                        com.example.pulsofirme.alarmas.AlarmaScheduler.eliminarTodo(context)
                        context.getSharedPreferences("tomas_medicacion", android.content.Context.MODE_PRIVATE)
                            .edit().clear().apply()
                        RecordatorioEjercicio.cancelar(context)
                        DificultadAdaptativa.reiniciar(context)

                        navController.navigate("login") {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            AjustesScreen(

                onBack = {
                    navController.popBackStack()
                },

                // El usuario confirmó: ahora se pide la contraseña
                onCuentaEliminada = {
                    pedirClaveBorrado = true
                },

                onExportarDatos = {
                    scopeAjustes.launch {
                        val archivo = exportarMisDatos(context)
                        Toast.makeText(
                            context,
                            if (archivo != null) "Guardado en Descargas: $archivo"
                            else "No se pudieron exportar tus datos",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }


        // =====================================================
        // PREMIUM
        // =====================================================

        composable("premium") {
            PremiumScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }


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
        // DATOS PROFESIONALES
        // =====================================================

        composable("professional_data") {

            val context = LocalContext.current
            val uid = auth.currentUser?.uid

            var lugarTrabajo by remember { mutableStateOf("") }
            var especialidad by remember { mutableStateOf("") }
            var matricula by remember { mutableStateOf("") }
            var formacionAdicional by remember { mutableStateOf("") }

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
                            Toast.makeText(
                                context,
                                "No se pudieron cargar los datos del tutor",
                                Toast.LENGTH_SHORT
                            ).show()
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
                        Toast.makeText(
                            context,
                            "No se encontró la sesión del tutor",
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
                                    "Datos del tutor actualizados correctamente",
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
                        Toast.makeText(
                            context,
                            "No se encontró la sesión del tutor",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (codigo.length != 6 || !codigo.all { it.isDigit() }) {
                        Toast.makeText(
                            context,
                            "Ingresá el código de 6 dígitos del paciente",
                            Toast.LENGTH_SHORT
                        ).show()
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
                                                Toast.makeText(
                                                    context,
                                                    "No existe un paciente con ese código",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }

                                            nuevoPacienteUid == pacienteActualUid -> {
                                                Toast.makeText(
                                                    context,
                                                    "Ese paciente ya está vinculado",
                                                    Toast.LENGTH_SHORT
                                                ).show()
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
                                                        Toast.makeText(
                                                            context,
                                                            "Paciente vinculado correctamente",
                                                            Toast.LENGTH_SHORT
                                                        ).show()

                                                        navController.popBackStack()
                                                    }
                                                    .addOnFailureListener { error ->
                                                        val denegado =
                                                            (error as? com.google.firebase.firestore
                                                            .FirebaseFirestoreException)?.code ==
                                                                    com.google.firebase.firestore
                                                                        .FirebaseFirestoreException.Code
                                                                        .PERMISSION_DENIED
                                                        Toast.makeText(
                                                            context,
                                                            if (denegado) "No se pudo vincular: ese paciente ya alcanzó " +
                                                                    "el máximo de tutores (${LimiteTutores.MAX_GRATIS} en el plan gratis, " +
                                                                    "${LimiteTutores.MAX_PREMIUM} con Premium)."
                                                            else "No se pudo cambiar de paciente",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                            }
                                        }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "No se pudo verificar el código",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "No se pudieron cargar los datos del vínculo",
                                    Toast.LENGTH_SHORT
                                ).show()
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

            var pacienteNombre by remember { mutableStateOf("") }
            var pacienteFechaVinculo by remember { mutableStateOf("") }
            var pacienteFechaNacimiento by remember { mutableStateOf("") }
            var pacienteGenero by remember { mutableStateOf("") }
            var pacientePais by remember { mutableStateOf("") }
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
                                Toast.makeText(
                                    context,
                                    "No se pudieron cargar los datos para el PDF",
                                    Toast.LENGTH_SHORT
                                ).show()
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
                var pedirClaveBorrado by remember { mutableStateOf(false) }
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
                                        Toast.makeText(
                                            context,
                                            "Tu cuenta y tus datos fueron eliminados",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        navController.navigate("login") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                    is ResultadoBorrado.ContrasenaIncorrecta -> {
                                        borrando = false
                                        Toast.makeText(context, "La contraseña no es correcta", Toast.LENGTH_LONG).show()
                                    }
                                    is ResultadoBorrado.SinConexion -> {
                                        borrando = false
                                        Toast.makeText(context, "Sin conexión. Probá de nuevo con internet.", Toast.LENGTH_LONG).show()
                                    }
                                    is ResultadoBorrado.Fallo -> {
                                        borrando = false
                                        Toast.makeText(context, r.detalle, Toast.LENGTH_LONG).show()
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
                            Toast.makeText(
                                context,
                                if (archivo != null) "Guardado en Descargas: $archivo"
                                else "No se pudieron exportar tus datos",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },

                    onDesvincularPaciente = {
                        if (tutorUid == null) {
                            Toast.makeText(
                                context,
                                "No se encontró la sesión del tutor",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val tutorRef = db.collection("usuarios").document(tutorUid)

                            tutorRef.get()
                                .addOnSuccessListener { tutorDocument ->
                                    val pacienteUid =
                                        tutorDocument.getString("pacienteVinculadoUid").orEmpty()

                                    if (pacienteUid.isBlank()) {
                                        Toast.makeText(
                                            context,
                                            "No hay un paciente vinculado",
                                            Toast.LENGTH_SHORT
                                        ).show()
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
                                                Toast.makeText(
                                                    context,
                                                    "Paciente desvinculado correctamente",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                auth.signOut()
                                                nombreUsuarioActual = "Usuario"
                                                rolCuidadorActual = "tutor"

                                                navController.navigate("tutor_login") {
                                                    popUpTo(0) { inclusive = true }
                                                }
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(
                                                    context,
                                                    "No se pudo desvincular al paciente",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        context,
                                        "No se pudieron cargar los datos del vínculo",
                                        Toast.LENGTH_SHORT
                                    ).show()
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
                        Toast.makeText(
                            context,
                            "No se encontró una aplicación de correo. Escribinos a holapulsofirmearg@gmail.com",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }


        // =====================================================
        // AJUSTES CUIDADOR
        // =====================================================

        composable("caregiver_settings") {
            val context = LocalContext.current
            var pedirClaveBorrado by remember { mutableStateOf(false) }

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

/**
 * Pide la contraseña y borra la cuenta completa (Firestore + Authentication).
 * Se usa desde Ajustes del paciente y Ajustes del cuidador.
 */
@Composable
private fun BorrarCuentaConClave(
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
                            "Tu cuenta y tus datos fueron eliminados",
                            Toast.LENGTH_LONG
                        ).show()
                        onBorrada()
                    }
                    is ResultadoBorrado.ContrasenaIncorrecta -> {
                        borrando = false
                        Toast.makeText(context, "La contraseña no es correcta", Toast.LENGTH_LONG).show()
                    }
                    is ResultadoBorrado.SinConexion -> {
                        borrando = false
                        Toast.makeText(
                            context,
                            "No hay conexión a internet. Probá de nuevo con WiFi o datos.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is ResultadoBorrado.Fallo -> {
                        borrando = false
                        Toast.makeText(context, r.detalle, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    )
}

/** Pide la contraseña antes de borrar la cuenta (Firebase exige reautenticar). */
@Composable
private fun DialogoClaveParaBorrar(
    borrando: Boolean,
    onCancelar: () -> Unit,
    onConfirmar: (String) -> Unit
) {
    var clave by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        containerColor = Color.White,
        title = {
            Text("Eliminar mi cuenta", fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
        },
        text = {
            Column {
                Text(
                    "Se borran tus datos y tu cuenta de forma definitiva. " +
                            "Para confirmar, escribí tu contraseña.",
                    fontSize = 14.sp,
                    color = Color(0xFF374151)
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = clave,
                    onValueChange = { clave = it },
                    label = { Text("Contraseña") },
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
                    if (borrando) "Eliminando…" else "Eliminar definitivamente",
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
            ) { Text("Cancelar", color = Color(0xFF6B7280)) }
        }
    )
}