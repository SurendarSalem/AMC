package com.amc.amcapp.ui.screens.amc

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.amc.amcapp.Equipment
import com.amc.amcapp.model.UserType
import com.amc.amcapp.ui.AmcDest
import com.amc.amcapp.ui.AmcPackageItem
import com.amc.amcapp.ui.ApiResult
import com.amc.amcapp.ui.AppError
import com.amc.amcapp.ui.AppLoadingBar
import com.amc.amcapp.ui.DrawerDest
import com.amc.amcapp.ui.GymDest
import com.amc.amcapp.ui.ListDest
import com.amc.amcapp.ui.screens.ListTypeKey
import com.amc.amcapp.ui.theme.LocalDimens
import com.amc.amcapp.util.Constants
import com.amc.amcapp.viewmodel.AmcPackageListViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun AmcPackageScreen(
    navController: NavController,
    amcPackageListViewModel: AmcPackageListViewModel = koinViewModel()
) {
    val amcPackageList = amcPackageListViewModel.amcPackagesList.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
    val scope = rememberCoroutineScope()
    val shouldRefresh by navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("refresh", false)
        ?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }

    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            amcPackageListViewModel.getAllAmcPackages()
        }
    }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            scope.launch {
                isRefreshing = true
                amcPackageListViewModel.getAllAmcPackages()
                isRefreshing = false
            }
        }
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            when (amcPackageList.value) {
                is ApiResult.Loading -> {
                    AppLoadingBar(this@Box)
                }

                is ApiResult.Error -> {
                    AppError(errorMessage = (amcPackageList.value as ApiResult.Error).message)
                }

                is ApiResult.Success -> {
                    val amcPackages = (amcPackageList.value as ApiResult.Success).data
                    if (amcPackages.isEmpty()) {
                        AppError(errorMessage = "No AMC Packages found")
                    } else {
                        LazyColumn {
                            items(items = amcPackages, key = { it.id }) {
                                AmcPackageItem(amcPackage = it)
                            }
                        }
                    }

                }

                ApiResult.Empty -> {}
            }

            ExtendedFloatingActionButton(
                onClick = {
                    navController.navigate(AmcDest.AddAmcPackages.route)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(LocalDimens.current.spacingExtraLarge.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add AMC Package"
                    )
                },
                text = {
                    Text("Add Package")
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            )

        }
    }
}