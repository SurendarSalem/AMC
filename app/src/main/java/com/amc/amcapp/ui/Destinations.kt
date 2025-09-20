package com.amc.amcapp.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.ui.graphics.vector.ImageVector

enum class DrawerDest(
    val route: String, val label: String, val icon: ImageVector, val badge: String? = null
) {
    Home("home", "Home", Icons.Default.Home), Users(
        "users",
        "Users",
        Icons.Default.Person
    ),
    Services("services", "AMC & Services", Icons.Default.HomeRepairService),

    Equipments("equipments", "Equipments", Icons.Default.SportsGymnastics),

    Settings("settings", "Settings", Icons.Default.Settings),

    Sales("sales", "Sales", Icons.Default.ShoppingBasket)

}

enum class UserDest(
    val route: String, val label: String, val icon: ImageVector, val badge: String? = null
) {
    AddUser("customers/addUser", "Add User", Icons.Default.Add),
    EditUser("customers/editUser", "Edit User", Icons.Default.Add),
    Equipments("equipments", "Equipments", Icons.Default.SportsGymnastics),
    AddAMC("customers/editUser/addAMC", "Add AMC", Icons.Default.SportsGymnastics)
}

enum class GymDest(
    val route: String, val label: String, val icon: ImageVector, val badge: String? = null
) {
    AddEquipment("customers/addEquipment", "Add Equipment", Icons.Default.Add),

    AddService("customers/addEquipment/addService", "Add Service", Icons.Default.Add)
}

enum class BottomDest(val route: String, val label: String, val icon: ImageVector) {
    Amc("amc", "AMC", Icons.AutoMirrored.Filled.List),
    Services("services", "Services", Icons.Default.SportsGymnastics),
    Favorites("sales", "Sales", Icons.Default.BusinessCenter)
}

enum class ListDest(
    val route: String, val label: String, val icon: ImageVector, val badge: String? = null
) {
    ListScreen("customers/addEquipment/listScreen", "Add Item", Icons.Default.Add)
}