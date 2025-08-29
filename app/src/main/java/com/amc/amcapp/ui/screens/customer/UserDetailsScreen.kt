package com.amc.amcapp.ui.screens.customer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.model.UserType
import com.amc.amcapp.ui.ApiResult
import com.amc.amcapp.ui.AuthResult
import com.amc.amcapp.ui.EmailField
import com.amc.amcapp.ui.PasswordField
import com.amc.amcapp.ui.showSnackBar
import com.amc.amcapp.ui.theme.LocalDimens
import com.amc.amcapp.util.AppImagePicker
import com.amc.amcapp.util.BubbleProgressBar
import com.amc.amcapp.util.openAppSettings
import com.amc.amcapp.viewmodel.AddUserViewModel
import com.google.android.gms.common.api.Api
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailsScreen(
    navController: NavController, addUserViewModel: AddUserViewModel = koinViewModel()
) {

    val signUpResult by addUserViewModel.addUserUiState.collectAsState()
    val userState by addUserViewModel.addUserState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val errorMessage by remember { mutableStateOf("") }
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            addUserViewModel.notifyState.collect { message ->
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
                text = "Welcome Admin!",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Add a new user to your business!",
                modifier = Modifier.padding(bottom = 16.dp),
                color = Color.Gray
            )

            AppImagePicker(onImageReturned = { bitmap ->
                addUserViewModel.onBitmapChanged(bitmap)
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
            OutlinedTextField(
                value = userState.name,
                onValueChange = addUserViewModel::onNameChanged,
                label = { Text("Name") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "Name Icon",
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            EmailField(
                text = userState.email,
                onValueChange = addUserViewModel::onEmailChanged,
            )
            Spacer(modifier = Modifier.height(8.dp))
            PasswordField(
                text = userState.password, onValueChange = addUserViewModel::onPasswordChanged
            )
            Spacer(modifier = Modifier.height(16.dp))
            PasswordField(
                label = "Confirm Password",
                text = userState.confirmPassword,
                onValueChange = addUserViewModel::onConfirmPassword
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Radio group for selecting role
            Text(
                "I am a:", modifier = Modifier.align(
                    Alignment.Start
                ), style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 18.sp, fontWeight = FontWeight.Bold
                )
            )
            Column(
                modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.SpaceBetween
            ) {
                UserType.entries.forEach { role ->
                    if (role != UserType.ADMIN) {
                        Row(
                            Modifier.clickable { addUserViewModel.onRoleChanged(role) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (userState.userType == role),
                                onClick = { addUserViewModel.onRoleChanged(role) })
                            Text(text = role.label, modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = "", onValueChange = {})

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        addUserViewModel.createUser()
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

        if (signUpResult is ApiResult.Loading) {
            BubbleProgressBar(
                count = 3,
                dotSize = 8.dp,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center),
                animationDurationMs = 300
            )
        }
    }
}