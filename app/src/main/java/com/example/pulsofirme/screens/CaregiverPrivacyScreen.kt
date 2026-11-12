package com.example.pulsofirme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

private val VerdeTitulo = Color(0xFF1B7A4B)
private val AzulTexto = Color(0xFF16324F)
private val GrisSubtitulo = Color(0xFF6B7280)
private val CelesteFondo = Color(0xFFEAF3F7)
private val VerdeIconoFondo = Color(0xFFDFF3E4)
private val RojoFondo = Color(0xFFFBE9E9)
private val RojoTexto = Color(0xFFC94A4A)
private val RojoBoton = Color(0xFFE05A4E)
private val GrisDivider = Color(0xFFDCE3E0)
private val VerdeBoton = Color(0xFFDCEFE3)
private val GrisBordeCancelar = Color(0xFFD8DED9)

@Composable
fun CaregiverPrivacyScreen(
    onBack: () -> Unit,
    onDescargarDatos: () -> Unit = {},
    onDesvincularPaciente: () -> Unit = {},
    onEliminarDatos: () -> Unit = {}
) {
    var mostrarPoliticaPrivacidad by remember { mutableStateOf(false) }
    var mostrarInfoPaciente by remember { mutableStateOf(false) }
    var mostrarConfirmarDesvincular by remember { mutableStateOf(false) }
    var mostrarConfirmarEliminar by remember { mutableStateOf(false) }
    var progresoYEjercicios by remember { mutableStateOf(true) }
    var alertasMedicacion by remember { mutableStateOf(true) }
    var ubicacion by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Privacidad y datos",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeTitulo
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Acá podés gestionar la información que compartís\ny los permisos de la app.",
            fontSize = 15.sp,
            color = GrisSubtitulo,
            lineHeight = 21.sp
        )

        Spacer(modifier = Modifier.height(26.dp))

        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = Icons.Filled.Security,
                contentDescription = null,
                tint = VerdeTitulo,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Información que podés consultar\ndel paciente vinculado",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AzulTexto,
                lineHeight = 21.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CelesteFondo, shape = RoundedCornerShape(20.dp))
                .padding(horizontal = 18.dp, vertical = 6.dp)
        ) {
            FilaPermiso(
                icono = Icons.Filled.ShowChart,
                titulo = "Progreso y ejercicios",
                descripcion = "Podés consultar el progreso\ny los ejercicios registrados.",
                activo = progresoYEjercicios,
                onCambio = { progresoYEjercicios = it }
            )
            HorizontalDivider(color = GrisDivider)

            FilaPermiso(
                icono = Icons.Filled.Notifications,
                titulo = "Alertas de medicación",
                descripcion = "Recibís avisos relacionados\ncon las tomas registradas.",
                activo = alertasMedicacion,
                onCambio = { alertasMedicacion = it }
            )
            HorizontalDivider(color = GrisDivider)

            FilaPermiso(
                icono = Icons.Filled.LocationOn,
                titulo = "Ubicación",
                descripcion = "Solo podés verla si el paciente\nautorizó compartirla.",
                activo = ubicacion,
                onCambio = { ubicacion = it },
                conDivider = false
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Person, contentDescription = null, tint = VerdeTitulo, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Datos de tu cuenta", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AzulTexto)
        }

        Spacer(modifier = Modifier.height(12.dp))

        FilaAccion(
            icono = Icons.Filled.Download,
            fondoIcono = VerdeIconoFondo,
            colorIcono = VerdeTitulo,
            titulo = "Descargar mis datos",
            descripcion = "Podés solicitar una copia de tus datos\npersonales.",
            onClick = onDescargarDatos
        )
        HorizontalDivider(color = GrisDivider)

        FilaAccion(
            icono = Icons.Filled.Description,
            fondoIcono = VerdeIconoFondo,
            colorIcono = VerdeTitulo,
            titulo = "Política de privacidad",
            descripcion = "Conocé cómo cuidamos tu información.",
            onClick = { mostrarPoliticaPrivacidad = true }
        )

        Spacer(modifier = Modifier.height(28.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.LinkOff, contentDescription = null, tint = VerdeTitulo, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Paciente vinculado", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AzulTexto)
        }

        Spacer(modifier = Modifier.height(12.dp))

        FilaAccion(
            icono = Icons.Filled.Person,
            fondoIcono = VerdeIconoFondo,
            colorIcono = VerdeTitulo,
            titulo = "Gestionar vínculo",
            descripcion = "Ver o administrar la vinculación\ncon el paciente.",
            onClick = { mostrarInfoPaciente = true }
        )
        HorizontalDivider(color = GrisDivider)

        FilaAccion(
            icono = Icons.Filled.LinkOff,
            fondoIcono = RojoFondo,
            colorIcono = RojoTexto,
            titulo = "Desvincular paciente",
            descripcion = "Se eliminará el vínculo con el paciente\ny dejarás de ver su información.",
            colorTitulo = AzulTexto,
            onClick = { mostrarConfirmarDesvincular = true }
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(RojoFondo, shape = RoundedCornerShape(16.dp))
                .clickable { mostrarConfirmarEliminar = true }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Delete, contentDescription = null, tint = RojoTexto, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Eliminar mis datos", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = RojoTexto)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "Esta acción es irreversible. Se eliminarán\ntodos tus datos de la app.",
                    fontSize = 13.sp,
                    color = RojoTexto,
                    lineHeight = 17.sp
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = RojoTexto,
                modifier = Modifier.size(14.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerdeBoton, shape = RoundedCornerShape(28.dp))
                .clickable { onBack() }
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Person, contentDescription = null, tint = VerdeTitulo, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Volver", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = VerdeTitulo)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    if (mostrarPoliticaPrivacidad) {
        Dialog(onDismissRequest = { mostrarPoliticaPrivacidad = false }) {
            Column(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(20.dp))
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Política de privacidad",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdeTitulo
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Pulso Firme protege tu información personal como cuidador, y la utiliza únicamente para brindarte un mejor servicio y permitirte acompañar al paciente vinculado.",
                    fontSize = 15.sp,
                    color = Color(0xFF1A1A1A),
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "No compartimos tus datos con terceros, salvo cuando sea necesario para el funcionamiento de la app o por obligación legal. Un cuidador solo puede ver la información del paciente que el usuario autoriza compartir.",
                    fontSize = 15.sp,
                    color = Color(0xFF1A1A1A),
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Podés borrar toda tu información en cualquier momento desde el botón \"Eliminar mis datos\". Esta acción no se puede deshacer.",
                    fontSize = 15.sp,
                    color = Color(0xFF1A1A1A),
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Esta es una versión de demostración desarrollada como proyecto escolar. Ante cualquier duda, podés escribirnos a holapulsofirmearg@gmail.com.",
                    fontSize = 15.sp,
                    color = Color(0xFF1A1A1A),
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Entendido",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdeTitulo,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { mostrarPoliticaPrivacidad = false }
                        .padding(8.dp)
                )
            }
        }
    }

    if (mostrarInfoPaciente) {
        Dialog(onDismissRequest = { mostrarInfoPaciente = false }) {
            Column(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(24.dp))
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(VerdeIconoFondo, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = VerdeTitulo,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Información del paciente",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulTexto,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Aquí podés ver los datos del paciente\ncon el que estás vinculado.",
                    fontSize = 14.sp,
                    color = GrisSubtitulo,
                    textAlign = TextAlign.Center,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(22.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CelesteFondo, RoundedCornerShape(18.dp))
                        .padding(horizontal = 18.dp, vertical = 4.dp)
                ) {
                    FilaInfoPaciente(
                        icono = Icons.Filled.Person,
                        etiqueta = "Nombre del paciente",
                        valor = "Juan Pérez"
                    )
                    HorizontalDivider(color = GrisDivider)

                    FilaInfoPaciente(
                        icono = Icons.Filled.Favorite,
                        etiqueta = "Vínculo",
                        valor = "Familiar"
                    )
                    HorizontalDivider(color = GrisDivider)

                    FilaInfoPaciente(
                        icono = Icons.Filled.CalendarMonth,
                        etiqueta = "Fecha de vinculación",
                        valor = "12 de marzo de 2025",
                        conDivider = false
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VerdeBoton, RoundedCornerShape(28.dp))
                        .clickable { mostrarInfoPaciente = false }
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Cerrar",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = VerdeTitulo
                    )
                }
            }
        }
    }

    if (mostrarConfirmarDesvincular) {
        Dialog(onDismissRequest = { mostrarConfirmarDesvincular = false }) {
            Column(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(24.dp))
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(RojoFondo, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LinkOff,
                        contentDescription = null,
                        tint = RojoTexto,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "¿Desvincular paciente?",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulTexto,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Se eliminará el vínculo con el paciente\ny dejarás de ver su información en la app.",
                    fontSize = 14.sp,
                    color = GrisSubtitulo,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.White, RoundedCornerShape(28.dp))
                            .border(BorderStroke(1.dp, GrisBordeCancelar), RoundedCornerShape(28.dp))
                            .clickable { mostrarConfirmarDesvincular = false }
                            .padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Cancelar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AzulTexto
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .background(RojoBoton, RoundedCornerShape(28.dp))
                            .clickable {
                                mostrarConfirmarDesvincular = false
                                onDesvincularPaciente()
                            }
                            .padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Desvincular",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    if (mostrarConfirmarEliminar) {
        Dialog(onDismissRequest = { mostrarConfirmarEliminar = false }) {
            Column(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(24.dp))
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(RojoFondo, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null,
                        tint = RojoTexto,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "¿Eliminar mis datos?",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulTexto,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Esta acción es irreversible. Se eliminarán todos tus datos de la app, incluyendo tu cuenta, configuraciones y registros.",
                    fontSize = 14.sp,
                    color = GrisSubtitulo,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "¿Estás seguro que querés continuar?",
                    fontSize = 14.sp,
                    color = GrisSubtitulo,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.White, RoundedCornerShape(28.dp))
                            .border(BorderStroke(1.dp, GrisBordeCancelar), RoundedCornerShape(28.dp))
                            .clickable { mostrarConfirmarEliminar = false }
                            .padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Cancelar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AzulTexto
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .background(RojoBoton, RoundedCornerShape(28.dp))
                            .clickable {
                                mostrarConfirmarEliminar = false
                                onEliminarDatos()
                            }
                            .padding(vertical = 14.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Eliminar mis datos",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaInfoPaciente(
    icono: ImageVector,
    etiqueta: String,
    valor: String,
    conDivider: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icono, contentDescription = null, tint = VerdeTitulo, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(etiqueta, fontSize = 13.sp, color = GrisSubtitulo)
            Spacer(modifier = Modifier.height(2.dp))
            Text(valor, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AzulTexto)
        }
    }
}

@Composable
private fun FilaPermiso(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    activo: Boolean,
    onCambio: (Boolean) -> Unit,
    conDivider: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icono, contentDescription = null, tint = VerdeTitulo, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AzulTexto)
            Spacer(modifier = Modifier.height(2.dp))
            Text(descripcion, fontSize = 13.sp, color = GrisSubtitulo, lineHeight = 17.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = activo,
            onCheckedChange = onCambio,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VerdeTitulo
            )
        )
    }
}

@Composable
private fun FilaAccion(
    icono: ImageVector,
    fondoIcono: Color,
    colorIcono: Color,
    titulo: String,
    descripcion: String,
    colorTitulo: Color = AzulTexto,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(fondoIcono, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colorTitulo)
            Spacer(modifier = Modifier.height(2.dp))
            Text(descripcion, fontSize = 13.sp, color = GrisSubtitulo, lineHeight = 17.sp)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(14.dp)
        )
    }
}