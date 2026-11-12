package com.example.pulsofirme

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

sealed class ResultadoBorrado {
    object Ok : ResultadoBorrado()
    object ContrasenaIncorrecta : ResultadoBorrado()
    object SinConexion : ResultadoBorrado()
    data class Fallo(val detalle: String) : ResultadoBorrado()
}

/** Borra todos los documentos de una subcolección, de a lotes. */
private suspend fun borrarColeccion(col: CollectionReference) {
    val db = col.firestore
    while (true) {
        val lote = col.limit(400).get().await()
        if (lote.isEmpty) return
        val batch = db.batch()
        lote.documents.forEach { batch.delete(it.reference) }
        batch.commit().await()
    }
}

/**
 * Elimina la cuenta del usuario actual:
 *  1) reautentica con la contraseña,
 *  2) borra sus datos en Firestore (ejercicios, QuickDASH, tomas de medicación,
 *     uso del guante, código, vínculos y el perfil con la sensación de cada día,
 *     el plan, los permisos y la configuración de alertas),
 *  3) borra la cuenta de Firebase Authentication.
 */
suspend fun eliminarCuentaCompleta(contrasena: String): ResultadoBorrado {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
        ?: return ResultadoBorrado.Fallo("No hay una sesión iniciada.")
    val correo = user.email
        ?: return ResultadoBorrado.Fallo("La cuenta no tiene un correo asociado.")
    val uid = user.uid
    val db = FirebaseFirestore.getInstance()

    // 1) Reautenticar (Firebase lo exige para borrar una cuenta)
    try {
        user.reauthenticate(EmailAuthProvider.getCredential(correo, contrasena)).await()
    } catch (e: FirebaseAuthInvalidCredentialsException) {
        return ResultadoBorrado.ContrasenaIncorrecta
    } catch (e: FirebaseNetworkException) {
        return ResultadoBorrado.SinConexion
    } catch (e: Exception) {
        return ResultadoBorrado.Fallo(e.message ?: "No se pudo verificar tu identidad.")
    }

    // 2) Borrar datos en Firestore
    try {
        val ref = db.collection("usuarios").document(uid)
        val datos = ref.get().await().data ?: emptyMap<String, Any>()
        val rol = datos["rol"] as? String

        // Subcolecciones del usuario.
        // Firestore NO las borra solas al borrar el perfil: hay que borrar cada una.
        // Si agregás una subcolección nueva en la app, sumala a esta lista.
        // (Las que no existen se saltean sin error.)
        val subcolecciones = listOf(
            "ejerciciosRealizados",
            "quickdash",
            "tomasMedicacion",   // "Ya la tomé"
            "configuracion",     // configuración vieja de medicación
            "usoGuante",         // uso del guante
            "usosGuante",
            "sesionesGuante",
            "notificaciones"
        )
        for (nombre in subcolecciones) {
            try {
                borrarColeccion(ref.collection(nombre))
            } catch (e: FirebaseFirestoreException) {
                // Si una no se puede leer (no existe o sin permiso), seguimos con las demás
                if (e.code == FirebaseFirestoreException.Code.UNAVAILABLE) throw e
            }
        }

        when (rol) {
            "paciente" -> {
                // Su código de vinculación (solo si realmente es suyo)
                val codigo = (datos["codigoVinculacion"] as? String)?.replace(" ", "")
                if (!codigo.isNullOrBlank()) {
                    try {
                        val refCodigo = db.collection("codigos").document(codigo)
                        val dueno = refCodigo.get().await().getString("uid")
                        if (dueno == uid) refCodigo.delete().await()
                    } catch (_: Exception) { /* si no se puede, seguimos */ }
                }
            }

            "tutor" -> {
                // Se quita de la lista de tutores de su paciente
                val pacienteUid = datos["pacienteVinculadoUid"] as? String
                if (!pacienteUid.isNullOrBlank()) {
                    try {
                        db.collection("usuarios").document(pacienteUid)
                            .update("tutoresVinculados", FieldValue.arrayRemove(uid))
                            .await()
                    } catch (_: Exception) { }
                }
            }

            "profesional" -> {
                // Se quita de cada paciente vinculado
                val pacientes = (datos["pacientesVinculados"] as? List<*>)
                    ?.filterIsInstance<String>().orEmpty()
                for (pacUid in pacientes) {
                    val refPaciente = db.collection("usuarios").document(pacUid)
                    try {
                        refPaciente.update("profesionalesVinculados", FieldValue.arrayRemove(uid)).await()
                    } catch (_: Exception) { }
                    try {
                        refPaciente.update("permisosProfesionales.$uid", FieldValue.delete()).await()
                    } catch (_: Exception) { }
                }
            }
        }

        // Perfil
        ref.delete().await()

    } catch (e: FirebaseFirestoreException) {
        return if (e.code == FirebaseFirestoreException.Code.UNAVAILABLE) {
            ResultadoBorrado.SinConexion
        } else {
            ResultadoBorrado.Fallo("No se pudieron borrar los datos (${e.code}). Intentá de nuevo.")
        }
    } catch (e: Exception) {
        return ResultadoBorrado.Fallo(e.message ?: "No se pudieron borrar los datos.")
    }

    // 3) Borrar la cuenta de Authentication
    return try {
        user.delete().await()
        ResultadoBorrado.Ok
    } catch (e: Exception) {
        ResultadoBorrado.Fallo(
            "Se borraron tus datos, pero no se pudo cerrar la cuenta de acceso. " +
                    "Escribinos a ${TextosLegales.CONTACTO}."
        )
    }
}