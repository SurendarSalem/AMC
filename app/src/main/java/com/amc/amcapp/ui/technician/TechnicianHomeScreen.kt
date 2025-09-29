package com.amc.amcapp.ui.technician

import android.content.Intent
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.amc.amcapp.AuthRepository
import com.amc.amcapp.MainActivity
import com.amc.amcapp.data.datastore.PreferenceHelper
import com.amc.amcapp.model.AMC
import com.amc.amcapp.model.User
import com.amc.amcapp.ui.ListDest
import com.amc.amcapp.ui.UserDest
import com.amc.amcapp.ui.screens.ListItemScreen
import com.amc.amcapp.ui.screens.amc.AMCListScreen
import com.amc.amcapp.ui.screens.amc.AddAmcScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TechnicianHomeScreen(
    authRepository: AuthRepository, preferenceHelper: PreferenceHelper,
) {
    val navController = rememberNavController()
    var menuEnabled by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val activity = LocalActivity.current
    val context = LocalContext.current

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = titleForDestination(currentDestination(navController)),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }, actions = {
                if (menuEnabled) {
                    IconButton(onClick = {
                        scope.launch {
                            authRepository.signOut()
                            preferenceHelper.clearAll()
                            activity?.let { finishAffinity(it) }
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Menu Action",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }, bottomBar = {
        BottomBar(navController, items = TechnicianBottomDest.entries)
    }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TechnicianBottomDest.Amc.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(TechnicianBottomDest.Amc.route) {
                AMCListScreen(navController)
            }
            composable(UserDest.AddAMC.route) {
                navController.previousBackStackEntry?.savedStateHandle?.let {
                    val amc = it.get<AMC>("amc")
                    AddAmcScreen(
                        navController = navController, amc = amc, isForEdit = true
                    )
                }
            }
            composable(ListDest.ListScreen.route) { backStackEntry ->
                ListItemScreen(navController) { title ->
                    //onTitleUpdated(it)
                }
            }
        }
    }
}

@Composable
private fun BottomBar(navController: NavHostController, items: List<TechnicianBottomDest>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        tonalElevation = 4.dp,
        windowInsets = androidx.compose.foundation.layout.WindowInsets(0.dp)
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
                        imageVector = item.icon, contentDescription = item.label
                    )
                }, label = {
                    Text(item.label)
                }, alwaysShowLabel = true, colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
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
    TechnicianBottomDest.entries.firstOrNull { it.route == route }?.let { return it.label }
    UserDest.entries.firstOrNull { it.route == route }?.let { return it.label }
    ListDest.entries.firstOrNull { it.route == route }?.let { return it.label }
    return route
}