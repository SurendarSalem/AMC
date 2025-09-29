package com.amc.amcapp.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.amc.amcapp.model.AMC
import com.amc.amcapp.model.User
import com.amc.amcapp.ui.BottomDest
import com.amc.amcapp.ui.ListDest
import com.amc.amcapp.ui.UserDest
import com.amc.amcapp.ui.screens.amc.AMCListScreen
import com.amc.amcapp.ui.screens.amc.AddAmcScreen
import com.amc.amcapp.ui.theme.LocalDimens

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ServiceScreen(onTitleUpdated: (String) -> Unit) {
    val navController = rememberNavController()
    val bottomItems = BottomDest.entries

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                startDestination = BottomDest.Amc.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(BottomDest.Amc.route) { AMCListScreen(navController) }
                composable(BottomDest.Services.route) {
                    AMCListScreen(navController)
                    onTitleUpdated("Services")
                }
                composable(UserDest.AddAMC.route) {
                    navController.previousBackStackEntry?.savedStateHandle?.let {
                        val user = it.get<User>("user")
                        val amc = it.get<AMC>("amc")
                        AddAmcScreen(
                            navController = navController,
                            gymOwner = user, amc = amc,
                            isForEdit = true
                        )
                    }
                }
                composable(ListDest.ListScreen.route) { backStackEntry ->
                    ListItemScreen(navController) {
                        onTitleUpdated(it)
                    }
                }
            }
        }


        BottomBar(navController, bottomItems)
    }
}

@Composable
private fun BottomBar(navController: NavHostController, items: List<BottomDest>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        tonalElevation = 4.dp,
        windowInsets = androidx.compose.foundation.layout.WindowInsets(0.dp) // 👈 ensures no padding
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route, onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }, icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        tint = if (currentRoute == item.route) White
                        else MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                    )
                }, label = {
                    Text(
                        item.label,
                        color = if (currentRoute == item.route) White
                        else MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        fontSize = LocalDimens.current.textMedium.sp,
                    )
                }, colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent // 👈 removes the rounded background
                )
            )
        }
    }
}
