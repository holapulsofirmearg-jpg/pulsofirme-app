package com.example.pulsofirme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

object UserData {
    var diagnosticos: List<String> = emptyList()
    var mano: String = ""
    var horariosMedicacion: List<String> = emptyList()
    var nombreUsuario by mutableStateOf("")
    var codigoVinculacion by mutableStateOf("482 913")
    var genero by mutableStateOf("Prefiero no decir")
    // Premium: se carga solo desde Firestore (ver "PREMIUM" más abajo)
    var esPremium by mutableStateOf(false)
    var rol by mutableStateOf("paciente")

    // Datos personales editables
    var correo by mutableStateOf("")
    var fechaNacimiento by mutableStateOf("")
    var pais by mutableStateOf("")
    var telefono by mutableStateOf("")
    var direccion by mutableStateOf("")
    var fotoPerfil by mutableStateOf("")

    // Datos médicos
    var diagnosticoPrincipal by mutableStateOf("")
    var zonaAfectada by mutableStateOf("")
    var profesionalACargo by mutableStateOf("")
    var notasCuidador by mutableStateOf("")

    // Permisos de privacidad
    var compartirProgreso by mutableStateOf(true)
    var compartirMedicacion by mutableStateOf(true)
    var compartirUbicacion by mutableStateOf(false)

    // Datos del paciente vinculado (para la vista del cuidador) — placeholder hasta tener backend real
    var pacienteVinculadoNombre by mutableStateOf("Morena Martinez")
    var pacienteVinculadoDiagnostico by mutableStateOf("Parkinson")
    var pacienteVinculadoUltimoEjercicio by mutableStateOf("Hoy, 09:30 AM")
    var pacienteVinculadoAdherencia by mutableStateOf(85)


    // =====================================================================
    // PREMIUM
    // Se guarda en la cuenta (Firestore), no en el celular:
    //  - esPremium:   lo pone el sistema de pago real (la app no puede escribirlo)
    //  - premiumDemo: lo pone el checkout de demostración
    // El tutor no tiene Premium propio: hereda el del paciente vinculado.
    // =====================================================================

    private var oyenteCuenta: ListenerRegistration? = null
    private var oyentePaciente: ListenerRegistration? = null

    init {
        // Cada vez que alguien entra o sale de su cuenta, se vuelve a leer el plan
        FirebaseAuth.getInstance().addAuthStateListener { fa ->
            escucharPremium(fa.currentUser?.uid)
        }
    }

    private fun tienePremium(doc: DocumentSnapshot): Boolean =
        doc.getBoolean("esPremium") == true || doc.getBoolean("premiumDemo") == true

    private fun escucharPremium(uid: String?) {
        oyenteCuenta?.remove(); oyenteCuenta = null
        oyentePaciente?.remove(); oyentePaciente = null

        if (uid == null) {
            esPremium = false
            rol = "paciente"
            nombreUsuario = ""
            return
        }

        val db = FirebaseFirestore.getInstance()
        oyenteCuenta = db.collection("usuarios").document(uid)
            .addSnapshotListener { doc, _ ->
                if (doc == null || !doc.exists()) return@addSnapshotListener
                rol = doc.getString("rol") ?: "paciente"
                // El nombre real de la cuenta (lo usan Cuenta, la invitación, etc.)
                doc.getString("nombre")?.takeIf { it.isNotBlank() }?.let { nombreUsuario = it.trim() }

                if (rol == "tutor") {
                    // Hereda el plan del paciente vinculado
                    oyentePaciente?.remove(); oyentePaciente = null
                    val pacienteUid = doc.getString("pacienteVinculadoUid").orEmpty()
                    if (pacienteUid.isBlank()) {
                        esPremium = false
                        return@addSnapshotListener
                    }
                    oyentePaciente = db.collection("usuarios").document(pacienteUid)
                        .addSnapshotListener { pac, _ ->
                            esPremium = pac != null && pac.exists() && tienePremium(pac)
                        }
                } else {
                    esPremium = tienePremium(doc)
                }
            }
    }

    /** Checkout de demostración: activa Premium en la cuenta. */
    fun activarPremiumDemo(onError: () -> Unit = {}) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return onError()
        esPremium = true
        FirebaseFirestore.getInstance().collection("usuarios").document(uid)
            .update(
                mapOf(
                    "premiumDemo" to true,
                    "premiumDemoFecha" to FieldValue.serverTimestamp()
                )
            )
            .addOnFailureListener {
                esPremium = false
                onError()
            }
    }

    /** Para volver a probar el checkout en una presentación. */
    fun desactivarPremiumDemo(onError: () -> Unit = {}) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return onError()
        FirebaseFirestore.getInstance().collection("usuarios").document(uid)
            .update("premiumDemo", false)
            .addOnFailureListener { onError() }
    }

    fun generarCodigoNuevo() {
        val nuevo = (100000..999999).random().toString()
        codigoVinculacion = nuevo.substring(0, 3) + " " + nuevo.substring(3)
    }

    fun borrarTodo() {
        diagnosticos = emptyList()
        mano = ""
        horariosMedicacion = emptyList()
        nombreUsuario = ""
        genero = "Prefiero no decir"
        esPremium = false
        correo = ""
        fechaNacimiento = ""
        pais = ""
        telefono = ""
        direccion = ""
        fotoPerfil = ""
        diagnosticoPrincipal = ""
        zonaAfectada = ""
        profesionalACargo = ""
        notasCuidador = ""
        compartirProgreso = true
        compartirMedicacion = true
        compartirUbicacion = false
        generarCodigoNuevo()
    }
}