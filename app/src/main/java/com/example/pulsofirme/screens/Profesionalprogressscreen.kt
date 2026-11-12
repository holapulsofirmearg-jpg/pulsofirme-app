package com.example.pulsofirme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class ProgresoProfesionalFirebase(val uid:String, val nombre:String, val diagnostico:String, val quickDash:String)

@Composable
fun ProfessionalProgressScreen(
    rutaActual: String = "caregiver_progress",
    onNavigate: (String) -> Unit,
    onPacienteClick: (String) -> Unit = {}
) {
    val db = FirebaseFirestore.getInstance(); val uidProfesional = FirebaseAuth.getInstance().currentUser?.uid
    var datos by remember { mutableStateOf<List<ProgresoProfesionalFirebase>>(emptyList()) }; var cargando by remember { mutableStateOf(true) }
    LaunchedEffect(uidProfesional) {
        if (uidProfesional == null) { cargando=false; return@LaunchedEffect }
        db.collection("usuarios").document(uidProfesional).get().addOnSuccessListener { prof ->
            val uids=(prof.get("pacientesVinculados") as? List<*>)?.mapNotNull{it as? String}.orEmpty()
            if(uids.isEmpty()){datos=emptyList();cargando=false;return@addOnSuccessListener}
            val lista=mutableListOf<ProgresoProfesionalFirebase>(); var pendientes=uids.size
            uids.forEach { uid -> db.collection("usuarios").document(uid).get().addOnCompleteListener { pt ->
                val p=pt.result; val nombre=listOfNotNull(p?.getString("nombre"),p?.getString("apellido")).filter{it.isNotBlank()}.joinToString(" ").ifBlank{"Paciente"}
                val ds=p?.get("diagnosticos") as? List<*>; val diag=ds?.firstOrNull()?.toString()?:p?.getString("diagnostico")?:"Sin diagnóstico"
                db.collection("usuarios").document(uid).collection("quickdash").orderBy("fecha", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener { qt ->
                    val q=if(qt.isSuccessful) qt.result.documents.firstOrNull() else null; val texto=q?.getDouble("puntaje")?.let{String.format("%.1f",it)}?:"Sin evaluación"
                    lista += ProgresoProfesionalFirebase(uid,nombre,diag,texto); pendientes--; if(pendientes==0){datos=lista.sortedBy{it.nombre};cargando=false}
                }
            }}
        }.addOnFailureListener { cargando=false }
    }
    Scaffold(bottomBar={ NavigationBar { listOf(Triple("Inicio",Icons.Filled.Home,"caregiver_home"),Triple("Pacientes",Icons.Filled.Groups,"caregiver_patients"),Triple("Progreso",Icons.Filled.ShowChart,"caregiver_progress"),Triple("Cuenta",Icons.Filled.Person,"caregiver_account")).forEach { x -> NavigationBarItem(selected=rutaActual==x.third,onClick={onNavigate(x.third)},icon={Icon(x.second,x.first)},label={Text(x.first,fontSize=11.sp)}) } } }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            item { Text("Progreso",fontSize=30.sp,fontWeight=FontWeight.Bold); Text("Seguimiento de pacientes vinculados") }
            if(cargando) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
            if(!cargando&&datos.isEmpty()) item { Text("No hay pacientes vinculados todavía.") }
            items(datos,key={it.uid}) { p -> Card(Modifier.fillMaxWidth().clickable{onPacienteClick(p.uid)}) { Row(Modifier.fillMaxWidth().padding(16.dp),horizontalArrangement=Arrangement.SpaceBetween){ Column(Modifier.weight(1f)){Text(p.nombre,fontWeight=FontWeight.Bold);Text(p.diagnostico);Text("QuickDASH: ${p.quickDash}")} ; Icon(Icons.Filled.ChevronRight,null) } } }
        }
    }
}
