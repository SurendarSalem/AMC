package com.amc.amcapp.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amc.amcapp.equipments.AddEquipmentScreen
import com.amc.amcapp.ui.screens.ForgotPasswordScreen
import com.amc.amcapp.ui.screens.LoginScreen
import com.amc.amcapp.ui.screens.SignUpScreen
import com.amc.amcapp.ui.ui.EqualSizeMenuGridScreen
import com.amc.amcapp.ui.ui.MenuItem

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