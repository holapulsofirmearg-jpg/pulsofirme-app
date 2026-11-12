package com.example.pulsofirme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.R

private val TealFondo = Color(0xFF1B7A6E)
private val SubtituloColor = Color(0xFFCFE9E3)
private val BotonSiguiente = Color(0xFF0D2B2B)
private val BarraIndicador = Color(0x33FFFFFF)

@Composable
fun CaregiverRoleScreen(
    onBack: () -> Unit,
    onNext: (rol: String) -> Unit
) {
    var rolSeleccionado by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = TealFondo
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TealFondo)
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Flecha de volver
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Título
            Text(
                text = "¿Eres profesional o Tutor?",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtítulo
            Text(
                text = "Elegí cómo vas a usar PulsoFirme para personalizar tu experiencia",
                fontSize = 14.sp,
                color = SubtituloColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            // Las opciones ocupan el espacio del medio. Si no entran
            // (celular chico o letra grande), se deslizan y el botón
            // "Siguiente" queda siempre visible abajo.
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
            ) {
                // Opción Tutor
                RolOpcion(
                    etiqueta = "Tutor",
                    imagenRes = R.drawable.tutor,
                    seleccionado = rolSeleccionado == "tutor",
                    onClick = { rolSeleccionado = "tutor" }
                )

                // Opción Profesional
                RolOpcion(
                    etiqueta = "Profesional",
                    imagenRes = R.drawable.profesional,
                    seleccionado = rolSeleccionado == "profesional",
                    onClick = { rolSeleccionado = "profesional" }
                )
            }

            // Barra indicadora decorativa
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(BarraIndicador, RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Botón Siguiente
            Button(
                onClick = { rolSeleccionado?.let { onNext(it) } },
                enabled = rolSeleccionado != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BotonSiguiente,
                    disabledContainerColor = BotonSiguiente.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = "Siguiente",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun RolOpcion(
    etiqueta: String,
    imagenRes: Int?,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(136.dp)
                .background(
                    color = if (seleccionado) Color.White.copy(alpha = 0.08f) else Color.Transparent,
                    shape = CircleShape
                )
                .border(
                    width = if (seleccionado) 2.dp else 0.dp,
                    color = if (seleccionado) Color.White else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (imagenRes != null) {
                Image(
                    painter = painterResource(id = imagenRes),
                    contentDescription = etiqueta,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(126.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = etiqueta,
                    tint = Color.White,
                    modifier = Modifier.size(90.dp)
                )
            }

            if (seleccionado) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(TealFondo, CircleShape)
                        .padding(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Seleccionado",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = etiqueta,
            fontSize = 18.sp,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
            color = Color.White
        )
    }
}