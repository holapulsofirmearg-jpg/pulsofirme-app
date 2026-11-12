package com.example.pulsofirme.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.Accesibilidad
import com.example.pulsofirme.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val VerdeMarca = Color(0xFF00A86B)
private val VerdeOnda = Color(0xFF43A047)
private val AzulMarca = Color(0xFF1565C0)

/**
 * Pantalla de inicio animada (unos 2 segundos):
 * 1. La mano sube y aparece con un rebote suave.
 * 2. Hace un temblor leve y se estabiliza (lo que hace el guante).
 * 3. Salen pulsos verdes desde atrás de la mano.
 * 4. Aparecen el nombre y el subtítulo.
 * 5. Aparece el botón "Tocá para comenzar", que late suave.
 *
 * Con "Reducir animaciones" activado se muestra todo quieto.
 * Se puede tocar en cualquier momento para seguir.
 */
@Composable
fun SplashScreen(onContinue: () -> Unit) {
    val sinMovimiento = Accesibilidad.reducirAnimaciones

    // Mano
    val manoAlfa = remember { Animatable(if (sinMovimiento) 1f else 0f) }
    val manoEscala = remember { Animatable(if (sinMovimiento) 1f else 0.6f) }
    val manoSubida = remember { Animatable(if (sinMovimiento) 0f else 60f) }
    val manoGiro = remember { Animatable(0f) }

    // Pulsos
    val pulsosAlfa = remember { Animatable(0f) }

    // Textos
    val tituloAlfa = remember { Animatable(if (sinMovimiento) 1f else 0f) }
    val tituloSubida = remember { Animatable(if (sinMovimiento) 0f else 24f) }
    val subtituloAlfa = remember { Animatable(if (sinMovimiento) 1f else 0f) }
    val botonAlfa = remember { Animatable(if (sinMovimiento) 1f else 0f) }

    LaunchedEffect(Unit) {
        if (sinMovimiento) return@LaunchedEffect

        // 1. La mano aparece con rebote
        launch { manoAlfa.animateTo(1f, tween(450, easing = FastOutSlowInEasing)) }
        launch {
            manoEscala.animateTo(
                1f,
                spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessLow)
            )
        }
        manoSubida.animateTo(
            0f,
            spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow)
        )

        // 2. Temblor leve que se va calmando hasta quedar firme
        manoGiro.animateTo(
            0f,
            keyframes {
                durationMillis = 650
                0f at 0
                -5f at 80
                4.5f at 170
                -3.5f at 260
                2.5f at 350
                -1.5f at 440
                0.8f at 530
                0f at 650
            }
        )

        // 3. Pulsos desde atrás de la mano
        launch { pulsosAlfa.animateTo(1f, tween(400)) }

        // 4. Nombre y subtítulo
        launch { tituloAlfa.animateTo(1f, tween(450)) }
        tituloSubida.animateTo(0f, tween(500, easing = FastOutSlowInEasing))
        subtituloAlfa.animateTo(1f, tween(400))

        // 5. "Tocá para continuar"
        delay(150)
        botonAlfa.animateTo(1f, tween(450))
    }

    // Animaciones que se repiten: los pulsos y el latido del texto
    val infinito = rememberInfiniteTransition(label = "splash")
    val progresoPulso by infinito.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Restart),
        label = "pulso"
    )
    val latido by infinito.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(tween(1100, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "latido"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClickLabel = "Continuar"
            ) { onContinue() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            // Pulsos: dos anillos verdes que se agrandan y se desvanecen
            if (!sinMovimiento) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radioBase = size.minDimension * 0.30f
                    listOf(0f, 0.5f).forEach { desfase ->
                        val p = (progresoPulso + desfase) % 1f
                        drawCircle(
                            color = VerdeOnda.copy(alpha = (1f - p) * 0.35f * pulsosAlfa.value),
                            radius = radioBase + p * radioBase * 0.75f,
                            style = Stroke(width = (4f - p * 3f).dp.toPx())
                        )
                    }
                }
            }

            Image(
                painter = painterResource(id = R.drawable.icon2),
                contentDescription = "Logo PulsoFirme",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(130.dp)
                    .graphicsLayer {
                        alpha = manoAlfa.value
                        scaleX = manoEscala.value
                        scaleY = manoEscala.value
                        translationY = manoSubida.value.dp.toPx()
                        rotationZ = manoGiro.value
                        // Gira desde la muñeca, como una mano de verdad
                        transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 0.9f)
                    }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // "Pulso" en azul y "Firme" en verde, separados (igual que en el onboarding)
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = AzulMarca)) { append("Pulso ") }
                withStyle(SpanStyle(color = VerdeMarca)) { append("Firme") }
                withStyle(SpanStyle(color = Color(0xFF9CA3AF), fontWeight = FontWeight.SemiBold)) { append("  ARG") }
            },
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.graphicsLayer {
                alpha = tituloAlfa.value
                translationY = tituloSubida.value.dp.toPx()
            }
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Tecnología que acompaña cada movimiento",
            fontSize = 14.sp,
            color = Color(0xFF6B7280),
            modifier = Modifier.graphicsLayer { alpha = subtituloAlfa.value }
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Botón "Comenzar": aparece al final y late muy suave
        Button(
            onClick = onContinue,
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .fillMaxWidth()
                .heightIn(min = 58.dp)
                .graphicsLayer {
                    alpha = botonAlfa.value
                    val s = if (sinMovimiento) 1f else latido
                    scaleX = s
                    scaleY = s
                },
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeMarca),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 1.dp)
        ) {
            Text(
                text = "Tocá para comenzar",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}