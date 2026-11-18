package com.example.pulsofirme.screens

import androidx.compose.runtime.saveable.rememberSaveable

import com.example.pulsofirme.ui.theme.enTema
import com.example.pulsofirme.ui.theme.ModoTema
import com.example.pulsofirme.ui.theme.TemaApp

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import com.example.pulsofirme.Accesibilidad

private val VerdeTitulo: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
private val AzulTexto: Color
    @Composable get() = Color(0xFF16324F).enTema()
private val GrisSubtitulo: Color
    @Composable get() = Color(0xFF6B7280).enTema()
private val CelesteFondo: Color
    @Composable get() = Color(0xFFEAF3F7).enTema()
private val VerdeClaroFondo: Color
    @Composable get() = Color(0xFFEFF6F1).enTema()
private val VerdeIconoFondo: Color
    @Composable get() = Color(0xFFDFF3E4).enTema()
private val RojoBoton: Color
    @Composable get() = Color(0xFFE05A4E).enTema()
private val GrisDivider: Color
    @Composable get() = Color(0xFFE5E7EB).enTema()
@Composable
fun AjustesCaregiverScreen(
    onBack: () -> Unit,
    // Abre el pedido de contraseña para borrar la cuenta de verdad (lo maneja NavGraph)
    onEliminarCuenta: () -> Unit = {}
) {
    val contexto = LocalContext.current
    var accesibilidadExpandida by rememberSaveable { mutableStateOf(false) }
    var mostrarDialogoIdioma by rememberSaveable { mutableStateOf(false) }
    if (mostrarDialogoIdioma) DialogoIdioma(onCerrar = { mostrarDialogoIdioma = false })

    // --- DIÁLOGO DE TEMA (claro / oscuro / automático) ---
    var mostrarDialogoTema by rememberSaveable { mutableStateOf(false) }
    if (mostrarDialogoTema) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoTema = false },
            containerColor = Color.White.enTema(),
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(tr("Tema"), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B4332).enTema())
            },
            text = {
                Column {
                    ModoTema.values().forEach { modo ->
                        val elegido = TemaApp.modo == modo
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = elegido,
                                    role = Role.RadioButton,
                                    onClick = { TemaApp.cambiar(contexto, modo) }
                                )
                                .heightIn(min = 48.dp)
                        ) {
                            RadioButton(
                                selected = elegido,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF1B7A4B).enTema())
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(tr(modo.etiqueta), fontSize = 16.sp, color = Color(0xFF1A1A1A).enTema())
                                if (modo == ModoTema.AUTOMATICO) {
                                    Text(tr("Igual que el celular"), fontSize = 13.sp, color = GrisSubtitulo)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoTema = false }) {
                    Text(tr("Listo"), color = Color(0xFF1B7A4B).enTema(), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        )
    }
    var mostrarTamanoTexto by rememberSaveable { mutableStateOf(false) }

    // Las opciones de accesibilidad son las mismas de toda la app (se guardan en el celular)
    if (mostrarTamanoTexto) {
        AlertDialog(
            onDismissRequest = { mostrarTamanoTexto = false },
            containerColor = Color.White.enTema(),
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(tr("Tamaño del texto"), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = VerdeTitulo)
            },
            text = {
                Column {
                    Accesibilidad.niveles.forEach { nivel ->
                        val elegido = Accesibilidad.tamanoLetra == nivel
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = elegido,
                                    role = Role.RadioButton,
                                    onClick = { Accesibilidad.cambiarTamanoLetra(contexto, nivel) }
                                )
                                .heightIn(min = 48.dp)
                        ) {
                            RadioButton(
                                selected = elegido,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(selectedColor = VerdeTitulo)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(tr(nivel), fontSize = 16.sp, color = Color(0xFF1A1A1A).enTema())
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarTamanoTexto = false }) {
                    Text(tr("Listo"), color = VerdeTitulo, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.enTema())
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = tr("Volver"),
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = tr("Ajustes"),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeTitulo,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        FilaAjuste(titulo = "Tema", valor = TemaApp.modo.etiqueta, onClick = { mostrarDialogoTema = true })

        Spacer(modifier = Modifier.height(12.dp))

        FilaAjuste(titulo = "Idioma", valor = nombreIdiomaActual, onClick = { mostrarDialogoIdioma = true })

        Spacer(modifier = Modifier.height(12.dp))

        FilaAjuste(
            titulo = "Tamaño del texto",
            valor = Accesibilidad.tamanoLetra,
            onClick = { mostrarTamanoTexto = true }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ---------- Accesibilidad (acordeón) ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .background(
                    if (accesibilidadExpandida) VerdeClaroFondo else CelesteFondo,
                    RoundedCornerShape(14.dp)
                )
                .clickable { accesibilidadExpandida = !accesibilidadExpandida }
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(VerdeIconoFondo, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Accessibility,
                        contentDescription = null,
                        tint = VerdeTitulo,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(tr("Accesibilidad"), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AzulTexto)
                    if (accesibilidadExpandida) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            tr("Adaptá la aplicación para usarla de la forma más cómoda para vos."),
                            fontSize = 12.sp,
                            color = GrisSubtitulo,
                            lineHeight = 16.sp
                        )
                    }
                }

                Icon(
                    imageVector = if (accesibilidadExpandida) Icons.Filled.ExpandLess else Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = GrisSubtitulo,
                    modifier = Modifier.size(if (accesibilidadExpandida) 22.dp else 14.dp)
                )
            }

            if (accesibilidadExpandida) {
                Spacer(modifier = Modifier.height(14.dp))

                FilaAccesibilidad(
                    icono = Icons.Filled.Contrast,
                    titulo = "Alto contraste",
                    descripcion = "Aumenta el contraste de colores\npara una mejor visualización.",
                    activo = Accesibilidad.altoContraste,
                    onCambio = { Accesibilidad.cambiarAltoContraste(contexto, it) }
                )
                HorizontalDivider(color = GrisDivider)

                FilaAccesibilidad(
                    icono = Icons.Filled.PanTool,
                    titulo = "Botones más grandes",
                    descripcion = "Aumenta el tamaño de los botones\npara facilitar su uso.",
                    activo = Accesibilidad.botonesGrandes,
                    onCambio = { Accesibilidad.cambiarBotonesGrandes(contexto, it) }
                )
                HorizontalDivider(color = GrisDivider)

                FilaAccesibilidad(
                    icono = Icons.Filled.PlayArrow,
                    titulo = "Reducir animaciones",
                    descripcion = "Disminuye los movimientos\ny transiciones de la aplicación.",
                    activo = Accesibilidad.reducirAnimaciones,
                    onCambio = { Accesibilidad.cambiarReducirAnimaciones(contexto, it) }
                )
                HorizontalDivider(color = GrisDivider)

                // TalkBack se activa desde el teléfono: abrimos esa pantalla
                FilaAccesibilidadAccion(
                    icono = Icons.Filled.RecordVoiceOver,
                    titulo = "Lectura de pantalla",
                    descripcion = "Abre los ajustes de TalkBack\ndel teléfono para activarlo.",
                    onClick = {
                        contexto.startActivity(
                            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                )
                HorizontalDivider(color = GrisDivider)

                FilaAccesibilidad(
                    icono = Icons.Filled.Notifications,
                    titulo = "Sonidos y vibración",
                    descripcion = "Una vibración corta al tocar\nconfirma cada acción.",
                    activo = Accesibilidad.sonidosYVibracion,
                    onCambio = { Accesibilidad.cambiarSonidosYVibracion(contexto, it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onEliminarCuenta,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RojoBoton)
        ) {
            Text(tr("Eliminar cuenta"), fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun FilaAjuste(
    titulo: String,
    valor: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CelesteFondo, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            tr(titulo),
            fontSize = 16.sp,
            color = Color(0xFF1A1A1A).enTema(),
            modifier = Modifier.weight(1f)
        )
        if (valor.isNotEmpty()) {
            Text(
                tr(valor),
                fontSize = 15.sp,
                color = GrisSubtitulo
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = GrisSubtitulo,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun FilaAccesibilidad(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    activo: Boolean,
    onCambio: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(VerdeIconoFondo, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icono, contentDescription = null, tint = VerdeTitulo, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(tr(titulo), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AzulTexto)
            Spacer(modifier = Modifier.height(2.dp))
            Text(tr(descripcion), fontSize = 12.sp, color = GrisSubtitulo, lineHeight = 16.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = activo,
            onCheckedChange = onCambio,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VerdeTitulo
            )
        )
    }
}

@Composable
private fun FilaAccesibilidadAccion(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(VerdeIconoFondo, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icono, contentDescription = null, tint = VerdeTitulo, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(tr(titulo), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AzulTexto)
            Spacer(modifier = Modifier.height(2.dp))
            Text(tr(descripcion), fontSize = 12.sp, color = GrisSubtitulo, lineHeight = 16.sp)
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = GrisSubtitulo,
            modifier = Modifier.size(14.dp)
        )
    }
}