import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amc.amcapp.Equipment
import com.amc.amcapp.gym.EquipmentsListViewModel
import com.amc.amcapp.model.User
import com.amc.amcapp.ui.ApiResult
import com.amc.amcapp.ui.AppLoadingBar
import com.amc.amcapp.ui.GymDest
import com.amc.amcapp.ui.theme.LocalDimens
import com.amc.amcapp.util.AppImagePicker
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EquipmentsListScreen(
    navController: NavController,
    user: User,
    equipmentsListViewModel: EquipmentsListViewModel = koinViewModel()
) {
    val equipmentsListState by equipmentsListViewModel.equipmentsListState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(user.firebaseId) {
        equipmentsListViewModel.preFillUserId(user.firebaseId)
        if (equipmentsListViewModel.equipmentsListState.value !is ApiResult.Success) {
            equipmentsListViewModel.fetchEquipments(user.firebaseId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(LocalDimens.current.spacingMedium.dp)
    ) {
        when (equipmentsListState) {
            is ApiResult.Loading -> AppLoadingBar(this@Box)
            is ApiResult.Error -> {
                LaunchedEffect(snackbarHostState) {
                    snackbarHostState.showSnackbar((equipmentsListState as ApiResult.Error).message)
                }
            }

            is ApiResult.Success -> {
                val equipments = (equipmentsListState as ApiResult.Success<List<Equipment>>).data
                if (equipments.isEmpty()) {
                    Text("No Equipments found.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                            8.dp
                        ),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                            8.dp
                        )
                    ) {
                        items(equipments) { equipment ->
                            EquipmentItem(equipment, {
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
            }

            ApiResult.Empty -> {}
        }

        Button(modifier = Modifier.align(Alignment.Center), onClick = {
            if (equipmentsListState is ApiResult.Success) {
                val list = (equipmentsListState as ApiResult.Success<List<Equipment>>).data
                if (list.isNotEmpty()) {
                    navController.currentBackStackEntry?.savedStateHandle?.set("equipment", list)
                    navController.navigate(GymDest.AddService.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }) {
            Text("Create Service")
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp), onClick = {
                navController.currentBackStackEntry?.savedStateHandle?.set("user", user)
                navController.navigate(GymDest.AddEquipment.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            }, shape = CircleShape, containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Equipment")
        }

        SnackbarHost(
            hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun EquipmentItem(equipment: Equipment, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .clickable {
                onClick()
            },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AppImagePicker(
                imageUrl = equipment.imageUrl,
                onImageReturned = {},
                onErrorReturned = {})
            Text(
                equipment.name,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
