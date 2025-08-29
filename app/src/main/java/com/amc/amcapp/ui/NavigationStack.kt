package com.amc.amcapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amc.amcapp.ui.screens.ForgotPasswordScreen
import com.amc.amcapp.ui.screens.LoginScreen
import com.amc.amcapp.ui.screens.SignUpScreen

@Composable
fun NavigationStack() {
    val navController = rememberNavController()


    NavHost(
        navController = navController, startDestination = Screen.LoginScreen.route
    ) {

        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navController = navController)
        }
        composable(route = Screen.SignUpScreen.route) {
            SignUpScreen(navController = navController)
        }
        composable(route = Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
        }
    }
}