package com.example.pulsofirme.screens

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.ui.theme.enTema
import com.example.pulsofirme.R

/**
 * Idioma de la app (español o inglés).
 *
 * Los textos se escriben en español en el código, como siempre. Al mostrarlos,
 * tr("texto") busca la traducción en res/raw/traducciones_en.txt si el usuario
 * eligió inglés. Si un texto no está en la lista, se muestra en español.
 *
 * Para traducir un texto nuevo: agregá una línea a traducciones_en.txt con
 *     texto en español<TAB>texto en inglés
 * Los saltos de línea se escriben como \n. Los datos variables van como {0}, {1}…
 */
object IdiomaApp {
    var actual by mutableStateOf("es")
        private set

    val esIngles: Boolean get() = actual == "en"

    private const val PREFS = "idioma_app"
    private const val CLAVE = "idioma"

    private var ingles: Map<String, String> = emptyMap()

    /** Se llama al abrir la app: carga el idioma elegido y las traducciones. */
    fun cargar(context: Context) {
        val app = context.applicationContext
        actual = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(CLAVE, "es") ?: "es"
        if (ingles.isEmpty()) {
            ingles = try {
                app.resources.openRawResource(R.raw.traducciones_en).bufferedReader(Charsets.UTF_8)
                    .useLines { lineas ->
                        lineas.mapNotNull { linea ->
                            val tab = linea.indexOf('\t')
                            if (tab <= 0) null
                            else desescapar(linea.substring(0, tab)) to desescapar(linea.substring(tab + 1))
                        }.toMap()
                    }
            } catch (_: Exception) {
                emptyMap()
            }
        }
    }

    /** Cambia el idioma y lo guarda en el celular. */
    fun cambiar(context: Context, codigo: String) {
        actual = codigo
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(CLAVE, codigo).apply()
    }

    fun traducir(texto: String): String =
        if (actual == "en") ingles[texto] ?: ingles[texto.trim()]?.let { conEspacios(texto, it) } ?: texto
        else texto

    private fun conEspacios(original: String, traducido: String): String {
        val inicio = original.takeWhile { it.isWhitespace() }
        val fin = original.takeLastWhile { it.isWhitespace() }
        return inicio + traducido + fin
    }

    private fun desescapar(s: String): String =
        s.replace("\\n", "\n").replace("\\t", "\t")
}

/** Traduce un texto si el usuario eligió inglés. */
fun tr(texto: String): String = IdiomaApp.traducir(texto)

@JvmName("trOpcional")
fun tr(texto: String?): String? = texto?.let { IdiomaApp.traducir(it) }

/** Los textos con estilos (AnnotatedString) se traducen por partes, con tr() en cada append. */
fun tr(texto: AnnotatedString): AnnotatedString = texto

/** Cualquier otro valor (números, etc.) se muestra igual. */
fun <T> tr(valor: T): T = valor

/**
 * Texto con datos variables. Ejemplo:
 *     trf("Hola, {0}", nombre)
 * busca "Hola, {0}" en las traducciones y después pone los datos.
 */
fun trf(plantilla: String, vararg datos: Any?): String {
    var resultado = IdiomaApp.traducir(plantilla)
    datos.forEachIndexed { i, dato -> resultado = resultado.replace("{$i}", dato.toString()) }
    return resultado
}


/** Nombre del idioma actual, para mostrarlo en Ajustes. */
val nombreIdiomaActual: String
    get() = if (IdiomaApp.esIngles) "English" else "Español"

/** Diálogo para elegir el idioma (se usa en los Ajustes del paciente y del tutor). */
@Composable
fun DialogoIdioma(onCerrar: () -> Unit) {
    val contexto = LocalContext.current
    val verde = Color(0xFF1B7A4B).enTema()
    AlertDialog(
        onDismissRequest = onCerrar,
        containerColor = Color.White.enTema(),
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(tr("Idioma"), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B4332).enTema())
        },
        text = {
            Column {
                listOf("es" to "Español", "en" to "English").forEach { (codigo, nombre) ->
                    val elegido = IdiomaApp.actual == codigo
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = elegido,
                                role = Role.RadioButton,
                                onClick = { IdiomaApp.cambiar(contexto, codigo) }
                            )
                            .heightIn(min = 48.dp)
                    ) {
                        RadioButton(
                            selected = elegido,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = verde)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(nombre, fontSize = 16.sp, color = Color(0xFF1A1A1A).enTema())
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onCerrar) {
                Text(tr("Listo"), color = verde, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    )
}