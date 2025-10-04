package com.amc.amcapp.ui.screens.service

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.amc.amcapp.Complaint
import com.amc.amcapp.Equipment
import com.amc.amcapp.Service
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.ui.AnimatedSectionCard
import com.amc.amcapp.ui.ApiResult
import com.amc.amcapp.ui.AppTextField
import com.amc.amcapp.ui.showSnackBar
import com.amc.amcapp.ui.theme.LocalDimens
import com.amc.amcapp.util.BubbleProgressBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AddServiceScreen(
    navController: NavController,
    service: Service? = null,
    equipments: List<Equipment>,
    addServiceViewModel: AddServiceViewModel = koinViewModel(),
    onMenuUpdated: (Boolean, ImageVector, () -> Unit) -> Unit
) {
    val addServiceState by addServiceViewModel.addServiceState.collectAsState()
    val serviceState by addServiceViewModel.serviceState.collectAsState()
    val errorMessage by addServiceViewModel.errorMessage.collectAsState()
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val isEditEnabled = remember { mutableStateOf(service == null) }
    val context = LocalContext.current

    fun updateMenu() {
        onMenuUpdated(
            true, if (isEditEnabled.value) Icons.Default.Cancel else Icons.Default.Edit
        ) { isEditEnabled.value = !isEditEnabled.value }
    }

    LaunchedEffect(service?.id) {
        if (service != null) {
            updateMenu()
            //addServiceViewModel.preFillDetails(service)
        } else {
            onMenuUpdated(false, Icons.Default.Edit) {}
        }
    }

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            addServiceViewModel.notifyState.collectLatest { message ->
                when (message) {
                    is NotifyState.ShowToast -> {
                        scope.launch {
                            snackBarHostState.showSnackbar(
                                message = message.message, actionLabel = "OK"
                            )
                        }
                    }

                    is NotifyState.LaunchActivity -> navController.popBackStack()
                    else -> {}
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(modifier = Modifier.fillMaxWidth()) {
                AnimatedSectionCard("Service Info", Icons.Default.Person, true) {
                    AppTextField(
                        value = serviceState.name,
                        onValueChange = addServiceViewModel::onNameChanged,
                        label = "Service Name",
                        enabled = isEditEnabled.value
                    )
                    Spacer(Modifier.height(12.dp))

                    AppTextField(
                        value = serviceState.description,
                        onValueChange = addServiceViewModel::onDescriptionChanged,
                        label = "Description",
                        minLines = 3,
                        enabled = isEditEnabled.value
                    )
                }
            }
            Text(
                text = "Select Equipments",
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                fontWeight = FontWeight.SemiBold
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(LocalDimens.current.spacingMedium.dp)
                    .border(
                        1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(10.dp)
                    )
                    .clip(RoundedCornerShape(10.dp))
            ) {
                Column {
                    equipments.forEach {
                        EquipmentItem(it)
                    }
                }
            }
            Button(
                onClick = {
                    scope.launch {
                        val error = addServiceViewModel.validate(service != null, serviceState)
                        if (error == null) {
                            addServiceViewModel.addServiceToFirebase()
                        } else {
                            showSnackBar(
                                scope,
                                snackBarHostState,
                                error,
                                snackBarDuration = SnackbarDuration.Short
                            )
                            delay(100)
                            scrollState.animateScrollTo(scrollState.maxValue)
                        }
                    }
                },
                enabled = isEditEnabled.value,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    if (service == null) "Create Service" else "Update Service",
                    fontSize = LocalDimens.current.textMedium.sp
                )
            }
        }
        Spacer(Modifier.height(LocalDimens.current.spacingMedium.dp))

        SnackbarHost(
            hostState = snackBarHostState
        )
        Spacer(Modifier.height(LocalDimens.current.spacingMedium.dp))
        if (addServiceState is ApiResult.Loading) {
            BubbleProgressBar(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center)
            )
        }
    }

}

@Composable
fun EquipmentItem(equipment: Equipment) {
    var selected by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(
                    horizontal = LocalDimens.current.spacingMedium.dp,
                    vertical = LocalDimens.current.spacingSmall.dp
                ), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = equipment.name, fontSize = 18.sp, modifier = Modifier.weight(1f)
            )
            Checkbox(
                checked = selected, onCheckedChange = { selected = it })
        }

        AnimatedVisibility(visible = selected) { // nice expand animation
            Column {
                equipment.complaints.forEachIndexed { index, complaint ->
                    ComplaintItem(complaint, index)
                }
            }
        }
    }
}

@Composable
fun ComplaintItem(complaint: Complaint, index: Int) {
    var selected by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = LocalDimens.current.spacingMedium.dp
            ), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = complaint.name, fontSize = 14.sp, modifier = Modifier.weight(1f)
        )
        Checkbox(
            checked = selected, onCheckedChange = { selected = it })
    }
}
