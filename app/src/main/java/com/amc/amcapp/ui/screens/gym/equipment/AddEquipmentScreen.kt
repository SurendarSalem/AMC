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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.amc.amcapp.EquipmentType
import com.amc.amcapp.equipments.AddEquipmentState
import com.amc.amcapp.equipments.AddEquipmentViewModel
import com.amc.amcapp.equipments.spares.Spare
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.model.User
import com.amc.amcapp.ui.*
import com.amc.amcapp.ui.screens.ListTypeKey
import com.amc.amcapp.ui.theme.LocalDimens
import com.amc.amcapp.util.AppImagePicker
import com.amc.amcapp.util.BubbleProgressBar
import com.amc.amcapp.util.openAppSettings
import kotlinx.coroutines.delay
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

    // SavedStateHandle collections
    val savedStateHandle = navController.currentBackStackEntry!!.savedStateHandle
    val selectedSpares by savedStateHandle
        .getStateFlow("selectedSpares", emptyList<Spare>())
        .collectAsState()
    val selectedComplaints by savedStateHandle
        .getStateFlow("selectedComplaints", emptyList<Complaint>())
        .collectAsState()

    val context = LocalContext.current

    fun updateMenu() {
        onMenuUpdated(
            true,
            if (isEditEnabled.value) Icons.Default.Cancel else Icons.Default.Edit
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

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            addEquipmentViewModel.notifyState.collectLatest { message ->
                when (message) {
                    is NotifyState.ShowToast -> {
                        showSnackBar(scope, snackBarHostState, message.message)
                    }
                    is NotifyState.LaunchActivity -> navController.popBackStack()
                    else -> {}
                }
            }
        }
    }

    // Sync selections into viewmodel
    LaunchedEffect(selectedComplaints) {
        addEquipmentViewModel.onComplaintsChanged(selectedComplaints)
    }
    LaunchedEffect(selectedSpares) {
        addEquipmentViewModel.onSparesChanged(selectedSpares)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated header
            Box(modifier = Modifier.fillMaxWidth()) {
                AnimatedContent(
                    modifier = Modifier.align(Alignment.TopCenter),
                    targetState = isEditEnabled.value,
                    transitionSpec = {
                        fadeIn(tween(300)) + slideInVertically { it / 2 } togetherWith
                                fadeOut(tween(300)) + slideOutVertically { -it / 2 }
                    },
                    label = "HeaderTransition"
                ) { editable ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (editable) {
                                if (equipment == null) "Create Equipment" else "Edit Equipment"
                            } else "Equipment Details",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = if (editable) {
                                if (equipment == null) "Fill in details to add equipment"
                                else "Modify this equipment’s details"
                            } else "Viewing in read-only mode",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Image picker
            AppImagePicker(
                imageUrl = equipmentState.imageUrl,
                bitmap = equipmentState.bitmap,
                onImageReturned = addEquipmentViewModel::onBitmapChanged,
                onErrorReturned = { error ->
                    scope.launch {
                        showSnackBar(
                            scope,
                            snackBarHostState,
                            if (error is NotifyState.ShowToast) error.message else "Error",
                            actionLabel = "Open Settings"
                        )
                    }
                    openAppSettings(context)
                },
                isEditEnabled = isEditEnabled.value
            )

            Spacer(Modifier.height(20.dp))

            // Equipment info
            AnimatedSectionCard("Equipment Info", Icons.Default.Person, true) {
                AppTextField(
                    value = equipmentState.name,
                    onValueChange = addEquipmentViewModel::onNameChanged,
                    label = "Equipment Name",
                    enabled = isEditEnabled.value
                )
                Spacer(Modifier.height(12.dp))

                EquipmentTypeSelection(
                    equipmentState, addEquipmentViewModel, isEditEnabled.value
                )
                AppTextField(
                    value = equipmentState.description,
                    onValueChange = addEquipmentViewModel::onDescriptionChanged,
                    label = "Description",
                    minLines = 3,
                    enabled = isEditEnabled.value
                )
            }

            // Spares selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.primary)
                    .padding(
                        vertical = LocalDimens.current.spacingMedium.dp,
                        horizontal = LocalDimens.current.spacingMedium.dp
                    )
                    .clickable {
                        savedStateHandle["listTypeKey"] = ListTypeKey.SPARES
                        navController.navigate(ListDest.ListScreen.route)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Spares",
                    fontSize = LocalDimens.current.textLarge.sp,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Spares Icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(LocalDimens.current.spacingMedium.dp))
            selectedSpares.forEachIndexed { index, spare ->
                Text(
                    "${index + 1}. ${spare.name}",
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(LocalDimens.current.spacingSmall.dp)
                )
            }

            // Complaints selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.primary)
                    .padding(
                        vertical = LocalDimens.current.spacingMedium.dp,
                        horizontal = LocalDimens.current.spacingMedium.dp
                    )
                    .clickable {
                        savedStateHandle["listTypeKey"] = ListTypeKey.COMPLAINTS
                        navController.navigate(ListDest.ListScreen.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Complaints",
                    fontSize = LocalDimens.current.textLarge.sp,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Complaints Icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            selectedComplaints.forEachIndexed { index, complaint ->
                Text(
                    "${index + 1}. ${complaint.name}",
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(LocalDimens.current.spacingSmall.dp)
                )
            }

            Spacer(Modifier.height(LocalDimens.current.spacingMedium.dp))

            Button(
                onClick = {
                    scope.launch {
                        val error =
                            addEquipmentViewModel.validate(equipment != null, equipmentState)
                        if (error == null) {
                            addEquipmentViewModel.addEquipmentToFirebase()
                        } else {
                            showSnackBar(scope, snackBarHostState, error)
                            delay(100)
                            scrollState.animateScrollTo(scrollState.maxValue)
                        }
                    }
                },
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

            Spacer(Modifier.height(LocalDimens.current.spacingMedium.dp))
            SnackbarHost(hostState = snackBarHostState)
            Spacer(Modifier.height(LocalDimens.current.spacingMedium.dp))
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

@Composable
fun EquipmentTypeSelection(
    addEquipmentState: AddEquipmentState,
    viewModel: AddEquipmentViewModel,
    isEditEnabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isEditEnabled) 1f else 0.6f)
    ) {
        Text(
            "Equipment Type:",
            modifier = Modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp, fontWeight = FontWeight.Bold
            )
        )
        EquipmentType.entries.forEach { equipmentType ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = isEditEnabled) {
                        viewModel.onEquipmentTypeChanged(equipmentType)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (addEquipmentState.equipmentType == equipmentType),
                    onClick = { viewModel.onEquipmentTypeChanged(equipmentType) },
                    enabled = isEditEnabled
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = equipmentType.label,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
