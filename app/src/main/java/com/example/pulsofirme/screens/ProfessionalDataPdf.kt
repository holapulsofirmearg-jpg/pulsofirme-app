package com.example.pulsofirme

import com.example.pulsofirme.screens.IdiomaApp
import com.example.pulsofirme.screens.tr
import com.example.pulsofirme.screens.trf

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

data class ProfessionalPdfData(
    val nombre: String = "",
    val apellido: String = "",
    val correo: String = "",
    val telefono: String = "",
    val fechaNacimiento: String = "",
    val genero: String = "",
    val pais: String = "",
    val lugarTrabajo: String = "",
    val especialidad: String = "",
    val matricula: String = "",
    val formacionAdicional: String = ""
)

fun generarDatosProfesionalPdf(
    context: Context,
    datos: ProfessionalPdfData
): File? {
    return try {
        val documento = PdfDocument()
        val paginaInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val pagina = documento.startPage(paginaInfo)
        val canvas = pagina.canvas

        val titulo = Paint().apply {
            textSize = 22f
            isFakeBoldText = true
        }
        val subtitulo = Paint().apply {
            textSize = 16f
            isFakeBoldText = true
        }
        val texto = Paint().apply {
            textSize = 12f
        }

        var y = 55f

        canvas.drawText("PulsoFirme", 45f, y, titulo)
        y += 30f
        canvas.drawText(tr("Datos de la cuenta profesional"), 45f, y, subtitulo)
        y += 35f

        fun campo(etiqueta: String, valor: String) {
            if (y > 800f) return
            val contenido = valor.ifBlank { tr("No informado") }
            canvas.drawText("${tr(etiqueta)}: $contenido", 45f, y, texto)
            y += 24f
        }

        canvas.drawText(tr("Datos personales"), 45f, y, subtitulo)
        y += 28f
        campo("Nombre", datos.nombre)
        campo("Apellido", datos.apellido)
        campo("Correo electrónico", datos.correo)
        campo("Número de teléfono", datos.telefono)
        campo("Fecha de nacimiento", datos.fechaNacimiento)
        campo("Género", datos.genero)
        campo("País", datos.pais)

        y += 12f
        canvas.drawText(tr("Datos profesionales"), 45f, y, subtitulo)
        y += 28f
        campo("Lugar de trabajo", datos.lugarTrabajo)
        campo("Especialidad", datos.especialidad)
        campo("Matrícula profesional", datos.matricula)
        campo("Formación adicional", datos.formacionAdicional)

        y += 18f
        canvas.drawText(
            tr("Documento generado desde PulsoFirme."),
            45f,
            y,
            texto
        )

        documento.finishPage(pagina)

        val carpeta = File(context.cacheDir, "pdfs")
        if (!carpeta.exists()) carpeta.mkdirs()

        val archivo = File(carpeta, "datos_profesional_pulsofirme.pdf")
        FileOutputStream(archivo).use { salida ->
            documento.writeTo(salida)
        }
        documento.close()
        archivo
    } catch (_: Exception) {
        null
    }
}

fun abrirPdfProfesional(context: Context, archivo: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        archivo
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, tr("Abrir PDF")))
}

fun compartirPdfProfesional(context: Context, archivo: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        archivo
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, tr("Compartir PDF")))
}