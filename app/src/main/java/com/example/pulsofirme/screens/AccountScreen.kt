package com.example.pulsofirme.screens

import com.example.pulsofirme.ui.theme.enTema

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.UserData
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val VerdeAc: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
private val VerdeOscuroAc: Color
    @Composable get() = Color(0xFF1B4332).enTema()
private val VerdeSuaveAc: Color
    @Composable get() = Color(0xFFEAF5EC).enTema()
private val GrisAc: Color
    @Composable get() = Color(0xFF8A8F98).enTema()
private val NegroAc: Color
    @Composable get() = Color(0xFF1A1A1A).enTema()
private val RojoAc: Color
    @Composable get() = Color(0xFFC0392B).enTema()
private val DoradoAc: Color
    @Composable get() = Color(0xFFB98A1E).enTema()
private val DoradoClaroAc: Color
    @Composable get() = Color(0xFFFFF6E3).enTema()
private val DoradoBordeAc: Color
    @Composable get() = Color(0xFFEBD9A8).enTema()
// Iniciales del nombre: "Morena Martinez" -> "MM"
private fun iniciales(nombre: String): String {
    val partes = nombre.trim().split(" ").filter { it.isNotBlank() }
    return when {
        partes.isEmpty() -> "?"
        partes.size == 1 -> partes[0].take(1).uppercase()
        else -> (partes[0].take(1) + partes[1].take(1)).uppercase()
    }
}

// Fecha de hoy: "10 de agosto de 2026"
// Mensaje listo para mandar por WhatsApp, mail o SMS
private fun mensajeInvitacion(nombre: String, codigo: String): String =
    trf("¡Hola! Soy {0} y uso PulsoFirme para mi rehabilitación. ", nombre) +
            "Te invito a acompañarme desde la app 💚\n\n" +
            "1. Abrí PulsoFirme y tocá «Acceder como cuidador».\n" +
            "2. Elegí Tutor (familiar o amigo) o Profesional (médico o kinesiólogo).\n" +
            trf("3. Cuando te pida el código, escribí: {0}\n\n", codigo) +
            "Este código es solo para vos, no lo compartas."

private fun fechaDeHoy(): String {
    val formato = SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "AR"))
    return formato.format(Calendar.getInstance().time)
}

@Composable
fun AccountScreen(
    rutaActual: String,
    onNavigate: (String) -> Unit,
    onEditarDatos: () -> Unit = {},
    onDiagnostico: () -> Unit = {},
    onPrivacidad: () -> Unit = {},
    onAyuda: () -> Unit = {},
    onAjustes: () -> Unit = {},
    onDudas: () -> Unit = {},
    onPremium: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    codigoVinculacion: String = "",
    cantidadTutoresVinculados: Int = 0
) {
    val nombre = UserData.nombreUsuario.ifBlank { "Usuario" }
    val contexto = LocalContext.current
    val codigoLimpio = codigoVinculacion.filter { it.isDigit() }

    fun invitar() {
        val enviar = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Te invito a acompañarme en PulsoFirme")
            putExtra(Intent.EXTRA_TEXT, mensajeInvitacion(nombre, codigoLimpio))
        }
        contexto.startActivity(Intent.createChooser(enviar, "Invitar con…"))
    }

    fun copiarCodigo() {
        val portapapeles = contexto.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        portapapeles.setPrimaryClip(ClipData.newPlainText("Código PulsoFirme", codigoLimpio))
        Avisos.mostrar("Código copiado")
    }

    Scaffold(
        containerColor = Color.White.enTema(),
        bottomBar = {
            NavigationBar(containerColor = Color.White.enTema()) {
                itemsMenuInferior.forEach { item ->
                    NavigationBarItem(
                        selected = rutaActual == item.ruta,
                        onClick = { onNavigate(item.ruta) },
                        icon = { Icon(item.icono, contentDescription = tr(item.label)) },
                        label = { Text(tr(item.label), fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = VerdeAc,
                            selectedTextColor = VerdeAc,
                            unselectedIconColor = Color(0xFF9CA3AF).enTema(),
                            unselectedTextColor = Color(0xFF9CA3AF).enTema(),
                            indicatorColor = VerdeSuaveAc
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

            Text(tr("Cuenta"), fontSize = 15.sp, color = GrisAc)

            Spacer(Modifier.height(16.dp))

            // ---------- Perfil del usuario ----------
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(66.dp)
                        .clip(CircleShape)
                        .background(VerdeOscuroAc),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tr(iniciales(nombre)),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = tr(nombre),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = VerdeOscuroAc
                    )
                    Text(
                        text = trf("Paciente, desde el {0}", fechaDeHoy()),
                        fontSize = 14.sp,
                        color = GrisAc
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            // ---------- Tarjeta Premium ----------
            TarjetaPremium(
                esPremium = UserData.esPremium,
                onClick = onPremium
            )

            Spacer(Modifier.height(26.dp))

            // ---------- Perfil ----------
            Text(tr("Perfil"), fontSize = 14.sp, color = GrisAc)
            Spacer(Modifier.height(6.dp))

            FilaOpcion("Editar datos personales", onEditarDatos)
            FilaOpcion("Diagnóstico y datos médicos", onDiagnostico)

            Spacer(Modifier.height(24.dp))

            // ---------- Cuidadores ----------
            Text(tr("Cuidadores"), fontSize = 14.sp, color = GrisAc)
            Spacer(Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(VerdeSuaveAc)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.enTema()),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.PersonAddAlt,
                            contentDescription = null,
                            tint = VerdeAc,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tr(if (cantidadTutoresVinculados > 0) {
                                if (cantidadTutoresVinculados == 1) {
                                    "1 tutor vinculado con vos"
                                } else {
                                    trf("{0} tutores vinculados con vos", cantidadTutoresVinculados)
                                }
                            } else {
                                "Nadie se vinculó con vos todavía"
                            }),
                            fontSize = 15.sp,
                            color = Color(0xFF374151).enTema()
                        )

                        if (codigoVinculacion.isNotBlank()) {
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = tr("Código de vinculación"),
                                fontSize = 12.sp,
                                color = GrisAc
                            )
                            Text(
                                text = tr(codigoVinculacion),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = VerdeOscuroAc,
                                letterSpacing = 3.sp
                            )
                            Text(
                                text = tr("Compartilo solo con tu tutor o profesional."),
                                fontSize = 12.sp,
                                color = GrisAc
                            )
                        } else {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = tr("Generando código de vinculación…"),
                                fontSize = 13.sp,
                                color = GrisAc
                            )
                        }
                    }
                }

                // ---------- Invitar a alguien ----------
                if (codigoLimpio.isNotBlank()) {
                    Spacer(Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { invitar() },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VerdeAc)
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(tr("Invitar a alguien"), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.width(10.dp))
                        OutlinedButton(
                            onClick = { copiarCodigo() },
                            modifier = Modifier.heightIn(min = 52.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, VerdeAc)
                        ) {
                            Icon(
                                Icons.Filled.ContentCopy,
                                contentDescription = tr("Copiar código"),
                                tint = VerdeAc,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(tr("Copiar"), fontSize = 15.sp, color = VerdeAc, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ---------- Preferencias ----------
            Text(tr("Preferencias"), fontSize = 14.sp, color = GrisAc)
            Spacer(Modifier.height(6.dp))

            FilaOpcion("Privacidad y datos", onPrivacidad)
            FilaOpcion("Ayuda y soporte", onAyuda)

            Spacer(Modifier.height(30.dp))

            // ---------- Enlaces centrados ----------
            Text(
                text = tr("Ajustes"),
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = NegroAc,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAjustes() }
                    .padding(vertical = 14.dp)
            )

            Text(
                text = tr("Dudas frecuentes"),
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = NegroAc,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDudas() }
                    .padding(vertical = 14.dp)
            )

            Text(
                text = tr("Cerrar sesión"),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = RojoAc,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCerrarSesion() }
                    .padding(vertical = 14.dp)
            )

            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
private fun TarjetaPremium(esPremium: Boolean, onClick: () -> Unit) {
    val forma = RoundedCornerShape(18.dp)

    if (esPremium) {
        // ----- Ya tiene el plan -----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(forma)
                .background(
                    Brush.horizontalGradient(
                        listOf(VerdeOscuroAc, VerdeAc)
                    )
                )
                .clickable { onClick() }
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.WorkspacePremium,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tr("Premium Guiada"),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = tr("Tu plan está activo"),
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
            Icon(
                Icons.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.8f)
            )
        }
    } else {
        // ----- Invitación a mejorar el plan -----
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(forma)
                .background(DoradoClaroAc)
                .border(1.dp, DoradoBordeAc, forma)
                .clickable { onClick() }
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.White.enTema()),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.WorkspacePremium,
                        contentDescription = null,
                        tint = DoradoAc,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tr("Premium Guiada"),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B4E0B).enTema()
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = tr("Estás usando la versión gratuita"),
                        fontSize = 13.sp,
                        color = Color(0xFF8C7434).enTema()
                    )
                }
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = DoradoAc
                )
            }

            Spacer(Modifier.height(14.dp))

            BeneficioPremium("Rutinas personalizadas según tu diagnóstico")
            Spacer(Modifier.height(8.dp))
            BeneficioPremium("Informes para compartir con tu profesional")

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DoradoAc)
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tr("Conocer el plan"),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun BeneficioPremium(texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = DoradoAc,
            modifier = Modifier.size(17.dp)
        )
        Spacer(Modifier.width(9.dp))
        Text(
            text = tr(texto),
            fontSize = 13.sp,
            color = Color(0xFF6B5A2E).enTema(),
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun FilaOpcion(texto: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = tr(texto),
            fontSize = 17.sp,
            color = NegroAc,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF9CA3AF).enTema()
        )
    }
}