package com.example.pulsofirme.screens

import androidx.compose.runtime.saveable.rememberSaveable

import com.example.pulsofirme.ui.theme.enTema

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsofirme.DialogoLegal
import com.example.pulsofirme.DificultadAdaptativa
import com.example.pulsofirme.ProfessionalPdfData
import com.example.pulsofirme.ResultadoBorrado
import com.example.pulsofirme.TextosLegales
import com.example.pulsofirme.eliminarCuentaCompleta
import kotlinx.coroutines.launch
import com.example.pulsofirme.generarDatosProfesionalPdf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private val VerdePP = Color(0xFF07884A); private val VerdeClaroPP = Color(0xFFEAF8F0); private val AzulClaroPP = Color(0xFFEEF7F8); private val GrisPP = Color(0xFF7A8294); private val AzulTextoPP = Color(0xFF17365D)

@Composable
fun ProfessionalPrivacyScreen(
    onBack: () -> Unit,
    datosProfesional: ProfessionalPdfData = ProfessionalPdfData(),
    onGestionarPacientes: () -> Unit = {},
    onPermisosAcceso: () -> Unit = {},
    // Se llama cuando la cuenta ya fue borrada de Firestore y de Authentication
    onCuentaEliminada: () -> Unit = {}
) {
    val context = LocalContext.current; val db = FirebaseFirestore.getInstance(); val uid = FirebaseAuth.getInstance().currentUser?.uid
    var progreso by rememberSaveable { mutableStateOf(true) }; var medicacion by rememberSaveable { mutableStateOf(true) }; var ubicacion by rememberSaveable { mutableStateOf(false) }; var cargando by remember { mutableStateOf(true) }; var mostrarPolitica by rememberSaveable { mutableStateOf(false) }
    var mostrarBorrar by rememberSaveable { mutableStateOf(false) }; var borrando by remember { mutableStateOf(false) }; var clave by rememberSaveable { mutableStateOf("") }; val scope = rememberCoroutineScope()
    LaunchedEffect(uid) { if (uid == null) { cargando=false; return@LaunchedEffect }; db.collection("usuarios").document(uid).get().addOnSuccessListener { d -> progreso=d.getBoolean("permisoProgresoEjercicios")?:true; medicacion=d.getBoolean("permisoAlertasMedicacion")?:true; ubicacion=d.getBoolean("permisoUbicacion")?:false; cargando=false }.addOnFailureListener { cargando=false } }
    fun guardar(campo:String, valor:Boolean) { if(uid!=null) db.collection("usuarios").document(uid).update(campo,valor).addOnFailureListener { Toast.makeText(context,"No se pudo guardar el permiso",Toast.LENGTH_SHORT).show() } }
    if (mostrarPolitica) DialogoLegal(titulo = "Política de privacidad", texto = TextosLegales.politica) { mostrarPolitica = false }
    if (mostrarBorrar) AlertDialog(
        onDismissRequest = { if (!borrando) { mostrarBorrar = false; clave = "" } },
        containerColor = Color.White.enTema(),
        title = { Text("Eliminar mi cuenta", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Se borran tus datos y tu cuenta de forma definitiva, y se te quita de todos tus pacientes vinculados. Para confirmar, escribí tu contraseña.", fontSize = 14.sp)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = clave, onValueChange = { clave = it }, label = { Text("Contraseña") }, singleLine = true, enabled = !borrando, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(enabled = clave.isNotBlank() && !borrando, modifier = Modifier.heightIn(min = 56.dp), onClick = {
                borrando = true
                scope.launch {
                    when (val r = eliminarCuentaCompleta(clave)) {
                        is ResultadoBorrado.Ok -> { borrando = false; mostrarBorrar = false; clave = ""; DificultadAdaptativa.reiniciar(context); Toast.makeText(context, "Tu cuenta y tus datos fueron eliminados", Toast.LENGTH_LONG).show(); onCuentaEliminada() }
                        is ResultadoBorrado.ContrasenaIncorrecta -> { borrando = false; Toast.makeText(context, "La contraseña no es correcta", Toast.LENGTH_LONG).show() }
                        is ResultadoBorrado.SinConexion -> { borrando = false; Toast.makeText(context, "Sin conexión. Probá de nuevo con internet.", Toast.LENGTH_LONG).show() }
                        is ResultadoBorrado.Fallo -> { borrando = false; Toast.makeText(context, r.detalle, Toast.LENGTH_LONG).show() }
                    }
                }
            }) { Text(if (borrando) "Eliminando…" else "Eliminar definitivamente", color = Color(0xFFC0392B).enTema(), fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(enabled = !borrando, modifier = Modifier.heightIn(min = 56.dp), onClick = { mostrarBorrar = false; clave = "" }) { Text("Cancelar", color = GrisPP) } }
    )
    Scaffold(containerColor=Color.White.enTema()){ padding -> Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal=22.dp,vertical=18.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically){IconButton(onClick=onBack){Icon(Icons.Filled.ArrowBack,"Volver",tint=AzulTextoPP)};Text("Privacidad y datos",color=VerdePP,fontSize=27.sp,fontWeight=FontWeight.Bold)}
        Text("Gestioná la información y los permisos de tu cuenta profesional.",color=GrisPP,fontSize=15.sp); if(cargando){Spacer(Modifier.height(8.dp));LinearProgressIndicator(Modifier.fillMaxWidth())}; Spacer(Modifier.height(24.dp))
        TituloPP(Icons.Filled.Security,"Permisos profesionales"); Spacer(Modifier.height(12.dp)); Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=AzulClaroPP)){Column(Modifier.padding(horizontal=16.dp,vertical=8.dp)){
        PermisoPP(Icons.Filled.ShowChart,"Progreso y ejercicios","Acceso al progreso de pacientes vinculados.",progreso){progreso=it;guardar("permisoProgresoEjercicios",it)};HorizontalDivider();PermisoPP(Icons.Filled.Notifications,"Alertas de medicación","Permite consultar avisos relacionados con medicación.",medicacion){medicacion=it;guardar("permisoAlertasMedicacion",it)};HorizontalDivider();PermisoPP(Icons.Filled.LocationOn,"Ubicación","Solo cuando el paciente la comparte y está autorizada.",ubicacion){ubicacion=it;guardar("permisoUbicacion",it)}
    }}
        Spacer(Modifier.height(26.dp));TituloPP(Icons.Filled.Person,"Datos de tu cuenta");Spacer(Modifier.height(10.dp));Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(18.dp)){Column{
        OpcionPP(Icons.Filled.Download,"Descargar mis datos","Generá una copia PDF de tus datos."){ val archivo=generarDatosProfesionalPdf(context,datosProfesional); Toast.makeText(context,if(archivo!=null) "PDF generado correctamente" else "No se pudo generar el PDF",Toast.LENGTH_SHORT).show() };HorizontalDivider(Modifier.padding(horizontal=16.dp));OpcionPP(Icons.Filled.Description,"Política de privacidad","Consultá cómo se utiliza la información."){mostrarPolitica=true};HorizontalDivider(Modifier.padding(horizontal=16.dp));OpcionPP(Icons.Filled.DeleteForever,"Eliminar mi cuenta","Borra tu cuenta y tus datos de forma definitiva."){mostrarBorrar=true}
    }}
        Spacer(Modifier.height(26.dp));TituloPP(Icons.Filled.Groups,"Pacientes vinculados");Spacer(Modifier.height(10.dp));Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(18.dp)){Column{
        OpcionPP(Icons.Filled.Groups,"Gestionar pacientes vinculados","Ver, vincular o desvincular pacientes (1 en el plan gratis, hasta 5 con Premium).",onGestionarPacientes);HorizontalDivider(Modifier.padding(horizontal=16.dp));OpcionPP(Icons.Filled.Key,"Permisos de acceso","Configurá los permisos de acceso.",onPermisosAcceso)
    }};Spacer(Modifier.height(30.dp))
    }}
}

@Composable private fun TituloPP(icono:ImageVector,texto:String){Row(verticalAlignment=Alignment.CenterVertically){Icon(icono,null,tint=VerdePP,modifier=Modifier.size(25.dp));Spacer(Modifier.width(12.dp));Text(texto,color=AzulTextoPP,fontSize=17.sp,fontWeight=FontWeight.Bold)}}
@Composable private fun PermisoPP(icono:ImageVector,titulo:String,descripcion:String,checked:Boolean,onCheckedChange:(Boolean)->Unit){Row(Modifier.fillMaxWidth().padding(vertical=14.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(48.dp).background(Color.White.enTema(),CircleShape),contentAlignment=Alignment.Center){Icon(icono,null,tint=VerdePP)};Spacer(Modifier.width(14.dp));Column(Modifier.weight(1f)){Text(titulo,color=AzulTextoPP,fontWeight=FontWeight.Bold);Text(descripcion,color=GrisPP,fontSize=13.sp)};Switch(checked,onCheckedChange)}}
@Composable private fun OpcionPP(icono:ImageVector,titulo:String,descripcion:String,onClick:()->Unit){Row(Modifier.fillMaxWidth().heightIn(min=56.dp).clickable{onClick()}.padding(16.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(46.dp).background(VerdeClaroPP,CircleShape),contentAlignment=Alignment.Center){Icon(icono,null,tint=VerdePP)};Spacer(Modifier.width(14.dp));Column(Modifier.weight(1f)){Text(titulo,color=AzulTextoPP,fontWeight=FontWeight.Bold);Text(descripcion,color=GrisPP,fontSize=13.sp)};Icon(Icons.Filled.ChevronRight,null,tint=GrisPP)}}