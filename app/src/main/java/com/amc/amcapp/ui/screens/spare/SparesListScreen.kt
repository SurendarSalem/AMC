package com.amc.amcapp.ui.screens.spare

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
import com.amc.amcapp.ui.SpareDest
import com.amc.amcapp.ui.screens.ListTypeKey
import com.amc.amcapp.ui.theme.LocalDimens
import com.amc.amcapp.util.Constants
import com.amc.amcapp.viewmodel.AmcPackageListViewModel
import com.amc.amcapp.viewmodel.SparesListViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SparesListScreen(
    navController: NavController, sparesListViewModel: SparesListViewModel = koinViewModel()
) {
    val sparesList = sparesListViewModel.spares.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
    val scope = rememberCoroutineScope()
    val shouldRefresh by navController.currentBackStackEntry?.savedStateHandle?.getStateFlow(
        "refresh",
        false
    )?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }

    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            sparesListViewModel.getAllSpares()
        }
    }

    SwipeRefresh(
        state = swipeRefreshState, onRefresh = {
            scope.launch {
                isRefreshing = true
                sparesListViewModel.getAllSpares()
                isRefreshing = false
            }
        }) {

        Box(modifier = Modifier.fillMaxSize()) {
            when (sparesList.value) {
                is ApiResult.Loading -> {
                    AppLoadingBar(this@Box)
                }

                is ApiResult.Error -> {
                    AppError(errorMessage = (sparesList.value as ApiResult.Error).message)
                }

                is ApiResult.Success -> {
                    val spares = (sparesList.value as ApiResult.Success).data
                    if (spares.isEmpty()) {
                        AppError(errorMessage = "No Spares found")
                    } else {
                        LazyColumn {
                            items(items = spares, key = { it.id }) { spare ->
                                SpareItem(spare = spare, onClick = {
                                    navController.currentBackStackEntry?.savedStateHandle?.apply {
                                        set("spare", spare)
                                    }
                                    navController.navigate(SpareDest.AddSpare.route)
                                })
                            }
                        }
                    }

                }

                ApiResult.Empty -> {}
            }

            ExtendedFloatingActionButton(
                onClick = {
                    navController.navigate(SpareDest.AddSpare.route)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(LocalDimens.current.spacingExtraLarge.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add, contentDescription = "Add AMC Package"
                    )
                },
                text = {
                    Text("Add Spare")
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            )

        }
    }
}