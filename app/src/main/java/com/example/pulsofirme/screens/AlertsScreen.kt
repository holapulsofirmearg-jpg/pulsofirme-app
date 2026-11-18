package com.example.pulsofirme.screens

import com.example.pulsofirme.ui.theme.enTema

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.ProgresoData
import com.example.pulsofirme.UserData
import com.example.pulsofirme.EjercicioPaciente
import com.example.pulsofirme.PacienteDelTutor
import com.example.pulsofirme.alarmas.RecordatorioEjercicio
import com.example.pulsofirme.alarmas.TomasMedicacion
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date

private val VerdeAl: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
private val VerdeOscuroAl: Color
    @Composable get() = Color(0xFF1B4332).enTema()
private val VerdeSuaveAl: Color
    @Composable get() = Color(0xFFEAF5EC).enTema()
private val GrisAl: Color
    @Composable get() = Color(0xFF6B7280).enTema()
private val NegroAl: Color
    @Composable get() = Color(0xFF1A1A1A).enTema()
private val AmarilloAl: Color
    @Composable get() = Color(0xFFE0A73A).enTema()
@Composable
fun AlertsScreen(
    rutaActual: String,
    onNavigate: (String) -> Unit,
    onConfigClick: () -> Unit = {},
    guanteConectado: Boolean = false
) {
    val contexto = LocalContext.current

    // ---------- Datos reales (antes había textos fijos) ----------
    // Ejercicios del último tiempo y si tiene cuidador vinculado, desde Firestore.
    // Si no hay internet, se usa lo guardado en el celular.
    var ejercicios by remember { mutableStateOf<List<EjercicioPaciente>?>(null) }
    var tieneCuidador by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        try {
            val ref = FirebaseFirestore.getInstance().collection("usuarios").document(uid)
            val yo = ref.get().await()
            val tutores = (yo.get("tutoresVinculados") as? List<*>).orEmpty()
            val profesionales = (yo.get("profesionalesVinculados") as? List<*>).orEmpty()
            tieneCuidador = tutores.isNotEmpty() || profesionales.isNotEmpty()

            val desde = Timestamp(Date(System.currentTimeMillis() - 120L * 24 * 60 * 60 * 1000))
            ejercicios = ref.collection("ejerciciosRealizados")
                .whereGreaterThanOrEqualTo("fecha", desde).get().await()
                .documents.mapNotNull { d ->
                    val ms = d.getTimestamp("fecha")?.toDate()?.time ?: return@mapNotNull null
                    EjercicioPaciente(
                        fechaMs = ms,
                        nombre = d.getString("ejercicio")?.ifBlank { null } ?: "Ejercicio",
                        segundos = d.getLong("segundos") ?: 0L,
                        repeticiones = d.getLong("repeticiones") ?: 0L
                    )
                }
                .sortedByDescending { it.fechaMs }
        } catch (_: Exception) {
            // Sin conexión: se muestran los datos del celular y se oculta el aviso de cuidador
        }
    }

    // Medicación: horarios que tocan hoy y cuáles ya marcó con "Ya la tomé"
    val horariosHoy = TomasMedicacion.horariosDeHoy(contexto)
    val tomadas = TomasMedicacion.tomadasHoy(contexto)
    val pendientes = horariosHoy.filter { it !in tomadas }
    val ahora = Calendar.getInstance()
    val minutosAhora = ahora.get(Calendar.HOUR_OF_DAY) * 60 + ahora.get(Calendar.MINUTE)
    fun minutosDe(h: String): Int {
        val partes = h.split(":")
        return (partes.getOrNull(0)?.toIntOrNull() ?: 0) * 60 + (partes.getOrNull(1)?.toIntOrNull() ?: 0)
    }
    val atrasada = pendientes.firstOrNull { minutosDe(it) <= minutosAhora }
    val proxima = pendientes.firstOrNull { minutosDe(it) > minutosAhora }

    val (textoMedicacion, medicacionDestacada) = when {
        UserData.horariosMedicacion.isEmpty() && horariosHoy.isEmpty() ->
            "Todavía no configuraste tus horarios" to false
        horariosHoy.isEmpty() ->
            "Hoy no tenés tomas programadas" to false
        atrasada != null ->
            "Te falta registrar la toma de las $atrasada hs" to true
        proxima != null ->
            "Próxima toma: $proxima hs (${tomadas.count { it in horariosHoy }} de ${horariosHoy.size} registradas)" to false
        else ->
            "Registraste todas las tomas de hoy (${horariosHoy.size} de ${horariosHoy.size})" to false
    }

    // Ejercicio de hoy y racha
    val hechosHoy = ejercicios?.let { PacienteDelTutor.deHoy(it).size } ?: ProgresoData.ejerciciosCompletados
    val racha = ejercicios?.let { PacienteDelTutor.racha(it) } ?: RecordatorioEjercicio.rachaActual(contexto)

    val tituloEjercicio = if (hechosHoy == 0) "Ejercicio pendiente" else "Ejercicio de hoy"
    val textoEjercicio = when (hechosHoy) {
        0 -> "Todavía no hiciste ejercicios hoy"
        1 -> "Hiciste 1 ejercicio hoy. ¡Bien!"
        else -> "Hiciste $hechosHoy ejercicios hoy. ¡Bien!"
    }

    val (tituloRacha, textoRacha) = when (racha) {
        0 -> "Empezá tu racha" to "Hacé un ejercicio hoy para arrancar"
        1 -> "Nueva racha" to "Vas por el primer día"
        else -> "Racha de $racha días" to "Llevás $racha días seguidos haciendo ejercicio"
    }

    Scaffold(
        containerColor = Color.White.enTema(),
        bottomBar = {
            NavigationBar(containerColor = Color.White.enTema()) {
                itemsMenuInferior.forEach { item ->
                    NavigationBarItem(
                        selected = rutaActual == item.ruta,
                        onClick = { onNavigate(item.ruta) },
                        icon = { Icon(item.icono, contentDescription = item.label) },
                        label = { Text(item.label, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = VerdeAl,
                            selectedTextColor = VerdeAl,
                            unselectedIconColor = Color(0xFF9CA3AF).enTema(),
                            unselectedTextColor = Color(0xFF9CA3AF).enTema(),
                            indicatorColor = VerdeSuaveAl
                        )
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text("Alertas", fontSize = 15.sp, color = GrisAl)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Al día con tu rutina",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = VerdeOscuroAl
                    )
                }
                IconButton(onClick = onConfigClick) {
                    Icon(Icons.Filled.Settings, contentDescription = "Configuración", tint = GrisAl)
                }
            }

            Spacer(Modifier.height(20.dp))

            // ---------- HOY ----------
            Text("Hoy", fontSize = 14.sp, color = GrisAl)
            Spacer(Modifier.height(10.dp))

            ItemAlerta(
                icono = Icons.Filled.Medication,
                colorIcono = AmarilloAl,
                titulo = "Medicación",
                detalle = textoMedicacion,
                destacada = medicacionDestacada,
                puntoVerde = medicacionDestacada
            )

            Spacer(Modifier.height(10.dp))

            ItemAlerta(
                icono = Icons.Filled.Bolt,
                colorIcono = VerdeAl,
                titulo = tituloEjercicio,
                detalle = textoEjercicio,
                destacada = hechosHoy == 0,
                puntoVerde = hechosHoy == 0
            )

            Spacer(Modifier.height(10.dp))

            ItemAlerta(
                icono = Icons.Filled.EmojiEvents,
                colorIcono = VerdeAl,
                titulo = tituloRacha,
                detalle = textoRacha,
                destacada = false,
                puntoVerde = false
            )

            // Solo si de verdad no tiene ningún tutor ni profesional vinculado
            if (tieneCuidador == false) {
                Spacer(Modifier.height(10.dp))

                ItemAlerta(
                    icono = Icons.Filled.PersonAdd,
                    colorIcono = VerdeAl,
                    titulo = "Todavía no tenés un cuidador",
                    detalle = "Vinculá a un familiar o profesional para que siga tu progreso",
                    destacada = false,
                    puntoVerde = false
                )
            }

            // ---------- ESTA SEMANA ----------
            if (guanteConectado) {
                Spacer(Modifier.height(24.dp))

                Text("Esta semana", fontSize = 14.sp, color = GrisAl)
                Spacer(Modifier.height(10.dp))

                ItemAlerta(
                    icono = Icons.Filled.Bluetooth,
                    colorIcono = VerdeAl,
                    titulo = "Guante emparejado",
                    detalle = "Se conectó correctamente a tu mano derecha",
                    destacada = false,
                    puntoVerde = false
                )
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
private fun ItemAlerta(
    icono: ImageVector,
    colorIcono: Color,
    titulo: String,
    detalle: String,
    destacada: Boolean,
    puntoVerde: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (destacada) VerdeSuaveAl else Color.Transparent)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(if (destacada) Color.White.enTema() else VerdeSuaveAl),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(19.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(titulo, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = NegroAl)
            Spacer(Modifier.height(2.dp))
            Text(detalle, fontSize = 13.sp, color = GrisAl, lineHeight = 18.sp)
        }
        if (puntoVerde) {
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(VerdeAl)
            )
        }
    }
}