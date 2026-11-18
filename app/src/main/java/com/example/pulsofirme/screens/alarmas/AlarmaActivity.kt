package com.example.pulsofirme.alarmas

import com.example.pulsofirme.screens.IdiomaApp
import com.example.pulsofirme.screens.tr
import com.example.pulsofirme.screens.trf

import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat

private val VerdeAlarma = Color(0xFF1B7A4B)

class AlarmaActivity : ComponentActivity() {

    private var ringtone: Ringtone? = null
    private var vibrator: Vibrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        IdiomaApp.cargar(applicationContext)

        // Se muestra aunque el celular esté bloqueado y prende la pantalla
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val horario = intent.getStringExtra("horario") ?: ""
        iniciarSonidoYVibracion()

        setContent {
            PantallaAlarma(
                horario = horario,
                onTomada = {
                    // Se guarda la toma (en el celular y para el tutor)
                    TomasMedicacion.registrar(this@AlarmaActivity, horario)
                    cerrar()
                },
                onPosponer = {
                    AlarmaScheduler.posponer(this@AlarmaActivity, 5, horario)
                    cerrar()
                }
            )
        }
    }

    private fun iniciarSonidoYVibracion() {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        ringtone = RingtoneManager.getRingtone(this, uri)?.apply {
            audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) isLooping = true
            play()
        }

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(VibratorManager::class.java).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        val patron = longArrayOf(0, 800, 600)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(patron, 0))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(patron, 0)
        }
    }

    private fun detenerSonido() {
        ringtone?.stop()
        vibrator?.cancel()
    }

    private fun cerrar() {
        detenerSonido()
        NotificationManagerCompat.from(this).cancel(AlarmaReceiver.NOTIF_ID)
        finish()
    }

    override fun onDestroy() {
        detenerSonido()
        super.onDestroy()
    }
}

@Composable
private fun PantallaAlarma(
    horario: String,
    onTomada: () -> Unit,
    onPosponer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeAlarma)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.White.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Alarm,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = tr("Hora de tu medicación"),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        if (horario.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(text = horario, fontSize = 40.sp, fontWeight = FontWeight.Light, color = Color.White)
        }

        Spacer(Modifier.height(48.dp))

        Button(
            onClick = onTomada,
            modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text(tr("Ya la tomé ✓"), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = VerdeAlarma)
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onPosponer,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
        ) {
            Text(tr("Posponer 5 minutos"), fontSize = 16.sp, color = Color.White)
        }
    }
}