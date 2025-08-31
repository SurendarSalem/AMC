package com.amc.amcapp.ui.screens.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.amc.amcapp.model.GymOwner
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.model.User
import com.amc.amcapp.ui.ApiResult
import com.amc.amcapp.ui.AppProgressBar
import com.amc.amcapp.ui.AppTextField
import com.amc.amcapp.ui.PhoneNumberField
import com.amc.amcapp.ui.showSnackBar
import com.amc.amcapp.ui.theme.LocalDimens
import com.amc.amcapp.util.AppImagePicker
import com.amc.amcapp.util.BubbleProgressBar
import com.amc.amcapp.util.openAppSettings
import com.amc.amcapp.viewmodel.UserDetailsViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailsScreen(
    navController: NavController, userDetailsViewModel: UserDetailsViewModel = koinViewModel()
) {

    val userDetails by userDetailsViewModel.userDetailsState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val errorMessage by remember { mutableStateOf("") }
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    lateinit var user: User

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            userDetailsViewModel.notifyState.collect { message ->
                if (message is NotifyState.ShowToast) {
                    showSnackBar(
                        scope = this,
                        snackBarHostState = snackBarHostState,
                        message = message.message
                    )
                } else if (message is NotifyState.Navigate) {
                    navController.navigate(message.route)
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Update User Details",
                modifier = Modifier.padding(bottom = 16.dp),
                color = Color.Gray
            )

            AppImagePicker(onImageReturned = { bitmap ->
                userDetailsViewModel.onBitmapChanged(bitmap)
            }, onErrorReturned = { error ->
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    message = if (error is NotifyState.ShowToast) error.message else "OK",
                    actionLabel = "Open Settings",
                    onActionClicked = {
                        openAppSettings(context)
                    })
            })
            Spacer(modifier = Modifier.height(16.dp))
            PhoneNumberField(
                value = userDetails.phoneNumber,
                onValueChange = userDetailsViewModel::onPhoneNumberChanged,
                label = "Phone  Number"
            )
            if (user is GymOwner) {
                AppTextField(
                    value = userDetails.gymName,
                    onValueChange = userDetailsViewModel::onPhoneNumberChanged,
                    label = "Gym Name"
                )
            }
            AppTextField(
                value = userDetails.address,
                onValueChange = userDetailsViewModel::onPhoneNumberChanged,
                label = "Gym Name",
                minLines = 3
            )

            Button(
                onClick = {
                    scope.launch {
                        userDetailsViewModel.updateUserDetails(user)
                    }
                }, modifier = Modifier.wrapContentWidth()
            ) {
                Text(
                    "Add User",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    fontSize = LocalDimens.current.textMedium.sp,
                )
            }
        }

        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )

        if (userDetails.isLoading) {
            AppProgressBar(this)
        }
    }
}