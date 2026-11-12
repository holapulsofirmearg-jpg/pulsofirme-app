package com.example.pulsofirme.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.core.content.ContextCompat
import com.example.pulsofirme.Accesibilidad
import com.example.pulsofirme.filtroAltoContraste
import com.example.pulsofirme.ui.theme.PulsoFirmeTheme

class MainActivity : ComponentActivity() {

    private val pedirPermisoNotificaciones =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cargamos las preferencias de accesibilidad (por defecto: todo grande)
        Accesibilidad.init(applicationContext)

        // Permiso de notificaciones en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            pedirPermisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            val densidadSistema = LocalDensity.current
            val vista = LocalView.current

            // Texto: escala del sistema x escala de PulsoFirme (tope 2x para que no se rompa)
            val densidadPulsoFirme = Density(
                density = densidadSistema.density,
                fontScale = (densidadSistema.fontScale * Accesibilidad.multiplicador())
                    .coerceAtMost(2.0f)
            )

            CompositionLocalProvider(
                LocalDensity provides densidadPulsoFirme,
                // Área táctil mínima para TODOS los componentes de Material 3
                LocalMinimumInteractiveComponentSize provides Accesibilidad.altoMinimo
            ) {
                // Vibración corta en cada toque (si "Sonidos y vibración" está activado).
                // Solo observa los toques: no los consume, así que no cambia nada más.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        // Alto contraste (Ajustes > Accesibilidad)
                        .filtroAltoContraste()
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val evento = awaitPointerEvent(PointerEventPass.Initial)
                                    if (Accesibilidad.sonidosYVibracion &&
                                        evento.changes.any { it.changedToDown() }
                                    ) {
                                        vista.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    }
                                }
                            }
                        }
                ) {
                    PulsoFirmeTheme {
                        AppNavGraph()
                    }
                }
            }
        }
    }
}