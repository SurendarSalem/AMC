package com.amc.amcapp.ui

import EquipmentsListScreen
import android.content.Intent
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Badge
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.amc.amcapp.Equipment
import com.amc.amcapp.MainActivity
import com.amc.amcapp.model.AMC
import com.amc.amcapp.model.User
import com.amc.amcapp.ui.screens.ListItemScreen
import com.amc.amcapp.ui.screens.ServiceScreen
import com.amc.amcapp.ui.screens.amc.AddAmcScreen
import com.amc.amcapp.ui.screens.customer.AddUserScreen
import com.amc.amcapp.ui.screens.customer.CustomerListScreen
import com.amc.amcapp.ui.screens.gym.equipment.AddEquipmentScreen
import com.amc.amcapp.ui.screens.service.AddServiceScreen
import com.amc.amcapp.ui.theme.LocalDimens
import com.amc.amcapp.util.Avatar
import com.amc.amcapp.viewmodel.LandingViewModel
import kotlinx.coroutines.launch

@Composable
private fun DrawerHeader(user: User?) {
    Row(modifier = Modifier.padding(16.dp)) {
        user?.let {
            Avatar(it.imageUrl, it.name)
        }
        Column(modifier = Modifier.padding(start = LocalDimens.current.spacingMedium.dp)) {
            Text(user?.name ?: "Guest", style = MaterialTheme.typography.titleLarge)
            Text(user?.email ?: "Guest", style = MaterialTheme.typography.bodyMedium)
            HorizontalDivider(
                modifier = Modifier.padding(top = 12.dp),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(landingViewModel: LandingViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val activity = LocalActivity.current
    val context = LocalContext.current
    val topDestinations = DrawerDest.entries

    // 🔑 State for menu (child controlled)
    var menuEnabled by remember { mutableStateOf(false) }
    var menuIcon by remember { mutableStateOf(Icons.Default.Search) }
    var menuClick: () -> Unit by remember { mutableStateOf({}) }

    ModalNavigationDrawer(
        drawerState = drawerState, drawerContent = {
            ModalDrawerSheet {
                val user = landingViewModel.user
                DrawerHeader(user)

                topDestinations.forEach { dest ->
                    NavigationDrawerItem(
                        label = { Text(dest.label) },
                        selected = currentDestination(navController)?.route == dest.route || currentDestination(
                            navController
                        )?.route?.contains(dest.route + "/") == true,
                        onClick = {
                            scope.launch { drawerState.close() }
                            try {
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            } catch (e: IllegalArgumentException) {
                                e.printStackTrace()
                            }
                        },
                        icon = { Icon(dest.icon, contentDescription = dest.label) },
                        badge = dest.badge?.let { { Badge { Text(it) } } })
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            scope.launch {
                                landingViewModel.logout()
                                drawerState.close()
                                activity?.let { finishAffinity(it) }
                                val intent = Intent(context, MainActivity::class.java)
                                context.startActivity(intent)
                            }
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Logout",
                        modifier = Modifier.padding(LocalDimens.current.spacingMedium.dp)
                    )
                    Text("Logout")
                }
            }
        }) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(titleForDestination(currentDestination(navController))) },
                    actions = {
                        if (menuEnabled) {
                            IconButton(onClick = { menuClick() }) {
                                Icon(menuIcon, contentDescription = "Menu Action")
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Open drawer")
                        }
                    })
            }) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = DrawerDest.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                val menuItems: List<MenuItem> = listOf(
                    MenuItem(
                        "pending_complaints", "Pending Complaints", Icons.Default.PendingActions
                    ),
                    MenuItem("create_schedule", "Create Schedule", Icons.Default.Schedule),
                    MenuItem("today_amcs", "Today AMCs", Icons.Default.Today),
                    MenuItem("service_report", "Generate Report", Icons.Default.FilePresent)
                )

                composable(DrawerDest.Home.route) {
                    EqualSizeMenuGridScreen(items = menuItems, onClick = {
                        navController.navigate(DrawerDest.Services.route)
                    })
                }

                composable(DrawerDest.Services.route) {
                    ServiceScreen()
                }

                composable(DrawerDest.Users.route) {
                    CustomerListScreen(navController)
                }

                composable(UserDest.Equipments.route) {
                    val user =
                        navController.previousBackStackEntry?.savedStateHandle?.get<User>("user")

                    user?.let {
                        EquipmentsListScreen(
                            navController = navController, user = it
                        )
                    }
                    menuEnabled = false
                }

                composable(GymDest.AddEquipment.route) {
                    val user =
                        navController.previousBackStackEntry?.savedStateHandle?.get<User>("user")
                    val equipment =
                        navController.previousBackStackEntry?.savedStateHandle?.get<Equipment>("equipment")

                    user?.let {
                        AddEquipmentScreen(
                            navController = navController,
                            user = it,
                            equipment = equipment,
                            onMenuUpdated = { enabled, icon, onClick ->
                                menuEnabled = enabled
                                menuIcon = icon
                                menuClick = onClick
                            })
                    }
                }

                composable(UserDest.AddUser.route) {
                    val user =
                        navController.previousBackStackEntry?.savedStateHandle?.get<User>("user")

                    AddUserScreen(
                        navController = navController,
                        user = user,
                        onMenuUpdated = { enabled, icon, onClick ->
                            menuEnabled = enabled
                            menuIcon = icon
                            menuClick = onClick
                        })
                }

                composable(GymDest.AddService.route) {
                    val equipments =
                        navController.previousBackStackEntry?.savedStateHandle?.get<List<Equipment>>(
                            "equipment"
                        )

                    AddServiceScreen(
                        navController = navController,
                        equipments = equipments!!,
                        onMenuUpdated = { enabled, icon, onClick ->
                            menuEnabled = enabled
                            menuIcon = icon
                            menuClick = onClick
                        })
                }

                composable(UserDest.EditUser.route) {
                    val user =
                        navController.previousBackStackEntry?.savedStateHandle?.get<User>("user")

                    AddUserScreen(
                        navController = navController,
                        user = user,
                        onMenuUpdated = { enabled, icon, onClick ->
                            menuEnabled = enabled
                            menuIcon = icon
                            menuClick = onClick
                        })
                }

                composable(UserDest.AddAMC.route) {
                    navController.previousBackStackEntry?.savedStateHandle?.let {
                        val user = it.get<User>("user")
                        val amc = it.get<AMC>("amc")
                        user?.let {
                            AddAmcScreen(
                                navController = navController,
                                user = user, amc = amc,
                            )
                        }
                    }

                }

                composable(ListDest.ListScreen.route) { backStackEntry ->

                    ListItemScreen(navController)
                }

            }
        }
    }
}


@Composable
fun currentDestination(navController: NavHostController): NavDestination? {
    val backStackEntry by navController.currentBackStackEntryAsState()
    return backStackEntry?.destination
}

private fun titleForDestination(dest: NavDestination?): String {
    val route = dest?.route ?: return "Compose Demo"
    DrawerDest.entries.firstOrNull { it.route == route }?.let { return it.label }
    BottomDest.entries.firstOrNull { it.route == route }?.let { return it.label }
    UserDest.entries.firstOrNull { it.route == route }?.let { return it.label }
    GymDest.entries.firstOrNull { it.route == route }?.let { return it.label }
    ListDest.entries.firstOrNull { it.route == route }?.let { return it.label }

    return when {
        route.startsWith("detail/") -> "Detail"
        else -> route
    }
}
