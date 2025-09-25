package com.amc.amcapp.ui.screens.amc

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.amc.amcapp.model.AMC
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.model.RecordItem
import com.amc.amcapp.model.Status
import com.amc.amcapp.model.User
import com.amc.amcapp.model.UserType
import com.amc.amcapp.ui.ApiResult
import com.amc.amcapp.ui.AppProgressBar
import com.amc.amcapp.ui.ListDest
import com.amc.amcapp.ui.RecordUiItem
import com.amc.amcapp.ui.screens.ListTypeKey
import com.amc.amcapp.ui.showSnackBar
import com.amc.amcapp.ui.theme.LocalDimens
import com.amc.amcapp.util.Constants
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAmcScreen(
    amc: AMC? = null,
    isForEdit: Boolean = false,
    navController: NavController,
    user: User?,
    addAmcViewModel: AddAmcViewModel = koinViewModel()
) {
    var selectedDate by rememberSaveable { mutableStateOf<Long?>(null) }
    var selectedTime by rememberSaveable { mutableStateOf("") }
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle ?: return
    val amcState = addAmcViewModel.amcState.collectAsState()
    val addAmcState = addAmcViewModel.addAmcState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val recordItems = addAmcViewModel.recordItems.collectAsState()
    val currentUser = addAmcViewModel.getCurrentUser()

    LaunchedEffect(amc?.id) {
        if (amc != null) {
            addAmcViewModel.preFillDetails(amc)
            selectedDate = amc.createdDate
            selectedTime = amc.createdTime
        }
    }

    // Get technician from ListScreen result
    LaunchedEffect(Unit) {
        if (isForEdit) {
        } else {
            addAmcViewModel.onGymNameChanged(user?.name ?: "")
        }

        if (currentUser?.userType == UserType.TECHNICIAN) {
            if (amc?.status == Status.PENDING) {
                addAmcViewModel.getEquipments(amc.gymId)
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
                    showSnackBar(scope, snackBarHostState, notifyState.message)
                }

                is NotifyState.LaunchActivity -> {
                    navController.popBackStack()
                }

                is NotifyState.Navigate -> {}
            }
        }
    }


    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .verticalScroll(scrollState)
    ) {

        Column(
            modifier = Modifier
                .padding(LocalDimens.current.spacingMedium.dp)
        ) {
            // --- Customer Info ---
            SectionCard(title = "Customer") {
                Text(amcState.value.gymName, style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Technician Selection ---
            SectionCard(
                title = "Select Technician", onClick = {
                    savedStateHandle["listTypeKey"] = ListTypeKey.USERS
                    savedStateHandle["filterType"] = UserType.TECHNICIAN
                    navController.navigate(ListDest.ListScreen.route)
                }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = amcState.value.assignedName.ifEmpty { "Technician" },
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = "Navigate to technician selection"
                    )
                }
            }


            Spacer(modifier = Modifier.height(16.dp))


            // --- AMC Date ---
            AmcDatePicker(
                selectedDate = selectedDate, onDateSelected = {
                    selectedDate = it
                    addAmcViewModel.onCreatedDateChange(it)
                })

            Spacer(modifier = Modifier.height(16.dp))

            // --- AMC Time ---
            AmcTimePicker(
                selectedTime = selectedTime, onTimeSelected = {
                    selectedTime = it
                    addAmcViewModel.onTimeChange(it)
                })

            Spacer(modifier = Modifier.height(24.dp))

            if (currentUser?.userType == UserType.TECHNICIAN) {
                RecordsPagerDynamicFlex(recordItems.value)
            }


            Button(
                onClick = {
                    val error = addAmcViewModel.validate(amcState.value)
                    if (error == null) {
                        scope.launch {
                            addAmcViewModel.addAmcToFirebase()
                        }
                    } else {
                        showSnackBar(scope, snackBarHostState, error)
                    }
                },
                enabled = amcState.value.assignedName.isNotEmpty() && selectedDate != null && selectedTime.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    if (isForEdit) "Reschedule AMC" else "Schedule AMC",
                    fontSize = LocalDimens.current.textLarge.sp
                )
            }
            SnackbarHost(hostState = snackBarHostState)
        }

        if (addAmcState.value is ApiResult.Loading) {
            AppProgressBar(this)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmcDatePicker(
    selectedDate: Long?, onDateSelected: (Long) -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val state = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate, selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis()
            }
        })

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .clickable { showDialog = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = selectedDate?.let {
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it)
            } ?: "Select AMC Date",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.EditCalendar,
                contentDescription = "Date selection",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    if (showDialog) {
        DatePickerDialog(onDismissRequest = { showDialog = false }, confirmButton = {
            TextButton(onClick = {
                showDialog = false
                state.selectedDateMillis?.let { onDateSelected(it) }
            }) { Text("OK") }
        }, dismissButton = {
            TextButton(onClick = { showDialog = false }) { Text("Cancel") }
        }) {
            DatePicker(state = state)
        }
    }
}

@Composable
fun AmcTimePicker(
    selectedTime: String, onTimeSelected: (String) -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .clickable {
                    val calendar = Calendar.getInstance()
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = calendar.get(Calendar.MINUTE)
                    TimePickerDialog(
                        context, { _, selectedHour, selectedMinute ->
                            calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                            calendar.set(Calendar.MINUTE, selectedMinute)
                            val formatted = SimpleDateFormat(
                                "hh:mm a", Locale.getDefault()
                            ).format(calendar.time)
                            onTimeSelected(formatted)
                        }, hour, minute, false
                    ).show()
                }, verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedTime.ifEmpty { "Select AMC Time" },
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = "Time selection",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SectionCard(
    title: String, onClick: (() -> Unit)? = null, content: @Composable () -> Unit
) {
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
fun RecordsPagerDynamicFlex(recordsState: List<RecordItem>, modifier: Modifier = Modifier) {

    val pagerState = rememberPagerState(
        initialPage = 0, initialPageOffsetFraction = 0f, pageCount = { recordsState.size })
    Column {
        HorizontalPager(
            state = pagerState, contentPadding = PaddingValues(horizontal = 16.dp)
        ) { pageIndex ->
            val recordItem = recordsState[pageIndex]
            RecordUiItem(pageIndex, recordItem)
        }
        SimpleHorizontalPagerIndicator(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            pagerState = pagerState,
            activeColor = MaterialTheme.colorScheme.primary,
            inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)

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
    Row(
        horizontalArrangement = Arrangement.spacedBy(indicatorSpacing), modifier = modifier
    ) {
        repeat(pagerState.pageCount) { index ->
            val color = if (pagerState.currentPage == index) activeColor else inactiveColor
            Box(
                modifier = Modifier
                    .size(indicatorSize)
                    .background(color = color, shape = CircleShape)
            )
        }
    }
}
