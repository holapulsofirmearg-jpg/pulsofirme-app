package com.example.pulsofirme.screens

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Registro del uso del guante cargado por el paciente (cronómetro o carga manual).
// Se guarda en Firestore:
//   usuarios/{uid}                      -> campo "guanteEnUsoDesde" (si está en uso ahora)
//   usuarios/{uid}/usoGuante/{sesión}   -> inicio, fin, minutos, manual, fecha
object UsoGuante {

    data class Resumen(
        val minutosHoy: Long = 0,
        val minutosSemana: Long = 0,
        val diasConUsoSemana: Int = 0,
        val enUsoDesde: Long? = null
    )

    private fun db() = FirebaseFirestore.getInstance()

    private fun docUsuario(uid: String) = db().collection("usuarios").document(uid)

    private fun claveDia(ms: Long): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(ms))

    private fun inicioDia(ms: Long, diasAtras: Int = 0): Long =
        Calendar.getInstance().apply {
            timeInMillis = ms
            add(Calendar.DAY_OF_YEAR, -diasAtras)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    // ---------- REGISTRAR ----------

    // "Me puse el guante"
    fun iniciar(uid: String) {
        docUsuario(uid).set(
            mapOf("guanteEnUsoDesde" to System.currentTimeMillis()),
            SetOptions.merge()
        )
    }

    // "Me saqué el guante". Si se pasan minutosReales, se usan esos (por si se olvidó de cortarlo)
    fun terminar(uid: String, inicio: Long, minutosReales: Long? = null) {
        val minutos = minutosReales
            ?: ((System.currentTimeMillis() - inicio) / 60_000L).coerceAtLeast(1)
        val fin = if (minutosReales != null) inicio + minutos * 60_000L else System.currentTimeMillis()

        guardarSesion(uid, inicio, fin, minutos, manual = minutosReales != null)
        docUsuario(uid).update("guanteEnUsoDesde", FieldValue.delete())
    }

    // Carga manual: "lo usé X minutos" (termina ahora)
    fun registrarManual(uid: String, minutos: Long) {
        val fin = System.currentTimeMillis()
        guardarSesion(uid, fin - minutos * 60_000L, fin, minutos, manual = true)
    }

    private fun guardarSesion(uid: String, inicio: Long, fin: Long, minutos: Long, manual: Boolean) {
        docUsuario(uid).collection("usoGuante").add(
            mapOf(
                "inicio" to inicio,
                "fin" to fin,
                "minutos" to minutos,
                "manual" to manual,
                "fecha" to claveDia(inicio)
            )
        )
    }

    // ---------- LEER (en tiempo real) ----------

    // Escucha los cambios y devuelve una función para dejar de escuchar
    fun escuchar(uid: String, onCambio: (Resumen) -> Unit): () -> Unit {
        var enUso: Long? = null
        var sesiones: List<Pair<Long, Long>> = emptyList() // (inicio, minutos)

        fun emitir() {
            val hoy = inicioDia(System.currentTimeMillis())
            onCambio(
                Resumen(
                    minutosHoy = sesiones.filter { it.first >= hoy }.sumOf { it.second },
                    minutosSemana = sesiones.sumOf { it.second },
                    diasConUsoSemana = sesiones.map { claveDia(it.first) }.distinct().size,
                    enUsoDesde = enUso
                )
            )
        }

        // Últimos 7 días (hoy incluido)
        val desde = inicioDia(System.currentTimeMillis(), 6)

        val r1 = docUsuario(uid).addSnapshotListener { snap, _ ->
            enUso = snap?.getLong("guanteEnUsoDesde")
            emitir()
        }

        val r2 = docUsuario(uid).collection("usoGuante")
            .whereGreaterThanOrEqualTo("inicio", desde)
            .addSnapshotListener { q, _ ->
                sesiones = q?.documents?.mapNotNull { d ->
                    val inicio = d.getLong("inicio") ?: return@mapNotNull null
                    inicio to (d.getLong("minutos") ?: 0L)
                } ?: emptyList()
                emitir()
            }

        return {
            r1.remove()
            r2.remove()
        }
    }

    // ---------- FORMATO ----------

    fun formatear(minutos: Long): String {
        if (minutos <= 0) return "0 min"
        val h = minutos / 60
        val m = minutos % 60
        return when {
            h == 0L -> "$m min"
            m == 0L -> "$h h"
            else -> "$h h $m min"
        }
    }

    fun cronometro(ms: Long): String {
        val s = (ms / 1000).coerceAtLeast(0)
        return "%02d:%02d:%02d".format(s / 3600, (s % 3600) / 60, s % 60)
    }
}