package com.example.pulsofirme.screens

import androidx.compose.runtime.saveable.rememberSaveable

import com.example.pulsofirme.ui.theme.enTema

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.UserData

private val VerdeBoton: Color
    @Composable get() = Color(0xFF2E9E63).enTema()
private val VerdeTexto: Color
    @Composable get() = Color(0xFF1B7A4B).enTema()
private val AzulCabecera = Color(0xFF1565C0)
private val VerdeFondoSel: Color
    @Composable get() = Color(0xFFEFF6F1).enTema()
private val BordeGris: Color
    @Composable get() = Color(0xFFD8DED9).enTema()
@Composable
fun AboutYouScreen(
    onBack: () -> Unit,
    onContinue: (List<String>, String) -> Unit
) {
    var parkinson by rememberSaveable { mutableStateOf(false) }
    var artritis by rememberSaveable { mutableStateOf(false) }
    var mano by rememberSaveable { mutableStateOf("") }

    val puedeContinuar = (parkinson || artritis) && mano.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.enTema())
            .verticalScroll(rememberScrollState())
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val ancho = size.width
                val alto = size.height
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(ancho, 0f)
                    lineTo(ancho, alto * 0.62f)
                    cubicTo(
                        ancho * 0.62f, alto * 1.08f,
                        ancho * 0.35f, alto * 0.60f,
                        0f, alto * 0.95f
                    )
                    close()
                }
                drawPath(path = path, color = AzulCabecera)
            }

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 10.dp, top = 62.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "Contanos un poco más",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 68.dp)
            )
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {

            Text(
                text = "¿Qué te trae a PulsoFirme?",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A).enTema()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Podés elegir más de una opción.",
                fontSize = 14.sp,
                color = Color(0xFF6B7280).enTema()
            )

            Spacer(modifier = Modifier.height(18.dp))

            OpcionTilde(
                texto = "Parkinson",
                seleccionado = parkinson,
                onClick = { parkinson = !parkinson }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OpcionTilde(
                texto = "Artritis",
                seleccionado = artritis,
                onClick = { artritis = !artritis }
            )

            Spacer(modifier = Modifier.height(28.dp))

            Divider(color = Color(0xFFE8EBE9).enTema(), thickness = 1.dp)

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "¿En qué mano usás el guante?",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A).enTema()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Así ajustamos los ejercicios a la mano que vas a monitorear.",
                fontSize = 14.sp,
                color = Color(0xFF6B7280).enTema(),
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            OpcionMano(
                texto = "Izquierda",
                seleccionado = false,
                habilitada = false,
                onClick = { }
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFF9AA0A6).enTema(),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Por ahora el guante solo está disponible para mano derecha.",
                    fontSize = 12.sp,
                    color = Color(0xFF9AA0A6).enTema()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OpcionMano(
                texto = "Derecha",
                seleccionado = mano == "derecha",
                habilitada = true,
                onClick = { mano = "derecha" }
            )

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = {
                    val diagnosticosElegidos = mutableListOf<String>()
                    if (parkinson) diagnosticosElegidos.add("Parkinson")
                    if (artritis) diagnosticosElegidos.add("Artritis")
                    UserData.diagnosticos = diagnosticosElegidos
                    UserData.mano = mano
                    onContinue(diagnosticosElegidos, mano)
                },
                enabled = puedeContinuar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdeBoton,
                    disabledContainerColor = Color(0xFFE0E0E0).enTema()
                )
            ) {
                Text(
                    text = "Continuar",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
private fun OpcionTilde(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = if (seleccionado) VerdeFondoSel else Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = if (seleccionado) 2.dp else 1.dp,
                color = if (seleccionado) VerdeTexto else BordeGris,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (seleccionado) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = VerdeTexto,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
        }
        Text(
            text = texto,
            fontSize = 16.sp,
            color = if (seleccionado) VerdeTexto else Color(0xFF3D3D3D).enTema()
        )
    }
}

@Composable
private fun OpcionMano(
    texto: String,
    seleccionado: Boolean,
    habilitada: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp)
            .background(
                color = when {
                    !habilitada -> Color(0xFFF5F5F5).enTema()
                    seleccionado -> VerdeFondoSel
                    else -> Color.White
                },
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = if (seleccionado) 2.dp else 1.dp,
                color = if (seleccionado) VerdeTexto else BordeGris,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = habilitada) { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (habilitada) {
            Text(text = "🖐", fontSize = 20.sp)
        } else {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color(0xFFB0B0B0).enTema(),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = texto,
            fontSize = 15.sp,
            color = when {
                !habilitada -> Color(0xFFB0B0B0).enTema()
                seleccionado -> VerdeTexto
                else -> Color(0xFF3D3D3D).enTema()
            },
            textAlign = TextAlign.Center
        )
        if (!habilitada) {
            Text(
                text = "Próximamente",
                fontSize = 11.sp,
                color = Color(0xFFB0B0B0).enTema()
            )
        }
    }
}