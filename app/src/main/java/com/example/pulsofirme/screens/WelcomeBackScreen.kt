package com.example.pulsofirme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

private val VerdeTitulo = Color(0xFF1B7A4B)
private val VerdeBoton = Color(0xFF1B7A4B)
private val AzulTexto = Color(0xFF16324F)
private val VerdeFondoIcono = Color(0xFFDFF3E4)
private val VerdeCirculoImagen = Color(0xFFE3F5E8)

@Composable
fun WelcomeBackScreen(
    nombre: String,
    onBack: () -> Unit,
    onIrAMiPanel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(220.dp)
                .background(VerdeCirculoImagen, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.chico),
                contentDescription = "Bienvenida",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(170.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡Qué alegría verte\nnuevamente!",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeTitulo,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = nombre,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AzulTexto,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Podrás ver nuevamente:",
            fontSize = 15.sp,
            color = Color(0xFF9AA3B2),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(14.dp))

        ItemBienvenida(icono = Icons.Filled.History, texto = "Tu historial de sesiones")
        Spacer(modifier = Modifier.height(12.dp))
        ItemBienvenida(icono = Icons.Filled.Notifications, texto = "Tus notificaciones")
        Spacer(modifier = Modifier.height(12.dp))
        ItemBienvenida(icono = Icons.Filled.BarChart, texto = "Tu progreso y estadísticas")

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onIrAMiPanel,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeBoton)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Ir a mi panel",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun ItemBienvenida(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    texto: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(VerdeFondoIcono, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = VerdeTitulo,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = texto,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = AzulTexto
        )
    }
}