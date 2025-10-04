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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.navigation.NavController
import com.amc.amcapp.Complaint
import com.amc.amcapp.Equipment
import com.amc.amcapp.EquipmentType
import com.amc.amcapp.equipments.AddEquipmentState
import com.amc.amcapp.equipments.AddEquipmentViewModel
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.model.Spare
import com.amc.amcapp.model.User
import com.amc.amcapp.ui.*
import com.amc.amcapp.ui.screens.ListTypeKey
import com.amc.amcapp.ui.theme.LocalDimens
import com.amc.amcapp.util.AppImagePicker
import com.amc.amcapp.util.BubbleProgressBar
import com.amc.amcapp.util.Constants
import com.amc.amcapp.util.openAppSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class,
    SavedStateHandleSaveableApi::class
)
@Composable
fun AddEquipmentScreen(
    navController: NavController,
    equipment: Equipment? = null,
    initialEditEnabled: Boolean = false,
    addEquipmentViewModel: AddEquipmentViewModel = koinViewModel(),
    onMenuUpdated: (Boolean, ImageVector, () -> Unit) -> Unit
) {
    val addEquipmentState by addEquipmentViewModel.addEquipmentState.collectAsState()
    val equipmentState by addEquipmentViewModel.equipmentState.collectAsState()
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle ?: return

    var isEditEnabled by rememberSaveable { mutableStateOf(true) }


    LaunchedEffect(equipment?.id) {
        if (equipment != null) {
            addEquipmentViewModel.preFillDetails(equipment)
        } else {
            onMenuUpdated(false, Icons.Default.Edit) {}
        }
    }

    LaunchedEffect(Unit) {
        combine(
            savedStateHandle.getStateFlow("selectedSpares", emptyList<Spare>()),
            savedStateHandle.getStateFlow("selectedComplaints", emptyList<Complaint>())
        ) { spares, complaints ->
            spares to complaints
        }.collect { (spares, complaints) ->
            if (spares.isNotEmpty()) {
                addEquipmentViewModel.onSparesChanged(spares)
                savedStateHandle["selectedSpares"] = emptyList<Spare>()
            }
            if (complaints.isNotEmpty()) {
                addEquipmentViewModel.onComplaintsChanged(complaints)
                savedStateHandle["selectedComplaints"] = emptyList<Complaint>()
            }
        }
    }


    // Collect notify state
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            addEquipmentViewModel.notifyState.collectLatest { message ->
                when (message) {
                    is NotifyState.ShowToast -> showSnackBar(
                        scope, snackBarHostState, message.message
                    )

                    is NotifyState.LaunchActivity -> navController.popBackStack()
                    else -> Unit
                }
            }
        }
    }

    // --- UI ---
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EquipmentHeader(isEditEnabled, equipment)

            SpacerLarge()

            AppImagePicker(
                imageUrl = equipmentState.imageUrl,
                bitmap = equipmentState.bitmap,
                onImageReturned = addEquipmentViewModel::onBitmapChanged,
                onErrorReturned = { error ->
                    scope.launch {
                        val result = snackBarHostState.showSnackbar(
                            message = if (error is NotifyState.ShowToast) error.message else "Error",
                            actionLabel = "Open Settings"
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            openAppSettings(context)
                        }
                    }
                },
                isEditEnabled = isEditEnabled
            )

            SpacerLarge()

            AnimatedSectionCard("Equipment Info", Icons.Default.Person, true) {
                AppTextField(
                    value = equipmentState.name,
                    onValueChange = addEquipmentViewModel::onNameChanged,
                    label = "Equipment Name",
                    enabled = isEditEnabled
                )
                SpacerMedium()

                EquipmentTypeSelection(equipmentState, addEquipmentViewModel, isEditEnabled)

                AppTextField(
                    value = equipmentState.description,
                    onValueChange = addEquipmentViewModel::onDescriptionChanged,
                    label = "Description",
                    minLines = 3,
                    enabled = isEditEnabled
                )
            }

            SparesSection(equipmentState.spares, navController, savedStateHandle)

            ComplaintsSection(equipmentState.complaints, navController, savedStateHandle)

            SpacerMedium()

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
                enabled = isEditEnabled,
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

            SpacerMedium()
            SnackbarHost(hostState = snackBarHostState)
            SpacerMedium()
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
private fun EquipmentHeader(isEditable: Boolean, equipment: Equipment?) {
    AnimatedContent(
        targetState = isEditable, transitionSpec = {
            fadeIn(tween(300)) + slideInVertically { it / 2 } togetherWith fadeOut(tween(300)) + slideOutVertically { -it / 2 }
        }, label = "HeaderTransition"
    ) { editable ->
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = when {
                    editable && equipment == null -> "Create Equipment"
                    editable -> "Edit Equipment"
                    else -> "Equipment Details"
                },
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = when {
                    editable && equipment == null -> "Fill in details to add equipment"
                    editable -> "Modify this equipment’s details"
                    else -> "Viewing in read-only mode"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SparesSection(
    selectedSpares: List<Spare>,
    navController: NavController,
    savedStateHandle: SavedStateHandle
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary)
            .padding(
                vertical = LocalDimens.current.spacingMedium.dp,
                horizontal = LocalDimens.current.spacingMedium.dp
            )
            .clickable {
                savedStateHandle[Constants.LIST_TYPE_KEY] = ListTypeKey.SPARES
                savedStateHandle[Constants.EXISTING_SPARES] = selectedSpares
                navController.navigate(ListDest.ListScreen.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            }, verticalAlignment = Alignment.CenterVertically
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
    SpacerMedium()
    Column(modifier = Modifier.fillMaxWidth()) {
        selectedSpares.forEachIndexed { index, spare ->
            Text(
                "${index + 1}. ${spare.name}",
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(LocalDimens.current.spacingSmall.dp)
            )
        }
    }
}

@Composable
private fun ComplaintsSection(
    selectedComplaints: List<Complaint>,
    navController: NavController,
    savedStateHandle: SavedStateHandle
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary)
            .padding(
                vertical = LocalDimens.current.spacingMedium.dp,
                horizontal = LocalDimens.current.spacingMedium.dp
            )
            .clickable {
                savedStateHandle[Constants.LIST_TYPE_KEY] = ListTypeKey.COMPLAINTS
                savedStateHandle[Constants.SELECTED_COMPLAINTS] = selectedComplaints
                navController.navigate(ListDest.ListScreen.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            }, verticalAlignment = Alignment.CenterVertically
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
    SpacerMedium()
    Column(modifier = Modifier.fillMaxWidth()) {
        selectedComplaints.forEachIndexed { index, complaint ->
            Text(
                "${index + 1}. ${complaint.name}",
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(LocalDimens.current.spacingSmall.dp)
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
                    }, verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (addEquipmentState.equipmentType == equipmentType),
                    onClick = { viewModel.onEquipmentTypeChanged(equipmentType) },
                    enabled = isEditEnabled
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = equipmentType.label, style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun SpacerMedium() = Spacer(Modifier.height(LocalDimens.current.spacingMedium.dp))

@Composable
fun SpacerLarge() = Spacer(Modifier.height(20.dp))
