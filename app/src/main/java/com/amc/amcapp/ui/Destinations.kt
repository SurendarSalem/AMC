package com.amc.amcapp.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.ui.graphics.vector.ImageVector

enum class DrawerDest(
    val route: String, val label: String, val icon: ImageVector, val badge: String? = null
) {
    Home("home", "Home", Icons.Default.Home),

    Services("services", "Services", Icons.Default.HomeRepairService),
    Customer(
        "customer", "Customer", Icons.Default.Person
    ),
    Technician("technician", "Technician", Icons.Default.PersonPin),

    Settings("settings", "Settings", Icons.Default.Settings),

    Sales("settings", "Sales", Icons.Default.Settings),

    Logout("logout", "Logout", Icons.Default.Settings),

}

enum class UserDest(
    val route: String, val label: String, val icon: ImageVector, val badge: String? = null
) {
    AddUser("customer/addUser", "Add User", Icons.Default.Add),
    Equipments("equipments", "Equipments", Icons.Default.SportsGymnastics)
}

enum class GymDest(
    val route: String, val label: String, val icon: ImageVector, val badge: String? = null
) {
    AddEquipment("customer/addEquipment", "Add Equipment", Icons.Default.Add)
}

enum class BottomDest(val route: String, val label: String, val icon: ImageVector) {
    Amc("amc", "AMC", Icons.AutoMirrored.Filled.List),
    Services(
        "services", "Services", Icons.Default.SportsGymnastics
    ),
    Favorites("calendar", "Calendar", Icons.Default.CalendarToday)
}