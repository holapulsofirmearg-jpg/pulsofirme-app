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

// Bienvenida, inicio de sesión y registro (paciente, tutor y profesional).
// Las rutas usan los datos compartidos de EstadoNav (nombreUsuarioActual, navController, etc.).
fun NavGraphBuilder.rutasInicio(estado: EstadoNav) {
    with(estado) {

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
                                Avisos.mostrar("No se pudieron cargar los datos de la sesión")

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
                        Avisos.mostrar("Completá el correo y la contraseña")

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
                                            Avisos.mostrar(ErroresFirebase.datos(error, "No se pudieron cargar tus datos. Intentá de nuevo."))
                                        }
                                }
                            }
                            .addOnFailureListener { error ->

                                cargando = false
                                Avisos.mostrar(ErroresFirebase.login(error))
                            }
                    }
                },

                onForgotPasswordClick = { email ->

                    if (email.isBlank()) {

                        cargando = false
                        Avisos.mostrar("Ingresá tu correo electrónico primero")

                    } else {

                        // Enviar el correo de recuperación en español
                        auth.setLanguageCode("es")

                        auth.sendPasswordResetEmail(email.trim())
                            .addOnSuccessListener {

                                cargando = false
                                Avisos.mostrar("Te enviamos un correo para restablecer tu contraseña", largo = true)
                            }
                            .addOnFailureListener { error ->

                                cargando = false
                                Avisos.mostrar(ErroresFirebase.recuperacion(error), largo = true)
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
                        Avisos.mostrar("No se encontró la sesión del usuario")
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
                                Avisos.mostrar("Datos guardados correctamente")

                                navController.navigate("about_you")
                            }
                            .addOnFailureListener {
                                Avisos.mostrar("No se pudieron guardar los datos")
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
                        Avisos.mostrar("No se encontró la sesión del usuario")
                    } else {
                        val datosSobreVos = mapOf(
                            "diagnosticos" to diagnosticos,
                            "mano" to mano
                        )

                        db.collection("usuarios")
                            .document(uid)
                            .update(datosSobreVos)
                            .addOnSuccessListener {
                                Avisos.mostrar("Datos guardados correctamente")

                                navController.navigate("congrats") {
                                    popUpTo("about_you") {
                                        inclusive = true
                                    }
                                }
                            }
                            .addOnFailureListener {
                                Avisos.mostrar("No se pudieron guardar los datos")
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
                        Avisos.mostrar("Completá el correo, la contraseña y el código de vinculación")
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
                                    Avisos.mostrar("No se encontró la sesión del tutor")
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
                                                    Avisos.mostrar("Esta cuenta no corresponde a un tutor")
                                                }

                                                pacienteUid.isBlank() -> {
                                                    auth.signOut()
                                                    cargando = false
                                                    Avisos.mostrar("Esta cuenta todavía no está vinculada a un paciente", largo = true)
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
                                                                Avisos.mostrar("El código no corresponde al paciente vinculado", largo = true)
                                                            } else {
                                                                nombreUsuarioActual =
                                                                    nombre
                                                                rolCuidadorActual =
                                                                    "tutor"

                                                                cargando = false
                                                                Avisos.mostrar("Sesión iniciada correctamente")

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
                                                            Avisos.mostrar(ErroresFirebase.datos(error, "No se pudo verificar el paciente vinculado"), largo = true)
                                                        }
                                                }
                                            }
                                        }
                                        .addOnFailureListener { error ->
                                            auth.signOut()
                                            cargando = false
                                            Avisos.mostrar(ErroresFirebase.datos(error, "No se pudieron cargar los datos del tutor"))
                                        }
                                }
                            }
                            .addOnFailureListener { error ->
                                cargando = false
                                Avisos.mostrar(ErroresFirebase.login(error))
                            }
                    }
                },

                onOlvidasteContrasena = { email ->

                    if (email.isBlank()) {
                        cargando = false
                        Avisos.mostrar("Ingresá tu correo electrónico primero")
                    } else {
                        auth.setLanguageCode("es")

                        auth.sendPasswordResetEmail(email.trim())
                            .addOnSuccessListener {
                                cargando = false
                                Avisos.mostrar("Te enviamos un correo para restablecer tu contraseña", largo = true)
                            }
                            .addOnFailureListener { error ->
                                cargando = false
                                Avisos.mostrar(ErroresFirebase.recuperacion(error), largo = true)
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
                        Avisos.mostrar("Completá los datos obligatorios")
                    } else {

                        cargando = true

                        // Buscamos al PACIENTE dueño del código en la colección "codigos".
                        db.collection("codigos")
                            .document(tutorCodigoVinculacion.trim())
                            .get()
                            .addOnSuccessListener { codigoDoc ->

                                if (!codigoDoc.exists()) {
                                    cargando = false
                                    Avisos.mostrar("El código de vinculación no pertenece a ningún paciente", largo = true)
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
                                                Avisos.mostrar("No se pudo crear la cuenta")
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
                                                        Avisos.mostrar("Tutor vinculado correctamente con el paciente")

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
                                                        Avisos.mostrar(if (denegado) "No se pudo vincular: el paciente ya alcanzó " +
                                                                "el máximo de tutores (${LimiteTutores.MAX_GRATIS} en el plan gratis, " +
                                                                "${LimiteTutores.MAX_PREMIUM} con Premium) o el código no es válido."
                                                        else ErroresFirebase.datos(error, "No se pudo completar la vinculación"), largo = true)
                                                    }
                                            }
                                        }
                                        .addOnFailureListener { error ->
                                            cargando = false
                                            Avisos.mostrar(ErroresFirebase.registro(error), largo = true)
                                        }
                                }
                            }
                            .addOnFailureListener { error ->
                                cargando = false
                                Avisos.mostrar(ErroresFirebase.datos(error, "No se pudo verificar el código del paciente"), largo = true)
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
                                Avisos.mostrar("No se pudo obtener la sesión del profesional", largo = true)
                            } else {
                                db.collection("usuarios")
                                    .document(uid)
                                    .get()
                                    .addOnSuccessListener { document ->
                                        val rol = document.getString("rol") ?: ""

                                        if (rol != "profesional") {
                                            auth.signOut()
                                            cargando = false
                                            Avisos.mostrar("Esta cuenta no corresponde a un profesional", largo = true)
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
                                        Avisos.mostrar(ErroresFirebase.datos(error, "No se pudieron verificar los datos del profesional"), largo = true)
                                    }
                            }
                        }
                        .addOnFailureListener { error ->
                            cargando = false
                            Avisos.mostrar(ErroresFirebase.login(error), largo = true)
                        }
                },

                onForgotPasswordClick = { correo ->
                    auth.sendPasswordResetEmail(correo.trim())
                        .addOnSuccessListener {
                            cargando = false
                            Avisos.mostrar("Te enviamos un correo para restablecer tu contraseña", largo = true)
                        }
                        .addOnFailureListener { error ->
                            cargando = false
                            Avisos.mostrar(ErroresFirebase.recuperacion(error), largo = true)
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
                                Avisos.mostrar("No se pudo crear la cuenta profesional", largo = true)
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
                                        Avisos.mostrar("Cuenta profesional creada correctamente")

                                        navController.navigate("caregiver_home") {
                                            popUpTo("welcome_caregiver") {
                                                inclusive = true
                                            }
                                        }
                                    }
                                    .addOnFailureListener { error ->
                                        resultado.user?.delete()
                                        cargando = false
                                        Avisos.mostrar(ErroresFirebase.datos(error, "No se pudieron guardar los datos del profesional"), largo = true)
                                    }
                            }
                        }
                        .addOnFailureListener { error ->
                            cargando = false
                            Avisos.mostrar(ErroresFirebase.registro(error), largo = true)
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
    }
}