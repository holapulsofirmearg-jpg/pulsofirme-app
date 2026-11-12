package com.example.pulsofirme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.PulsiBotIA
import com.example.pulsofirme.R
import kotlinx.coroutines.launch
import java.text.Normalizer

private val VerdePB = Color(0xFF07885C)
private val VerdeClaroPB = Color(0xFFF1F9F5)
private val VerdeTarjetaPB = Color(0xFFE6F4ED)
private val BordePB = Color(0xFFD5E6DE)
private val TextoPB = Color(0xFF092B2A)
private val GrisPB = Color(0xFF66817B)

private data class MensajePulsiBot(
    val texto: String,
    val esUsuario: Boolean
)

@Composable
fun PulsiBotScreen(
    nombreUsuario: String = "Usuario",
    rolUsuario: String = "paciente",
    onBack: () -> Unit,
    onEmpezarEjercicio: () -> Unit = {},
    onVerProgreso: () -> Unit = {},
    onComoConectarGuante: () -> Unit = {}
) {
    var texto by remember { mutableStateOf("") }
    val lista = remember { mutableStateListOf<MensajePulsiBot>() }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val teclado = LocalSoftwareKeyboardController.current

    // IA real: se prepara al abrir la pantalla con los datos del usuario
    val ia = remember { PulsiBotIA() }
    var iaLista by remember { mutableStateOf<Boolean?>(null) } // null = preparando
    var pensando by remember { mutableStateOf(false) }

    val rolNormalizado = rolUsuario.lowercase()
    val saludo = remember(nombreUsuario, rolNormalizado) {
        when (rolNormalizado) {
            "profesional" ->
                "¡Hola, $nombreUsuario! Soy PulsiBot. Puedo ayudarte con PulsoFirme, el seguimiento de pacientes y el uso de las funciones profesionales."
            "tutor", "cuidador" ->
                "¡Hola, $nombreUsuario! Soy PulsiBot. Puedo ayudarte con PulsoFirme y con el acompañamiento del paciente."
            else ->
                "¡Hola, $nombreUsuario! Soy PulsiBot. Puedo ayudarte con tus ejercicios, el guante, tu progreso y el uso de PulsoFirme. ¿En qué te ayudo hoy?"
        }
    }

    LaunchedEffect(Unit) {
        if (lista.isEmpty()) lista.add(MensajePulsiBot(saludo, false))
        iaLista = ia.preparar(rolNormalizado)
    }

    fun enviar(mensajeForzado: String? = null) {
        val consulta = (mensajeForzado ?: texto).trim()
        if (consulta.isBlank() || pensando) return

        lista.add(MensajePulsiBot(consulta, true))
        texto = ""
        teclado?.hide()

        // Si la persona menciona hacerse daño, se responde siempre con ayuda directa
        respuestaDeCrisis(consulta)?.let { ayuda ->
            lista.add(MensajePulsiBot(ayuda, false))
            scope.launch { listState.animateScrollToItem((lista.size + 1).coerceAtLeast(0)) }
            return
        }

        pensando = true
        scope.launch {
            listState.animateScrollToItem((lista.size + 1).coerceAtLeast(0))

            // Si la IA todavía se está preparando, se espera un momento
            if (iaLista == null) iaLista = ia.preparar(rolNormalizado)

            val respuesta = (if (iaLista == true) ia.responder(consulta) else null)
                ?: responderPulsiBot(consulta = consulta, rol = rolNormalizado) // sin internet: respuestas locales

            lista.add(MensajePulsiBot(respuesta, false))
            pensando = false
            listState.animateScrollToItem((lista.size + 1).coerceAtLeast(0))
        }
    }

    Scaffold(
        containerColor = VerdeClaroPB,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VerdePB)
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }

                Image(
                    painter = painterResource(R.drawable.pulsibot),
                    contentDescription = "PulsiBot",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.width(10.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        "PulsiBot",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(8.dp)
                                .background(Color(0xFF5CE5A3), CircleShape)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            when (iaLista) {
                                true -> "Conectado · conoce tu progreso"
                                false -> "Modo básico (sin conexión)"
                                null -> "Conectando…"
                            },
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it.take(500) },
                    placeholder = { Text("Escribile a PulsiBot...", color = GrisPB) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(28.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { enviar() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VerdePB,
                        unfocusedBorderColor = BordePB,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        lista.add(
                            MensajePulsiBot(
                                "El ingreso por voz todavía no está activado. Por ahora escribime tu consulta.",
                                false
                            )
                        )
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(VerdeTarjetaPB, CircleShape)
                ) {
                    Icon(Icons.Filled.Mic, "Micrófono", tint = VerdePB)
                }

                Spacer(Modifier.width(6.dp))

                IconButton(
                    onClick = { enviar() },
                    enabled = !pensando,
                    modifier = Modifier
                        .size(48.dp)
                        .background(if (pensando) BordePB else VerdePB, CircleShape)
                ) {
                    Icon(Icons.Filled.Send, "Enviar", tint = Color.White)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = VerdeTarjetaPB,
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Text(
                            "Hoy",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                            color = GrisPB,
                            fontSize = 12.sp
                        )
                    }
                }

                items(lista) { mensaje ->
                    BurbujaPulsiBot(mensaje)
                }

                if (pensando) {
                    item { BurbujaPulsiBot(MensajePulsiBot("PulsiBot está escribiendo…", false), escribiendo = true) }
                }

                item {
                    Column {
                        Text(
                            "Preguntas rápidas",
                            color = GrisPB,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 6.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            if (rolNormalizado == "paciente") {
                                BotonRapidoPB(
                                    texto = "¿Cómo conecto el guante?",
                                    modifier = Modifier.weight(1f)
                                ) {
                                    enviar("¿Cómo conecto el guante?")
                                }
                            }

                            BotonRapidoPB(
                                texto = if (rolNormalizado == "paciente")
                                    "Ver mi progreso"
                                else
                                    "Ver progreso",
                                modifier = Modifier.weight(1f)
                            ) {
                                onVerProgreso()
                            }
                        }

                        if (rolNormalizado == "paciente") {
                            Spacer(Modifier.height(7.dp))
                            BotonRapidoPB(
                                texto = "¿Qué ejercicio me toca hoy?",
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                enviar("¿Qué ejercicio me toca hoy?")
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 18.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = null,
                            tint = GrisPB,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            "PulsiBot usa inteligencia artificial y puede equivocarse. Brinda orientación general y no reemplaza a un profesional de la salud.",
                            color = GrisPB,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BurbujaPulsiBot(mensaje: MensajePulsiBot, escribiendo: Boolean = false) {
    if (mensaje.esUsuario) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                modifier = Modifier.widthIn(max = 285.dp),
                shape = RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp),
                color = VerdePB
            ) {
                Text(
                    mensaje.texto,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                    color = Color.White,
                    fontSize = 15.sp,
                    lineHeight = 21.sp
                )
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Image(
                painter = painterResource(R.drawable.pulsibot),
                contentDescription = null,
                modifier = Modifier
                    .size(29.dp)
                    .clip(CircleShape)
                    .border(1.dp, VerdePB, CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(7.dp))
            Surface(
                modifier = Modifier.widthIn(max = 300.dp),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, BordePB)
            ) {
                Text(
                    mensaje.texto,
                    modifier = Modifier.padding(14.dp),
                    color = if (escribiendo) GrisPB else TextoPB,
                    fontSize = 15.sp,
                    lineHeight = 21.sp,
                    fontStyle = if (escribiendo) androidx.compose.ui.text.font.FontStyle.Italic else null
                )
            }
        }
    }
}

@Composable
private fun BotonRapidoPB(
    texto: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, VerdePB)
    ) {
        Text(
            texto,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 9.dp),
            color = VerdePB,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/*
 * Motor local de respuestas.
 * No necesita Internet ni API.
 * Sirve para la versión demo/prototipo de PulsoFirme.
 */
private fun responderPulsiBot(
    consulta: String,
    rol: String
): String {
    val q = normalizarPB(consulta)

    fun tiene(vararg palabras: String) = palabras.any { q.contains(it) }
    fun todas(vararg palabras: String) = palabras.all { q.contains(it) }

    // =========================================================
    // SEGURIDAD
    // =========================================================
    val bloqueados = listOf(
        "pornografia", "porno", "sexo explicito", "desnudos",
        "como matar", "asesinar", "hacer una bomba", "explosivo",
        "cocaina", "metanfetamina", "hackear", "robar contrasena", "estafar"
    )
    if (bloqueados.any { q.contains(it) }) {
        return "No puedo ayudarte con contenido peligroso, sexual explícito, ilegal o que pueda causar daño. " +
                "Sí puedo ayudarte con PulsoFirme, el guante, ejercicios, progreso, alertas, QuickDASH, navegación y funciones de la cuenta."
    }

    if (tiene("que enfermedad tengo", "diagnostica", "diagnostico tengo", "que tengo")) {
        return "No puedo realizar diagnósticos médicos. Puedo ayudarte a usar PulsoFirme y entender sus funciones, pero un diagnóstico debe hacerlo un profesional de la salud."
    }

    if (tiene("que medicamento", "que remedio", "que pastilla", "cambiar dosis", "subir dosis", "bajar dosis", "dejar de tomar")) {
        return "No puedo indicar medicamentos, dosis ni cambios de tratamiento. Para eso consultá a tu profesional de salud. Sí puedo ayudarte con las funciones y alertas de PulsoFirme."
    }

    // =========================================================
    // 1-8. PULSO FIRME
    // =========================================================
    if (tiene(
            "que es pulso firme", "que es pulsofirme", "para que sirve pulso firme",
            "para que sirve pulsofirme", "como funciona pulso firme", "como funciona pulsofirme",
            "que significa pulso firme", "contame sobre pulso firme", "hablame de pulso firme"
        )
    ) {
        return "PulsoFirme es un proyecto tecnológico orientado a la rehabilitación y al acompañamiento de personas con patologías que afectan la mano. Integra un guante inteligente con una aplicación móvil que permite realizar ejercicios, consultar el progreso, gestionar alertas, completar evaluaciones como QuickDASH y facilitar el seguimiento por parte de tutores y profesionales de la salud. El proyecto está integrado por 4 personas: Ivan Correa, Gustavo Carrasco, Ana Cabagna y Morena Martinez."
    }

    if (tiene(
            "quien creo pulso firme", "quienes crearon pulso firme", "quienes hicieron pulso firme",
            "quienes integran pulso firme", "equipo de pulso firme", "cuantos integrantes",
            "cuantas personas forman", "como se llaman los integrantes"
        )
    ) {
        return "PulsoFirme está integrado por 4 personas: Ivan Correa, Gustavo Carrasco, Ana Cabagna y Morena Martinez."
    }

    if (tiene("ivan correa", "gustavo carrasco", "ana cabagna", "morena martinez")) {
        return "Ivan Correa, Gustavo Carrasco, Ana Cabagna y Morena Martinez son los 4 integrantes de PulsoFirme."
    }

    if (todas("pulso firme", "reemplaza") || todas("pulsofirme", "reemplaza")) {
        return "No. PulsoFirme es una herramienta de apoyo para rehabilitación y seguimiento. No reemplaza la consulta, evaluación, diagnóstico ni indicaciones de un profesional de la salud."
    }

    // =========================================================
    // 9-22. GUANTE
    // =========================================================
    if (todas("para que", "guante") || todas("sirve", "guante")) {
        return "El guante PulsoFirme forma parte del sistema de apoyo para la rehabilitación de la mano y trabaja junto con la aplicación. Permite acompañar las actividades y el seguimiento previsto dentro de PulsoFirme."
    }

    if (todas("como", "coloco", "guante") || todas("como", "poner", "guante") || todas("como", "uso", "guante")) {
        return "Para colocarte el guante: primero asegurate de que esté apagado. Introducí la mano suavemente, acomodá los dedos y verificá que quede firme pero cómodo, sin apretar demasiado. Revisá que ningún cable o componente quede tirante o doblado de forma incómoda. Después podés encenderlo y gestionar su conexión desde Dispositivo."
    }

    if (tiene("me lo puse bien", "esta bien puesto", "bien colocado")) {
        return "El guante está correctamente colocado cuando los dedos están bien acomodados, queda firme pero cómodo y no provoca dolor, adormecimiento ni presión excesiva. Los cables y componentes tampoco deberían quedar tirantes."
    }

    if (todas("que mano", "guante") || todas("en que mano", "guante")) {
        return "Usá el guante en la mano indicada para tu seguimiento o rutina. Si no sabés cuál corresponde, revisá tus datos médicos en PulsoFirme o consultá al profesional que acompaña tu tratamiento."
    }

    if (todas("apretado", "guante")) {
        return "No. El guante debe quedar firme, pero cómodo. No debería causar dolor, adormecimiento ni presión excesiva."
    }

    if (todas("guante", "molesta") || todas("guante", "incomodo")) {
        return "Si el guante te molesta, apagalo y retiralo. Volvé a colocarlo verificando que los dedos estén acomodados y que no quede demasiado apretado. Si la molestia continúa, no sigas utilizándolo hasta consultar con el responsable correspondiente."
    }

    if ((tiene("duele", "dolor")) && q.contains("guante")) {
        return "Si sentís dolor mientras usás el guante, detené su uso y retiralo. PulsiBot no puede determinar la causa del dolor. Si el dolor es importante o continúa, consultá a un profesional de salud."
    }

    if (todas("enciendo", "guante") || todas("encender", "guante")) {
        return "Para usar el guante, primero colocátelo correctamente y luego utilizá su control de encendido. Una vez encendido, podés ir a la sección Dispositivo de PulsoFirme para gestionar la conexión."
    }

    if ((tiene("conecto", "conectar", "conexion", "vincular", "bluetooth")) && q.contains("guante")) {
        return "Para gestionar la conexión del guante, entrá en Dispositivo dentro de PulsoFirme. Asegurate de que el guante esté encendido y seguí las opciones de conexión disponibles en pantalla."
    }

    if ((tiene("no conecta", "no se conecta", "desconecta", "no funciona", "falla", "problema")) && q.contains("guante")) {
        return "Si el guante no conecta o presenta un problema, verificá que esté encendido y correctamente colocado. Después entrá en Dispositivo y volvé a intentar la conexión. Si continúa fallando, entrá en Cuenta → Ayuda y soporte para reportar el problema."
    }

    if (todas("ejercicio", "sin", "guante")) {
        return "Eso depende de la actividad indicada. Revisá las instrucciones del ejercicio dentro de PulsoFirme. Si el ejercicio requiere el guante, utilizalo como se indica; si tenés dudas sobre una indicación clínica, consultá a tu profesional."
    }

    if (q.contains("guante")) {
        return "Puedo ayudarte con el guante PulsoFirme: cómo colocarlo, cómo conectarlo desde Dispositivo, qué hacer si queda incómodo y cómo actuar si presenta un problema."
    }

    // =========================================================
    // 23-31. EJERCICIOS
    // =========================================================
    if (tiene("donde estan mis ejercicios", "donde veo mis ejercicios", "donde encuentro mis ejercicios")) {
        return "Entrá en la sección Ejercicios de PulsoFirme. Ahí podés consultar las actividades disponibles para tu seguimiento."
    }

    if (tiene("que ejercicio tengo", "que ejercicio me toca", "ejercicio de hoy")) {
        return "Entrá en Ejercicios para consultar la rutina disponible para hoy. PulsiBot no cambia por su cuenta los ejercicios indicados por un profesional."
    }

    if (tiene("como empiezo un ejercicio", "como iniciar un ejercicio", "empezar ejercicio")) {
        return "Entrá en Ejercicios, elegí la actividad disponible y utilizá el botón para comenzar. Antes de iniciar, revisá las indicaciones que aparecen en pantalla."
    }

    if (tiene("cuantos ejercicios tengo", "cantidad de ejercicios")) {
        return "La cantidad de ejercicios depende de la rutina disponible para tu cuenta. Podés verla entrando en Ejercicios."
    }

    if (tiene("cuanto dura un ejercicio", "duracion del ejercicio")) {
        return "La duración depende del ejercicio asignado. Revisá la tarjeta o el detalle del ejercicio dentro de PulsoFirme para ver el tiempo indicado."
    }

    if (tiene("no puedo terminar", "no puedo completar")) {
        return "Si no podés terminar un ejercicio, no te fuerces. Si aparece dolor importante, mareo o un síntoma inesperado, detené la actividad. Para modificar una indicación de rehabilitación, consultá a tu profesional."
    }

    if (tiene("repetir un ejercicio", "hacer de nuevo un ejercicio")) {
        return "Repetí un ejercicio solamente si la rutina o tu profesional lo indican. PulsiBot no debe aumentar por su cuenta la cantidad de repeticiones."
    }

    if ((tiene("dolor", "me duele")) && q.contains("ejercicio")) {
        return "Si sentís dolor importante durante un ejercicio, detenelo. No continúes forzando la mano y consultá a tu profesional si el dolor continúa o te preocupa."
    }

    if (tiene("ejercicios que ya hice", "ejercicios realizados")) {
        return "Podés revisar la actividad registrada desde Progreso. Allí se refleja el seguimiento disponible de tus ejercicios."
    }

    // =========================================================
    // 32-40. PROGRESO
    // =========================================================
    if (tiene("donde veo mi progreso", "como veo mi progreso", "ver mi progreso")) {
        return "Entrá en Progreso desde la navegación de PulsoFirme. Ahí podés consultar la actividad y evolución registrada."
    }

    if (tiene("estoy mejorando", "como se si mejoro", "como se si estoy mejorando")) {
        return "Revisá la sección Progreso para comparar la evolución registrada. La interpretación clínica de una mejora debe realizarla un profesional de salud."
    }

    if (todas("porcentaje", "progreso")) {
        return "El porcentaje mostrado en Progreso resume el cumplimiento o avance que la app registra para esa métrica. Para una interpretación clínica de tu evolución, consultá a tu profesional."
    }

    if (tiene("cuantos ejercicios hice", "cantidad de ejercicios realizados")) {
        return "Podés consultar la cantidad de ejercicios registrados desde Progreso."
    }

    if (tiene("cuanto tiempo entrene", "tiempo de actividad", "cuanto tiempo hice")) {
        return "En Progreso podés consultar el tiempo de actividad registrado por PulsoFirme."
    }

    if (tiene("que es la racha", "para que sirve la racha")) {
        return "La racha representa la continuidad de actividad registrada durante días consecutivos. Sirve como referencia para acompañar la constancia."
    }

    if (tiene("perdi mi racha", "perder la racha")) {
        return "Si se interrumpe la continuidad de actividad registrada, la racha puede cambiar. Podés revisar tu estado actual desde Progreso."
    }

    if (todas("profesional", "ver", "progreso")) {
        return "El profesional puede consultar la información de seguimiento disponible de sus pacientes vinculados, de acuerdo con los permisos y funciones de PulsoFirme."
    }

    if ((tiene("tutor", "cuidador")) && todas("ver", "progreso")) {
        return "El tutor o cuidador puede consultar el progreso disponible del paciente vinculado según los permisos establecidos en PulsoFirme."
    }

    if (tiene("progreso", "evolucion", "avance", "cumplimiento")) {
        return when (rol) {
            "profesional" -> "En Progreso podés consultar actividad, ejercicios realizados, cumplimiento y evolución de tus pacientes."
            "tutor", "cuidador" -> "En Progreso podés consultar la evolución y actividad disponible del paciente vinculado."
            else -> "En Progreso podés consultar tu actividad y evolución registrada en PulsoFirme."
        }
    }

    // =========================================================
    // 41-48. QUICKDASH
    // =========================================================
    if (tiene("que es quickdash", "que es quick dash", "para que sirve quickdash", "para que sirve quick dash")) {
        return "QuickDASH es un cuestionario utilizado en PulsoFirme para registrar dificultades relacionadas con el miembro superior. Ayuda a incorporar esa información al seguimiento."
    }

    if (tiene("donde hago quickdash", "donde esta quickdash", "donde encuentro quickdash")) {
        return "Buscá la sección QuickDASH dentro de las opciones de seguimiento de PulsoFirme. Desde allí podés completar el cuestionario."
    }

    if (tiene("como respondo", "como completar") && tiene("quickdash", "quick dash", "cuestionario")) {
        return "Respondé cada pregunta de QuickDASH según tu propia experiencia y dificultad real. No elijas una opción porque parezca mejor o peor."
    }

    if (tiene("resultado quickdash", "resultado quick dash", "puntaje quickdash", "puntaje quick dash")) {
        return "El resultado de QuickDASH queda registrado para el seguimiento. PulsiBot puede explicarte la función del cuestionario, pero la interpretación clínica del resultado corresponde a un profesional de salud."
    }

    if (tiene("responder quickdash por mi", "quickdash por mi", "responder el cuestionario por mi")) {
        return "No. QuickDASH debe reflejar tu propia experiencia. PulsiBot puede explicarte las preguntas, pero no debe elegir las respuestas por vos."
    }

    if (todas("profesional", "quickdash")) {
        return "El profesional puede consultar los resultados QuickDASH disponibles de sus pacientes vinculados según los permisos de acceso de PulsoFirme."
    }

    if (tiene("volver a hacer quickdash", "repetir quickdash", "hacer quickdash de nuevo")) {
        return "PulsoFirme puede registrar evaluaciones QuickDASH para el seguimiento. Realizalo cuando corresponda según el uso previsto de la app o la indicación de tu profesional."
    }

    if (tiene("quickdash", "quick dash")) {
        return "QuickDASH es una evaluación de seguimiento del miembro superior. Si querés, preguntame cómo completarla, para qué sirve o qué ocurre con el resultado."
    }

    // =========================================================
    // 49-56. ALERTAS Y NOTIFICACIONES
    // =========================================================
    if (tiene("donde estan mis alertas", "donde veo alertas")) {
        return "Podés consultar las alertas desde la sección Alertas cuando esté disponible para tu rol y también revisar las notificaciones desde el ícono de la campana."
    }

    if (tiene("para que sirve la campana", "campanita", "icono de campana")) {
        return "La campana abre las notificaciones de PulsoFirme. Allí aparecen avisos relevantes relacionados con tu actividad o seguimiento."
    }

    if (tiene("por que tengo una notificacion", "por que tengo una alerta")) {
        return "Las notificaciones aparecen cuando PulsoFirme tiene un aviso relevante para mostrarte, por ejemplo sobre actividad, progreso o seguimiento."
    }

    if (tiene("marcar una alerta", "marcar notificacion", "marcar todas como leidas")) {
        return "Entrá en la pantalla de notificaciones. Cuando la opción esté disponible, podés utilizar “Marcar todas como leídas” para actualizar el estado de los avisos."
    }

    if (tiene("ejercicio pendiente")) {
        return "“Ejercicio pendiente” indica que una actividad programada todavía no figura como completada en el seguimiento."
    }

    if (tiene("objetivo semanal cumplido")) {
        return "“Objetivo semanal cumplido” indica que se alcanzó el objetivo de actividad establecido para esa semana."
    }

    if (tiene("avisar cuando tengo que hacer", "recordatorio de ejercicio")) {
        return "PulsoFirme utiliza alertas y notificaciones para acompañar el seguimiento. Las opciones disponibles dependen de la configuración de tu cuenta."
    }

    if (tiene("alertas de medicacion", "alerta de medicacion")) {
        return "Las alertas de medicación funcionan como recordatorios dentro del seguimiento. PulsiBot no indica medicamentos ni dosis; esas indicaciones deben provenir de un profesional de salud."
    }

    if (tiene("alerta", "notificacion", "campana", "aviso")) {
        return when (rol) {
            "profesional" -> "Las notificaciones profesionales pueden mostrar avisos como ejercicios pendientes, objetivos cumplidos, mejoras, baja actividad o rutinas completadas."
            else -> "Las alertas y notificaciones muestran avisos importantes relacionados con PulsoFirme y el seguimiento disponible para tu cuenta."
        }
    }

    // =========================================================
    // 57-66. NAVEGACIÓN
    // =========================================================
    if (tiene("donde esta inicio", "como vuelvo al inicio")) {
        return "Usá la opción Inicio de la barra de navegación para volver al panel principal de PulsoFirme."
    }

    if (tiene("como voy a progreso", "donde esta progreso", "donde encuentro progreso")) {
        return "Tocá Progreso en la barra de navegación correspondiente a tu cuenta."
    }

    if (tiene("donde esta mi cuenta", "donde encuentro cuenta")) {
        return "Tocá Cuenta en la barra inferior. Ahí están las opciones de perfil, privacidad, ayuda, contraseña y sesión."
    }

    if (tiene("donde encuentro el dispositivo", "donde esta dispositivo")) {
        return "Entrá en la sección Dispositivo desde los accesos disponibles en PulsoFirme. Esa pantalla se utiliza para gestionar el guante."
    }

    if (tiene("donde cambio mis datos", "editar mis datos")) {
        return "Entrá en Cuenta y seleccioná la opción para editar tus datos personales."
    }

    if (tiene("donde encuentro ayuda", "donde esta ayuda")) {
        return "Entrá en Cuenta → Ayuda y soporte."
    }

    if (tiene("donde veo mis pacientes", "donde estan mis pacientes") && rol == "profesional") {
        return "Tocá Pacientes en la barra de navegación profesional. Allí podés acceder a los pacientes vinculados."
    }

    if (tiene("pantalla anterior", "como vuelvo atras")) {
        return "Utilizá la flecha de volver que aparece en la parte superior de la pantalla o el botón de retroceso del dispositivo."
    }

    if (tiene("menu de cuenta", "que puedo hacer desde cuenta", "funciones de cuenta")) {
        return when (rol) {
            "profesional" -> "En Cuenta podés editar datos personales, consultar datos profesionales, gestionar el plan, privacidad y datos, ayuda y soporte, cambiar contraseña y cerrar sesión."
            "tutor", "cuidador" -> "En Cuenta podés gestionar tus datos, privacidad, ayuda y soporte, contraseña, sesión y opciones disponibles relacionadas con el paciente vinculado."
            else -> "En Cuenta podés gestionar tu perfil, privacidad y datos, ayuda y soporte, contraseña y cierre de sesión."
        }
    }

    if (tiene("menu", "navegacion", "como llego", "donde encuentro")) {
        return when (rol) {
            "profesional" -> "La navegación profesional principal incluye Inicio, Pacientes, Progreso y Cuenta."
            "tutor", "cuidador" -> "La navegación del tutor/cuidador incluye Inicio, Progreso, Alertas y Cuenta."
            else -> "Desde la navegación y los accesos del Inicio podés llegar a las principales funciones de PulsoFirme, como ejercicios, progreso, dispositivo y cuenta."
        }
    }

    // =========================================================
    // 67-78. CUENTA Y CONFIGURACIÓN
    // =========================================================
    if (tiene("cambio mi nombre", "cambiar mi nombre", "cambio mi correo", "cambiar mi correo")) {
        return "Entrá en Cuenta → Editar datos personales. Desde allí podés modificar la información disponible de tu perfil."
    }

    if (tiene("foto de perfil", "agregar una foto", "cambiar foto")) {
        return "Desde Editar datos personales podés tocar la imagen de perfil para seleccionar una foto desde tu dispositivo cuando esa función esté disponible para tu tipo de cuenta."
    }

    if (tiene("cambio mi contrasena", "cambiar contrasena", "cambiar mi clave")) {
        return "Entrá en Cuenta → Cambiar contraseña y seguí los pasos de la pantalla."
    }

    if (tiene("olvide mi contrasena", "recuperar contrasena", "olvide mi clave")) {
        return "En la pantalla de inicio de sesión utilizá la opción de recuperación de contraseña e ingresá el correo asociado a tu cuenta."
    }

    if (tiene("cierro sesion", "cerrar sesion", "salir de la cuenta")) {
        return "Entrá en Cuenta y seleccioná Cerrar sesión."
    }

    if (tiene("datos personales", "donde estan mis datos")) {
        return "Tus datos personales se gestionan desde Cuenta → Editar datos personales."
    }

    if (tiene("datos profesionales", "matricula", "especialidad", "lugar de trabajo") && rol == "profesional") {
        return "Entrá en Cuenta → Datos profesionales. Allí podés gestionar lugar de trabajo, especialidad, matrícula profesional y formación adicional."
    }

    if (tiene("descargo mis datos", "descargar mis datos")) {
        return "Entrá en Cuenta → Privacidad y datos → Descargar mis datos. La función permite generar una copia de la información disponible de tu cuenta."
    }

    if (tiene("donde esta privacidad", "privacidad y datos", "permisos de acceso")) {
        return "Entrá en Cuenta → Privacidad y datos. Allí podés revisar las opciones de información, permisos y datos disponibles para tu rol."
    }

    if (tiene("contacto con pulso firme", "contactar pulso firme", "correo de pulso firme")) {
        return "Entrá en Cuenta → Ayuda y soporte. Allí encontrás las opciones de contacto de PulsoFirme."
    }

    if (tiene("reporto un problema", "reportar un problema", "problema con la app")) {
        return "Entrá en Cuenta → Ayuda y soporte → Reportar un problema. Desde esa opción podés preparar un correo para contactar al equipo de PulsoFirme."
    }

    if (tiene("cuenta", "perfil")) {
        return when (rol) {
            "profesional" -> "En Cuenta podés gestionar datos personales, datos profesionales, plan, privacidad, ayuda, contraseña y sesión."
            "tutor", "cuidador" -> "En Cuenta podés gestionar tu perfil, privacidad, ayuda, contraseña, sesión y opciones relacionadas con el vínculo."
            else -> "En Cuenta podés gestionar tu perfil, privacidad y datos, ayuda, contraseña y sesión."
        }
    }

    // =========================================================
    // 79-92. PREMIUM
    // =========================================================
    if (tiene("que es premium", "para que sirve pagar premium", "beneficios de premium")) {
        return "PulsoFirme contempla funciones Premium según el tipo de cuenta. Premium busca ampliar las herramientas disponibles más allá de las funciones básicas."
    }

    if (tiene("que es premium profesional", "beneficios premium profesional", "para que sirve premium profesional")) {
        return "Premium Profesional es una suscripción independiente orientada al seguimiento profesional. Permite vincular más de un paciente, exportar reportes en PDF, acceder a un panel comparativo entre pacientes y utilizar herramientas avanzadas de seguimiento."
    }

    if (tiene("diferencia entre premium", "premium paciente y premium profesional", "dos premium", "2 premium")) {
        return "Premium del paciente y Premium Profesional son facetas independientes. Premium Profesional está orientado a la gestión y comparación de pacientes, reportes PDF y seguimiento avanzado. Tener una suscripción no activa automáticamente la otra."
    }

    if (todas("profesional", "premium", "paciente", "tambien")) {
        return "No. La suscripción Premium Profesional es independiente de la suscripción Premium del paciente."
    }

    if (todas("paciente", "premium", "profesional", "tambien")) {
        return "No. Que un paciente tenga Premium no activa automáticamente Premium Profesional."
    }

    if (tiene("cuantos pacientes puedo tener", "limite de pacientes")) {
        return "El plan profesional base que muestra actualmente PulsoFirme contempla hasta 5 pacientes. Las funciones Premium están pensadas para ampliar las capacidades profesionales."
    }

    if (tiene("premium puedo agregar mas pacientes", "premium mas pacientes")) {
        return "Sí. Una de las funciones planteadas para Premium Profesional es permitir vincular más de un paciente y ampliar las capacidades de gestión respecto del plan base."
    }

    if (tiene("descargar informes pdf", "reportes pdf", "informe pdf")) {
        return "La exportación de reportes en PDF forma parte de las funciones planteadas para Premium Profesional."
    }

    if (tiene("comparar pacientes", "comparativa de pacientes")) {
        return "Sí. Premium Profesional contempla un panel comparativo para analizar información de seguimiento entre pacientes."
    }

    if (tiene("cuanto cuesta premium", "precio premium", "precio del premium")) {
        return if (rol == "profesional") {
            "En la pantalla actual de Premium Profesional se muestran dos opciones: plan anual de $11.900 y plan mensual de $14.900. El plan anual se presenta con la indicación de 2 meses gratis y un detalle de $142.800 al año."
        } else {
            "El precio depende del plan Premium correspondiente a tu tipo de cuenta. Consultá la pantalla Premium de la app para ver la opción mostrada actualmente."
        }
    }

    if (tiene("donde activo premium", "activar premium", "contratar premium")) {
        return "Entrá en Cuenta → Plan para consultar y activar la opción Premium disponible para tu tipo de cuenta."
    }

    if (tiene("premium", "plan")) {
        return if (rol == "profesional") {
            "Desde Cuenta → Plan podés consultar Premium Profesional, sus beneficios y las opciones de suscripción."
        } else {
            "Desde las opciones de Cuenta podés consultar el plan Premium disponible para tu tipo de usuario."
        }
    }

    // =========================================================
    // 93-99. TUTOR / CUIDADOR
    // =========================================================
    if (tiene("progreso del paciente") && rol != "paciente") {
        return "Entrá en Progreso para consultar la evolución y actividad disponible del paciente vinculado."
    }

    if (tiene("alertas del paciente") && (rol == "tutor" || rol == "cuidador")) {
        return "Entrá en Alertas desde la navegación del tutor/cuidador para consultar los avisos disponibles del paciente vinculado."
    }

    if (tiene("vinculo un paciente", "vincular paciente")) {
        return "La vinculación se realiza desde las opciones de paciente disponibles para la cuenta del tutor o profesional, utilizando el mecanismo de vinculación definido por PulsoFirme."
    }

    if (tiene("cambiar de paciente", "cambio de paciente")) {
        return "Utilizá la opción Gestionar/Cambiar paciente dentro de las funciones del vínculo cuando esté disponible para tu cuenta."
    }

    if (tiene("que informacion puedo ver del paciente", "informacion del paciente puedo ver")) {
        return "La información visible depende del rol y de los permisos otorgados. Puede incluir progreso, ejercicios, QuickDASH, alertas, datos médicos y otras áreas autorizadas."
    }

    if (tiene("ver sus ejercicios", "ejercicios del paciente")) {
        return "Podés consultar los ejercicios del paciente desde las opciones de seguimiento disponibles para tu rol, siempre según los permisos correspondientes."
    }

    if (tiene("ver su quickdash", "quickdash del paciente")) {
        return "El resultado QuickDASH del paciente puede consultarse desde su seguimiento cuando tu rol y los permisos de acceso lo permiten."
    }

    // =========================================================
    // 100-111. PROFESIONAL
    // =========================================================
    if (tiene("donde veo mis pacientes", "lista de pacientes") && rol == "profesional") {
        return "Tocá Pacientes en la barra inferior profesional. Allí aparece el listado de pacientes vinculados."
    }

    if (tiene("como agrego un paciente", "agregar paciente") && rol == "profesional") {
        return "La incorporación de pacientes se gestiona desde las funciones de pacientes vinculados. El plan y los permisos determinan las opciones disponibles."
    }

    if (tiene("cuantos pacientes puedo tener") && rol == "profesional") {
        return "El plan profesional base mostrado actualmente en PulsoFirme contempla hasta 5 pacientes."
    }

    if ((tiene("progreso de juan", "progreso de maria", "progreso de lucia", "progreso de diego", "progreso de ana")) && rol == "profesional") {
        return "Entrá en Progreso o en Pacientes, seleccioná al paciente correspondiente y abrí su detalle para consultar el seguimiento disponible."
    }

    if (tiene("ejercicios de un paciente", "ejercicios del paciente") && rol == "profesional") {
        return "Entrá en Pacientes, seleccioná al paciente y revisá la sección Ejercicios de su detalle."
    }

    if (tiene("quickdash de un paciente", "su quickdash") && rol == "profesional") {
        return "Entrá en el detalle del paciente y abrí QuickDASH para consultar los resultados disponibles."
    }

    if (tiene("donde veo la evolucion", "evolucion del paciente") && rol == "profesional") {
        return "Podés consultar la evolución desde Progreso y desde el detalle de cada paciente."
    }

    if (tiene("como descargo un informe", "descargar informe") && rol == "profesional") {
        return "La exportación de reportes PDF está planteada como una función de Premium Profesional. Cuando esté habilitada, se accederá desde las herramientas avanzadas de seguimiento."
    }

    if (tiene("puedo comparar pacientes", "comparar pacientes") && rol == "profesional") {
        return "Sí. La comparación entre pacientes está contemplada dentro de las funciones de Premium Profesional."
    }

    if (tiene("cambio mi matricula", "cambiar matricula") && rol == "profesional") {
        return "Entrá en Cuenta → Datos profesionales → Matrícula profesional para modificar ese dato."
    }

    if (tiene("cambio mi especialidad", "cambiar especialidad") && rol == "profesional") {
        return "Entrá en Cuenta → Datos profesionales → Especialidad."
    }

    if (tiene("lugar de trabajo", "donde pongo mi trabajo") && rol == "profesional") {
        return "Entrá en Cuenta → Datos profesionales → Lugar de trabajo para cargar o modificar esa información."
    }

    // =========================================================
    // RESPUESTA GENERAL
    // =========================================================
    return "No encontré una respuesta específica para esa forma de preguntarlo. " +
            "Probá reformulando la consulta. Puedo responder dudas sobre PulsoFirme, el guante, ejercicios, progreso, alertas, QuickDASH, navegación, Premium, pacientes y funciones de la cuenta."
}

/**
 * Si la persona habla de hacerse daño o de no querer vivir, PulsiBot no
 * deriva a la IA: responde siempre con contención y con dónde pedir ayuda.
 */
private fun respuestaDeCrisis(consulta: String): String? {
    val q = normalizarPB(consulta)
    val senales = listOf(
        "suicid", "matarme", "quitarme la vida", "no quiero vivir", "quiero morir",
        "autolesion", "lastimarme", "cortarme", "hacerme dano", "terminar con todo"
    )
    if (senales.none { q.contains(it) }) return null
    return "Gracias por contármelo. Lo que sentís importa y no tenés que pasarlo solo/a. " +
            "Si estás en peligro ahora, llamá al 911. Podés hablar gratis y en confianza con la línea " +
            "de prevención del suicidio: 135 (CABA y GBA) o 0800 345 1435 desde todo el país, las 24 horas. " +
            "También contale ahora a alguien de confianza que esté cerca tuyo."
}

private fun normalizarPB(texto: String): String {
    val sinTildes = Normalizer.normalize(texto.lowercase(), Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
    return sinTildes
        .replace(Regex("[^a-z0-9ñ ]"), " ")
        .replace(Regex("\\s+"), " ")
        .trim()
}