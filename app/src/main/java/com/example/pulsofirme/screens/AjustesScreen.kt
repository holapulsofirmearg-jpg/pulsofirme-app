package com.example.pulsofirme.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.Accesibilidad
import com.example.pulsofirme.UserData
import com.example.pulsofirme.animarTamano
import com.example.pulsofirme.areaTactil
import com.example.pulsofirme.alarmas.AlarmaScheduler
import com.example.pulsofirme.alarmas.RecordatorioEjercicio
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private val VerdePr = Color(0xFF1B7A4B)
private val VerdeOscuroPr = Color(0xFF1B4332)
private val GrisPr = Color(0xFF6B7280)
private val NegroPr = Color(0xFF1A1A1A)
private val RojoEliminar = Color(0xFFC63927)
private val VerdeClaroFondoPr = Color(0xFFEFF6F1)
private val VerdeIconoFondoPr = Color(0xFFDFF3E4)

@Composable
fun AjustesScreen(
    onBack: () -> Unit,
    onCuentaEliminada: () -> Unit = {},
    onExportarDatos: () -> Unit = {}
) {
    val contexto = LocalContext.current

    // Switches de notificaciones (ahora se guardan)
    val prefsAjustes = remember {
        contexto.getSharedPreferences("pulsofirme_ajustes", Context.MODE_PRIVATE)
    }
    // Medicación: refleja el estado real de la alarma (se carga en la pantalla Medicación)
    var medicacion by remember { mutableStateOf(AlarmaScheduler.activoGuardado(contexto)) }
    // Ejercicio: mismo dato que "Configurar alertas" (Firestore: alertaEjercicioPendiente)
    var ejercicio by remember { mutableStateOf(prefsAjustes.getBoolean("notif_ejercicio", true)) }
    var alertas by remember { mutableStateOf(prefsAjustes.getBoolean("notif_alertas", true)) }

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        FirebaseFirestore.getInstance().collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                ejercicio = doc.getBoolean("alertaEjercicioPendiente") ?: true
            }
    }

    // Estado para controlar el diálogo de confirmación
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    // Diálogo de exportar datos
    var mostrarDialogoExportar by remember { mutableStateOf(false) }

    // Acordeón de accesibilidad
    var accesibilidadExpandida by remember { mutableStateOf(false) }

    // --- DIÁLOGO DE CONFIRMACIÓN ELIMINAR CUENTA ---
    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "¿Eliminar cuenta?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NegroPr
                )
            },
            text = {
                Text(
                    text = "Esta acción borrará todos tus datos e historial de forma permanente. ¿Estás seguro de que querés continuar?",
                    fontSize = 16.sp,
                    color = GrisPr,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoEliminar = false
                        // Sigue con la contraseña: el borrado real lo hace NavGraph
                        onCuentaEliminada()
                    }
                ) {
                    Text(
                        text = "Eliminar",
                        color = RojoEliminar,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text("Cancelar", color = GrisPr, fontSize = 17.sp)
                }
            }
        )
    }

    // --- DIÁLOGO DE EXPORTAR DATOS ---
    if (mostrarDialogoExportar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoExportar = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(VerdeIconoFondoPr, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Download,
                        contentDescription = null,
                        tint = VerdePr,
                        modifier = Modifier.size(26.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Exportar mis datos",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdeOscuroPr
                )
            },
            text = {
                Text(
                    text = "Vamos a generar un PDF con tu resumen semanal: ejercicios realizados, gráfico por día, evaluación QuickDASH, medicación y notas.",
                    fontSize = 16.sp,
                    color = GrisPr,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoExportar = false
                        onExportarDatos()
                    }
                ) {
                    Text(
                        text = "Generar PDF",
                        color = VerdePr,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoExportar = false }) {
                    Text("Cancelar", color = GrisPr, fontSize = 17.sp)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(48.dp))

        // Encabezado
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = VerdeOscuroPr
                )
            }
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Ajustes",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeOscuroPr
            )
        }

        Spacer(Modifier.height(24.dp))

        // SECCIÓN GENERAL
        TituloSeccion("General")
        FilaAjusteConValor(label = "Tema", valor = "Claro") { }

        Spacer(Modifier.height(20.dp))

        // SECCIÓN NOTIFICACIONES
        TituloSeccion("Notificaciones")
        FilaAjusteSwitch(
            label = "Recordatorios de medicación",
            checked = medicacion,
            onCheckedChange = { quiere ->
                val horarios = AlarmaScheduler.horariosGuardados(contexto)
                if (quiere && horarios.isEmpty()) {
                    // No hay nada que activar: primero hay que cargar horarios
                    Toast.makeText(
                        contexto,
                        "Primero cargá tus horarios en Cuenta > Medicación.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    medicacion = quiere
                    // Conserva horarios y días; solo prende o apaga las alarmas
                    AlarmaScheduler.guardarYProgramar(
                        contexto,
                        horarios,
                        AlarmaScheduler.diasGuardados(contexto) ?: setOf(0, 1, 2, 3, 4),
                        quiere
                    )
                    prefsAjustes.edit().putBoolean("notif_medicacion", quiere).apply()
                }
            }
        )
        FilaAjusteSwitch(
            label = "Recordatorios de ejercicio",
            checked = ejercicio,
            onCheckedChange = { quiere ->
                ejercicio = quiere
                prefsAjustes.edit().putBoolean("notif_ejercicio", quiere).apply()

                // Alarma real del recordatorio diario
                if (quiere) RecordatorioEjercicio.programar(contexto)
                else RecordatorioEjercicio.cancelar(contexto)

                // Mismo valor que usa "Configurar alertas", para que no se contradigan
                FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
                    FirebaseFirestore.getInstance().collection("usuarios").document(uid)
                        .update("alertaEjercicioPendiente", quiere)
                }
            }
        )
        FilaAjusteSwitch(
            label = "Alertas del dispositivo",
            checked = alertas,
            onCheckedChange = {
                alertas = it
                prefsAjustes.edit().putBoolean("notif_alertas", it).apply()
            }
        )

        Spacer(Modifier.height(20.dp))

        // SECCIÓN ACCESIBILIDAD
        TituloSeccion("Accesibilidad")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animarTamano()
                .background(
                    if (accesibilidadExpandida) VerdeClaroFondoPr else Color(0xFFF5F5F5),
                    RoundedCornerShape(14.dp)
                )
                .clickable { accesibilidadExpandida = !accesibilidadExpandida }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.areaTactil(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(VerdeIconoFondoPr, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Accessibility,
                        contentDescription = null,
                        tint = VerdePr,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    "Accesibilidad",
                    fontSize = 16.sp,
                    color = NegroPr,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = if (accesibilidadExpandida) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (accesibilidadExpandida) "Cerrar" else "Abrir",
                    tint = GrisPr,
                    modifier = Modifier.size(24.dp)
                )
            }

            if (accesibilidadExpandida) {
                Spacer(Modifier.height(12.dp))

                FilaAccesibilidadPr(
                    icono = Icons.Filled.PanTool,
                    titulo = "Botones más grandes",
                    descripcion = "Todo lo que se toca mide 56 dp o más\npara acertar mejor con temblor.",
                    activo = Accesibilidad.botonesGrandes,
                    onCambio = { Accesibilidad.cambiarBotonesGrandes(contexto, it) }
                )
                HorizontalDivider(color = Color(0xFFE5E7EB))

                SelectorTamanoLetra(
                    seleccionado = Accesibilidad.tamanoLetra,
                    onSeleccionar = { Accesibilidad.cambiarTamanoLetra(contexto, it) }
                )
                HorizontalDivider(color = Color(0xFFE5E7EB))

                FilaAccesibilidadPr(
                    icono = Icons.Filled.Contrast,
                    titulo = "Alto contraste",
                    descripcion = "Textos más oscuros y fondos\nmás limpios para leer mejor.",
                    activo = Accesibilidad.altoContraste,
                    onCambio = { Accesibilidad.cambiarAltoContraste(contexto, it) }
                )
                HorizontalDivider(color = Color(0xFFE5E7EB))

                FilaAccesibilidadPr(
                    icono = Icons.Filled.PlayArrow,
                    titulo = "Reducir animaciones",
                    descripcion = "Disminuye los movimientos\ny transiciones de la aplicación.",
                    activo = Accesibilidad.reducirAnimaciones,
                    onCambio = { Accesibilidad.cambiarReducirAnimaciones(contexto, it) }
                )
                HorizontalDivider(color = Color(0xFFE5E7EB))

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
                HorizontalDivider(color = Color(0xFFE5E7EB))

                FilaAccesibilidadPr(
                    icono = Icons.Filled.Notifications,
                    titulo = "Sonidos y vibración",
                    descripcion = "Una vibración corta al tocar\nconfirma cada acción.",
                    activo = Accesibilidad.sonidosYVibracion,
                    onCambio = { Accesibilidad.cambiarSonidosYVibracion(contexto, it) }
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // SECCIÓN DATOS
        TituloSeccion("Datos")
        FilaAjusteConValor(label = "Exportar mis datos", valor = "") {
            mostrarDialogoExportar = true
        }

        Spacer(Modifier.height(40.dp))

        // BOTÓN ELIMINAR CUENTA
        Button(
            onClick = { mostrarDialogoEliminar = true },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp),
            shape = RoundedCornerShape(32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RojoEliminar)
        ) {
            Text(
                text = "Eliminar cuenta",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(Modifier.height(30.dp))
    }
}

@Composable
private fun TituloSeccion(texto: String) {
    Text(
        text = texto,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        color = GrisPr,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun FilaAjusteConValor(label: String, valor: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .areaTactil()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = NegroPr,
            modifier = Modifier.weight(1f)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (valor.isNotEmpty()) {
                Text(text = valor, fontSize = 15.sp, color = GrisPr)
                Spacer(Modifier.width(4.dp))
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = GrisPr,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun FilaAjusteSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    // Toda la fila es tocable (no hace falta apuntarle al switch chiquito)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .areaTactil()
            .toggleable(
                value = checked,
                role = Role.Switch,
                onValueChange = onCheckedChange
            )
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = NegroPr,
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        )
        Switch(
            checked = checked,
            onCheckedChange = null, // lo maneja la fila entera
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VerdePr,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFD1D1D6)
            )
        )
    }
}

@Composable
private fun FilaAccesibilidadPr(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    activo: Boolean,
    onCambio: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .areaTactil()
            .toggleable(
                value = activo,
                role = Role.Switch,
                onValueChange = onCambio
            )
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(VerdeIconoFondoPr, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icono, contentDescription = null, tint = VerdePr, modifier = Modifier.size(16.dp))
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NegroPr)
            Spacer(Modifier.height(2.dp))
            Text(descripcion, fontSize = 13.sp, color = GrisPr, lineHeight = 17.sp)
        }

        Spacer(Modifier.width(8.dp))

        Switch(
            checked = activo,
            onCheckedChange = null, // lo maneja la fila entera
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VerdePr
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
            .areaTactil()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(VerdeIconoFondoPr, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icono, contentDescription = null, tint = VerdePr, modifier = Modifier.size(16.dp))
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NegroPr)
            Spacer(Modifier.height(2.dp))
            Text(descripcion, fontSize = 13.sp, color = GrisPr, lineHeight = 17.sp)
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = GrisPr,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun SelectorTamanoLetra(
    seleccionado: String,
    onSeleccionar: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(VerdeIconoFondoPr, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aa",
                    color = VerdePr,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Tamaño de letra",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NegroPr
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Elegí cómo querés ver los textos.",
                    fontSize = 13.sp,
                    color = GrisPr,
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Accesibilidad.niveles.forEach { opcion ->
                val elegido = seleccionado == opcion

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .areaTactil()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (elegido) VerdePr else Color.White)
                        .selectable(
                            selected = elegido,
                            role = Role.RadioButton,
                            onClick = { onSeleccionar(opcion) }
                        )
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = opcion,
                        fontSize = 13.sp,
                        fontWeight = if (elegido) FontWeight.Bold else FontWeight.Medium,
                        color = if (elegido) Color.White else VerdePr,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                }
            }
        }
    }
}