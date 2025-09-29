package com.amc.amcapp.ui.screens.amc

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.amc.amcapp.model.*
import com.amc.amcapp.ui.*
import com.amc.amcapp.ui.screens.ListTypeKey
import com.amc.amcapp.ui.theme.LocalDimens
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAmcScreen(
    amc: AMC? = null,
    gymOwner: User? = null,
    isForEdit: Boolean = false,
    navController: NavController,
    addAmcViewModel: AddAmcViewModel = koinViewModel()
) {
    val scrollState = rememberScrollState()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val amcState by addAmcViewModel.amcState.collectAsState()
    val addAmcState by addAmcViewModel.addAmcState.collectAsState()
    val recordUiItems by addAmcViewModel.recordUiItems.collectAsState(emptyList())
    val currentUser = addAmcViewModel.getCurrentUser()
    val equipmentState by addAmcViewModel.equipmentsState.collectAsState()

    var selectedDate by rememberSaveable { mutableStateOf(amc?.createdDate) }
    var selectedTime by rememberSaveable { mutableStateOf(amc?.createdTime ?: "") }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle ?: return

    // Observe technician selection
    LaunchedEffect(Unit) {
        amc?.let {
            addAmcViewModel.preFillDetails(it)
        } ?: run {
            gymOwner?.let {
                addAmcViewModel.onGymNameChanged(it.name)
            }
        }
        savedStateHandle.getLiveData<User>("selectedTechnician")
            .observe(navController.currentBackStackEntry!!) { technician ->
                addAmcViewModel.onAssignedChange(
                    technician.firebaseId, technician.name, technician.imageUrl
                )
            }

        addAmcViewModel.notifyState.collect { notifyState ->
            when (notifyState) {
                is NotifyState.ShowToast -> {
                    showSnackBar(
                        scope, snackBarHostState, notifyState.message
                    )
                    delay(100)
                    scrollState.animateScrollTo(scrollState.maxValue)
                }

                is NotifyState.LaunchActivity -> navController.popBackStack()
                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(LocalDimens.current.spacingMedium.dp)
    ) {
        Column {

            SectionCard(title = "Customer") {
                Text(amcState.gymName, style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(Modifier.height(LocalDimens.current.spacingMedium.dp))

            // Technician Selection
            SectionCard(title = "Select Technician", onClick = {
                if (currentUser?.userType == UserType.ADMIN) {
                    savedStateHandle["listTypeKey"] = ListTypeKey.USERS
                    savedStateHandle["filterType"] = UserType.TECHNICIAN
                    navController.navigate(ListDest.ListScreen.route)
                }
            }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = amcState.assignedName.ifEmpty { "Technician" },
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = "Navigate to technician selection"
                    )
                }
            }

            Spacer(Modifier.height(LocalDimens.current.spacingMedium.dp))

            // Date & Time Pickers
            AmcDatePicker(selectedDate) {
                selectedDate = it
                addAmcViewModel.onCreatedDateChange(it)
            }

            Spacer(Modifier.height(LocalDimens.current.spacingMedium.dp))

            AmcTimePicker(selectedTime) {
                selectedTime = it
                addAmcViewModel.onTimeChange(it)
            }

            Spacer(Modifier.height(LocalDimens.current.spacingMedium.dp))

            // Technician Records
            if (recordUiItems.isNotEmpty() && (currentUser?.userType == UserType.TECHNICIAN ||
                        (currentUser?.userType == UserType.ADMIN && (amcState.status == Status.PROGRESS || amcState.status == Status.APPROVED)))
            ) {
                TechnicianRecordsHeader()
                Spacer(Modifier.height(LocalDimens.current.spacingMedium.dp))
                RecordPagerContainer(
                    recordUiItems, addAmcViewModel, addAmcState is ApiResult.Loading
                )
            }


            Spacer(Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = {
                    val error = addAmcViewModel.validate(amcState)
                    if (error == null) {
                        scope.launch {
                            if (isForEdit) {
                                amc?.let { addAmcViewModel.onUpdateAmcClicked() }
                            } else {
                                addAmcViewModel.addAmcToFirebase(gymOwner?.equipmentList)
                            }
                        }
                    } else {
                        showSnackBar(scope, snackBarHostState, error)
                    }
                },
                enabled = addAmcState !is ApiResult.Loading && amcState.assignedName.isNotEmpty() && selectedDate != null && selectedTime.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    if (isForEdit) "Reschedule AMC" else "Schedule AMC",
                    fontSize = LocalDimens.current.textLarge.sp
                )
            }
            Spacer(Modifier.height(LocalDimens.current.spacingMedium.dp))
            if (currentUser?.userType == UserType.ADMIN && (amc?.status == Status.PROGRESS
                        || amc?.status == Status.APPROVED)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TrailingIconsButton(
                        modifier = Modifier.weight(0.8f),
                        onClick = {
                            scope.launch {
                                addAmcViewModel.approveReject(Status.APPROVED)
                            }
                        },
                        trailingIcons = listOf(Icons.Default.GppGood),
                        text = "Approve",
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                        textColor = Color.Black
                    )
                    Spacer(Modifier.width(LocalDimens.current.spacingLarge.dp))
                    TrailingIconsButton(
                        modifier = Modifier.weight(0.8f),
                        onClick = {
                            scope.launch {
                                addAmcViewModel.approveReject(Status.APPROVED)
                            }
                        },
                        trailingIcons = listOf(Icons.Default.Cancel),
                        text = "Reject",
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        textColor = Color.White
                    )
                }
            }

            SnackbarHost(hostState = snackBarHostState)
        }
        if (addAmcState is ApiResult.Loading) {
            AppProgressBar(this)
        }
    }
}

@Composable
fun TechnicianRecordsHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary
                    )
                ), shape = RoundedCornerShape(8.dp)
            )
            .padding(
                vertical = LocalDimens.current.spacingMedium.dp,
                horizontal = LocalDimens.current.spacingLarge.dp
            ), contentAlignment = Alignment.Center
    ) {
        Text("Equipment's Records", color = Color.White)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmcDatePicker(selectedDate: Long?, onDateSelected: (Long) -> Unit) {
    val context = LocalContext.current
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = selectedDate?.let {
                SimpleDateFormat(
                    "dd MMM yyyy", Locale.getDefault()
                ).format(it)
            } ?: "Select AMC Date",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f))
            Icon(
                Icons.Default.EditCalendar,
                contentDescription = "Date selection",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    if (showDialog) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate, selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long) =
                    utcTimeMillis >= System.currentTimeMillis()
            })

        DatePickerDialog(onDismissRequest = { showDialog = false }, confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { onDateSelected(it) }
                showDialog = false
            }) { Text("OK") }
        }, dismissButton = {
            TextButton(onClick = { showDialog = false }) { Text("Cancel") }
        }) {
            DatePicker(state = state)
        }
    }
}

@Composable
fun AmcTimePicker(selectedTime: String, onTimeSelected: (String) -> Unit) {
    val context = LocalContext.current
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = selectedTime.ifEmpty { "Select AMC Time" },
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Default.AccessTime,
                contentDescription = "Time selection",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    if (showDialog) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context, { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                val formatted =
                    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
                onTimeSelected(formatted)
                showDialog = false
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
        ).show()
    }
}

@Composable
fun SectionCard(title: String, onClick: (() -> Unit)? = null, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun RecordPagerContainer(
    recordsState: List<RecordUiItem>, amcViewModel: AddAmcViewModel, enabled: Boolean
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { recordsState.size })

    Column {
        HorizontalPager(
            state = pagerState,
            key = { index -> recordsState[index].recordItem.equipmentId }) { pageIndex ->
            val recordUiItem = recordsState[pageIndex]
            RecordUiItem(enabled, pageIndex, recordUiItem) { index, recordItem ->
                amcViewModel.onRecordUpdated(index, recordItem)
            }
        }

        SimpleHorizontalPagerIndicator(
            pagerState = pagerState,
            activeColor = MaterialTheme.colorScheme.primary,
            inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )
    }
}

@Composable
fun SimpleHorizontalPagerIndicator(
    pagerState: PagerState,
    activeColor: Color,
    inactiveColor: Color,
    modifier: Modifier = Modifier,
    indicatorSize: Dp = 8.dp,
    indicatorSpacing: Dp = 8.dp
) {
    Row(horizontalArrangement = Arrangement.spacedBy(indicatorSpacing), modifier = modifier) {
        repeat(pagerState.pageCount) { index ->
            val color = if (pagerState.currentPage == index) activeColor else inactiveColor
            Box(
                modifier = Modifier
                    .size(indicatorSize)
                    .background(color, CircleShape)
            )
        }
    }
}
