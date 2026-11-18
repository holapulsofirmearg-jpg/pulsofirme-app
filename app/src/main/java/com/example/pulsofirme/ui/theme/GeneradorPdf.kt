package com.example.pulsofirme

import com.example.pulsofirme.screens.IdiomaApp
import com.example.pulsofirme.screens.tr
import com.example.pulsofirme.screens.trf

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val VERDE = Color.parseColor("#1B7A4B")
private val VERDE_OSCURO = Color.parseColor("#1B4332")
private val VERDE_SUAVE = Color.parseColor("#EAF5EC")
private val VERDE_BARRA = Color.parseColor("#C9E6D4")
private val GRIS = Color.parseColor("#6B7280")
private val GRIS_LINEA = Color.parseColor("#E5E7EB")
private val NEGRO = Color.parseColor("#1A1A1A")

private const val ANCHO = 595
private const val ALTO = 842
private const val MARGEN = 44f

private fun fuente(bold: Boolean) =
    Typeface.create("sans-serif", if (bold) Typeface.BOLD else Typeface.NORMAL)

private fun pincelTexto(tam: Float, color: Int, bold: Boolean = false) = Paint().apply {
    isAntiAlias = true
    textSize = tam
    this.color = color
    typeface = fuente(bold)
}

private fun pincelRelleno(color: Int) = Paint().apply {
    isAntiAlias = true
    this.color = color
    style = Paint.Style.FILL
}

// Dibuja texto largo cortándolo en varias líneas. Devuelve la Y final.
private fun dibujarParrafo(
    canvas: Canvas,
    texto: String,
    x: Float,
    yInicial: Float,
    anchoMax: Float,
    pincel: Paint,
    interlineado: Float
): Float {
    var y = yInicial
    val palabras = texto.split(" ")
    var linea = StringBuilder()

    for (palabra in palabras) {
        val prueba = if (linea.isEmpty()) palabra else "$linea $palabra"
        if (pincel.measureText(prueba) > anchoMax) {
            canvas.drawText(linea.toString(), x, y, pincel)
            y += interlineado
            linea = StringBuilder(palabra)
        } else {
            linea = StringBuilder(prueba)
        }
    }
    if (linea.isNotEmpty()) {
        canvas.drawText(linea.toString(), x, y, pincel)
        y += interlineado
    }
    return y
}

private fun tarjetaStat(
    canvas: Canvas,
    x: Float,
    y: Float,
    ancho: Float,
    titulo: String,
    valor: String
) {
    canvas.drawRoundRect(RectF(x, y, x + ancho, y + 78f), 14f, 14f, pincelRelleno(VERDE_SUAVE))
    canvas.drawText(titulo, x + 14f, y + 26f, pincelTexto(9.5f, GRIS))
    canvas.drawText(valor, x + 14f, y + 58f, pincelTexto(22f, NEGRO, true))
}

// Un día de la semana en el resumen
private data class DiaResumen(
    val letra: String,      // "L"
    val fecha: String,      // "03/11"
    val ejercicios: Int,
    val sensacion: String?, // "Rigidez baja" o null
    val tomas: Int,         // tomas marcadas
    val tomasTotal: Int     // tomas que le tocaban (0 = no tenía)
)

/**
 * Genera el resumen de los últimos 7 días en PDF y devuelve el archivo, o null si algo falla.
 * Incluye: ejercicios por día, cómo se sintió cada día, tomas de medicación,
 * último QuickDASH y horarios de medicación.
 */
fun generarResumenSemanal(
    context: Context,
    onResultado: (File?) -> Unit
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    if (uid == null) {
        onResultado(null)
        return
    }

    CoroutineScope(Dispatchers.Main).launch {
        val archivo = try {
            val db = FirebaseFirestore.getInstance()
            val ref = db.collection("usuarios").document(uid)
            val doc = ref.get().await()

            val unDia = 24L * 60 * 60 * 1000
            val clave = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val corto = if (IdiomaApp.esIngles) SimpleDateFormat("MM/dd", Locale.US) else SimpleDateFormat("dd/MM", Locale("es", "AR"))
            val letras = if (IdiomaApp.esIngles) listOf("M", "T", "W", "T", "F", "S", "S")
            else listOf("L", "M", "M", "J", "V", "S", "D")

            // Los últimos 7 días (de hace 6 días a hoy)
            val dias = (6 downTo 0).map { atras ->
                Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -atras) }
            }
            val desdeClave = clave.format(dias.first().time)
            val desdeMs = Calendar.getInstance().apply {
                timeInMillis = dias.first().timeInMillis
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            // Ejercicios
            val ejercicios = ref.collection("ejerciciosRealizados").get().await().documents
            val fechasEj = ejercicios.mapNotNull { it.getTimestamp("fecha")?.toDate()?.time }
            val porDia = fechasEj.filter { it >= desdeMs }.groupingBy { clave.format(it) }.eachCount()

            // Sensación de cada día
            val sensaciones = (doc.get("sensaciones") as? Map<*, *>)
                ?.mapNotNull { (k, v) -> (k as? String)?.let { kk -> (v as? String)?.let { kk to it } } }
                ?.toMap().orEmpty()

            // Medicación: horarios, días y tomas marcadas
            var horarios = (doc.get("medicacionHorarios") as? List<*>)?.filterIsInstance<String>().orEmpty()
            if (horarios.isEmpty()) {
                horarios = try {
                    (ref.collection("configuracion").document("medicacion").get().await()
                        .get("horarios") as? List<*>)?.mapNotNull { it?.toString() }.orEmpty()
                } catch (_: Exception) { emptyList() }
            }
            if (horarios.isEmpty()) horarios = UserData.horariosMedicacion
            val medicacionActiva = doc.getBoolean("medicacionActiva") ?: horarios.isNotEmpty()
            val diasMedicacion = (doc.get("medicacionDias") as? List<*>)
                ?.mapNotNull { (it as? Number)?.toInt() }?.toSet() ?: (0..6).toSet()

            val tomas = try {
                ref.collection("tomasMedicacion").whereGreaterThanOrEqualTo("fecha", desdeClave).get().await()
                    .documents.mapNotNull { it.getString("fecha") }
            } catch (_: Exception) { emptyList() }
            val tomasPorDia = tomas.groupingBy { it }.eachCount()

            val semana = dias.map { c ->
                val k = clave.format(c.time)
                val indice = (c.get(Calendar.DAY_OF_WEEK) + 5) % 7 // lunes = 0
                val tocaban = if (medicacionActiva && indice in diasMedicacion) horarios.size else 0
                DiaResumen(
                    letra = letras[indice],
                    fecha = corto.format(c.time),
                    ejercicios = porDia[k] ?: 0,
                    sensacion = sensaciones[k],
                    tomas = (tomasPorDia[k] ?: 0).coerceAtMost(maxOf(tocaban, tomasPorDia[k] ?: 0)),
                    tomasTotal = tocaban
                )
            }

            // Último QuickDASH (de Firestore; si falla, el de la sesión)
            val ultimaEval = try {
                ref.collection("quickdash").orderBy("fecha", Query.Direction.DESCENDING).limit(1).get().await()
                    .documents.firstOrNull()
            } catch (_: Exception) { null }
            val puntaje = ultimaEval?.getDouble("puntaje")?.toFloat() ?: ProgresoData.puntajeQuickDash
            val fechaEval = ultimaEval?.getTimestamp("fecha")?.toDate()?.time ?: ProgresoData.fechaQuickDash

            val diagnosticos = (doc.get("diagnosticos") as? List<*>)?.filterIsInstance<String>().orEmpty()

            crearPdfPaciente(
                context = context,
                nombre = doc.getString("nombre").orEmpty().ifBlank { UserData.nombreUsuario.ifBlank { "Usuario" } },
                correo = doc.getString("correo").orEmpty(),
                diagnostico = (doc.getString("diagnosticoPrincipal") ?: doc.getString("diagnostico")).orEmpty().ifBlank {
                    when {
                        diagnosticos.isNotEmpty() -> diagnosticos.joinToString(" y ")
                        UserData.diagnosticoPrincipal.isNotBlank() -> UserData.diagnosticoPrincipal
                        UserData.diagnosticos.isNotEmpty() -> UserData.diagnosticos.joinToString(" y ")
                        else -> tr("Sin diagnóstico cargado")
                    }
                },
                mano = doc.getString("mano").orEmpty().ifBlank { UserData.mano },
                ejerciciosTotales = fechasEj.size,
                semana = semana,
                horariosMedicacion = if (medicacionActiva) horarios else emptyList(),
                puntajeQuickDash = puntaje,
                fechaQuickDash = fechaEval
            )
        } catch (_: Exception) {
            null
        }
        onResultado(archivo)
    }
}

private fun crearPdfPaciente(
    context: Context,
    nombre: String,
    correo: String,
    diagnostico: String,
    mano: String,
    ejerciciosTotales: Int,
    semana: List<DiaResumen>,
    horariosMedicacion: List<String>,
    puntajeQuickDash: Float?,
    fechaQuickDash: Long?
): File? {
    return try {
        val documento = PdfDocument()
        val info = PdfDocument.PageInfo.Builder(ANCHO, ALTO, 1).create()
        val pagina = documento.startPage(info)
        val canvas = pagina.canvas
        val anchoUtil = ANCHO - MARGEN * 2

        val hoy = Calendar.getInstance()
        val formatoLargo = if (IdiomaApp.esIngles) SimpleDateFormat("MMMM d, yyyy", Locale.US)
        else SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "AR"))
        val rango = trf("{0} al {1}", semana.first().fecha, semana.last().fecha)

        // ---------- Encabezado ----------
        canvas.drawRect(RectF(0f, 0f, ANCHO.toFloat(), 118f), pincelRelleno(VERDE))
        canvas.drawText("PulsoFirme", MARGEN, 46f, pincelTexto(13f, Color.WHITE))
        canvas.drawText(tr("Resumen del paciente"), MARGEN, 82f, pincelTexto(28f, Color.WHITE, true))
        val pincelFecha = pincelTexto(11f, Color.WHITE).apply { alpha = 220 }
        canvas.drawText(rango, ANCHO - MARGEN - pincelFecha.measureText(rango), 82f, pincelFecha)

        // ---------- Paciente ----------
        var y = 158f
        canvas.drawText(tr("PACIENTE"), MARGEN, y, pincelTexto(10f, GRIS, true))
        y += 22f
        canvas.drawText(nombre, MARGEN, y, pincelTexto(20f, VERDE_OSCURO, true))
        y += 18f
        if (correo.isNotBlank()) {
            canvas.drawText(correo, MARGEN, y, pincelTexto(10.5f, GRIS))
            y += 18f
        }
        val manoTexto = if (mano.isBlank()) tr("No informada") else tr(mano)
        y = dibujarParrafo(canvas, trf("{0} · Mano del guante: {1}", tr(diagnostico), manoTexto), MARGEN, y, anchoUtil, pincelTexto(11f, GRIS), 16f)
        y += 14f

        // ---------- Tarjetas ----------
        val ejSemana = semana.sumOf { it.ejercicios }
        val tomasHechas = semana.sumOf { it.tomas }
        val tomasTotal = semana.sumOf { it.tomasTotal }
        val anchoTarjeta = (anchoUtil - 24f) / 3f
        tarjetaStat(canvas, MARGEN, y, anchoTarjeta, tr("Ejercicios (7 días)"), "$ejSemana")
        tarjetaStat(canvas, MARGEN + anchoTarjeta + 12f, y, anchoTarjeta, tr("Tomas de medicación"),
            if (tomasTotal == 0) "—" else "$tomasHechas/$tomasTotal")
        tarjetaStat(canvas, MARGEN + (anchoTarjeta + 12f) * 2, y, anchoTarjeta, tr("Ejercicios en total"), "$ejerciciosTotales")
        y += 104f

        // ---------- Semana día por día ----------
        canvas.drawText(tr("Tus últimos 7 días"), MARGEN, y, pincelTexto(13f, VERDE_OSCURO, true))
        y += 14f

        val anchoEtiqueta = 78f
        val anchoCol = (anchoUtil - anchoEtiqueta) / 7f
        val altoFila = 30f
        val filas = listOf("Día", "Ejercicios", "Cómo se sintió", "Medicación").map { tr(it) }
        canvas.drawRoundRect(RectF(MARGEN, y, ANCHO - MARGEN, y + altoFila * filas.size), 10f, 10f, pincelRelleno(VERDE_SUAVE))

        filas.forEachIndexed { f, etiqueta ->
            val yFila = y + altoFila * f
            if (f > 0) canvas.drawLine(MARGEN + 8f, yFila, ANCHO - MARGEN - 8f, yFila, Paint().apply { color = GRIS_LINEA })
            canvas.drawText(etiqueta, MARGEN + 10f, yFila + 19f, pincelTexto(9f, GRIS, f == 0))
        }

        semana.forEachIndexed { i, d ->
            val cx = MARGEN + anchoEtiqueta + anchoCol * i + anchoCol / 2f
            fun centrado(texto: String, yy: Float, p: Paint) =
                canvas.drawText(texto, cx - p.measureText(texto) / 2f, yy, p)

            // Día
            centrado("${d.letra} ${d.fecha}", y + 19f, pincelTexto(9f, NEGRO, true))
            // Ejercicios
            centrado(if (d.ejercicios == 0) "—" else "${d.ejercicios}", y + altoFila + 19f,
                pincelTexto(10.5f, if (d.ejercicios > 0) VERDE else GRIS, d.ejercicios > 0))
            // Sensación: punto de color + texto corto
            val ySens = y + altoFila * 2
            if (d.sensacion == null) {
                centrado("—", ySens + 19f, pincelTexto(10f, GRIS))
            } else {
                val color = SensacionDiaria.colorDe(d.sensacion).toArgb()
                canvas.drawCircle(cx, ySens + 9f, 3.5f, pincelRelleno(color))
                val palabras = tr(d.sensacion).split(" ")
                centrado(palabras.first(), ySens + 21f, pincelTexto(7f, NEGRO))
                if (palabras.size > 1) centrado(palabras.drop(1).joinToString(" "), ySens + 28.5f, pincelTexto(7f, NEGRO))
            }
            // Medicación
            val yMed = y + altoFila * 3 + 19f
            when {
                d.tomasTotal == 0 && d.tomas == 0 -> centrado("—", yMed, pincelTexto(10f, GRIS))
                d.tomas >= d.tomasTotal -> centrado("${d.tomas}/${d.tomasTotal} ✓", yMed, pincelTexto(9.5f, VERDE, true))
                else -> centrado("${d.tomas}/${d.tomasTotal}", yMed, pincelTexto(9.5f, Color.parseColor("#C0392B"), true))
            }
        }
        y += altoFila * filas.size + 14f
        y = dibujarParrafo(
            canvas,
            tr("Medicación: tomas marcadas con \"Ya la tomé\" sobre las que tocaban ese día. ") +
                    tr("Cómo se sintió: lo que registró en \"Cómo te sentís hoy\"."),
            MARGEN, y, anchoUtil, pincelTexto(8.5f, GRIS), 12f
        )
        y += 16f

        canvas.drawLine(MARGEN, y, ANCHO - MARGEN, y, Paint().apply { color = GRIS_LINEA })
        y += 26f

        // ---------- QuickDASH ----------
        canvas.drawText(tr("Autoevaluación QuickDASH"), MARGEN, y, pincelTexto(13f, VERDE_OSCURO, true))
        y += 20f
        val textoQuickDash = if (puntajeQuickDash == null) {
            tr("Todavía no se realizó ninguna evaluación.")
        } else {
            val nivel = when {
                puntajeQuickDash < 15f -> tr("sin dificultad o muy leve")
                puntajeQuickDash < 40f -> tr("dificultad leve")
                puntajeQuickDash < 60f -> tr("dificultad moderada")
                else -> "dificultad importante"
            }
            val cuando = if (fechaQuickDash == null) "" else " · ${formatoLargo.format(fechaQuickDash)}"
            trf("Puntaje {0} sobre 100 ({1})", String.format(Locale.US, "%.1f", puntajeQuickDash), nivel) + cuando
        }
        y = dibujarParrafo(canvas, textoQuickDash, MARGEN, y, anchoUtil, pincelTexto(11f, GRIS), 16f)
        y += 18f

        // ---------- Horarios de medicación ----------
        canvas.drawText(tr("Horarios de medicación"), MARGEN, y, pincelTexto(13f, VERDE_OSCURO, true))
        y += 20f
        val textoMedicacion = if (horariosMedicacion.isEmpty()) {
            tr("No hay recordatorios de medicación activos.")
        } else {
            trf("Recordatorios: {0} hs.", horariosMedicacion.joinToString(" · ") { it.removeSuffix(" hs").trim() })
        }
        y = dibujarParrafo(canvas, textoMedicacion, MARGEN, y, anchoUtil, pincelTexto(11f, GRIS), 16f)
        y += 18f

        // ---------- Seguimiento ----------
        if (y < 700f && (UserData.profesionalACargo.isNotBlank() || UserData.notasCuidador.isNotBlank())) {
            canvas.drawText(tr("Seguimiento"), MARGEN, y, pincelTexto(13f, VERDE_OSCURO, true))
            y += 20f
            if (UserData.profesionalACargo.isNotBlank()) {
                y = dibujarParrafo(canvas, trf("Profesional a cargo: {0}", UserData.profesionalACargo), MARGEN, y, anchoUtil, pincelTexto(11f, GRIS), 16f)
            }
            if (UserData.notasCuidador.isNotBlank() && y < 730f) {
                dibujarParrafo(canvas, trf("Notas: {0}", UserData.notasCuidador.take(220)), MARGEN, y, anchoUtil, pincelTexto(11f, GRIS), 16f)
            }
        }

        // ---------- Pie ----------
        val yPie = 762f
        canvas.drawRoundRect(RectF(MARGEN, yPie, ANCHO - MARGEN, yPie + 50f), 12f, 12f, pincelRelleno(VERDE_SUAVE))
        dibujarParrafo(
            canvas,
            trf("Documento generado por PulsoFirme el {0}. Este resumen es informativo y no reemplaza una evaluación profesional.", formatoLargo.format(hoy.time)),
            MARGEN + 16f, yPie + 21f, anchoUtil - 32f, pincelTexto(9.5f, GRIS), 14f
        )

        documento.finishPage(pagina)
        val carpeta = File(context.cacheDir, "informes")
        if (!carpeta.exists()) carpeta.mkdirs()
        val nombreArchivo = "PulsoFirme_datos_paciente_${SimpleDateFormat("yyyy-MM-dd", Locale.US).format(hoy.time)}.pdf"
        val archivo = File(carpeta, nombreArchivo)
        FileOutputStream(archivo).use { documento.writeTo(it) }
        documento.close()
        archivo
    } catch (_: Exception) {
        null
    }
}

/** Abre el PDF con el visor del celular. */
fun abrirPdf(context: Context, archivo: File): Boolean {
    return try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivo
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, tr("Abrir resumen")))
        true
    } catch (e: Exception) {
        false
    }
}

/** Comparte el PDF (WhatsApp, mail, Drive...). */
fun compartirPdf(context: Context, archivo: File): Boolean {
    return try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivo
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, tr("Resumen semanal de PulsoFirme"))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, tr("Compartir resumen")))
        true
    } catch (e: Exception) {
        false
    }
}