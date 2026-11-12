package com.example.pulsofirme

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException
import kotlin.math.abs

/**
 * Conexión con el guante (ESP32) por WiFi.
 *
 * El ESP32 crea la red WiFi "PulsoFirme" y escucha en 192.168.4.1:8080.
 *
 * Guante -> App: una línea por lectura, "anguloX,anguloY" (ej "12.34,-4.56")
 * App -> Guante: un solo carácter con la velocidad de los motores:
 *      'L' lento · 'M' intermedio · 'F' fuerte
 *
 * El guante no avisa cuando se apaga (si se corta la alimentación la placa
 * deja de ejecutar código), así que el estado "Encendido / Apagado" se
 * deduce acá: mientras lleguen datos está encendido; si dejan de llegar
 * por más de TIEMPO_SIN_DATOS_MS, apagado.
 */
object GuanteConexion {

    const val IP_GUANTE = "192.168.4.1"
    const val PUERTO = 8080
    const val RED_WIFI = "PulsoFirme"

    private const val TIEMPO_SIN_DATOS_MS = 2000L

    enum class Estado { DESCONECTADO, CONECTANDO, CONECTADO, ERROR }

    /** Velocidades de vibración. El carácter es lo que espera el ESP32. */
    enum class Velocidad(val comando: Char, val etiqueta: String, val detalle: String) {
        LENTO('L', "Lento", "Vibración suave"),
        INTERMEDIO('M', "Intermedio", "Vibración media"),
        FUERTE('F', "Fuerte", "Máxima potencia")
    }

    var estado by mutableStateOf(Estado.DESCONECTADO)
        private set

    var guanteEncendido by mutableStateOf(false)
        private set

    /** El ESP32 arranca en intermedio, así que la app muestra lo mismo */
    var velocidad by mutableStateOf(Velocidad.INTERMEDIO)
        private set

    /** Inclinación del eje X en grados, con signo */
    var anguloX by mutableStateOf(0f)
        private set

    /** Inclinación del eje Y en grados, con signo */
    var anguloY by mutableStateOf(0f)
        private set

    var maximoX by mutableStateOf(0f)
        private set

    var maximoY by mutableStateOf(0f)
        private set

    var lecturasRecibidas by mutableStateOf(0)
        private set

    var mensajeError by mutableStateOf("")
        private set

    private var ultimoDatoMs = 0L
    private var trabajoLectura: Job? = null
    private var trabajoVigilante: Job? = null
    private var socket: Socket? = null
    private var salida: OutputStream? = null
    private val alcance = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun descripcionInclinacion(): String {
        if (!guanteEncendido) return "Sin datos"

        val quietoX = abs(anguloX) < 8f
        val quietoY = abs(anguloY) < 8f

        if (quietoX && quietoY) return "Mano en posición neutra"

        val partes = mutableListOf<String>()
        if (!quietoX) partes.add(if (anguloX > 0f) "hacia arriba" else "hacia abajo")
        if (!quietoY) partes.add(if (anguloY > 0f) "hacia la derecha" else "hacia la izquierda")

        return "Inclinada " + partes.joinToString(" y ")
    }

    /** Le manda al guante la velocidad elegida. Devuelve false si no se pudo. */
    fun cambiarVelocidad(nueva: Velocidad): Boolean {
        velocidad = nueva
        val flujo = salida ?: return false

        alcance.launch {
            try {
                flujo.write(nueva.comando.code)
                flujo.flush()
            } catch (e: Exception) {
                // Si el guante se apagó, el vigilante ya lo va a marcar
            }
        }
        return true
    }

    fun conectar(context: Context) {
        if (estado == Estado.CONECTANDO || estado == Estado.CONECTADO) return

        estado = Estado.CONECTANDO
        mensajeError = ""
        maximoX = 0f
        maximoY = 0f
        anguloX = 0f
        anguloY = 0f
        lecturasRecibidas = 0
        guanteEncendido = false

        trabajoLectura?.cancel()
        trabajoLectura = alcance.launch {
            try {
                usarRedWifi(context)

                val s = Socket()
                s.connect(InetSocketAddress(IP_GUANTE, PUERTO), 5000)
                s.soTimeout = 3000
                socket = s
                salida = s.getOutputStream()

                estado = Estado.CONECTADO
                ultimoDatoMs = System.currentTimeMillis()
                arrancarVigilante()

                // Le avisamos al guante con qué velocidad tiene que arrancar
                cambiarVelocidad(velocidad)

                val lector = BufferedReader(InputStreamReader(s.getInputStream()))
                while (isActive) {
                    val linea = try {
                        lector.readLine()
                    } catch (e: SocketTimeoutException) {
                        continue
                    } ?: break

                    procesarLinea(linea)
                }

                guanteEncendido = false
                if (estado == Estado.CONECTADO) estado = Estado.DESCONECTADO
            } catch (e: Exception) {
                guanteEncendido = false
                mensajeError = "No se pudo conectar. Revisá que el celular esté en la red $RED_WIFI y que el guante esté encendido."
                estado = Estado.ERROR
            } finally {
                cerrarSocket()
            }
        }
    }

    /**
     * Interpreta una línea del guante: "anguloX,anguloY".
     * Si llega un solo número, se toma como eje X.
     */
    private fun procesarLinea(linea: String) {
        val partes = linea.trim().split(",")
        if (partes.isEmpty()) return

        val x = partes[0].trim().toFloatOrNull() ?: return
        val y = if (partes.size > 1) partes[1].trim().toFloatOrNull() ?: 0f else 0f

        anguloX = x
        anguloY = y
        if (abs(x) > abs(maximoX)) maximoX = x
        if (abs(y) > abs(maximoY)) maximoY = y

        ultimoDatoMs = System.currentTimeMillis()
        guanteEncendido = true
        lecturasRecibidas++
    }

    private fun arrancarVigilante() {
        trabajoVigilante?.cancel()
        trabajoVigilante = alcance.launch {
            while (isActive && estado == Estado.CONECTADO) {
                delay(500)
                if (System.currentTimeMillis() - ultimoDatoMs > TIEMPO_SIN_DATOS_MS) {
                    guanteEncendido = false
                }
            }
        }
    }

    fun desconectar(context: Context) {
        trabajoLectura?.cancel()
        trabajoVigilante?.cancel()
        trabajoLectura = null
        trabajoVigilante = null
        cerrarSocket()
        liberarRed(context)
        estado = Estado.DESCONECTADO
        guanteEncendido = false
        anguloX = 0f
        anguloY = 0f
        lecturasRecibidas = 0
        mensajeError = ""
    }

    private fun cerrarSocket() {
        try {
            salida?.close()
        } catch (e: Exception) {
            // ignorado
        }
        try {
            socket?.close()
        } catch (e: Exception) {
            // ignorado
        }
        salida = null
        socket = null
    }

    /** Fuerza a la app a usar el WiFi del guante aunque no tenga internet */
    private fun usarRedWifi(context: Context) {
        try {
            val cm = context.applicationContext
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val redWifi = cm.allNetworks.firstOrNull { red ->
                cm.getNetworkCapabilities(red)
                    ?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
            }
            if (redWifi != null) cm.bindProcessToNetwork(redWifi)
        } catch (e: Exception) {
            // Si falla, igual intentamos por la red por defecto
        }
    }

    private fun liberarRed(context: Context) {
        try {
            val cm = context.applicationContext
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            cm.bindProcessToNetwork(null)
        } catch (e: Exception) {
            // ignorado
        }
    }
}