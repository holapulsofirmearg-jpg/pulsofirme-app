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

// Pantallas del paciente: inicio, ejercicios, progreso, alertas, cuenta y ajustes.
// Las rutas usan los datos compartidos de EstadoNav (nombreUsuarioActual, navController, etc.).
fun NavGraphBuilder.rutasPaciente(estado: EstadoNav) {
    with(estado) {


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
                            Avisos.mostrar("No se pudieron cargar las preferencias de alertas")
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
                        Avisos.mostrar("No se encontró la sesión del usuario")
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

                                Avisos.mostrar("Preferencias de alertas guardadas")

                                navController.popBackStack()
                            }
                            .addOnFailureListener {
                                Avisos.mostrar("No se pudieron guardar las preferencias")
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
                                        Avisos.mostrar("No se pudo generar el código de vinculación")
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
                                                                Avisos.mostrar("No se pudo guardar el código de vinculación")
                                                            }
                                                    }
                                                    .addOnFailureListener {
                                                        Avisos.mostrar("No se pudo reservar el código")
                                                    }
                                            }
                                        }
                                        .addOnFailureListener {
                                            Avisos.mostrar("No se pudo verificar el código de vinculación")
                                        }
                                }

                                intentarGenerarCodigo(10)
                            }
                        }
                        .addOnFailureListener {
                            Avisos.mostrar("No se pudieron cargar los datos de la cuenta")
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
                        Avisos.mostrar("No se encontró la sesión del usuario")
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
                                Avisos.mostrar("Evaluación QuickDASH guardada")
                            }
                            .addOnFailureListener {
                                Avisos.mostrar("No se pudo guardar la evaluación")
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
                            Avisos.mostrar("No se pudieron cargar tus datos")
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
                        Avisos.mostrar("No se encontró la sesión del usuario")
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

                                Avisos.mostrar("Datos actualizados correctamente")
                            }
                            .addOnFailureListener {
                                Avisos.mostrar("No se pudieron guardar los cambios")
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
                            Avisos.mostrar("No se pudieron cargar los datos médicos")
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
                        Avisos.mostrar("No se encontró la sesión del usuario")
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
                                Avisos.mostrar("Datos médicos actualizados correctamente")
                            }
                            .addOnFailureListener {
                                Avisos.mostrar("No se pudieron guardar los datos médicos")
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
            var pedirClaveBorrado by rememberSaveable { mutableStateOf(false) }

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
                        Avisos.mostrar(if (archivo != null) "Guardado en Descargas: $archivo"
                        else "No se pudieron exportar tus datos", largo = true)
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
    }
}