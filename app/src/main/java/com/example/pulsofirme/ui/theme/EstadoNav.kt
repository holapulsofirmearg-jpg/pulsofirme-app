package com.example.pulsofirme.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Datos que comparten todas las pantallas de la app (antes eran variables
 * sueltas adentro de AppNavGraph). Así las rutas pueden vivir en archivos separados:
 * RutasInicio, RutasPaciente, RutasCuidador y RutasProfesional.
 * Cada dato se guarda con rememberSaveable: no se pierde al girar el celular.
 */
class EstadoNav(
    val navController: NavHostController,
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    nombreEjercicioActual: MutableState<String>,
    zonaEjercicioActual: MutableState<String>,
    tipoEjercicioActual: MutableState<String>,
    repeticionesCompletadas: MutableState<Int>,
    totalRepeticiones: MutableState<Int>,
    tiempoTotalSegundos: MutableState<Int>,
    sensacionInicial: MutableState<String>,
    nombreUsuarioActual: MutableState<String>,
    rolCuidadorActual: MutableState<String>,
    profesionalMatricula: MutableState<String>,
    profesionalLugarTrabajo: MutableState<String>,
    profesionalEspecialidad: MutableState<String>,
    rolPulsiBotActual: MutableState<String>,
    pacienteProfesionalSeleccionado: MutableState<String>,
    pacienteProfesionalUidSeleccionado: MutableState<String>,
    tutorNombre: MutableState<String>,
    tutorApellido: MutableState<String>,
    tutorCorreo: MutableState<String>,
    tutorTelefono: MutableState<String>,
    tutorFechaNacimiento: MutableState<String>,
    tutorCodigoVinculacion: MutableState<String>,
    tutorPerfilNombre: MutableState<String>,
    tutorPerfilApellido: MutableState<String>,
    tutorPerfilCorreo: MutableState<String>,
    tutorPerfilTelefono: MutableState<String>,
    tutorPerfilFechaNacimiento: MutableState<String>,
    tutorPerfilGenero: MutableState<String>,
    tutorPerfilPais: MutableState<String>,
    editNombre: MutableState<String>,
    editCorreo: MutableState<String>,
    editFecha: MutableState<String>,
    editPais: MutableState<String>,
    editTelefono: MutableState<String>,
    editDireccion: MutableState<String>,
    editGenero: MutableState<String>,
    medicalDiagnostico: MutableState<String>,
    medicalZona: MutableState<String>,
    medicalProfesional: MutableState<String>,
    medicalNotas: MutableState<String>,
    alertaMedicacion: MutableState<Boolean>,
    alertaEjercicioPendiente: MutableState<Boolean>,
    alertaRacha: MutableState<Boolean>,
    alertaNuevoCuidador: MutableState<Boolean>,
    codigoVinculacionPaciente: MutableState<String>,
    cantidadTutoresVinculados: MutableState<Int>
) {
    var nombreEjercicioActual by nombreEjercicioActual
    var zonaEjercicioActual by zonaEjercicioActual
    var tipoEjercicioActual by tipoEjercicioActual
    var repeticionesCompletadas by repeticionesCompletadas
    var totalRepeticiones by totalRepeticiones
    var tiempoTotalSegundos by tiempoTotalSegundos
    var sensacionInicial by sensacionInicial
    var nombreUsuarioActual by nombreUsuarioActual
    var rolCuidadorActual by rolCuidadorActual
    var profesionalMatricula by profesionalMatricula
    var profesionalLugarTrabajo by profesionalLugarTrabajo
    var profesionalEspecialidad by profesionalEspecialidad
    var rolPulsiBotActual by rolPulsiBotActual
    var pacienteProfesionalSeleccionado by pacienteProfesionalSeleccionado
    var pacienteProfesionalUidSeleccionado by pacienteProfesionalUidSeleccionado
    var tutorNombre by tutorNombre
    var tutorApellido by tutorApellido
    var tutorCorreo by tutorCorreo
    var tutorTelefono by tutorTelefono
    var tutorFechaNacimiento by tutorFechaNacimiento
    var tutorCodigoVinculacion by tutorCodigoVinculacion
    var tutorPerfilNombre by tutorPerfilNombre
    var tutorPerfilApellido by tutorPerfilApellido
    var tutorPerfilCorreo by tutorPerfilCorreo
    var tutorPerfilTelefono by tutorPerfilTelefono
    var tutorPerfilFechaNacimiento by tutorPerfilFechaNacimiento
    var tutorPerfilGenero by tutorPerfilGenero
    var tutorPerfilPais by tutorPerfilPais
    var editNombre by editNombre
    var editCorreo by editCorreo
    var editFecha by editFecha
    var editPais by editPais
    var editTelefono by editTelefono
    var editDireccion by editDireccion
    var editGenero by editGenero
    var medicalDiagnostico by medicalDiagnostico
    var medicalZona by medicalZona
    var medicalProfesional by medicalProfesional
    var medicalNotas by medicalNotas
    var alertaMedicacion by alertaMedicacion
    var alertaEjercicioPendiente by alertaEjercicioPendiente
    var alertaRacha by alertaRacha
    var alertaNuevoCuidador by alertaNuevoCuidador
    var codigoVinculacionPaciente by codigoVinculacionPaciente
    var cantidadTutoresVinculados by cantidadTutoresVinculados
}

@Composable
fun rememberEstadoNav(navController: NavHostController): EstadoNav {
    val nombreEjercicioActual = rememberSaveable { mutableStateOf("") }
    val zonaEjercicioActual = rememberSaveable { mutableStateOf("") }
    val tipoEjercicioActual = rememberSaveable { mutableStateOf("") }
    val repeticionesCompletadas = rememberSaveable { mutableStateOf(0) }
    val totalRepeticiones = rememberSaveable { mutableStateOf(0) }
    val tiempoTotalSegundos = rememberSaveable { mutableStateOf(0) }
    val sensacionInicial = rememberSaveable { mutableStateOf("") }
    val nombreUsuarioActual = rememberSaveable { mutableStateOf("Usuario") }
    val rolCuidadorActual = rememberSaveable { mutableStateOf("tutor") }
    val profesionalMatricula = rememberSaveable { mutableStateOf("") }
    val profesionalLugarTrabajo = rememberSaveable { mutableStateOf("") }
    val profesionalEspecialidad = rememberSaveable { mutableStateOf("") }
    val rolPulsiBotActual = rememberSaveable { mutableStateOf("paciente") }
    val pacienteProfesionalSeleccionado = rememberSaveable { mutableStateOf("") }
    val pacienteProfesionalUidSeleccionado = rememberSaveable { mutableStateOf("") }
    val tutorNombre = rememberSaveable { mutableStateOf("") }
    val tutorApellido = rememberSaveable { mutableStateOf("") }
    val tutorCorreo = rememberSaveable { mutableStateOf("") }
    val tutorTelefono = rememberSaveable { mutableStateOf("") }
    val tutorFechaNacimiento = rememberSaveable { mutableStateOf("") }
    val tutorCodigoVinculacion = rememberSaveable { mutableStateOf("") }
    val tutorPerfilNombre = rememberSaveable { mutableStateOf("") }
    val tutorPerfilApellido = rememberSaveable { mutableStateOf("") }
    val tutorPerfilCorreo = rememberSaveable { mutableStateOf("") }
    val tutorPerfilTelefono = rememberSaveable { mutableStateOf("") }
    val tutorPerfilFechaNacimiento = rememberSaveable { mutableStateOf("") }
    val tutorPerfilGenero = rememberSaveable { mutableStateOf("") }
    val tutorPerfilPais = rememberSaveable { mutableStateOf("") }
    val editNombre = rememberSaveable { mutableStateOf("") }
    val editCorreo = rememberSaveable { mutableStateOf("") }
    val editFecha = rememberSaveable { mutableStateOf("") }
    val editPais = rememberSaveable { mutableStateOf("") }
    val editTelefono = rememberSaveable { mutableStateOf("") }
    val editDireccion = rememberSaveable { mutableStateOf("") }
    val editGenero = rememberSaveable { mutableStateOf("") }
    val medicalDiagnostico = rememberSaveable { mutableStateOf("") }
    val medicalZona = rememberSaveable { mutableStateOf("") }
    val medicalProfesional = rememberSaveable { mutableStateOf("") }
    val medicalNotas = rememberSaveable { mutableStateOf("") }
    val alertaMedicacion = rememberSaveable { mutableStateOf(true) }
    val alertaEjercicioPendiente = rememberSaveable { mutableStateOf(true) }
    val alertaRacha = rememberSaveable { mutableStateOf(true) }
    val alertaNuevoCuidador = rememberSaveable { mutableStateOf(true) }
    val codigoVinculacionPaciente = rememberSaveable { mutableStateOf("") }
    val cantidadTutoresVinculados = rememberSaveable { mutableStateOf(0) }
    return remember(navController) {
        EstadoNav(
            navController = navController,
            auth = FirebaseAuth.getInstance(),
            db = FirebaseFirestore.getInstance(),
            nombreEjercicioActual = nombreEjercicioActual,
            zonaEjercicioActual = zonaEjercicioActual,
            tipoEjercicioActual = tipoEjercicioActual,
            repeticionesCompletadas = repeticionesCompletadas,
            totalRepeticiones = totalRepeticiones,
            tiempoTotalSegundos = tiempoTotalSegundos,
            sensacionInicial = sensacionInicial,
            nombreUsuarioActual = nombreUsuarioActual,
            rolCuidadorActual = rolCuidadorActual,
            profesionalMatricula = profesionalMatricula,
            profesionalLugarTrabajo = profesionalLugarTrabajo,
            profesionalEspecialidad = profesionalEspecialidad,
            rolPulsiBotActual = rolPulsiBotActual,
            pacienteProfesionalSeleccionado = pacienteProfesionalSeleccionado,
            pacienteProfesionalUidSeleccionado = pacienteProfesionalUidSeleccionado,
            tutorNombre = tutorNombre,
            tutorApellido = tutorApellido,
            tutorCorreo = tutorCorreo,
            tutorTelefono = tutorTelefono,
            tutorFechaNacimiento = tutorFechaNacimiento,
            tutorCodigoVinculacion = tutorCodigoVinculacion,
            tutorPerfilNombre = tutorPerfilNombre,
            tutorPerfilApellido = tutorPerfilApellido,
            tutorPerfilCorreo = tutorPerfilCorreo,
            tutorPerfilTelefono = tutorPerfilTelefono,
            tutorPerfilFechaNacimiento = tutorPerfilFechaNacimiento,
            tutorPerfilGenero = tutorPerfilGenero,
            tutorPerfilPais = tutorPerfilPais,
            editNombre = editNombre,
            editCorreo = editCorreo,
            editFecha = editFecha,
            editPais = editPais,
            editTelefono = editTelefono,
            editDireccion = editDireccion,
            editGenero = editGenero,
            medicalDiagnostico = medicalDiagnostico,
            medicalZona = medicalZona,
            medicalProfesional = medicalProfesional,
            medicalNotas = medicalNotas,
            alertaMedicacion = alertaMedicacion,
            alertaEjercicioPendiente = alertaEjercicioPendiente,
            alertaRacha = alertaRacha,
            alertaNuevoCuidador = alertaNuevoCuidador,
            codigoVinculacionPaciente = codigoVinculacionPaciente,
            cantidadTutoresVinculados = cantidadTutoresVinculados
        )
    }
}