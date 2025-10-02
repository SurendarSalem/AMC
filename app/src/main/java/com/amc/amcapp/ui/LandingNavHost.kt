package com.amc.amcapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Today
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.amc.amcapp.Equipment
import com.amc.amcapp.model.AMC
import com.amc.amcapp.model.User
import com.amc.amcapp.ui.screens.ListItemScreen
import com.amc.amcapp.ui.screens.ServiceScreen
import com.amc.amcapp.ui.screens.amc.AddAmcPackageScreen
import com.amc.amcapp.ui.screens.amc.AddAmcScreen
import com.amc.amcapp.ui.screens.amc.AmcPackageScreen
import com.amc.amcapp.ui.screens.customer.AddUserScreen
import com.amc.amcapp.ui.screens.customer.CustomerListScreen
import com.amc.amcapp.ui.screens.gym.EquipmentsListScreen
import com.amc.amcapp.ui.screens.gym.equipment.AddEquipmentScreen
import com.amc.amcapp.ui.screens.service.AddServiceScreen
import com.amc.amcapp.util.Constants

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LandingNavHost(
    currentUser: User?,
    navController: NavHostController,
    innerPadding: PaddingValues,
    onListItemScreenTitleChange: (String) -> Unit,
    onMenuEnabledChange: (Boolean) -> Unit,
    onMenuIconChange: (ImageVector) -> Unit,
    onMenuClickChange: ((() -> Unit) -> Unit) // ✅ changed here){}
) {
    NavHost(
        navController = navController,
        startDestination = DrawerDest.Home.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        val menuItems: List<MenuItem> = listOf(
            MenuItem("pending_complaints", "Pending Complaints", Icons.Default.PendingActions),
            MenuItem("create_schedule", "Create Schedule", Icons.Default.Schedule),
            MenuItem("today_amcs", "Today AMCs", Icons.Default.Today),
            MenuItem("service_report", "Generate Report", Icons.Default.FilePresent)
        )

        // Home Screen
        composable(DrawerDest.Home.route) {
            EqualSizeMenuGridScreen(
                items = menuItems, onClick = { navController.navigate(DrawerDest.Services.route) })
        }

        // Services Screen
        composable(DrawerDest.Services.route) {
            ServiceScreen { onListItemScreenTitleChange(it) }
        }

        composable(DrawerDest.DrawerEquipments.route) {

            EquipmentsListScreen(navController = navController, gymOwner = null)

        }

        composable(UserDest.Equipments.route) {
            val user = navController.previousBackStackEntry?.savedStateHandle?.get<User>("user")
            user?.let {
                EquipmentsListScreen(navController = navController, gymOwner = it)
                onMenuEnabledChange(false)
            }
        }

        // Customers Screen
        composable(DrawerDest.Users.route) {
            CustomerListScreen(navController)
        }


        composable(DrawerDest.AmcPackages.route) {
            AmcPackageScreen(navController)
        }

        composable(AmcDest.AddAmcPackages.route) {
            AddAmcPackageScreen(navController)
        }
        // Equipments List

        // Add/Edit Equipment
        composable(GymDest.AddEquipment.route) {
            val equipment =
                navController.previousBackStackEntry?.savedStateHandle?.get<Equipment>("equipment")

            AddEquipmentScreen(
                navController = navController,
                equipment = equipment,
                onMenuUpdated = { enabled, icon, onClick ->
                    onMenuEnabledChange(enabled)
                    onMenuIconChange(icon)
                    onMenuClickChange(onClick) // ✅ fixed: pass callback
                })
        }

        // Add User
        composable(UserDest.AddUser.route) {
            val user = navController.previousBackStackEntry?.savedStateHandle?.get<User>("user")

            AddUserScreen(
                navController = navController,
                user = user,
                onMenuUpdated = { enabled, icon, onClick ->
                    onMenuEnabledChange(enabled)
                    onMenuIconChange(icon)
                    onMenuClickChange(onClick) // ✅
                })
        }

        // Add Service
        composable(GymDest.AddService.route) {
            val equipments =
                navController.previousBackStackEntry?.savedStateHandle?.get<List<Equipment>>("equipment")

            equipments?.let {
                AddServiceScreen(
                    navController = navController,
                    equipments = it,
                    onMenuUpdated = { enabled, icon, onClick ->
                        onMenuEnabledChange(enabled)
                        onMenuIconChange(icon)
                        onMenuClickChange(onClick) // ✅
                    })
            }
        }

        // Edit User
        composable(UserDest.EditUser.route) {
            val user = navController.previousBackStackEntry?.savedStateHandle?.get<User>("user")

            AddUserScreen(
                navController = navController,
                user = user,
                onMenuUpdated = { enabled, icon, onClick ->
                    onMenuEnabledChange(enabled)
                    onMenuIconChange(icon)
                    onMenuClickChange(onClick) // ✅
                })
        }

        // Add AMC
        composable(UserDest.AddAMC.route) {
            navController.previousBackStackEntry?.savedStateHandle?.let {
                val amc = it.get<AMC>("amc")
                val gymOwner = it.get<User?>(Constants.GYM_OWNER)
                AddAmcScreen(
                    navController = navController, amc = amc, gymOwner = gymOwner
                )
            }
        }

        // Generic List Screen
        composable(ListDest.ListScreen.route) {
            ListItemScreen(navController) { onListItemScreenTitleChange(it) }
        }
    }
}
