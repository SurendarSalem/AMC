package com.amc.amcapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.amc.amcapp.ui.screens.ServiceScreen
import com.amc.amcapp.ui.screens.customer.CustomerListScreen
import com.amc.amcapp.ui.ui.EqualSizeMenuGridScreen
import com.amc.amcapp.ui.ui.MenuItem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen() {
    val navController = rememberNavController()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val topDestinations = DrawerDest.entries

    ModalNavigationDrawer(
        drawerState = drawerState, drawerContent = {
            ModalDrawerSheet {

                DrawerHeader()

                topDestinations.forEach { dest ->
                    NavigationDrawerItem(
                        label = { Text(dest.label) },
                        selected = currentDestination(navController)?.route == dest.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(dest.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(dest.icon, contentDescription = dest.label) },
                        badge = dest.badge?.let { { Badge { Text(it) } } },
                        modifier = Modifier
                    )
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
                                Firebase.auth.signOut()
                                drawerState.close()
                            }
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Menu",
                        modifier = Modifier.padding(8.dp)
                    )
                    Text("Logout")
                }
            }
        }) {

        Scaffold(topBar = {
            TopAppBar(
                title = { Text(titleForDestination(currentDestination(navController))) },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Open drawer")
                    }
                },
            )
        }) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = DrawerDest.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                val menuItems: List<MenuItem> = listOf(
                    MenuItem(
                        "pending_complaints",
                        "Pending Complaints",
                        Icons.Default.PendingActions
                    ),
                    MenuItem("create_schedule", "Create Schedule", Icons.Default.Schedule),
                    MenuItem("today_amcs", "Today AMCs", Icons.Default.Today),
                    MenuItem("service_report", "Generate Report", Icons.Default.FilePresent)
                )

                composable(DrawerDest.Home.route) {
                    EqualSizeMenuGridScreen(
                        items = menuItems, onClick = {

                        })
                }

                composable(DrawerDest.Customer.route) {
                    CustomerListScreen(navController)
                }

            }
        }
    }
}

@Composable
private fun DrawerHeader() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Compose Demo", style = MaterialTheme.typography.titleLarge)
        Text("Bottom Nav + Drawer", style = MaterialTheme.typography.bodyMedium)
        HorizontalDivider(
            modifier = Modifier.padding(top = 12.dp),
            thickness = DividerDefaults.Thickness,
            color = DividerDefaults.color
        )
    }
}


@Composable
fun SearchScreen() {
    Text("Search UI...")
}

@Composable
fun FavoritesScreen() {
    Text("Saved items...")
}

@Composable
fun currentDestination(navController: NavHostController): NavDestination? {
    val backStackEntry by navController.currentBackStackEntryAsState()
    return backStackEntry?.destination
}

private fun titleForDestination(dest: NavDestination?): String {
    val route = dest?.route ?: return "Compose Demo"
    // Map known routes to titles; fall back to route string
    DrawerDest.entries.firstOrNull { it.route == route }?.let { return it.label }
    BottomDest.entries.firstOrNull { it.route == route }?.let { return it.label }
    return when {
        route.startsWith("detail/") -> "Detail"
        else -> route
    }
}
