package com.amc.amcapp.ui.screens.gym

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.amc.amcapp.Equipment
import com.amc.amcapp.gym.EquipmentsListViewModel
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.model.User
import com.amc.amcapp.model.UserType
import com.amc.amcapp.ui.ApiResult
import com.amc.amcapp.ui.AppError
import com.amc.amcapp.ui.AppProgressBar
import com.amc.amcapp.ui.GymDest
import com.amc.amcapp.ui.ListDest
import com.amc.amcapp.ui.screens.ListTypeKey
import com.amc.amcapp.ui.showSnackBar
import com.amc.amcapp.ui.theme.LocalDimens
import com.amc.amcapp.util.Constants
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EquipmentsListScreen(
    navController: NavController, user: User
) {
    val equipmentsListViewModel: EquipmentsListViewModel = koinViewModel(
        parameters = { parametersOf(user) })
    val equipmentsListState by equipmentsListViewModel.equipmentsListState.collectAsState()
    val updateEquipmentState by equipmentsListViewModel.updateEquipmentState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    val selectedEquipments by savedStateHandle?.getStateFlow(
        Constants.SELECTED_EQUIPMENTS, emptyList<Equipment>()
    )?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) }
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    var originaEquipments = emptyList<Equipment>()
    var newEquipmentSelected by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            equipmentsListViewModel.notifyState.collectLatest { message ->
                when (message) {
                    is NotifyState.ShowToast -> showSnackBar(
                        scope, snackBarHostState, message.message
                    )

                    else -> Unit
                }
            }
        }
    }

    LaunchedEffect(selectedEquipments) {
        newEquipmentSelected = originaEquipments.toSet() != selectedEquipments.toSet()
        equipmentsListViewModel.onEquipmentsAdded(selectedEquipments)
    }

    // Listen for toast events
    LaunchedEffect(Unit) {
        equipmentsListViewModel.notifyState.collectLatest { notify ->
            if (notify is NotifyState.ShowToast) snackBarHostState.showSnackbar(notify.message)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(LocalDimens.current.spacingMedium.dp)
    ) {
        when (val state = equipmentsListState) {
            is ApiResult.Loading -> AppProgressBar(this@Box)

            is ApiResult.Error -> {
                LaunchedEffect(state.message) {
                    snackBarHostState.showSnackbar(state.message)
                }
            }

            is ApiResult.Success -> {
                val equipments = state.data
                if (equipments.isEmpty()) {
                    AppError("No Equipments found.")
                } else {
                    EquipmentsGrid(
                        equipments = equipments, user = user, navController = navController
                    )
                }
            }

            ApiResult.Empty -> {}
        }

        when (updateEquipmentState) {
            is ApiResult.Loading -> AppProgressBar(this@Box)
            else -> {}
        }
        if (user.userType != UserType.ADMIN) {

            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(LocalDimens.current.spacingLarge.dp), onClick = {
                    savedStateHandle?.apply {
                        this[Constants.LIST_TYPE_KEY] = ListTypeKey.EQUIPMENTS
                        if (equipmentsListState is ApiResult.Success) {
                            this[Constants.SELECTED_EQUIPMENTS] =
                                (equipmentsListState as ApiResult.Success<List<Equipment>>).data
                        }
                    }
                    navController.navigate(ListDest.ListScreen.route)
                }, shape = CircleShape, containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Equipment")
            }

            Button(
                enabled = newEquipmentSelected && equipmentsListState !is ApiResult.Loading && updateEquipmentState !is ApiResult.Loading,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(LocalDimens.current.spacingLarge.dp),
                onClick = {

                    val currentEquipments = if (equipmentsListState is ApiResult.Success) {
                        (equipmentsListState as ApiResult.Success<List<Equipment>>).data
                    } else {
                        emptyList()
                    }
                    val equipmentIds = currentEquipments.map { it.id }
                    user.equipments = equipmentIds
                    scope.launch {
                        equipmentsListViewModel.updateEquipments(user)
                    }
                }) {
                Text("Update Equipments")
            }
        }

        SnackbarHost(
            hostState = snackBarHostState, modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun EquipmentsGrid(
    equipments: List<Equipment>, user: User, navController: NavController
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(equipments, key = { it.id }) { equipment ->
            EquipmentItem(
                equipment = equipment, onClick = {
                    navController.currentBackStackEntry?.savedStateHandle?.apply {
                        set("user", user)
                        set("equipment", equipment)
                    }
                    navController.navigate(GymDest.AddEquipment.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                })
        }
    }
}

@Composable
private fun EquipmentItem(
    equipment: Equipment, onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // ensures uniform square items
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(equipment.imageUrl)
                    .crossfade(true).diskCachePolicy(CachePolicy.ENABLED).build(),
                contentDescription = equipment.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            Text(
                equipment.name,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
