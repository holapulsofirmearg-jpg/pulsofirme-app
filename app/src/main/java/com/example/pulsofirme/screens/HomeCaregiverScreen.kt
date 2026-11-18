package com.example.pulsofirme.screens

import androidx.compose.runtime.saveable.rememberSaveable

import com.example.pulsofirme.ui.theme.enTema

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.Role
import com.example.pulsofirme.R
import com.example.pulsofirme.SensacionDiaria
import com.example.pulsofirme.AvisosTutor
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import android.Manifest
import android.os.Build
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Date

private val VerdeCuidador: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
private val VerdeClaroCuidador: Color
    @Composable get() = Color(0xFFEFF6F1).enTema()
private val BordeGrisCuidador: Color
    @Composable get() = Color(0xFFE5E7EB).enTema()
private val CelesteVinculado: Color
    @Composable get() = Color(0xFF2196F3).enTema()
private val AzulTiempoTotal: Color
    @Composable get() = Color(0xFF1565C0).enTema()
private val MoradoSensacion: Color
    @Composable get() = Color(0xFF8E24AA).enTema()
private data class ItemMenuCuidador(
    val label: String,
    val icono: ImageVector,
    val ruta: String
)

// MENÚ TUTOR / CUIDADOR
private val itemsMenuInferiorCuidador = listOf(
    ItemMenuCuidador("Inicio", Icons.Filled.Home, "caregiver_home"),
    ItemMenuCuidador("Progreso", Icons.Filled.ShowChart, "caregiver_progress"),
    ItemMenuCuidador("Alertas", Icons.Filled.Notifications, "caregiver_alerts"),
    ItemMenuCuidador("Cuenta", Icons.Filled.Person, "caregiver_account")
)

// MENÚ PROFESIONAL
private val itemsMenuInferiorProfesional = listOf(
    ItemMenuCuidador("Inicio", Icons.Filled.Home, "caregiver_home"),
    ItemMenuCuidador("Pacientes", Icons.Filled.Groups, "caregiver_patients"),
    ItemMenuCuidador("Progreso", Icons.Filled.ShowChart, "caregiver_progress"),
    ItemMenuCuidador("Cuenta", Icons.Filled.Person, "caregiver_account")
)

@Composable
fun HomeCaregiverScreen(
    nombreUsuario: String,
    rutaActual: String,
    onNavigate: (String) -> Unit,
    onEjercicioClick: () -> Unit,
    onSensacionClick: (String) -> Unit = {},
    onEvolucionClick: () -> Unit = {},
    rolUsuario: String = "tutor"
) {

    val avisosNuevos = notificacionesDelUsuario().count { it.nueva }

    // Avisos reales al tutor: se programa la revisión en segundo plano
    // y, en Android 13+, se pide permiso para mostrar notificaciones.
    val contexto = LocalContext.current
    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido -> if (concedido) AvisosTutor.programar(contexto) }

    LaunchedEffect(rolUsuario) {
        if (rolUsuario != "tutor") return@LaunchedEffect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !AvisosTutor.puedeNotificar(contexto)) {
            pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            AvisosTutor.programar(contexto)
        }
    }

    // Datos reales del paciente vinculado (null = sin dato o sin permiso)
    val miUid = FirebaseAuth.getInstance().currentUser?.uid
    var nombrePaciente by remember { mutableStateOf<String?>(null) }
    var ejerciciosHoy by remember { mutableStateOf<Int?>(null) }
    var segundosHoy by remember { mutableStateOf<Long?>(null) }
    var rachaDias by remember { mutableStateOf<Int?>(null) }

    // Sensación de hoy: null = cargando; "" = no la registró hoy
    var sensacionHoy by remember { mutableStateOf<String?>(null) }
    // El paciente decide si comparte su progreso con el tutor (Privacidad y datos)
    var progresoCompartido by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(miUid) {
        if (miUid == null || rolUsuario == "profesional") return@LaunchedEffect
        val db = FirebaseFirestore.getInstance()
        db.collection("usuarios").document(miUid).get().addOnSuccessListener { yo ->
            val pacUid = yo.getString("pacienteVinculadoUid").orEmpty()
            if (pacUid.isBlank()) return@addOnSuccessListener
            val pacRef = db.collection("usuarios").document(pacUid)
            pacRef.get().addOnSuccessListener { pac ->
                nombrePaciente = listOfNotNull(pac.getString("nombre"), pac.getString("apellido"))
                    .filter { it.isNotBlank() }.joinToString(" ").ifBlank { null }

                progresoCompartido = pac.getBoolean("permisosTutor.progreso") ?: true
                if (!progresoCompartido) return@addOnSuccessListener

                sensacionHoy = SensacionDiaria.deHoy(
                    pac.getString("sensacionHoy"),
                    pac.getString("sensacionFecha")
                ) ?: ""

                val desde = Timestamp(Date(System.currentTimeMillis() - 60L * 24 * 60 * 60 * 1000))
                pacRef.collection("ejerciciosRealizados").whereGreaterThanOrEqualTo("fecha", desde).get()
                    .addOnSuccessListener { res ->
                        fun diaDe(ms: Long): Long {
                            val c = Calendar.getInstance().apply { timeInMillis = ms }
                            c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0)
                            c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0)
                            return c.timeInMillis
                        }
                        val hoy = diaDe(System.currentTimeMillis())
                        val dias = HashSet<Long>()
                        var n = 0
                        var seg = 0L
                        res.documents.forEach { d ->
                            val ms = d.getTimestamp("fecha")?.toDate()?.time ?: return@forEach
                            val dia = diaDe(ms)
                            dias += dia
                            if (dia == hoy) { n++; seg += d.getLong("segundos") ?: 0L }
                        }
                        ejerciciosHoy = n
                        segundosHoy = seg
                        // racha: días consecutivos hacia atrás (si hoy no hizo, cuenta desde ayer)
                        val unDia = 24L * 60 * 60 * 1000
                        var cursor = if (hoy in dias) hoy else hoy - unDia
                        var racha = 0
                        while (cursor in dias) { racha++; cursor -= unDia }
                        rachaDias = racha
                    }
            }
        }
    }

    // Texto que se muestra cuando el paciente no comparte su progreso
    val noCompartido = "No compartido"

    val itemsMenu = if (rolUsuario == "profesional") {
        itemsMenuInferiorProfesional
    } else {
        itemsMenuInferiorCuidador
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White.enTema(),
                tonalElevation = 6.dp
            ) {

                itemsMenu.forEach { item ->

                    NavigationBarItem(
                        selected = rutaActual == item.ruta,
                        onClick = {
                            onNavigate(item.ruta)
                        },
                        icon = {
                            Icon(
                                imageVector = item.icono,
                                contentDescription = tr(item.label),
                                modifier = Modifier.size(26.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tr(item.label),
                                fontSize = 11.sp,
                                fontWeight = if (rutaActual == item.ruta) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Medium
                                }
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = VerdeCuidador,
                            selectedTextColor = VerdeCuidador,
                            unselectedIconColor = Color(0xFF6B7280).enTema(),
                            unselectedTextColor = Color(0xFF6B7280).enTema(),
                            indicatorColor = VerdeClaroCuidador
                        )
                    )
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            // ---------------------------------------------------------
            // ENCABEZADO
            // ---------------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = tr("Panel de cuidador"),
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280).enTema()
                    )

                    Text(
                        text = trf("Hola, {0}", nombreUsuario),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A).enTema()
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = tr("Acá podés ver el progreso de tu paciente y acompañarlo en su camino"),
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280).enTema(),
                        lineHeight = 20.sp
                    )
                }

                // PULSIBOT
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(role = Role.Button, onClickLabel = tr("Abrir PulsiBot")) {
                            onNavigate("pulsibot")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pulsibot),
                        contentDescription = tr("PulsiBot"),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // NOTIFICACIONES
                Box {

                    IconButton(
                        onClick = {
                            onNavigate("notifications")
                        },
                        modifier = Modifier.size(56.dp)
                    ) {

                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = tr("Notificaciones"),
                            tint = Color(0xFF1A1A1A).enTema(),
                            modifier = Modifier.size(27.dp)
                        )
                    }

                    if (avisosNuevos > 0) {

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(
                                    x = (-4).dp,
                                    y = 4.dp
                                )
                                .size(19.dp)
                                .background(
                                    Color.Red,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = tr("$avisosNuevos"),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ---------------------------------------------------------
            // PACIENTE VINCULADO
            // ---------------------------------------------------------

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        VerdeClaroCuidador,
                        RoundedCornerShape(18.dp)
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Color.White,
                            CircleShape
                        )
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {

                    Text(
                        text = tr("Paciente"),
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280).enTema()
                    )

                    Text(
                        text = tr(nombrePaciente ?: "Paciente"),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A).enTema()
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = tr("Vinculado"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CelesteVinculado
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ---------------------------------------------------------
            // RESUMEN DE HOY
            // ---------------------------------------------------------

            Text(
                text = tr("Resumen de hoy"),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A).enTema()
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                CuadradoEjerciciosRealizados(
                    valor = if (!progresoCompartido) noCompartido else ejerciciosHoy?.toString() ?: "—",
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                CuadradoTiempoTotal(
                    valor = if (!progresoCompartido) noCompartido
                    else segundosHoy?.let { if (it < 60) "$it s" else "${it / 60} min" } ?: "—",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                CuadradoSensacionHoy(
                    valor = when {
                        !progresoCompartido -> noCompartido
                        sensacionHoy == null -> "—"
                        sensacionHoy == "" -> "Sin registrar hoy"
                        else -> sensacionHoy!!
                    },
                    colorPunto = SensacionDiaria.colorDe(sensacionHoy),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                CuadradoRachaActual(
                    valor = if (!progresoCompartido) noCompartido
                    else rachaDias?.let { if (it == 1) "1 día" else trf("{0} días", it) } ?: "—",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ---------------------------------------------------------
            // PROGRESOS RECIENTES
            // ---------------------------------------------------------

            Text(
                text = tr("Progresos recientes"),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A).enTema()
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.enTema())
                    .border(
                        1.dp,
                        BordeGrisCuidador,
                        RoundedCornerShape(18.dp)
                    )
                    .clickable(role = Role.Button, onClickLabel = tr("Ver evolución")) {
                        onEvolucionClick()
                    }
                    .padding(18.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                VerdeClaroCuidador,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Filled.BarChart,
                            contentDescription = tr("Evolución del paciente"),
                            tint = VerdeCuidador,
                            modifier = Modifier.size(25.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = tr("Evolución del paciente"),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A).enTema()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = tr("Tocá para ver cómo evolucionan los ejercicios y el QuickDASH de tu paciente."),
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280).enTema(),
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

// =====================================================================
// TARJETA: EJERCICIOS REALIZADOS
// =====================================================================

@Composable
private fun CuadradoEjerciciosRealizados(
    valor: String,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .heightIn(min = 132.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(VerdeClaroCuidador)
            .border(
                width = 1.5.dp,
                color = Color(0xFFB9DDC8).enTema(),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
            contentDescription = tr("Ejercicios realizados"),
            tint = VerdeCuidador,
            modifier = Modifier.size(31.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = tr("Ejercicios realizados"),
            fontSize = 13.sp,
            color = VerdeCuidador,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = tr(valor),
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1A1A1A).enTema()
        )
    }
}

// =====================================================================
// TARJETA: TIEMPO TOTAL
// =====================================================================

@Composable
private fun CuadradoTiempoTotal(
    valor: String,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .heightIn(min = 132.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFEEF5FF).enTema())
            .border(
                width = 1.5.dp,
                color = Color(0xFFBDD6F6).enTema(),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = Icons.Filled.Schedule,
            contentDescription = tr("Tiempo total"),
            tint = AzulTiempoTotal,
            modifier = Modifier.size(31.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = tr("Tiempo total"),
            fontSize = 13.sp,
            color = AzulTiempoTotal,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = tr(valor),
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1A1A1A).enTema()
        )
    }
}

// =====================================================================
// TARJETA: SENSACIÓN DE HOY
// =====================================================================

@Composable
private fun CuadradoSensacionHoy(
    valor: String,
    colorPunto: Color,
    modifier: Modifier = Modifier
) {
    val registrada = valor != "—" && valor != "Sin registrar hoy" && valor != "No compartido"


    Column(
        modifier = modifier
            .heightIn(min = 132.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFF8F0FC).enTema())
            .border(
                width = 1.5.dp,
                color = Color(0xFFE0C3EB).enTema(),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = Icons.Filled.SentimentSatisfied,
            contentDescription = tr("Sensación de hoy"),
            tint = MoradoSensacion,
            modifier = Modifier.size(31.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = tr("Sensación de hoy"),
            fontSize = 13.sp,
            color = MoradoSensacion,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(5.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (registrada) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(colorPunto, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = tr(valor),
                fontSize = if (registrada) 17.sp else 15.sp,
                fontWeight = if (registrada) FontWeight.ExtraBold else FontWeight.SemiBold,
                color = if (registrada) Color(0xFF1A1A1A).enTema() else Color(0xFF6B7280).enTema(),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

// =====================================================================
// TARJETA: RACHA ACTUAL
// =====================================================================

@Composable
private fun CuadradoRachaActual(
    valor: String,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .heightIn(min = 132.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFF1F8F4).enTema())
            .border(
                width = 1.5.dp,
                color = Color(0xFFB9DDC8).enTema(),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
            contentDescription = tr("Racha actual"),
            tint = VerdeCuidador,
            modifier = Modifier.size(31.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = tr("Racha actual"),
            fontSize = 13.sp,
            color = VerdeCuidador,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = tr(valor),
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1A1A1A).enTema()
        )
    }
}