package com.example.pulsofirme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.OutlinedButton
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.os.Build
import android.widget.Toast
import com.example.pulsofirme.AvisosTutor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeTitulo = Color(0xFF1B7A4B)
private val AzulTexto = Color(0xFF16324F)
private val GrisSubtitulo = Color(0xFF6B7280)
private val VerdeIconoFondo = Color(0xFFDFF3E4)
private val GrisBorde = Color(0xFFE5E7EB)

@Composable
fun AlertsConfigCaregiverScreen(
    onBack: () -> Unit,
    onGuardarCambios: (
        alertasMedicacion: Boolean,
        alertasDispositivo: Boolean,
        evaluacionQuickDash: Boolean,
        progreso: Boolean,
        alertasGenerales: Boolean
    ) -> Unit = { _, _, _, _, _ -> }
) {
    var alertasMedicacion by remember { mutableStateOf(true) }
    var alertasDispositivo by remember { mutableStateOf(true) }
    var evaluacionQuickDash by remember { mutableStateOf(true) }
    var progreso by remember { mutableStateOf(false) }
    var alertasGenerales by remember { mutableStateOf(true) }
    var guardando by remember { mutableStateOf(false) }

    val contexto = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    // Carga lo que el tutor ya había guardado
    LaunchedEffect(uid) {
        if (uid == null) return@LaunchedEffect
        db.collection("usuarios").document(uid).get().addOnSuccessListener { doc ->
            alertasMedicacion = doc.getBoolean("tutorAlertaMedicacion") ?: true
            alertasDispositivo = doc.getBoolean("tutorAlertaDispositivo") ?: true
            evaluacionQuickDash = doc.getBoolean("tutorAlertaQuickdash") ?: true
            progreso = doc.getBoolean("tutorAlertaProgreso") ?: false
            alertasGenerales = doc.getBoolean("tutorAlertaGenerales") ?: true
        }
    }

    // Android 13+: pide permiso para mostrar notificaciones
    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            AvisosTutor.programar(contexto)
            AvisosTutor.probarAhora(contexto)
        } else {
            Toast.makeText(
                contexto,
                "Sin permiso de notificaciones no te podemos avisar. Activalo en Ajustes del celular.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun probarAvisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !AvisosTutor.puedeNotificar(contexto)) {
            pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            AvisosTutor.programar(contexto)
            AvisosTutor.probarAhora(contexto)
            Toast.makeText(contexto, "Enviando un aviso de prueba…", Toast.LENGTH_SHORT).show()
        }
    }

    fun guardar() {
        if (uid == null) {
            Toast.makeText(contexto, "No se encontró la sesión", Toast.LENGTH_SHORT).show()
            return
        }
        guardando = true
        db.collection("usuarios").document(uid)
            .update(
                mapOf(
                    "tutorAlertaMedicacion" to alertasMedicacion,
                    "tutorAlertaDispositivo" to alertasDispositivo,
                    "tutorAlertaQuickdash" to evaluacionQuickDash,
                    "tutorAlertaProgreso" to progreso,
                    "tutorAlertaGenerales" to alertasGenerales
                )
            )
            .addOnSuccessListener {
                guardando = false
                AvisosTutor.programar(contexto)
                Toast.makeText(contexto, "Preferencias de alertas guardadas", Toast.LENGTH_SHORT).show()
                onGuardarCambios(
                    alertasMedicacion,
                    alertasDispositivo,
                    evaluacionQuickDash,
                    progreso,
                    alertasGenerales
                )
            }
            .addOnFailureListener {
                guardando = false
                Toast.makeText(contexto, "No se pudieron guardar. Revisá tu conexión.", Toast.LENGTH_SHORT).show()
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Configuración de alertas",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = AzulTexto
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Elegí qué alertas querés recibir del usuario asociado.",
            fontSize = 15.sp,
            color = GrisSubtitulo,
            lineHeight = 21.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        FilaAlertaConfig(
            icono = Icons.Filled.Medication,
            titulo = "Alertas de medicación",
            descripcion = "Si pasa 1 hora de su toma y no marcó \"Ya la tomé\".",
            activo = alertasMedicacion,
            onCambio = { alertasMedicacion = it }
        )

        Spacer(modifier = Modifier.height(14.dp))

        FilaAlertaConfig(
            icono = Icons.Filled.Watch,
            titulo = "Alertas del dispositivo",
            descripcion = "Próximamente: avisos si el guante presenta un problema.",
            activo = alertasDispositivo,
            onCambio = { alertasDispositivo = it }
        )

        Spacer(modifier = Modifier.height(14.dp))

        FilaAlertaConfig(
            icono = Icons.AutoMirrored.Filled.Assignment,
            titulo = "Evaluación QuickDASH",
            descripcion = "Avisame cuando complete su evaluación QuickDASH.",
            activo = evaluacionQuickDash,
            onCambio = { evaluacionQuickDash = it }
        )

        Spacer(modifier = Modifier.height(14.dp))

        FilaAlertaConfig(
            icono = Icons.AutoMirrored.Filled.TrendingUp,
            titulo = "Progreso",
            descripcion = "Avisame cuando complete ejercicios en el día.",
            activo = progreso,
            onCambio = { progreso = it }
        )

        Spacer(modifier = Modifier.height(14.dp))

        FilaAlertaConfig(
            icono = Icons.Filled.Notifications,
            titulo = "Alertas generales",
            descripcion = "Si a las 18 h no hizo ejercicio, si lleva 3 días sin hacerlo o si registró dolor.",
            activo = alertasGenerales,
            onCambio = { alertasGenerales = it }
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = { guardar() },
            enabled = !guardando,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeTitulo)
        ) {
            Text(
                if (guardando) "Guardando…" else "Guardar cambios",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Para probar en el momento (por ejemplo, en una presentación)
        OutlinedButton(
            onClick = { probarAvisos() },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, VerdeTitulo)
        ) {
            Icon(
                Icons.Filled.NotificationsActive,
                contentDescription = null,
                tint = VerdeTitulo,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Probar avisos ahora", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = VerdeTitulo)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(


            text = "La app revisa los datos del paciente más o menos cada 1 hora, aunque no la tengas abierta. " +
                    "Android puede demorar un poco el aviso para cuidar la batería.",
            fontSize = 13.sp,
            color = GrisSubtitulo,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}


@Composable
private fun FilaAlertaConfig(

    icono: ImageVector,
    titulo: String,
    descripcion: String,

    activo: Boolean,
    onCambio: (Boolean) -> Unit

) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GrisBorde, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(VerdeIconoFondo, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icono, contentDescription = null, tint = VerdeTitulo, modifier = Modifier.size(22.dp))
        }


        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AzulTexto)
            Spacer(modifier = Modifier.height(2.dp))
            Text(descripcion, fontSize = 13.sp, color = GrisSubtitulo, lineHeight = 18.sp)
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