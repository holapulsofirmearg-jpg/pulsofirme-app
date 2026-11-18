package com.example.pulsofirme.screens

import androidx.compose.runtime.saveable.rememberSaveable

import com.example.pulsofirme.ui.theme.enTema

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.DialogoLegal
import com.example.pulsofirme.ResultadoBorrado
import com.example.pulsofirme.TextosLegales
import com.example.pulsofirme.UserData
import com.example.pulsofirme.abrirPdf
import com.example.pulsofirme.areaTactil
import com.example.pulsofirme.compartirPdf
import com.example.pulsofirme.eliminarCuentaCompleta
import com.example.pulsofirme.generarResumenSemanal
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.semantics.Role
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

private val VerdePv: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
private val VerdeOscuroPv: Color
    @Composable get() = Color(0xFF1B4332).enTema()
private val VerdeSuavePv: Color
    @Composable get() = Color(0xFFEAF5EC).enTema()
private val RojoPv: Color
    @Composable get() = Color(0xFFC0392B).enTema()
private val BordePv: Color
    @Composable get() = Color(0xFFE5E7EB).enTema()
private val GrisPv: Color
    @Composable get() = Color(0xFF6B7280).enTema()
private val NegroPv: Color
    @Composable get() = Color(0xFF1A1A1A).enTema()
// Permisos que el paciente le da a cada profesional.
// Las claves son las mismas que lee la app del profesional.
private val permisosProfesional = listOf(
    "progreso" to "Progreso y ejercicios",
    "quickdash" to "Evaluación QuickDASH",
    "medicacion" to "Medicación",
    "datosMedicos" to "Diagnóstico y datos médicos",
    "ubicacion" to "Ubicación",
    "reportes" to "Reportes en PDF"
)

private data class ProfesionalVinculado(
    val uid: String,
    val nombre: String,
    val detalle: String
)

@Composable
fun PrivacyScreen(
    onBack: () -> Unit,
    onDatosEliminados: () -> Unit = {}
) {
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()

    var mostrarPolitica by rememberSaveable { mutableStateOf(false) }
    var mostrarTerminos by rememberSaveable { mutableStateOf(false) }
    var mostrarDescarga by rememberSaveable { mutableStateOf(false) }
    var mostrarEliminar by rememberSaveable { mutableStateOf(false) }
    var generando by remember { mutableStateOf(false) }
    var errorPdf by rememberSaveable { mutableStateOf(false) }
    var archivoPdf by remember { mutableStateOf<File?>(null) }

    // Estado del borrado real
    var contrasenaEliminar by rememberSaveable { mutableStateOf("") }
    var eliminando by remember { mutableStateOf(false) }
    var errorEliminar by rememberSaveable { mutableStateOf("") }

    // ---------- Datos reales de permisos (Firestore) ----------
    val db = FirebaseFirestore.getInstance()
    val miUid = FirebaseAuth.getInstance().currentUser?.uid
    var profesionales by remember { mutableStateOf<List<ProfesionalVinculado>>(emptyList()) }
    // { profUid: { "progreso": true, ... } }
    var permisosPro by remember { mutableStateOf<Map<String, Map<String, Boolean>>>(emptyMap()) }
    var cargandoPro by remember { mutableStateOf(true) }
    var abierto by remember { mutableStateOf<String?>(null) }

    fun avisarError() {
        Avisos.mostrar("No se pudo guardar el cambio. Revisá tu conexión.")
    }

    LaunchedEffect(miUid) {
        if (miUid == null) { cargandoPro = false; return@LaunchedEffect }
        db.collection("usuarios").document(miUid).get()
            .addOnSuccessListener { doc ->
                // Permisos del tutor (si no existen, quedan activados)
                UserData.compartirProgreso = doc.getBoolean("permisosTutor.progreso") ?: true
                UserData.compartirMedicacion = doc.getBoolean("permisosTutor.medicacion") ?: true
                UserData.compartirUbicacion = doc.getBoolean("permisosTutor.ubicacion") ?: false

                // Permisos guardados de cada profesional
                val mapa = doc.get("permisosProfesionales") as? Map<*, *> ?: emptyMap<Any, Any>()
                permisosPro = mapa.mapNotNull { (k, v) ->
                    val uid = k as? String ?: return@mapNotNull null
                    val permisos = (v as? Map<*, *>)?.mapNotNull { (pk, pv) ->
                        val clave = pk as? String ?: return@mapNotNull null
                        val valor = pv as? Boolean ?: return@mapNotNull null
                        clave to valor
                    }?.toMap() ?: emptyMap()
                    uid to permisos
                }.toMap()

                // Profesionales vinculados
                val uids = (doc.get("profesionalesVinculados") as? List<*>)
                    ?.filterIsInstance<String>()
                    ?.filter { it.isNotBlank() }
                    .orEmpty()

                if (uids.isEmpty()) {
                    cargandoPro = false
                    return@addOnSuccessListener
                }

                // Primero se muestran con nombre genérico; después se completa el nombre
                profesionales = uids.mapIndexed { i, uid ->
                    ProfesionalVinculado(uid, "Profesional ${i + 1}", "Vinculado a tu cuenta")
                }
                cargandoPro = false

                uids.forEach { uid ->
                    db.collection("usuarios").document(uid).get()
                        .addOnSuccessListener { pro ->
                            val nombre = listOfNotNull(pro.getString("nombre"), pro.getString("apellido"))
                                .filter { it.isNotBlank() }
                                .joinToString(" ")
                            val detalle = pro.getString("especialidad")
                                ?: pro.getString("profesion")
                                ?: ""
                            if (nombre.isNotBlank()) {
                                profesionales = profesionales.map {
                                    if (it.uid == uid) it.copy(
                                        nombre = nombre,
                                        detalle = detalle.ifBlank { "Profesional de salud" }
                                    ) else it
                                }
                            }
                        }
                    // Si las reglas no dejan leer el nombre, queda "Profesional N"
                }
            }
            .addOnFailureListener { cargandoPro = false }
    }

    fun cambiarPermisoTutor(clave: String, valor: Boolean, aplicar: (Boolean) -> Unit) {
        aplicar(valor)
        val uid = miUid ?: return
        db.collection("usuarios").document(uid)
            .update("permisosTutor.$clave", valor)
            .addOnFailureListener {
                aplicar(!valor)
                avisarError()
            }
    }

    fun cambiarPermisoPro(profUid: String, clave: String, valor: Boolean) {
        val anterior = permisosPro
        val actuales = permisosPro[profUid].orEmpty()
        permisosPro = permisosPro + (profUid to (actuales + (clave to valor)))
        val uid = miUid ?: return
        db.collection("usuarios").document(uid)
            .update("permisosProfesionales.$profUid.$clave", valor)
            .addOnFailureListener {
                permisosPro = anterior
                avisarError()
            }
    }

    if (mostrarPolitica) {
        DialogoLegal(
            titulo = "Política de privacidad",
            texto = TextosLegales.politica,
            conLogo = true,
            onCerrar = { mostrarPolitica = false }
        )
    }

    if (mostrarTerminos) {
        DialogoLegal(
            titulo = "Términos y condiciones",
            texto = TextosLegales.terminos,
            onCerrar = { mostrarTerminos = false }
        )
    }

    if (mostrarDescarga) {
        val listo = archivoPdf != null
        AlertDialog(
            onDismissRequest = { mostrarDescarga = false },
            containerColor = Color.White.enTema(),
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    if (listo) "Tu resumen está listo" else "Resumen semanal en PDF",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdeOscuroPv
                )
            },
            text = {
                Text(
                    text = when {
                        errorPdf -> "No pudimos generar el archivo. Probá de nuevo en unos segundos."
                        listo -> "Incluye tus ejercicios, cómo te sentiste cada día, tus tomas de medicación y tu autoevaluación. Podés abrirlo o compartirlo con tu médico."
                        else -> "Vamos a armar un PDF con tus últimos 7 días: ejercicios, cómo te sentiste, tomas de medicación y tu autoevaluación QuickDASH."
                    },
                    fontSize = 14.sp,
                    color = GrisPv,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                if (listo) {
                    TextButton(onClick = {
                        archivoPdf?.let { abrirPdf(contexto, it) }
                    }) {
                        Text("Abrir", color = VerdePv, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                } else {
                    TextButton(
                        enabled = !generando,
                        onClick = {
                            generando = true
                            errorPdf = false
                            generarResumenSemanal(contexto) { archivo ->
                                generando = false
                                if (archivo == null) {
                                    errorPdf = true
                                } else {
                                    archivoPdf = archivo
                                }
                            }
                        }
                    ) {
                        Text(
                            if (generando) "Generando..." else "Generar PDF",
                            color = VerdePv,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            },
            dismissButton = {
                if (listo) {
                    TextButton(onClick = {
                        archivoPdf?.let { compartirPdf(contexto, it) }
                    }) {
                        Text("Compartir", color = GrisPv, fontSize = 15.sp)
                    }
                } else {
                    TextButton(onClick = { mostrarDescarga = false }) {
                        Text("Cancelar", color = GrisPv, fontSize = 15.sp)
                    }
                }
            }
        )
    }

    // ---------- ELIMINAR MIS DATOS (borrado real) ----------
    if (mostrarEliminar) {
        AlertDialog(
            onDismissRequest = {
                if (!eliminando) {
                    mostrarEliminar = false
                    contrasenaEliminar = ""
                    errorEliminar = ""
                }
            },
            containerColor = Color.White.enTema(),
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    "¿Eliminar todos tus datos?",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = RojoPv
                )
            },
            text = {
                Column {
                    Text(
                        text = "Se borran de forma permanente tu perfil, tus datos de salud, tus ejercicios, " +
                                "tus evaluaciones, tus tomas de medicación, cómo te sentiste cada día, " +
                                "tus vínculos con cuidadores y profesionales y tu cuenta de acceso. " +
                                "Esta acción no se puede deshacer.\n\nPara confirmar, ingresá tu contraseña.",
                        fontSize = 14.sp,
                        color = GrisPv,
                        lineHeight = 20.sp
                    )

                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = contrasenaEliminar,
                        onValueChange = { contrasenaEliminar = it },
                        enabled = !eliminando,
                        singleLine = true,
                        label = { Text("Tu contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorEliminar.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = errorEliminar,
                            fontSize = 13.sp,
                            color = RojoPv,
                            lineHeight = 18.sp
                        )
                    }

                    if (eliminando) {
                        Spacer(Modifier.height(12.dp))
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = RojoPv
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !eliminando && contrasenaEliminar.isNotBlank(),
                    onClick = {
                        eliminando = true
                        errorEliminar = ""
                        scope.launch {
                            when (val r = eliminarCuentaCompleta(contrasenaEliminar)) {
                                is ResultadoBorrado.Ok -> {
                                    UserData.borrarTodo()
                                    // También se borra lo guardado en este celular
                                    com.example.pulsofirme.alarmas.AlarmaScheduler.eliminarTodo(contexto)
                                    contexto.getSharedPreferences("tomas_medicacion", android.content.Context.MODE_PRIVATE)
                                        .edit().clear().apply()
                                    eliminando = false
                                    mostrarEliminar = false
                                    contrasenaEliminar = ""
                                    onDatosEliminados()
                                }
                                is ResultadoBorrado.ContrasenaIncorrecta -> {
                                    eliminando = false
                                    errorEliminar = "La contraseña no es correcta."
                                }
                                is ResultadoBorrado.SinConexion -> {
                                    eliminando = false
                                    errorEliminar = "Sin conexión. Verificá tu internet e intentá de nuevo."
                                }
                                is ResultadoBorrado.Fallo -> {
                                    eliminando = false
                                    errorEliminar = r.detalle
                                }
                            }
                        }
                    }
                ) {
                    Text(
                        if (eliminando) "Eliminando..." else "Eliminar",
                        color = RojoPv,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !eliminando,
                    onClick = {
                        mostrarEliminar = false
                        contrasenaEliminar = ""
                        errorEliminar = ""
                    }
                ) {
                    Text("Cancelar", color = GrisPv, fontSize = 16.sp)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.enTema())
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(48.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = VerdePv
                )
            }
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Privacidad y datos",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeOscuroPv
            )
        }

        Spacer(Modifier.height(28.dp))

        Text("Qué compartís con tu tutor", fontSize = 15.sp, color = GrisPv)

        Spacer(Modifier.height(10.dp))

        FilaPermiso(
            titulo = "Progreso y ejercicios",
            detalle = "Puede ver tu racha, tus ejercicios y cómo te sentís cada día.",
            activo = UserData.compartirProgreso,
            onCambio = { v -> cambiarPermisoTutor("progreso", v) { UserData.compartirProgreso = it } }
        )

        FilaPermiso(
            titulo = "Alertas de medicación",
            detalle = "Recibe un aviso si no registraste una toma.",
            activo = UserData.compartirMedicacion,
            onCambio = { v -> cambiarPermisoTutor("medicacion", v) { UserData.compartirMedicacion = it } }
        )

        FilaPermiso(
            titulo = "Ubicación",
            detalle = "Compartí dónde estás solo en caso de emergencia.",
            activo = UserData.compartirUbicacion,
            onCambio = { v -> cambiarPermisoTutor("ubicacion", v) { UserData.compartirUbicacion = it } }
        )

        Spacer(Modifier.height(24.dp))

        Text("Qué compartís con tus profesionales", fontSize = 15.sp, color = GrisPv)

        Spacer(Modifier.height(10.dp))

        when {
            cargandoPro -> Text(
                "Cargando…",
                fontSize = 14.sp,
                color = GrisPv,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            profesionales.isEmpty() -> Text(
                "Todavía no tenés profesionales vinculados. Cuando un profesional ingrese tu código, va a aparecer acá.",
                fontSize = 14.sp,
                color = GrisPv,
                lineHeight = 20.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            else -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                profesionales.forEach { pro ->
                    val permisos = permisosPro[pro.uid].orEmpty()
                    TarjetaProfesional(
                        profesional = pro,
                        abierta = abierto == pro.uid,
                        compartidos = permisosProfesional.count { (clave, _) -> permisos[clave] ?: true },
                        onTocar = { abierto = if (abierto == pro.uid) null else pro.uid }
                    ) {
                        permisosProfesional.forEach { (clave, titulo) ->
                            FilaPermiso(
                                titulo = titulo,
                                detalle = null,
                                activo = permisos[clave] ?: true,
                                onCambio = { cambiarPermisoPro(pro.uid, clave, it) }
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(VerdeSuavePv)
                .padding(16.dp)
        ) {
            Text(
                text = "Podés cambiar estos permisos cuando quieras. Si desactivás uno, tu tutor o profesional deja de ver esa información la próxima vez que abra la app.",
                fontSize = 13.sp,
                color = Color(0xFF374151).enTema(),
                lineHeight = 19.sp
            )
        }

        Spacer(Modifier.height(28.dp))

        Text("Mis datos", fontSize = 15.sp, color = GrisPv)

        Spacer(Modifier.height(6.dp))

        FilaEnlace("Descargar mis datos") {
            archivoPdf = null
            errorPdf = false
            mostrarDescarga = true
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BordePv)
        )

        FilaEnlace("Política de privacidad") { mostrarPolitica = true }

        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BordePv)
        )

        FilaEnlace("Términos y condiciones") { mostrarTerminos = true }

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = {
                errorEliminar = ""
                contrasenaEliminar = ""
                mostrarEliminar = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 58.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RojoPv)
        ) {
            Text("Eliminar mis datos", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(30.dp))
    }
}

@Composable
private fun FilaPermiso(
    titulo: String,
    detalle: String?,
    activo: Boolean,
    onCambio: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(text = titulo, fontSize = 17.sp, color = NegroPv)
            if (detalle != null) {
                Spacer(Modifier.height(2.dp))
                Text(text = detalle, fontSize = 13.sp, color = GrisPv, lineHeight = 18.sp)
            }
        }
        Spacer(Modifier.width(14.dp))
        Switch(
            checked = activo,
            onCheckedChange = onCambio,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VerdePv,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFC7C7C7).enTema(),
                uncheckedBorderColor = Color(0xFFC7C7C7).enTema()
            )
        )
    }
}

@Composable
private fun TarjetaProfesional(
    profesional: ProfesionalVinculado,
    abierta: Boolean,
    compartidos: Int,
    onTocar: () -> Unit,
    contenido: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, if (abierta) VerdePv else BordePv, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(role = Role.Button, onClickLabel = "Ver permisos") { onTocar() }
                .heightIn(min = 72.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(VerdeSuavePv),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.MedicalServices,
                    contentDescription = null,
                    tint = VerdePv,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    profesional.nombre,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NegroPv
                )
                Text(
                    "${profesional.detalle} · comparte $compartidos de ${permisosProfesional.size}",
                    fontSize = 13.sp,
                    color = GrisPv,
                    lineHeight = 18.sp
                )
            }
            Icon(
                if (abierta) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = Color(0xFF9CA3AF).enTema()
            )
        }

        if (abierta) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BordePv)
            )
            Column(Modifier.padding(horizontal = 16.dp)) {
                contenido()
            }
        }
    }
}

@Composable
private fun FilaEnlace(texto: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .areaTactil()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = texto,
            fontSize = 17.sp,
            color = NegroPv,

            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF9CA3AF).enTema()
        )
    }
}