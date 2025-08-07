package com.amc.amcapp.ui.screens.gym

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amc.amcapp.Equipment
import com.amc.amcapp.R
import com.amc.amcapp.gym.AddGymViewModel
import com.amc.amcapp.gym.EquipmentsListViewModel
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.ui.ApiResult
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentsListScreen(
    navController: NavController, equipmentsListViewModel: EquipmentsListViewModel = koinViewModel()
) {
    val equipmentsListState by equipmentsListViewModel.equipmentsListState.collectAsState()

    when (val state = equipmentsListState) {
        is ApiResult.Success -> {
            val equipmentsList = state.data
            if (equipmentsList.isEmpty()) {
                Text(
                    "No equipments found",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    items(
                        equipmentsList.size
                    ) { index ->
                        val equipment = equipmentsList[index]
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = equipment.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    items(equipmentsList.size) { index ->
                        Text(
                            text = equipmentsList[index].name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

        }

        is ApiResult.Error -> {
            val errorMessage = (equipmentsListState as ApiResult.Error).message
            Text(text = "Error: $errorMessage")
        }

        else -> {
            Text(text = "Loading...")
        }
    }

}




