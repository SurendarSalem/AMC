package com.amc.amcapp.ui.screens.gym.equipment

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.amc.amcapp.Equipment
import com.amc.amcapp.equipments.AddEquipmentViewModel
import com.amc.amcapp.model.User
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.ui.AnimatedSectionCard
import com.amc.amcapp.ui.ApiResult
import com.amc.amcapp.ui.AppTextField
import com.amc.amcapp.ui.showSnackBar
import com.amc.amcapp.ui.theme.LocalDimens
import com.amc.amcapp.util.AppImagePicker
import com.amc.amcapp.util.BubbleProgressBar
import com.amc.amcapp.util.openAppSettings
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AddEquipmentScreen(
    navController: NavController,
    user: User,
    equipment: Equipment? = null,
    addEquipmentViewModel: AddEquipmentViewModel = koinViewModel(),
    onMenuUpdated: (Boolean, ImageVector, () -> Unit) -> Unit
) {
    val addEquipmentState by addEquipmentViewModel.addEquipmentState.collectAsState()
    val equipmentState by addEquipmentViewModel.equipmentState.collectAsState()
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val isEditEnabled = remember { mutableStateOf(equipment == null) }
    val context = LocalContext.current

    fun updateMenu() {
        onMenuUpdated(
            true, if (isEditEnabled.value) Icons.Default.Cancel else Icons.Default.Edit
        ) { isEditEnabled.value = !isEditEnabled.value }
    }

    LaunchedEffect(user.firebaseId) {
        addEquipmentViewModel.updateUser(user)
    }

    LaunchedEffect(equipment?.id) {
        if (equipment != null) {
            updateMenu()
            addEquipmentViewModel.preFillDetails(equipment)
        } else {
            onMenuUpdated(false, Icons.Default.Edit) {}
        }
    }

    // Collect notifications
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            addEquipmentViewModel.notifyState.collectLatest { message ->
                when (message) {
                    is NotifyState.ShowToast -> {
                        showSnackBar(scope, snackBarHostState, message.message)
                    }

                    is NotifyState.Navigate -> {
                    }

                    NotifyState.LaunchActivity -> navController.popBackStack()
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
                AnimatedContent(
                    modifier = Modifier.align(Alignment.TopCenter),
                    targetState = isEditEnabled.value, transitionSpec = {
                        fadeIn(tween(300)) + slideInVertically { it } togetherWith fadeOut(
                            tween(
                                300
                            )
                        ) + slideOutVertically { -it }
                    }, label = "HeaderTransition"
                ) { editable ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (editable) {
                                if (equipment == null) "Create Equipment" else "Edit Equipment"
                            } else "Equipment Details",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        )
                        Text(
                            text = if (editable) {
                                if (equipment == null) "Fill in details to add equipment" else "Modify this equipment’s details"
                            } else "Viewing in read-only mode",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            AppImagePicker(
                imageUrl = equipmentState.imageUrl,
                onImageReturned = addEquipmentViewModel::onBitmapChanged,
                onErrorReturned = { error ->
                    showSnackBar(
                        scope,
                        snackBarHostState,
                        if (error is NotifyState.ShowToast) error.message else "OK",
                        actionLabel = "Open Settings"
                    ) { openAppSettings(context) }
                },
                isEditEnabled = isEditEnabled.value
            )

            Spacer(Modifier.height(20.dp))

            AnimatedSectionCard("Equipment Info", Icons.Default.Person, true) {
                AppTextField(
                    value = equipmentState.name,
                    onValueChange = addEquipmentViewModel::onNameChanged,
                    label = "Equipment Name",
                    enabled = isEditEnabled.value
                )

                Spacer(Modifier.height(12.dp))

                AppTextField(
                    value = equipmentState.description,
                    onValueChange = addEquipmentViewModel::onDescriptionChanged,
                    label = "Address",
                    minLines = 3,
                    enabled = isEditEnabled.value
                )
            }

            Button(
                onClick = { scope.launch { addEquipmentViewModel.addEquipmentToFirebase() } },
                enabled = isEditEnabled.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .animateContentSize(),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Text(
                    if (equipment == null) "Create Equipment" else "Update Equipment",
                    fontSize = LocalDimens.current.textMedium.sp
                )
            }
        }

        if (addEquipmentState is ApiResult.Loading) {
            BubbleProgressBar(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center)
            )
        }
    }
}
