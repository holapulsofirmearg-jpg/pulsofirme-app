package com.example.pulsofirme.screens

import com.example.pulsofirme.ui.theme.enTema

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.EstadoPacienteTutor

private val VerdeEstadoTutor: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
private val GrisEstadoTutor: Color
    @Composable get() = Color(0xFF6B7280).enTema()
private val FondoEstadoTutor: Color
    @Composable get() = Color(0xFFF7F8F8).enTema()
/**
 * Lo que ve el tutor mientras se cargan los datos del paciente, si no hay
 * paciente vinculado o si falló la carga (por ejemplo, sin internet).
 * Para el estado "Listo" no muestra nada: ahí cada pantalla dibuja sus datos.
 */
@Composable
fun MensajeEstadoTutor(
    estado: EstadoPacienteTutor,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (estado) {
        is EstadoPacienteTutor.Cargando -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = VerdeEstadoTutor,
                    modifier = Modifier.semantics { contentDescription = tr("Cargando datos del paciente") }
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(tr("Cargando datos del paciente…"), fontSize = 14.sp, color = GrisEstadoTutor)
            }
        }

        is EstadoPacienteTutor.SinPaciente -> {
            CajaMensajeTutor(
                titulo = "Todavía no hay un paciente vinculado",
                texto = "Cuando te vincules con el código del paciente, vas a ver acá sus datos.",
                modifier = modifier
            )
        }

        is EstadoPacienteTutor.Error -> {
            Column(modifier = modifier.fillMaxWidth()) {
                CajaMensajeTutor(
                    titulo = "No pudimos cargar los datos",
                    texto = estado.mensaje
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onReintentar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeEstadoTutor)
                ) {
                    Text(tr("Reintentar"), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        is EstadoPacienteTutor.Listo -> Unit
    }
}

/** Aviso de que el paciente decidió no compartir esta información. */
@Composable
fun TarjetaNoCompartido(
    texto: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(FondoEstadoTutor, RoundedCornerShape(16.dp))
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Lock,
            contentDescription = null,
            tint = GrisEstadoTutor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(tr(texto), fontSize = 14.sp, color = GrisEstadoTutor, lineHeight = 19.sp)
    }
}

@Composable
private fun CajaMensajeTutor(
    titulo: String,
    texto: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(FondoEstadoTutor, RoundedCornerShape(16.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            tr(titulo),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A).enTema(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            tr(texto),
            fontSize = 14.sp,
            color = GrisEstadoTutor,
            textAlign = TextAlign.Center,
            lineHeight = 19.sp
        )
    }
}