package com.amc.amcapp.ui.technician

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.Person2
import androidx.compose.ui.graphics.vector.ImageVector

enum class TechnicianBottomDest(val route: String, val label: String, val icon: ImageVector) {
    Amc("amc", "AMC", Icons.Default.HomeRepairService),
    Services(
        "services",
        "General Services",
        Icons.Default.CleaningServices
    ),
    Profile(
        "profile",
        "Profile",
        Icons.Default.Person2
    )
}