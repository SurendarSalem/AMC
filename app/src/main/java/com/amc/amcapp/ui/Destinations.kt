package com.amc.amcapp.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBasket
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
    Technician("technician", "Technician", Icons.Default.CleaningServices),

    Settings("settings", "Settings", Icons.Default.Settings),

    Sales("sales", "Sales", Icons.Default.ShoppingBasket)

}

enum class UserDest(
    val route: String, val label: String, val icon: ImageVector, val badge: String? = null
) {
    AddUser("customer/addUser", "Add User", Icons.Default.Add),

    EditUser("customer/editUser", "Edit User", Icons.Default.Add),
    Equipments("equipments", "Equipments", Icons.Default.SportsGymnastics)
}

enum class GymDest(
    val route: String, val label: String, val icon: ImageVector, val badge: String? = null
) {
    AddEquipment("customer/addEquipment", "Add Equipment", Icons.Default.Add),

    AddService("customer/addEquipment/addService", "Add Service", Icons.Default.Add)
}

enum class BottomDest(val route: String, val label: String, val icon: ImageVector) {
    Amc("amc", "AMC", Icons.AutoMirrored.Filled.List),
    Services(
        "services", "Services", Icons.Default.SportsGymnastics
    ),
    Favorites("calendar", "Calendar", Icons.Default.CalendarToday)
}

enum class ListDest(
    val route: String, val label: String, val icon: ImageVector, val badge: String? = null
) {
    ListScreen("customer/addEquipment/listScreen", "Add Item", Icons.Default.Add)
}