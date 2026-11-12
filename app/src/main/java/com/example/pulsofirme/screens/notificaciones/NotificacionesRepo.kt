package com.example.pulsofirme.notificaciones

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

object NotificacionesRepo {

    private const val PREFS = "notificaciones_pulsofirme"
    private const val KEY_LISTA = "lista"
    private const val KEY_BIENVENIDA = "bienvenida_creada"
    private const val MAX_AVISOS = 100

    // tipo: "bienvenida", "medicacion", "ejercicio", "progreso", "racha", "evaluacion", "general"
    data class Aviso(
        val id: Long,
        val tipo: String,
        val titulo: String,
        val detalle: String,
        val timestamp: Long,
        val leida: Boolean
    )

    // Lista observable: cuando cambia, se actualizan la pantalla y el globito del inicio
    var avisos by mutableStateOf<List<Aviso>>(emptyList())
        private set

    private var cargado = false

    fun cargar(context: Context) {
        if (cargado) return
        val json = prefs(context).getString(KEY_LISTA, "[]") ?: "[]"
        avisos = leerJson(json).sortedByDescending { it.timestamp }
        cargado = true
    }

    // Usalo desde cualquier pantalla o receiver para registrar un aviso nuevo
    fun agregar(context: Context, tipo: String, titulo: String, detalle: String) {
        cargar(context)
        val nuevo = Aviso(
            id = System.currentTimeMillis() * 1000 + Random.nextInt(1000),
            tipo = tipo,
            titulo = titulo,
            detalle = detalle,
            timestamp = System.currentTimeMillis(),
            leida = false
        )
        avisos = (listOf(nuevo) + avisos).take(MAX_AVISOS)
        guardar(context)
    }

    fun marcarTodasLeidas(context: Context) {
        cargar(context)
        if (avisos.none { !it.leida }) return
        avisos = avisos.map { it.copy(leida = true) }
        guardar(context)
    }

    // Crea el aviso de bienvenida una sola vez (la primera vez que se abre la app)
    fun asegurarBienvenida(context: Context) {
        cargar(context)
        val p = prefs(context)
        if (!p.getBoolean(KEY_BIENVENIDA, false)) {
            agregar(context, "bienvenida", "", "")
            p.edit().putBoolean(KEY_BIENVENIDA, true).apply()
        }
    }

    fun borrarTodo(context: Context) {
        avisos = emptyList()
        prefs(context).edit().remove(KEY_LISTA).apply()
    }

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun guardar(context: Context) {
        val arr = JSONArray()
        avisos.forEach { a ->
            arr.put(
                JSONObject().apply {
                    put("id", a.id)
                    put("tipo", a.tipo)
                    put("titulo", a.titulo)
                    put("detalle", a.detalle)
                    put("ts", a.timestamp)
                    put("leida", a.leida)
                }
            )
        }
        prefs(context).edit().putString(KEY_LISTA, arr.toString()).apply()
    }

    private fun leerJson(json: String): List<Aviso> = try {
        val arr = JSONArray(json)
        (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            Aviso(
                id = o.getLong("id"),
                tipo = o.optString("tipo", "general"),
                titulo = o.optString("titulo", ""),
                detalle = o.optString("detalle", ""),
                timestamp = o.getLong("ts"),
                leida = o.optBoolean("leida", false)
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}