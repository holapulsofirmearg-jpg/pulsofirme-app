package com.example.pulsofirme.screens

import com.example.pulsofirme.ui.theme.enTema

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.R
import kotlinx.coroutines.launch

data class OnboardingPage(
    val imageRes: Int?,
    val titlePlain: String,
    val titleBlue: String,
    val titleGreen: String,
    val description: String
)

val onboardingPages = listOf(

    OnboardingPage(
        imageRes = R.drawable.imagen1,
        titlePlain = "Bienvenidos a ",
        titleBlue = "Pulso",
        titleGreen = " Firme",
        description = "Tu compañero para el monitoreo y rehabilitación de Parkinson y Artritis. Realizá ejercicios guiados para mejorar la movilidad de tus manos y otras extremidades, y seguí tu progreso día a día. Estamos acá para acompañarte en cada movimiento."
    ),

    OnboardingPage(
        imageRes = R.drawable.imagen2,
        titlePlain = "Todo tu ",
        titleBlue = "progreso",
        titleGreen = ", en un solo lugar",
        description = "Realizá ejercicios personalizados para mejorar tu movilidad, seguí tu evolución día a día y recibí recomendaciones para tu bienestar. Estamos con vos en cada paso de tu tratamiento."
    ),

    OnboardingPage(
        imageRes = R.drawable.imagen3,
        titlePlain = "",
        titleBlue = "Control y acceso",
        titleGreen = " compartido de datos",
        description = "¡Excelente! Ahora, tu médico, tutor, padre o amigo autorizado podrá monitorizar tu progreso, ver tu historial de sesiones y estadísticas en tiempo real para apoyarte en tu recuperación. Todo controlado por ti desde la app."
    )
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {

    val pagerState = rememberPagerState(
        pageCount = {
            onboardingPages.size
        }
    )

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00695C).enTema())
            .padding(24.dp)
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->

            val data = onboardingPages[page]

            // Si el texto no entra (celular chico o letra grande), se desliza
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                if (data.imageRes != null) {

                    Image(
                        painter = painterResource(
                            id = data.imageRes
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)
                            .aspectRatio(1.6f)
                            .clip(
                                RoundedCornerShape(16.dp)
                            )
                    )

                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )

                } else {

                    Spacer(
                        modifier = Modifier.height(60.dp)
                    )
                }

                Text(
                    text = buildAnnotatedString {

                        withStyle(
                            SpanStyle(
                                color = Color.White
                            )
                        ) {
                            append(data.titlePlain)
                        }

                        withStyle(
                            SpanStyle(
                                color = Color(0xFF4FC3F7).enTema()
                            )
                        ) {
                            append(data.titleBlue)
                        }

                        withStyle(
                            SpanStyle(
                                color = Color(0xFF8BC34A).enTema()
                            )
                        ) {
                            append(data.titleGreen)
                        }
                    },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 28.sp
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = data.description,
                    color = Color.White,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }
        }

        // --------------------------------------------------
        // PARTE INFERIOR
        // --------------------------------------------------

        // Antes medía 55dp de alto y el botón (56dp) quedaba cortado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 16.dp)
                .heightIn(min = 64.dp)
        ) {

            // Indicadores
            Row(
                modifier = Modifier.align(
                    Alignment.Center
                )
            ) {

                repeat(onboardingPages.size) { index ->

                    val selected =
                        pagerState.currentPage == index

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(
                                if (selected) {
                                    10.dp
                                } else {
                                    8.dp
                                }
                            )
                            .clip(CircleShape)
                            .background(
                                if (selected) {
                                    Color.White
                                } else {
                                    Color.White.copy(
                                        alpha = 0.4f
                                    )
                                }
                            )
                    )
                }
            }

            // Botón de siguiente
            FloatingActionButton(
                onClick = {

                    if (
                        pagerState.currentPage <
                        onboardingPages.size - 1
                    ) {

                        scope.launch {

                            pagerState.animateScrollToPage(
                                pagerState.currentPage + 1
                            )
                        }

                    } else {

                        onFinish()
                    }
                },

                containerColor = Color.White.enTema(),

                shape = CircleShape,

                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(60.dp)
            ) {

                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Siguiente",
                    tint = Color(0xFF00695C).enTema()
                )
            }
        }
    }
}