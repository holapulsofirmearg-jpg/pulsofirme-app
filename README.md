<p align="center">
  <img src="app/src/main/res/drawable/logooo.png" alt="Logo de PulsoFirme" width="140">
</p>

<h1 align="center">PulsoFirme</h1>

<p align="center">
  App Android que acompaña la rehabilitación de manos en personas con <b>Parkinson</b> y <b>artritis</b>,<br>
  junto con un guante inteligente.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Android-7.0%2B-3DDC84?logo=android&logoColor=white" alt="Android 7.0+">
  <img src="https://img.shields.io/badge/Kotlin-Jetpack%20Compose-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin y Jetpack Compose">
  <img src="https://img.shields.io/badge/Firebase-Auth%20%C2%B7%20Firestore%20%C2%B7%20AI-FFCA28?logo=firebase&logoColor=black" alt="Firebase">
  <img src="https://img.shields.io/badge/Idiomas-Español%20%C2%B7%20English-1B7A4B" alt="Español e inglés">
</p>

---

## ¿Qué es?

PulsoFirme combina un **guante inteligente** con una **app móvil** para que la persona haga ejercicios de mano guiados desde su casa, registre cómo se siente y comparta su progreso con quienes la acompañan.

La app tiene tres tipos de cuenta:

| Rol | Qué puede hacer |
|---|---|
| **Paciente** | Hace rutinas guiadas, conecta el guante, registra medicación y cómo se siente, completa la evaluación QuickDASH y ve su progreso. |
| **Tutor / cuidador** | Se vincula con un código, sigue el progreso del paciente y recibe avisos (medicación olvidada, días sin ejercicio, dolor). |
| **Profesional de salud** | Gestiona varios pacientes, revisa su evolución, cumplimiento y resultados de QuickDASH. |

> PulsoFirme es una herramienta de apoyo. No diagnostica ni reemplaza la indicación de un profesional de la salud.

## Funciones principales

- **Ejercicios guiados** para Parkinson y artritis, con imágenes paso a paso y temporizador.
- **Rutinas adaptativas:** la dificultad se ajusta según el estado de la medicación (on/off), el resultado de QuickDASH y cómo se sintió la persona.
- **Conexión con el guante** por WiFi para registrar el movimiento de los dedos y la muñeca.
- **Evaluación QuickDASH** con historial de resultados.
- **Recordatorios** de ejercicio y alarmas de medicación a pantalla completa, aunque el celular esté bloqueado.
- **Avisos al tutor** en segundo plano, incluso con la app cerrada.
- **PulsiBot:** asistente con inteligencia artificial (Gemini) que responde dudas sobre la app, con respuestas de respaldo sin internet.
- **Resumen semanal en PDF** para compartir con el médico.
- **Privacidad:** el paciente decide qué ve cada tutor o profesional, y puede descargar o eliminar todos sus datos.
- **Accesibilidad:** letra grande, alto contraste, botones más grandes, menos animaciones y compatibilidad con TalkBack.
- **Modo oscuro** (claro, oscuro o automático).
- **Dos idiomas:** español e inglés.

## Capturas

| Inicio | Ejercicios | Progreso | PulsiBot |
|---|---|---|---|
| <img src="capturas/inicio.png" width="200"> | <img src="capturas/ejercicios.png" width="200"> | <img src="capturas/progreso.png" width="200"> | <img src="capturas/pulsibot.png" width="200"> |

## Tecnologías

- **Lenguaje:** Kotlin
- **Interfaz:** Jetpack Compose + Material 3
- **Navegación:** Navigation Compose, con las rutas divididas por rol
- **Backend:** Firebase Authentication y Cloud Firestore, con reglas de seguridad por rol
- **IA:** Firebase AI Logic (Gemini) para PulsiBot
- **Seguridad:** Firebase App Check con Play Integrity
- **Tareas en segundo plano:** WorkManager y AlarmManager
- **Hardware:** guante con sensores de movimiento (MPU6050) y microcontrolador con WiFi

## Cómo correrla

1. Cloná el repositorio:
   ```bash
   git clone https://github.com/holapulsofirmearg-jpg/pulsofirme-app.git
   ```
2. Abrilo con **Android Studio**.
3. Creá un proyecto en [Firebase](https://console.firebase.google.com), agregá una app Android con el paquete `com.example.pulsofirme` y descargá el archivo `google-services.json` dentro de la carpeta `app/`.
4. En Firebase activá **Authentication** (correo y contraseña), **Cloud Firestore** y **AI Logic**.
5. Conectá un celular o un emulador con Android 7.0 o superior y tocá **Run ▶**.

## Estructura

```
app/src/main/java/com/example/pulsofirme/
├── screens/          Pantallas de la app (paciente, tutor y profesional)
│   └── alarmas/      Alarmas de medicación
├── ui/theme/         Colores, tema claro/oscuro y generador de PDF
├── Rutas*.kt         Navegación separada por rol
├── pulsibotia.kt     Asistente PulsiBot
└── AvisosTutor.kt    Avisos al tutor en segundo plano
```

## Equipo

Proyecto desarrollado por estudiantes de la **Escuela Experimental PROA, sede Alta Gracia** (Córdoba, Argentina):

- Ivan Correa
- Gustavo Carrasco
- Ana Cabagna
- Morena Martínez

📩 Contacto: **holapulsofirmearg@gmail.com**