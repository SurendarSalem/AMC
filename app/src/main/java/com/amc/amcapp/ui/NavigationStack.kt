package com.amc.amcapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.amc.amcapp.equipments.AddEquipmentScreen
import com.amc.amcapp.ui.screens.ForgotPasswordScreen
import com.amc.amcapp.ui.screens.LoginScreen
import com.amc.amcapp.ui.screens.SignUpScreen
import com.amc.amcapp.ui.screens.gym.AddGymScreen

@Composable
fun NavigationStack() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.LoginScreen.route) {

        composable(route = Screen.LoginScreen.route) {
            AddEquipmentScreen(navController = navController)
        }
        composable(route = Screen.SignUpScreen.route) {
            AddEquipmentScreen(navController = navController)
        }
        composable(route = Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
        }
    }
}