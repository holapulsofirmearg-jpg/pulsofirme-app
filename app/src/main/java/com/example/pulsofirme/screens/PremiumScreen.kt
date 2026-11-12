package com.example.pulsofirme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pulsofirme.UserData
import kotlinx.coroutines.delay

/**
 * MODO DEMO: mientras sea true, el checkout es simulado. No se cobra nada,
 * no se guardan datos de tarjeta y Premium se activa solo en el teléfono.
 * Ponelo en false antes de lanzar la app: el botón avisa que el pago
 * todavía no está disponible.
 */
private const val MODO_DEMO_PREMIUM = true

/** Precio que se muestra en el checkout. Cambialo acá. */
private const val PRECIO_MENSUAL = "$4.999"

private val VerdePm = Color(0xFF1B7A4B)
private val VerdeBotonPm = Color(0xFF2E9E63)
private val VerdeOscuroPm = Color(0xFF1B4332)
private val VerdeFondoPm = Color(0xFFEFF6F1)
private val VerdeIconoPm = Color(0xFFDFF3E4)
private val GrisPm = Color(0xFF6B7280)
private val GrisBordePm = Color(0xFFE5E7EB)
private val NegroPm = Color(0xFF1A1A1A)
private val AmarilloDemo = Color(0xFFFFF7E0)
private val AmarilloBordeDemo = Color(0xFFF2D58A)
private val CelesteMp = Color(0xFF009EE3)

private data class Beneficio(
    val emoji: String,
    val titulo: String,
    val descripcion: String
)

private val beneficios = listOf(
    Beneficio(
        emoji = "🎚",
        titulo = "Dificultad adaptativa",
        descripcion = "Sube o baja según el último QuickDASH y cómo se sintió el paciente en la encuesta."
    ),
    Beneficio(
        emoji = "💊",
        titulo = "Rutinas según la medicación",
        descripcion = "Una versión para cuando el paciente está \"on\" y otra más suave para cuando está \"off\"."
    ),
    Beneficio(
        emoji = "🏋",
        titulo = "12 ejercicios exclusivos",
        descripcion = "6 para Parkinson y 6 para artritis: manos, hombros, piernas y pies."
    ),
    Beneficio(
        emoji = "👥",
        titulo = "Más tutores",
        descripcion = "Sumá más familiares o cuidadores para que acompañen y vean tu progreso."
    ),
    Beneficio(
        emoji = "📋",
        titulo = "Rutinas según tu diagnóstico",
        descripcion = "Ejercicios elegidos para tu condición y la mano que más necesitás trabajar."
    ),
    Beneficio(
        emoji = "📄",
        titulo = "Informes para tu profesional",
        descripcion = "Reportes claros de tu evolución para compartir en la consulta."
    )
)

// Pasos de la pantalla
private enum class PasoPremium { INFO, CHECKOUT }

private enum class MetodoPago { MERCADO_PAGO, TARJETA }

@Composable
fun PremiumScreen(
    onBack: () -> Unit
) {
    var paso by remember { mutableStateOf(PasoPremium.INFO) }
    var procesando by remember { mutableStateOf(false) }
    var aprobado by remember { mutableStateOf(false) }
    var mostrarProximamente by remember { mutableStateOf(false) }
    var comprobante by remember { mutableStateOf("") }
    val contexto = LocalContext.current

    // Simula la respuesta del pago (2,5 segundos)
    LaunchedEffect(procesando) {
        if (procesando) {
            delay(2500)
            comprobante = "PF-" + (100000..999999).random()
            procesando = false
            aprobado = true
            // Se guarda en la cuenta: sigue activo aunque cierres sesión o cambies de celular
            UserData.activarPremiumDemo {
                aprobado = false
                Toast.makeText(
                    contexto,
                    "No se pudo activar Premium. Revisá tu conexión e intentá de nuevo.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // ---------- PROCESANDO ----------
    if (procesando) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(horizontal = 32.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = VerdePm, strokeWidth = 3.dp)
                Spacer(Modifier.height(18.dp))
                Text(
                    "Procesando pago…",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NegroPm
                )
                Spacer(Modifier.height(4.dp))
                Text("No cierres la app", fontSize = 14.sp, color = GrisPm)
            }
        }
    }

    // ---------- PAGO APROBADO ----------
    if (aprobado) {
        AlertDialog(
            onDismissRequest = { },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(VerdeIconoPm, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = VerdePm,
                        modifier = Modifier.size(44.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "¡Pago aprobado!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdePm,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Activaste Premium Guiada. Tus rutinas ahora se adaptan a vos y ya podés usar todos los ejercicios exclusivos.",
                        fontSize = 16.sp,
                        color = Color(0xFF374151),
                        textAlign = TextAlign.Center,
                        lineHeight = 23.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "Comprobante $comprobante · demostración",
                        fontSize = 13.sp,
                        color = GrisPm,
                        textAlign = TextAlign.Center
                    )

                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        aprobado = false
                        onBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()

                        .heightIn(min = 56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBotonPm)
                ) {
                    Text(
                        text = "¡Empezar!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        )
    }

    // ---------- SOLO SI MODO_DEMO_PREMIUM = false ----------
    if (mostrarProximamente) {
        AlertDialog(
            onDismissRequest = { mostrarProximamente = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "Muy pronto",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdeOscuroPm
                )
            },
            text = {
                Text(
                    text = "El pago de Premium todavía no está disponible dentro de la app. Lo vamos a habilitar en la próxima versión.",
                    fontSize = 15.sp,
                    color = GrisPm,
                    lineHeight = 21.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { mostrarProximamente = false }) {
                    Text("Entendido", color = VerdePm, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        )
    }

    when (paso) {
        PasoPremium.INFO -> PantallaInfo(
            onBack = onBack,
            onDesactivarDemo = {
                UserData.desactivarPremiumDemo {
                    Toast.makeText(contexto, "No se pudo desactivar. Revisá tu conexión.", Toast.LENGTH_SHORT).show()
                }
            },
            onActivar = {
                if (MODO_DEMO_PREMIUM) paso = PasoPremium.CHECKOUT
                else mostrarProximamente = true
            }
        )

        PasoPremium.CHECKOUT -> PantallaCheckout(
            onBack = { paso = PasoPremium.INFO },
            onPagar = { procesando = true }
        )
    }
}

// =====================================================================
// PASO 1: QUÉ INCLUYE
// =====================================================================

@Composable
private fun PantallaInfo(
    onBack: () -> Unit,
    onDesactivarDemo: () -> Unit,
    onActivar: () -> Unit
) {
    // El tutor no paga: hereda el plan del paciente
    val esTutor = UserData.rol == "tutor"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(48.dp))

        Encabezado(titulo = "Premium Guiada", onBack = onBack)

        Spacer(Modifier.height(20.dp))

        // Tarjeta principal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerdeFondoPm, RoundedCornerShape(18.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (UserData.esPremium) "Ya tenés Premium" else "Entrená con una guía a tu medida",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = VerdePm,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Tus rutinas se adaptan a cómo estás hoy: a tu medicación, a tu evolución y a cómo te sentís.",
                fontSize = 15.sp,
                color = Color(0xFF3D3D3D),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
            if (!UserData.esPremium && !esTutor) {
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "$PRECIO_MENSUAL por mes",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = VerdeOscuroPm
                )
                Text("Cancelás cuando quieras", fontSize = 13.sp, color = GrisPm)
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Qué incluye",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = GrisPm
        )
        Spacer(Modifier.height(8.dp))

        beneficios.forEach { b ->
            FilaBeneficio(b)
        }

        Spacer(Modifier.height(24.dp))

        if (esTutor) {
            // El tutor no puede comprar: ve el plan que tiene el paciente
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VerdeFondoPm, RoundedCornerShape(14.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (UserData.esPremium) "Tu paciente tiene Premium" else "Tu paciente usa el plan gratis",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VerdePm,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Como tutor, accedés a los mismos beneficios que tenga activos tu paciente.",
                    fontSize = 14.sp,
                    color = GrisPm,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        } else if (!UserData.esPremium) {
            Button(
                onClick = onActivar,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 62.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeBotonPm)
            ) {
                Text(
                    text = "Activar Premium",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VerdeFondoPm, RoundedCornerShape(14.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = VerdePm)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Tu plan Premium está activo",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VerdePm
                )
            }

            // Solo en la demostración: para volver a mostrar el checkout
            if (MODO_DEMO_PREMIUM) {
                TextButton(
                    onClick = onDesactivarDemo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                ) {
                    Text("Desactivar Premium (demostración)", color = GrisPm, fontSize = 14.sp)
                }
            }
        }

        Spacer(Modifier.height(36.dp))
    }
}

// =====================================================================
// PASO 2: CHECKOUT (simulado)
// =====================================================================

@Composable
private fun PantallaCheckout(
    onBack: () -> Unit,
    onPagar: () -> Unit
) {
    var metodo by remember { mutableStateOf(MetodoPago.MERCADO_PAGO) }

    // Datos de tarjeta: solo viven en esta pantalla, nunca se guardan ni se envían
    var numero by remember { mutableStateOf("") }      // solo dígitos
    var titular by remember { mutableStateOf("") }
    var vencimiento by remember { mutableStateOf("") } // solo dígitos MMAA
    var cvv by remember { mutableStateOf("") }

    val mes = vencimiento.take(2).toIntOrNull() ?: 0
    val tarjetaValida = numero.length == 16 &&
            titular.isNotBlank() &&
            vencimiento.length == 4 && mes in 1..12 &&
            cvv.length in 3..4

    val puedePagar = metodo == MetodoPago.MERCADO_PAGO || tarjetaValida

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(48.dp))

        Encabezado(titulo = "Pagar Premium", onBack = onBack)

        Spacer(Modifier.height(16.dp))

        // Cartel de demostración
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(AmarilloDemo)
                .border(1.dp, AmarilloBordeDemo, RoundedCornerShape(14.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Info, contentDescription = null, tint = Color(0xFF9A6B00), modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                "Modo demostración: no se realizan cobros. Usá datos de prueba, no tu tarjeta real.",
                fontSize = 13.sp,
                color = Color(0xFF5C4400),
                lineHeight = 18.sp
            )
        }

        Spacer(Modifier.height(18.dp))

        // Resumen
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GrisBordePm, RoundedCornerShape(16.dp))
                .padding(18.dp)
        ) {
            Text("Resumen", fontSize = 14.sp, color = GrisPm)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Premium Guiada", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NegroPm)
                    Text("Plan mensual · cancelás cuando quieras", fontSize = 13.sp, color = GrisPm)
                }
                Text(PRECIO_MENSUAL, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = VerdeOscuroPm)
            }
        }

        Spacer(Modifier.height(22.dp))

        Text("Método de pago", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = GrisPm)
        Spacer(Modifier.height(10.dp))

        OpcionMetodo(
            icono = Icons.Filled.AccountBalanceWallet,
            colorIcono = CelesteMp,
            titulo = "Mercado Pago",
            detalle = "Pagá con tu cuenta o dinero disponible",
            seleccionado = metodo == MetodoPago.MERCADO_PAGO,
            onClick = { metodo = MetodoPago.MERCADO_PAGO }
        )

        Spacer(Modifier.height(10.dp))

        OpcionMetodo(
            icono = Icons.Filled.CreditCard,
            colorIcono = VerdePm,
            titulo = "Tarjeta de crédito o débito",
            detalle = "Visa, Mastercard, American Express",
            seleccionado = metodo == MetodoPago.TARJETA,
            onClick = { metodo = MetodoPago.TARJETA }
        )

        if (metodo == MetodoPago.TARJETA) {
            Spacer(Modifier.height(18.dp))

            CampoPago(
                valor = numero,
                onCambio = { numero = it.filter(Char::isDigit).take(16) },
                etiqueta = "Número de tarjeta",
                teclado = KeyboardType.Number,
                transformacion = FormatoTarjeta
            )
            Spacer(Modifier.height(12.dp))
            CampoPago(
                valor = titular,
                onCambio = { titular = it.take(40) },
                etiqueta = "Nombre como figura en la tarjeta",
                teclado = KeyboardType.Text
            )
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth()) {
                CampoPago(
                    valor = vencimiento,
                    onCambio = { vencimiento = it.filter(Char::isDigit).take(4) },
                    etiqueta = "Vence (MM/AA)",
                    teclado = KeyboardType.Number,
                    transformacion = FormatoVencimiento,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(12.dp))
                CampoPago(
                    valor = cvv,
                    onCambio = { cvv = it.filter(Char::isDigit).take(4) },
                    etiqueta = "CVV",
                    teclado = KeyboardType.NumberPassword,
                    modifier = Modifier.weight(1f)
                )
            }

            TextButton(
                onClick = {
                    numero = "4509953566233704"
                    titular = "APRO PRUEBA"
                    vencimiento = "1130"
                    cvv = "123"
                },
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text("Completar con tarjeta de prueba", color = VerdePm, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        } else {
            Spacer(Modifier.height(14.dp))
            Text(
                "Al tocar Pagar, en la versión real se abriría Mercado Pago para confirmar. En la demostración se aprueba directo.",
                fontSize = 13.sp,
                color = GrisPm,
                lineHeight = 18.sp
            )
        }

        Spacer(Modifier.height(22.dp))

        Button(
            onClick = onPagar,
            enabled = puedePagar,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 62.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (metodo == MetodoPago.MERCADO_PAGO) CelesteMp else VerdeBotonPm,
                disabledContainerColor = Color(0xFFBFC5CC)
            )
        ) {
            Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (metodo == MetodoPago.MERCADO_PAGO) "Pagar con Mercado Pago" else "Pagar $PRECIO_MENSUAL",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            "Pago protegido. PulsoFirme no guarda los datos de tu tarjeta.",
            fontSize = 12.sp,
            color = GrisPm,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(36.dp))
    }
}

// =====================================================================
// PIEZAS
// =====================================================================

@Composable
private fun Encabezado(titulo: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = VerdeOscuroPm
            )
        }
        Spacer(Modifier.width(4.dp))
        Text(
            text = titulo,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeOscuroPm
        )
    }
}

@Composable
private fun OpcionMetodo(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    colorIcono: Color,
    titulo: String,
    detalle: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (seleccionado) VerdeFondoPm else Color.White)
            .border(
                width = if (seleccionado) 2.dp else 1.dp,
                color = if (seleccionado) VerdePm else GrisBordePm,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(role = Role.RadioButton) { onClick() }
            .heightIn(min = 72.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color.White, CircleShape)
                .border(1.dp, GrisBordePm, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NegroPm)
            Text(detalle, fontSize = 13.sp, color = GrisPm, lineHeight = 18.sp)
        }
        RadioButton(
            selected = seleccionado,
            onClick = null,
            colors = RadioButtonDefaults.colors(selectedColor = VerdePm)
        )
    }
}

@Composable
private fun CampoPago(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    teclado: KeyboardType,
    modifier: Modifier = Modifier,
    transformacion: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        visualTransformation = transformacion,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VerdePm,
            unfocusedBorderColor = GrisBordePm,
            focusedLabelColor = VerdePm
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun FilaBeneficio(b: Beneficio) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(VerdeFondoPm, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = b.emoji, fontSize = 20.sp)
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = b.titulo,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = NegroPm
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = b.descripcion,
                fontSize = 14.sp,
                color = GrisPm,
                lineHeight = 20.sp
            )
        }
    }
}

// =====================================================================
// FORMATOS DE LOS CAMPOS
// =====================================================================

/** Muestra el número de tarjeta como "1234 5678 9012 3456". */
private object FormatoTarjeta : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digitos = text.text
        val conEspacios = digitos.chunked(4).joinToString(" ")
        val mapeo = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = when {
                offset <= 4 -> offset
                offset <= 8 -> offset + 1
                offset <= 12 -> offset + 2
                else -> offset + 3
            }.coerceAtMost(conEspacios.length)

            override fun transformedToOriginal(offset: Int): Int = when {
                offset <= 4 -> offset
                offset <= 9 -> offset - 1
                offset <= 14 -> offset - 2
                else -> offset - 3
            }.coerceIn(0, digitos.length)
        }
        return TransformedText(AnnotatedString(conEspacios), mapeo)
    }
}


/** Muestra el vencimiento como "MM/AA". */
private object FormatoVencimiento : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val d = text.text
        val conBarra = if (d.length > 2) d.substring(0, 2) + "/" + d.substring(2) else d
        val mapeo = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                (if (offset <= 2) offset else offset + 1).coerceAtMost(conBarra.length)

            override fun transformedToOriginal(offset: Int): Int =
                (if (offset <= 2) offset else offset - 1).coerceIn(0, d.length)
        }
        return TransformedText(AnnotatedString(conBarra), mapeo)
    }
}