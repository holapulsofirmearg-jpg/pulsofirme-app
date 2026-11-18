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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.DificultadAdaptativa
import com.example.pulsofirme.ProgresoData
import com.example.pulsofirme.UserData
import com.example.pulsofirme.R
import com.example.pulsofirme.SensacionDiaria
import com.example.pulsofirme.EjerciciosPremium
import com.example.pulsofirme.alarmas.TomasMedicacion
import com.example.pulsofirme.TarjetaOnOff
import com.example.pulsofirme.areaTactil
import com.example.pulsofirme.notificaciones.NotificacionesRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

private val Verde: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
private val VerdeClaro: Color
    @Composable get() = Color(0xFFEFF6F1).enTema()
private val BordeGris: Color
    @Composable get() = Color(0xFFE5E7EB).enTema()
private val RojoSuave: Color
    @Composable get() = Color(0xFFD32F2F).enTema()
// ---------------------------------------------------------------------
// EJERCICIO RECOMENDADO DEL DÍA
// Cambia según el diagnóstico, lo que ya hizo hoy, si tiene Premium
// y cómo se siente. Cada día propone uno distinto.
// ---------------------------------------------------------------------

private val gratisParkinson = listOf(
    "Extensión de dedos", "Toques de dedos", "Pronación y supinación",
    "Abrir y cerrar la mano", "Golpeteo alternado de dedos"
)
private val gratisArtritis = listOf(
    "Movilidad de muñecas", "Puño y extensión", "Rodar una pelota blanda",
    "Oposición del pulgar", "Deslizamiento de dedos"
)

// Los más suaves, para los días con dolor
private const val SUAVE_PARKINSON = "Abrir y cerrar la mano"
private const val SUAVE_ARTRITIS = "Rodar una pelota blanda"

private data class Recomendado(
    val nombre: String,
    val detalle: String,
    val todoHecho: Boolean = false
)

private fun recomendarEjercicio(
    diagnosticos: List<String>,
    hechosHoy: Set<String>,
    esPremium: Boolean,
    sensacion: String?
): Recomendado {
    val tieneParkinson = diagnosticos.any { it.equals("Parkinson", ignoreCase = true) }
    val tieneArtritis = diagnosticos.any { it.equals("Artritis", ignoreCase = true) }
    val diaDelAnio = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)

    // Si tiene los dos diagnósticos (o ninguno cargado), alterna día por medio
    val diagnostico = when {
        tieneParkinson && !tieneArtritis -> "Parkinson"
        tieneArtritis && !tieneParkinson -> "Artritis"
        else -> if (diaDelAnio % 2 == 0) "Parkinson" else "Artritis"
    }
    val gratis = if (diagnostico == "Parkinson") gratisParkinson else gratisArtritis
    val conDolor = sensacion?.contains("Dolor", ignoreCase = true) == true

    // Día con dolor: siempre el más suave (si todavía no lo hizo)
    if (conDolor) {
        val suave = if (diagnostico == "Parkinson") SUAVE_PARKINSON else SUAVE_ARTRITIS
        if (suave !in hechosHoy) {
            return Recomendado(suave, "Suave, porque hoy registraste dolor · 2 min")
        }
    }

    // Con Premium se suman los exclusivos (primero), sin Premium solo los gratis
    val premium = if (esPremium && !conDolor) EjerciciosPremium.seleccion(diagnostico) else emptyList()
    val candidatos = premium.map { it.nombre } + gratis
    val pendientes = candidatos.filter { it !in hechosHoy }

    if (pendientes.isEmpty()) {
        return Recomendado(
            nombre = "¡Hiciste todos los de hoy!",
            detalle = "Podés repetir uno o descansar",
            todoHecho = true
        )
    }

    val elegido = pendientes[diaDelAnio % pendientes.size]
    val ejPremium = premium.firstOrNull { it.nombre == elegido }
    val detalle = if (ejPremium != null) {
        val minutos = (ejPremium.segundos / 60).coerceAtLeast(1)
        "★ Premium · ${ejPremium.zona} · $minutos min"
    } else {
        "$diagnostico · 2 min"
    }
    return Recomendado(elegido, detalle)
}

data class ItemMenu(val label: String, val icono: ImageVector, val ruta: String)

val itemsMenuInferior = listOf(
    ItemMenu("Inicio", Icons.Filled.Home, "home"),
    ItemMenu("Ejercicios", Icons.Filled.FitnessCenter, "exercises"),
    ItemMenu("Progreso", Icons.Filled.ShowChart, "progress"),
    ItemMenu("Alertas", Icons.Filled.Notifications, "alerts"),
    ItemMenu("Dispositivo", Icons.Filled.Wifi, "device"),
    ItemMenu("Cuenta", Icons.Filled.Person, "account")
)

// El síntoma del medio depende del diagnóstico: el temblor es de
// Parkinson, la hinchazón es de artritis.
private fun sintomaDelMedio(): Pair<String, Color> {
    val d = UserData.diagnosticos
    val soloArtritis = d.size == 1 && d.first().equals("Artritis", ignoreCase = true)
    return if (soloArtritis) {
        "Hinchazón leve" to Color(0xFFE0A030)
    } else {
        "Temblor moderado" to Color(0xFFE0A030)
    }
}

@Composable
fun HomeScreen(
    nombreUsuario: String,
    rutaActual: String,
    onNavigate: (String) -> Unit,
    onEjercicioClick: () -> Unit,
    // Abre directo el ejercicio recomendado
    onEmpezarEjercicio: (String) -> Unit = { onEjercicioClick() },
    onSensacionClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val (textoMedio, colorMedio) = sintomaDelMedio()

    // Sensación de hoy: arranca vacía y se carga desde Firestore
    var sensacionElegida by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        FirebaseFirestore.getInstance().collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                SensacionDiaria.deHoy(
                    doc.getString("sensacionHoy"),
                    doc.getString("sensacionFecha")
                )?.let { sensacionElegida = it }
            }
    }

    fun elegirSensacion(texto: String) {
        DificultadAdaptativa.registrarSensacionDesdeTexto(context, texto)
        sensacionElegida = texto
        FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
            SensacionDiaria.guardar(uid, texto) {
                Avisos.mostrar("No se pudo guardar tu sensación. Revisá tu conexión.")
            }
        }
        onSensacionClick(texto)
    }

    // Pregunta on/off (solo Premium): vale para hoy
    var estadoMedicacion by remember { mutableStateOf(DificultadAdaptativa.estadoMedicacionHoy(context)) }

    // Nivel de dificultad de hoy (se recalcula al cambiar sensación, on/off o plan)
    val dificultadHoy = remember(sensacionElegida, estadoMedicacion, UserData.esPremium) {
        DificultadAdaptativa.vigente(context)
    }

    var hechos by remember { mutableIntStateOf(0) }
    // Nombres de los ejercicios que ya hizo hoy (para no recomendarlos de nuevo)
    var nombresHoy by remember { mutableStateOf<Set<String>>(emptySet()) }

    DisposableEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        var registration: com.google.firebase.firestore.ListenerRegistration? = null

        if (uid != null) {
            registration = FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(uid)
                .collection("ejerciciosRealizados")
                .addSnapshotListener { snapshot, _ ->
                    hechos = snapshot?.size() ?: 0
                    ProgresoData.ejerciciosCompletados = hechos
                    val hoy = SensacionDiaria.claveHoy()
                    nombresHoy = snapshot?.documents?.mapNotNull { d ->
                        val ms = d.getTimestamp("fecha")?.toDate()?.time ?: return@mapNotNull null
                        if (SensacionDiaria.claveDe(ms) == hoy) d.getString("ejercicio") else null
                    }?.toSet() ?: emptySet()
                }
        }

        onDispose {
            registration?.remove()
        }
    }

    // ---------------- USO DEL GUANTE ----------------

    val uidActual = FirebaseAuth.getInstance().currentUser?.uid
    var usoGuante by remember { mutableStateOf(UsoGuante.Resumen()) }

    DisposableEffect(uidActual) {
        val dejarDeEscuchar = if (uidActual != null) {
            UsoGuante.escuchar(uidActual) { usoGuante = it }
        } else null

        onDispose { dejarDeEscuchar?.invoke() }
    }

    // Reloj que avanza cada segundo mientras el guante está en uso
    var ahora by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(usoGuante.enUsoDesde) {
        while (usoGuante.enUsoDesde != null) {
            ahora = System.currentTimeMillis()
            delay(1000)
        }
    }

    val minutosEnCurso = usoGuante.enUsoDesde?.let {
        ((ahora - it) / 60_000L).coerceAtLeast(0)
    } ?: 0L
    val minutosHoyTotal = usoGuante.minutosHoy + minutosEnCurso

    var mostrarCargaManual by rememberSaveable { mutableStateOf(false) }
    var inicioOlvidado by remember { mutableStateOf<Long?>(null) }

    fun avisarUsoRegistrado(minutos: Long) {
        NotificacionesRepo.agregar(
            context,
            tipo = "progreso",
            titulo = "Uso del guante registrado",
            detalle = "Registraste ${UsoGuante.formatear(minutos)} de uso del guante."
        )
    }

    fun terminarUso() {
        val uid = uidActual ?: return
        val inicio = usoGuante.enUsoDesde ?: return
        val minutos = (System.currentTimeMillis() - inicio) / 60_000L

        if (minutos > 240) {
            // Más de 4 horas seguidas: seguramente se olvidó de cortarlo
            inicioOlvidado = inicio
            mostrarCargaManual = true
        } else {
            UsoGuante.terminar(uid, inicio)
            avisarUsoRegistrado(minutos.coerceAtLeast(1))
        }
    }

    if (mostrarCargaManual) {
        DialogoCargaManual(
            titulo = if (inicioOlvidado != null)
                "¿Cuánto usaste el guante?"
            else
                "Registrar uso del guante",
            texto = if (inicioOlvidado != null)
                "Pasaron más de 4 horas desde que marcaste que te lo pusiste. Elegí cuánto tiempo lo usaste en realidad."
            else
                "Elegí cuánto tiempo usaste el guante, por si te olvidaste de registrarlo.",
            onConfirmar = { minutos ->
                val uid = uidActual
                if (uid != null) {
                    val inicio = inicioOlvidado
                    if (inicio != null) {
                        UsoGuante.terminar(uid, inicio, minutosReales = minutos)
                    } else {
                        UsoGuante.registrarManual(uid, minutos)
                    }
                    avisarUsoRegistrado(minutos)
                }
                inicioOlvidado = null
                mostrarCargaManual = false
            },
            onCancelar = {
                inicioOlvidado = null
                mostrarCargaManual = false
            }
        )
    }

    // -------------------------------------------------

    // Medicación de hoy: horarios que le tocan y cuáles ya marcó "Ya la tomé".
    // Se recalcula cada vez que se vuelve al Inicio.
    val horariosHoy = remember { TomasMedicacion.horariosDeHoy(context) }
    val tomadasHoy = remember { TomasMedicacion.tomadasHoy(context) }
    val cantidadTomadas = horariosHoy.count { it in tomadasHoy }

    val recomendado = recomendarEjercicio(
        diagnosticos = UserData.diagnosticos,
        hechosHoy = nombresHoy,
        esPremium = UserData.esPremium,
        sensacion = sensacionElegida
    )

    val valorEjercicios = if (hechos == 0) "—" else hechos.toString()
    val etiquetaEjercicios = if (hechos == 0) "Sin ejercicios completados" else "Ejercicios completados"

    val valorUso = if (minutosHoyTotal == 0L && usoGuante.enUsoDesde == null)
        "—"
    else
        UsoGuante.formatear(minutosHoyTotal)
    val etiquetaUso = if (minutosHoyTotal == 0L && usoGuante.enUsoDesde == null)
        "Todavía no usaste el guante"
    else
        "Uso del guante hoy"

    val avisosNuevos = notificacionesDelUsuario().count { it.nueva }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White.enTema()) {
                itemsMenuInferior.forEach { item ->
                    NavigationBarItem(
                        selected = rutaActual == item.ruta,
                        onClick = { onNavigate(item.ruta) },
                        icon = { Icon(item.icono, contentDescription = item.label) },
                        label = { Text(item.label, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Verde,
                            selectedTextColor = Verde,
                            unselectedIconColor = Color(0xFF9CA3AF).enTema(),
                            unselectedTextColor = Color(0xFF9CA3AF).enTema(),
                            indicatorColor = VerdeClaro
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Panel de usuario", fontSize = 13.sp, color = Color(0xFF6B7280).enTema())
                    Text(
                        text = "Hola, $nombreUsuario",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A).enTema()
                    )
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { onNavigate("pulsibot") },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pulsibot),
                        contentDescription = "PulsiBot",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Box {
                    IconButton(onClick = { onNavigate("notifications") }) {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = "Notificaciones",
                            tint = Color(0xFF1A1A1A).enTema()
                        )
                    }
                    if (avisosNuevos > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-4).dp, y = 4.dp)
                                .size(18.dp)
                                .background(Color.Red, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$avisosNuevos",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Resumen de hoy", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                TarjetaResumen(
                    icono = Icons.Filled.Schedule,
                    valor = valorUso,
                    etiqueta = etiquetaUso,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                TarjetaResumen(
                    icono = Icons.Filled.CheckCircle,
                    valor = valorEjercicios,
                    etiqueta = etiquetaEjercicios,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ---------- Uso del guante ----------
            TarjetaUsoGuante(
                hayUsuario = uidActual != null,
                enUsoDesde = usoGuante.enUsoDesde,
                ahora = ahora,
                minutosSemana = usoGuante.minutosSemana + minutosEnCurso,
                diasConUso = usoGuante.diasConUsoSemana,
                onIniciar = { uidActual?.let { UsoGuante.iniciar(it) } },
                onTerminar = { terminarUso() },
                onCargaManual = {
                    inicioOlvidado = null
                    mostrarCargaManual = true
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Cómo te sentís hoy", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (sensacionElegida == null)
                    "Tocá una opción para completar tu registro diario"
                else
                    "Ya registraste cómo te sentís hoy. Podés cambiarlo cuando quieras.",
                fontSize = 13.sp,
                color = Color(0xFF6B7280).enTema()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OpcionSensacion(
                    texto = "Rigidez baja",
                    color = Color(0xFF2E9E63).enTema(),
                    seleccionado = sensacionElegida == "Rigidez baja",
                    onClick = { elegirSensacion("Rigidez baja") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OpcionSensacion(
                    texto = textoMedio,
                    color = colorMedio,
                    seleccionado = sensacionElegida == textoMedio,
                    onClick = { elegirSensacion(textoMedio) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OpcionSensacion(
                    texto = "Dolor leve",
                    color = Color(0xFF1565C0).enTema(),
                    seleccionado = sensacionElegida == "Dolor leve",
                    onClick = { elegirSensacion("Dolor leve") },
                    modifier = Modifier.weight(1f)
                )
            }

            // ---------- Premium: on/off + nivel de hoy ----------
            if (UserData.esPremium) {
                Spacer(modifier = Modifier.height(16.dp))

                TarjetaOnOff(
                    estado = estadoMedicacion,
                    onElegir = {
                        DificultadAdaptativa.registrarEstadoMedicacion(context, it)
                        estadoMedicacion = it
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Tus rutinas de hoy: nivel ${dificultadHoy.nivel} de 5 (${dificultadHoy.etiqueta}) · ${dificultadHoy.variante}",
                    fontSize = 13.sp,
                    color = Color(0xFF374151).enTema(),
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .areaTactil()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Verde)
                    .clickable {
                        if (recomendado.todoHecho) onEjercicioClick()
                        else onEmpezarEjercicio(recomendado.nombre)
                    }
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (recomendado.todoHecho) "Ejercicios de hoy" else "Ejercicio recomendado",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = recomendado.nombre,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = recomendado.detalle,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.enTema(), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (recomendado.todoHecho) Icons.Filled.Check else Icons.Filled.PlayArrow,
                        contentDescription = if (recomendado.todoHecho) "Ver ejercicios" else "Empezar ejercicio",
                        tint = Verde
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Recordatorios", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .areaTactil()
                    .border(1.dp, BordeGris, RoundedCornerShape(14.dp))
                    .clickable { onNavigate("medication") }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFFCE9D8).enTema(), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.MedicalServices, contentDescription = null, tint = Color(0xFFE0894A).enTema(), modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Medicación", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    Text(
                        text = when {
                            horariosHoy.isNotEmpty() ->
                                "Hoy: $cantidadTomadas de ${horariosHoy.size} tomas" +
                                        (if (cantidadTomadas == horariosHoy.size) " ✓" else "") +
                                        " · " + horariosHoy.joinToString(" · ")
                            UserData.horariosMedicacion.isEmpty() -> "Sin horario configurado"
                            else -> "Hoy no tenés tomas · " + UserData.horariosMedicacion.joinToString(" · ")
                        },
                        fontSize = 13.sp,
                        color = if (horariosHoy.isNotEmpty() && cantidadTomadas == horariosHoy.size)
                            Verde else Color(0xFF6B7280).enTema(),
                        lineHeight = 18.sp
                    )
                }
                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color(0xFF9CA3AF).enTema())
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// =====================================================================
// TARJETA: USO DEL GUANTE (cronómetro + carga manual)
// =====================================================================

@Composable
private fun TarjetaUsoGuante(
    hayUsuario: Boolean,
    enUsoDesde: Long?,
    ahora: Long,
    minutosSemana: Long,
    diasConUso: Int,
    onIniciar: () -> Unit,
    onTerminar: () -> Unit,
    onCargaManual: () -> Unit
) {
    val enUso = enUsoDesde != null

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (enUso) Verde else BordeGris, RoundedCornerShape(16.dp))
            .background(if (enUso) VerdeClaro else Color.White.enTema(), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(if (enUso) Color.White.enTema() else VerdeClaro, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Timer, contentDescription = null, tint = Verde, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Uso del guante", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = if (enUso) "En uso ahora" else "Registrá cuándo te lo ponés y te lo sacás",
                    fontSize = 13.sp,
                    color = if (enUso) Verde else Color(0xFF6B7280).enTema()
                )
            }
        }

        if (enUso && enUsoDesde != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = UsoGuante.cronometro(ahora - enUsoDesde),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Verde,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (!hayUsuario) {
            Text(
                text = "Iniciá sesión para registrar el uso del guante.",
                fontSize = 13.sp,
                color = Color(0xFF6B7280).enTema()
            )
        } else if (enUso) {
            Button(
                onClick = onTerminar,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RojoSuave)
            ) {
                Icon(Icons.Filled.Stop, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Me saqué el guante", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        } else {
            Button(
                onClick = onIniciar,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Verde)
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Me puse el guante", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            TextButton(
                onClick = onCargaManual,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "¿Te olvidaste de registrarlo? Cargalo a mano",
                    fontSize = 13.sp,
                    color = Verde
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        HorizontalDivider(color = BordeGris)
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Últimos 7 días: ${UsoGuante.formatear(minutosSemana)} · usado $diasConUso de 7 días",
            fontSize = 13.sp,
            color = Color(0xFF374151).enTema()
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Tiempo registrado por vos desde la app.",
            fontSize = 11.sp,
            color = Color(0xFF9CA3AF).enTema()
        )
    }
}

// =====================================================================
// DIÁLOGO: CARGA MANUAL
// =====================================================================

@Composable
private fun DialogoCargaManual(
    titulo: String,
    texto: String,
    onConfirmar: (Long) -> Unit,
    onCancelar: () -> Unit
) {
    val opciones = listOf(10L, 15L, 20L, 30L, 45L, 60L)
    var elegido by remember { mutableLongStateOf(15L) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo) },
        text = {
            Column {
                Text(text = texto, fontSize = 14.sp, color = Color(0xFF374151).enTema())
                Spacer(modifier = Modifier.height(14.dp))

                opciones.chunked(3).forEach { fila ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        fila.forEachIndexed { i, minutos ->
                            FilterChip(
                                selected = elegido == minutos,
                                onClick = { elegido = minutos },
                                label = {
                                    Text(
                                        text = UsoGuante.formatear(minutos),
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = VerdeClaro,
                                    selectedLabelColor = Verde
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            if (i < fila.size - 1) Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirmar(elegido) }) {
                Text("Guardar", color = Verde, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar", color = Color(0xFF6B7280).enTema())
            }
        }
    )
}

@Composable
private fun TarjetaResumen(
    icono: ImageVector,
    valor: String,
    etiqueta: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .border(1.dp, BordeGris, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Icon(icono, contentDescription = null, tint = Verde, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = valor, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A).enTema())
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = etiqueta, fontSize = 12.sp, color = Color(0xFF6B7280).enTema(), lineHeight = 16.sp)
    }
}

@Composable
private fun OpcionSensacion(
    texto: String,
    color: Color,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .areaTactil()
            .border(
                width = if (seleccionado) 2.dp else 1.dp,
                color = if (seleccionado) Verde else BordeGris,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = texto,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = if (seleccionado) Verde else Color(0xFF374151).enTema(),
            fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}