package com.example.pulsofirme.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.ui.graphics.vector.ImageVector

data class ItemMenuCuidadorItem(
    val label: String,
    val icono: ImageVector,
    val ruta: String
)

object ItemMenuCuidadorCompartido {
    val items = listOf(
        ItemMenuCuidadorItem("Inicio", Icons.Filled.Home, "caregiver_home"),
        ItemMenuCuidadorItem("Progreso", Icons.Filled.ShowChart, "caregiver_progress"),
        ItemMenuCuidadorItem("Alertas", Icons.Filled.Notifications, "caregiver_alerts"),
        ItemMenuCuidadorItem("Cuenta", Icons.Filled.Person, "caregiver_account")
    )
}