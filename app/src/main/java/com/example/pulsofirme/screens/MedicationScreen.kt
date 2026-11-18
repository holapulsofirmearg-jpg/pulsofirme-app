package com.example.pulsofirme.screens

import com.example.pulsofirme.ui.theme.enTema

import android.Manifest
import androidx.compose.ui.semantics.Role
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import android.widget.Toast
import com.example.pulsofirme.UserData
import com.example.pulsofirme.alarmas.AlarmaScheduler

private val Verde: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
private val BordeGris: Color
    @Composable get() = Color(0xFFD8DED9).enTema()
data class DiaSemana(
    val letra: String,
    val nombre: String
)

private val diasSemana = listOf(
    DiaSemana("L", "lunes"),
    DiaSemana("M", "martes"),
    DiaSemana("M", "miércoles"),
    DiaSemana("J", "jueves"),
    DiaSemana("V", "viernes"),
    DiaSemana("S", "sábado"),
    DiaSemana("D", "domingo")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationScreen(
    onBack: () -> Unit,

    // DEVUELVE LOS DATOS AL NAVGRAPH (como lo tenías antes)
    onSave: (
        horarios: List<String>,
        diasSeleccionados: Set<Int>,
        activo: Boolean
    ) -> Unit,

    onDelete: () -> Unit
) {

    val context = LocalContext.current

    // Carga lo que ya estaba guardado (aunque se haya cerrado la app)
    var horarios by remember {
        mutableStateOf(
            UserData.horariosMedicacion.ifEmpty {
                AlarmaScheduler.horariosGuardados(context)
            }
        )
    }

    var diasSeleccionados by remember {
        mutableStateOf(
            AlarmaScheduler.diasGuardados(context) ?: setOf(0, 1, 2, 3, 4)
        )
    }

    var activo by remember {
        mutableStateOf(AlarmaScheduler.activoGuardado(context))
    }

    var mostrarSelectorHora by remember { mutableStateOf(false) }
    var mostrarAvisoPantallaCompleta by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(is24Hour = true)

    // Avisa al NavGraph con los datos guardados
    fun avisarGuardado() {
        onSave(horarios, diasSeleccionados, activo)
    }

    // =========================================================
    // GUARDAR Y PROGRAMAR LA ALARMA
    // =========================================================

    fun guardarTodo() {
        UserData.horariosMedicacion = horarios

        AlarmaScheduler.guardarYProgramar(
            context,
            horarios,
            diasSeleccionados,
            activo
        )

        // Android 14+: hay que permitir que la alarma salga a pantalla completa
        val faltaPantallaCompleta =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
                    activo &&
                    !context.getSystemService(NotificationManager::class.java)
                        .canUseFullScreenIntent()

        if (faltaPantallaCompleta) {
            mostrarAvisoPantallaCompleta = true
        } else {
            avisarGuardado()
        }
    }

    // Prende o apaga el recordatorio en el momento, sin tocar "Guardar"
    fun aplicarInterruptor(nuevo: Boolean) {
        if (!nuevo) {
            AlarmaScheduler.desactivar(context)
            Toast.makeText(context, "Recordatorio desactivado", Toast.LENGTH_SHORT).show()
        } else if (horarios.isNotEmpty() && diasSeleccionados.isNotEmpty()) {
            UserData.horariosMedicacion = horarios
            AlarmaScheduler.guardarYProgramar(context, horarios, diasSeleccionados, true)
            Toast.makeText(context, "Recordatorio activado", Toast.LENGTH_SHORT).show()
        } else {
            activo = false
            Toast.makeText(context, "Agregá al menos un horario y un día para activarlo", Toast.LENGTH_SHORT).show()
        }
    }

    // Al prender el interruptor en Android 13+, primero pide permiso de notificaciones
    val pedirPermisoAlActivar = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        aplicarInterruptor(true)
    }

    // Pide el permiso de notificaciones (Android 13+) y después guarda
    val pedirPermisoNotificaciones = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        guardarTodo()
    }

    // =========================================================
    // AVISO: PERMITIR ALARMA A PANTALLA COMPLETA
    // =========================================================

    if (mostrarAvisoPantallaCompleta) {
        AlertDialog(
            onDismissRequest = {
                mostrarAvisoPantallaCompleta = false
                avisarGuardado()
            },
            title = {
                Text("Activá la alarma en pantalla completa")
            },
            text = {
                Text(
                    "Para que la alarma aparezca grande en la pantalla (aunque el celular esté bloqueado), " +
                            "activá el permiso de PulsoFirme en los ajustes. Si no lo activás, igual vas a recibir " +
                            "la notificación con sonido."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    mostrarAvisoPantallaCompleta = false
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT,
                            Uri.parse("package:${context.packageName}")
                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }
                    avisarGuardado()
                }) {
                    Text("Ir a ajustes", color = Verde)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarAvisoPantallaCompleta = false
                    avisarGuardado()
                }) {
                    Text("Ahora no", color = Color(0xFF6B7280).enTema())
                }
            }
        )
    }

    // =========================================================
    // SELECTOR DE HORA
    // =========================================================

    if (mostrarSelectorHora) {
        AlertDialog(
            onDismissRequest = { mostrarSelectorHora = false },
            confirmButton = {
                TextButton(onClick = {
                    val hora = timePickerState.hour.toString().padStart(2, '0')
                    val minuto = timePickerState.minute.toString().padStart(2, '0')
                    val nuevoHorario = "$hora:$minuto hs"

                    // EVITA AGREGAR DOS VECES EL MISMO HORARIO
                    if (!horarios.contains(nuevoHorario)) {
                        horarios = horarios + nuevoHorario
                    }
                    mostrarSelectorHora = false
                }) {
                    Text(text = "Aceptar", color = Verde)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarSelectorHora = false }) {
                    Text(text = "Cancelar", color = Color(0xFF6B7280).enTema())
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    // =========================================================
    // PANTALLA
    // =========================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        // ENCABEZADO
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Verde
                )
            }
            Text(
                text = "Medicación",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Verde
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color(0xFFECECEC).enTema())
        Spacer(modifier = Modifier.height(24.dp))

        // HORARIOS
        Text(text = "Horarios", fontSize = 14.sp, color = Color(0xFF6B7280).enTema())
        Spacer(modifier = Modifier.height(10.dp))

        if (horarios.isEmpty()) {
            Text(
                text = "Todavía no agregaste ningún horario.",
                fontSize = 14.sp,
                color = Color(0xFF9CA3AF).enTema(),
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        horarios.forEachIndexed { index, horario ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .border(1.dp, BordeGris, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = null,
                    tint = Verde,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = horario,
                    fontSize = 15.sp,
                    color = Color(0xFF1A1A1A).enTema(),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Quitar horario",
                    tint = Color(0xFF9CA3AF).enTema(),
                    modifier = Modifier
                        .size(56.dp)
                        .clickable(role = Role.Button) {
                            horarios = horarios.toMutableList().also { it.removeAt(index) }
                        }
                        .padding(19.dp)
                )
            }
        }

        // AGREGAR HORARIO
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 4.dp)
                .heightIn(min = 56.dp)
                .clickable(role = Role.Button) { mostrarSelectorHora = true }
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = Verde,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Agregar horario",
                fontSize = 14.sp,
                color = Verde,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // DÍAS
        Text(text = "Días", fontSize = 14.sp, color = Color(0xFF6B7280).enTema())
        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            diasSemana.forEachIndexed { index, dia ->
                val seleccionado = diasSeleccionados.contains(index)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = if (index < diasSemana.size - 1) 4.dp else 0.dp)
                        .heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (seleccionado) Verde else Color.White.enTema())
                        .border(
                            width = 1.dp,
                            color = if (seleccionado) Verde else BordeGris,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable(role = Role.Checkbox) {
                            diasSeleccionados =
                                if (seleccionado) diasSeleccionados - index
                                else diasSeleccionados + index
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dia.letra,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (seleccionado) Color.White else Color(0xFF374151).enTema()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ACTIVAR / DESACTIVAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BordeGris, RoundedCornerShape(14.dp))
                .heightIn(min = 56.dp)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Activo", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text(
                    text = if (activo) "Vas a recibir este recordatorio" else "Recordatorio apagado",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280).enTema()
                )
            }
            Switch(
                checked = activo,
                onCheckedChange = { nuevo ->
                    activo = nuevo
                    val faltaPermiso = nuevo &&
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                    if (faltaPermiso) {
                        pedirPermisoAlActivar.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        aplicarInterruptor(nuevo)
                    }
                },
                colors = SwitchDefaults.colors(checkedTrackColor = Verde)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // GUARDAR
        Button(
            onClick = {
                val faltaPermiso =
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED

                if (faltaPermiso) {
                    pedirPermisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    guardarTodo()
                }
            },
            enabled = horarios.isNotEmpty() && diasSeleccionados.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Verde)
        ) {
            Text(text = "Guardar cambios", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ELIMINAR
        OutlinedButton(
            onClick = {
                UserData.horariosMedicacion = emptyList()
                AlarmaScheduler.eliminarTodo(context)
                onDelete()
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD32F2F).enTema())
        ) {
            Text(text = "Eliminar recordatorio", fontSize = 16.sp, color = Color(0xFFD32F2F).enTema())
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}