package com.example.pulsofirme

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestoreException

// Traduce los errores de Firebase a mensajes claros para el usuario.
// Así "sin internet" no se confunde con "contraseña incorrecta".
object ErroresFirebase {

    private const val SIN_CONEXION =
        "No hay conexión a internet. Revisá tu WiFi o datos móviles e intentá de nuevo."

    private const val DEMASIADOS_INTENTOS =
        "Hiciste demasiados intentos. Esperá unos minutos y volvé a probar."

    /** ¿El error fue por falta de internet? */
    fun esSinConexion(e: Exception?): Boolean =
        e is FirebaseNetworkException ||
                (e as? FirebaseFirestoreException)?.code == FirebaseFirestoreException.Code.UNAVAILABLE

    /** Mensaje para un inicio de sesión que falló. */
    fun login(e: Exception?): String = when {
        esSinConexion(e) -> SIN_CONEXION
        e is FirebaseTooManyRequestsException -> DEMASIADOS_INTENTOS
        e is FirebaseAuthInvalidUserException && e.errorCode == "ERROR_USER_DISABLED" ->
            "Esta cuenta está deshabilitada. Escribinos a holapulsofirmearg@gmail.com."
        e is FirebaseAuthInvalidCredentialsException || e is FirebaseAuthInvalidUserException ->
            "El correo o la contraseña no son correctos."
        else -> "No pudimos iniciar sesión. Intentá de nuevo en un momento."
    }

    /** Mensaje para la creación de una cuenta que falló. */
    fun registro(e: Exception?): String = when {
        esSinConexion(e) -> SIN_CONEXION
        e is FirebaseTooManyRequestsException -> DEMASIADOS_INTENTOS
        e is FirebaseAuthUserCollisionException -> "Ese correo ya tiene una cuenta registrada."
        e is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil. Usá al menos 6 caracteres."
        e is FirebaseAuthInvalidCredentialsException -> "El correo electrónico no es válido."
        e is FirebaseAuthException -> "No se pudo crear la cuenta. Revisá el correo y la contraseña."
        else -> "No se pudo crear la cuenta. Intentá de nuevo en un momento."
    }

    /** Mensaje para el correo de recuperación de contraseña. */
    fun recuperacion(e: Exception?): String = when {
        esSinConexion(e) -> SIN_CONEXION
        e is FirebaseTooManyRequestsException -> DEMASIADOS_INTENTOS
        e is FirebaseAuthInvalidCredentialsException -> "El correo electrónico no es válido."
        else -> "No se pudo enviar el correo. Verificá la dirección ingresada."
    }

    /** Mensaje para lecturas/escrituras de datos: si es por internet lo dice, si no usa el mensaje propio. */
    fun datos(e: Exception?, mensajeNormal: String): String =
        if (esSinConexion(e)) SIN_CONEXION else mensajeNormal
}