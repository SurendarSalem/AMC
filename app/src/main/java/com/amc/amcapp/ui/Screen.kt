package com.amc.amcapp.ui

sealed class Screen(val route: String) {
    object LoginScreen : Screen("login_screen")
    object SignUpScreen : Screen("signup_screen")
    object ForgotPassword : Screen("forgot_password_scree")
}