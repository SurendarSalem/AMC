package com.amc.amcapp.ui

sealed class Screen(val route: String) {
    object LoginScreen : Screen("login_screen")
    object SignUpScreen : Screen("signup_screen")
    object ForgotPassword : Screen("forgot_password_scree")

    object AddGymScreen : Screen("add_gym_screen")

    object EquipmentScreen : Screen("equipments_screen")
}