package com.example.pulsofirme

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * "Descargar mis datos" (derecho de acceso, Ley 25.326 art. 14).
 * Junta el perfil y los registros del usuario en un archivo JSON y lo guarda en Descargas.
 * Devuelve el nombre del archivo, o null si algo falló.
 */
suspend fun exportarMisDatos(context: Context): String? {
    val user = FirebaseAuth.getInstance().currentUser ?: return null
    val db = FirebaseFirestore.getInstance()
    val ref = db.collection("usuarios").document(user.uid)

    return try {
        val raiz = JSONObject()
        raiz.put("exportadoEl", fechaIso(Date()))
        raiz.put("correo", user.email ?: "")
        raiz.put("perfil", aJson(ref.get().await().data ?: emptyMap<String, Any>()))

        for (nombre in listOf("ejerciciosRealizados", "quickdash")) {
            val docs = ref.collection(nombre).get().await().documents
            val arr = JSONArray()
            docs.forEach { arr.put(aJson(it.data ?: emptyMap<String, Any>())) }
            raiz.put(nombre, arr)
        }

        val texto = raiz.toString(2)
        val nombreArchivo = "PulsoFirme_mis_datos_" +
                SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date()) + ".json"

        withContext(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val valores = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, nombreArchivo)
                    put(MediaStore.Downloads.MIME_TYPE, "application/json")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver
                    .insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, valores)
                    ?: return@withContext null
                context.contentResolver.openOutputStream(uri)?.use { it.write(texto.toByteArray()) }
                    ?: return@withContext null
            } else {
                // Android 9 o menor: carpeta propia de la app (no pide permisos)
                val carpeta = context.getExternalFilesDir(null) ?: context.filesDir
                File(carpeta, nombreArchivo).writeText(texto)
            }
            nombreArchivo
        }
    } catch (e: Exception) {
        null
    }
}

private fun fechaIso(fecha: Date): String =
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
        .format(fecha)

/** Convierte lo que devuelve Firestore a algo que JSON entienda. */
private fun aJson(valor: Any?): Any? = when (valor) {
    null -> JSONObject.NULL
    is Timestamp -> fechaIso(valor.toDate())
    is Map<*, *> -> JSONObject().also { o ->
        valor.forEach { (k, v) -> o.put(k.toString(), aJson(v)) }
    }
    is Iterable<*> -> JSONArray().also { a -> valor.forEach { a.put(aJson(it)) } }
    is Number, is Boolean, is String -> valor
    else -> valor.toString()
}