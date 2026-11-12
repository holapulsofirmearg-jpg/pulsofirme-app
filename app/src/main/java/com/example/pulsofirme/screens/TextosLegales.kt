package com.example.pulsofirme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Textos legales de PulsoFirme (Ley 25.326).
 * IMPORTANTE: si cambiás el contenido de forma importante, subí VERSION:
 * así sabés qué versión aceptó cada usuario (se guarda en Firestore al registrarse).
 */
object TextosLegales {

    const val VERSION = "1.0"
    const val FECHA = "13 de octubre de 2026"
    const val CONTACTO = "holapulsofirmearg@gmail.com"
    const val RESPONSABLE = "el equipo de PulsoFirme (proyecto de la Escuela Experimental PROA, sede Alta Gracia)"
    const val DOMICILIO = "Alta Gracia, Córdoba, Argentina"

    val politica: String = """
POLÍTICA DE PRIVACIDAD
Versión $VERSION · Última actualización: $FECHA

1. RESPONSABLE
El responsable del tratamiento de tus datos es $RESPONSABLE, con domicilio en $DOMICILIO. Contacto: $CONTACTO.

2. MARCO LEGAL
Tratamos tus datos conforme a la Ley 25.326 de Protección de los Datos Personales, su Decreto Reglamentario 1558/2001 y las normas de la Agencia de Acceso a la Información Pública (AAIP).

3. QUÉ DATOS RECOLECTAMOS
- Cuenta: nombre, correo y contraseña. La contraseña la guarda cifrada el servicio de autenticación; nosotros no la vemos.
- Datos personales opcionales: fecha de nacimiento, país, teléfono, dirección, foto y género.
- Datos de salud (datos sensibles): diagnóstico, zona afectada, mano, horarios y registro de medicación, ejercicios realizados, resultados de la evaluación QuickDASH y notas.
- Vinculación: códigos de vinculación y vínculos con cuidadores y profesionales.
Solo guardamos lo que cargás vos o lo que genera tu uso de la app.

4. PARA QUÉ LOS USAMOS
- Crear y mantener tu cuenta.
- Mostrarte tu progreso y recordarte ejercicios y medicación.
- Permitir que un cuidador o un profesional que vos autorices vea la información que decidas compartir.
- Mejorar la seguridad y el funcionamiento de la app.
No vendemos tus datos, no los usamos para publicidad y no los cedemos a terceros con fines comerciales.

5. CON QUIÉN LOS COMPARTIMOS
- Cuidadores y profesionales vinculados: solo después de que ingresen tu código de vinculación. Vos controlás qué información pueden ver y podés quitarles el acceso cuando quieras.
- Proveedor tecnológico: usamos Google Firebase (Authentication y Cloud Firestore) para autenticar usuarios y guardar datos. Google actúa como proveedor de servicios.
- Autoridades: solo si una ley o una orden judicial lo exige.

6. TRANSFERENCIA INTERNACIONAL
Los servidores de Google pueden estar fuera de la Argentina. La Ley 25.326 (art. 12) limita la transferencia de datos a países sin nivel adecuado de protección, salvo con consentimiento expreso del titular. Al dar tu consentimiento en el registro, autorizás esa transferencia para las finalidades descriptas.

7. CUÁNTO TIEMPO LOS CONSERVAMOS
Mientras tu cuenta esté activa. Si usás "Eliminar mis datos", borramos tu perfil, tus registros de salud, tus vínculos y tu cuenta de acceso. Copias de respaldo del proveedor pueden persistir por un plazo limitado antes de eliminarse definitivamente.

8. SEGURIDAD
Aplicamos medidas técnicas razonables: conexión cifrada, autenticación y reglas de acceso que impiden que otras personas lean tus datos sin tu autorización (art. 9, Ley 25.326). Ningún sistema es infalible; si detectáramos un incidente que te afecte, te lo informaríamos.

9. TUS DERECHOS
Tenés derecho a acceder a tus datos, rectificarlos o actualizarlos, pedir su supresión y retirar tu consentimiento en cualquier momento, sin efecto retroactivo. Podés borrar todo desde Privacidad y datos → "Eliminar mis datos", y descargar un resumen desde "Descargar mis datos". También podés escribirnos a $CONTACTO. Respondemos en los plazos legales: 10 días corridos para el acceso y 5 días hábiles para rectificación o supresión.

El titular de los datos personales tiene la facultad de ejercer el derecho de acceso a los mismos en forma gratuita a intervalos no inferiores a seis meses, salvo que se acredite un interés legítimo al efecto conforme lo establecido en el artículo 14, inciso 3 de la Ley Nº 25.326. La AGENCIA DE ACCESO A LA INFORMACIÓN PÚBLICA, en su carácter de Órgano de Control de la Ley Nº 25.326, tiene la atribución de atender las denuncias y reclamos que interpongan quienes resulten afectados en sus derechos por incumplimiento de las normas vigentes en materia de protección de datos personales.

10. MENORES DE EDAD
PulsoFirme está destinada a personas mayores de 18 años. Si el paciente es menor o no puede decidir por sí mismo, el registro y el consentimiento deben hacerlos su madre, padre, tutor o representante legal.

11. CAMBIOS
Si modificamos esta política de forma importante, te lo avisaremos en la app y te pediremos un nuevo consentimiento cuando corresponda.
""".trimIndent()

    val terminos: String = """
TÉRMINOS Y CONDICIONES
Versión $VERSION · Última actualización: $FECHA

1. QUÉ ES PULSOFIRME
Es una aplicación de acompañamiento y seguimiento para personas con temblor, rigidez o artritis, y para sus cuidadores y profesionales de la salud. Funciona junto con un guante inteligente.

2. NO ES UN SERVICIO MÉDICO
PulsoFirme no diagnostica, no trata ni reemplaza la consulta con tu médico. Los ejercicios, recordatorios y evaluaciones son orientativos. Ante cualquier duda o síntoma, consultá a un profesional. En una emergencia, llamá al 107 o al servicio de emergencias de tu zona.

3. TU CUENTA
Sos responsable de que tus datos sean verdaderos y de cuidar tu contraseña y tu código de vinculación. Compartí tu código solo con personas de confianza.

4. CUIDADORES Y PROFESIONALES
El acceso de terceros a tus datos depende de que vos los vincules y de los permisos que otorgues. PulsoFirme no se responsabiliza por el uso que una persona autorizada haga de la información que vos decidiste compartir.

5. PLAN PREMIUM
Algunas funciones (por ejemplo, más pacientes por profesional y reportes) pueden requerir un plan Premium. Las condiciones y precios se informarán antes de contratarlo.

6. USO ACEPTABLE
No podés usar la app para fines ilegales, vulnerar la seguridad del servicio, acceder a datos de otras personas sin autorización ni cargar datos falsos de terceros.

7. DISPONIBILIDAD
Trabajamos para que la app funcione bien, pero no garantizamos que esté libre de interrupciones o errores. Los recordatorios también dependen de la configuración de tu teléfono (batería, notificaciones, alarmas).

8. LIMITACIÓN DE RESPONSABILIDAD
En la medida permitida por la ley, PulsoFirme no responde por daños derivados de decisiones médicas tomadas únicamente en base a la información de la app.

9. ELIMINAR TU CUENTA
Podés eliminar tu cuenta y tus datos en cualquier momento desde Privacidad y datos → "Eliminar mis datos".

10. LEY APLICABLE
Estos términos se rigen por las leyes de la República Argentina. Para cualquier controversia serán competentes los tribunales de la ciudad de Córdoba, sin perjuicio de los derechos que te correspondan como consumidor.

11. CONTACTO
$CONTACTO
""".trimIndent()
}

/** Diálogo con scroll para mostrar un texto legal largo. */
@Composable
fun DialogoLegal(
    titulo: String,
    texto: String,
    conLogo: Boolean = false,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = titulo,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B4332)
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = texto,
                    fontSize = 14.sp,
                    color = Color(0xFF374151),
                    lineHeight = 21.sp
                )
                if (conLogo) {
                    Spacer(Modifier.height(20.dp))
                    Image(
                        painter = painterResource(id = R.drawable.logoo),
                        contentDescription = "Logo de PulsoFirme",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(130.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onCerrar) {
                Text(
                    "Entendido",
                    color = Color(0xFF1B7A4B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    )
}