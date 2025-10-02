package com.amc.amcapp.ui.screens.customer

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.amc.amcapp.model.AmcPackage
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.model.User
import com.amc.amcapp.model.UserType
import com.amc.amcapp.ui.AnimatedSectionCard
import com.amc.amcapp.ui.ApiResult
import com.amc.amcapp.ui.AppProgressBar
import com.amc.amcapp.ui.AppTextField
import com.amc.amcapp.ui.EmailField
import com.amc.amcapp.ui.PasswordField
import com.amc.amcapp.ui.PhoneNumberField
import com.amc.amcapp.ui.RoleSelectionSection
import com.amc.amcapp.ui.UserDest
import com.amc.amcapp.ui.showSnackBar
import com.amc.amcapp.ui.theme.LocalDimens
import com.amc.amcapp.util.AppImagePicker
import com.amc.amcapp.util.Constants
import com.amc.amcapp.util.openAppSettings
import com.amc.amcapp.viewmodel.AddUserViewModel
import com.amc.amcapp.viewmodel.toUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AddUserScreen(
    navController: NavController,
    user: User? = null,
    addUserViewModel: AddUserViewModel = koinViewModel(),
    onMenuUpdated: (Boolean, ImageVector, () -> Unit) -> Unit
) {
    val addUserResult by addUserViewModel.addUserUiState.collectAsState()
    val userState by addUserViewModel.addUserState.collectAsState()
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val isEditEnabled = remember { mutableStateOf(user == null) }
    val context = LocalContext.current
    val errorMessage by addUserViewModel.errorMessage.collectAsState()
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle ?: return

    // Setup menu toggle
    fun updateMenu() {
        onMenuUpdated(
            true, if (isEditEnabled.value) Icons.Default.Cancel else Icons.Default.Edit
        ) { isEditEnabled.value = !isEditEnabled.value }
    }



    LaunchedEffect(user?.firebaseId) {
        if (user != null) {
            updateMenu()
            addUserViewModel.preFillUserState(user)
            /*if (user.userType == UserType.GYM_OWNER) {
                addUserViewModel.getEquipments(user)
            }*/
        } else {
            onMenuUpdated(false, Icons.Default.Edit) {}
        }
    }

    // Collect notifications
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            addUserViewModel.notifyState.collectLatest { message ->
                when (message) {
                    is NotifyState.ShowToast -> {
                        delay(300)
                        showSnackBar(scope, snackBarHostState, message.message)
                    }

                    is NotifyState.Navigate -> {
                        navController.navigate(message.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        }
                    }

                    NotifyState.LaunchActivity -> {
                        delay(300)
                        navController.popBackStack()
                    }

                    NotifyState.GoBack -> {

                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            savedStateHandle.getLiveData<AmcPackage>("selectedAmcPackage")
                .observe(navController.currentBackStackEntry!!) { amcPackage ->
                    addUserViewModel.onAmcPackageChanged(amcPackage)
                }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(LocalDimens.current.spacingMedium.dp)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                if (user != null && user.userType == UserType.GYM_OWNER && user.equipmentList.isNotEmpty()) {
                    Text(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .clickable {
                                navController.currentBackStackEntry?.savedStateHandle?.apply {
                                    set(Constants.GYM_OWNER, user)
                                }
                                navController.navigate(UserDest.AddAMC.route)
                            },
                        text = "Add AMC",
                        fontSize = LocalDimens.current.textMedium.sp
                    )
                }
                AnimatedContent(
                    modifier = Modifier.align(Alignment.TopCenter),
                    targetState = isEditEnabled.value,
                    transitionSpec = {
                        fadeIn(tween(300)) + slideInVertically { it } togetherWith fadeOut(
                            tween(
                                300
                            )
                        ) + slideOutVertically { -it }
                    },
                    label = "HeaderTransition"
                ) { editable ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (editable) {
                                if (user == null) "Create User" else "Edit User"
                            } else "User Details",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        )
                        Text(
                            text = if (editable) {
                                if (user == null) "Fill in details to register" else "Modify this user’s details"
                            } else "Viewing in read-only mode",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (user != null && user.userType == UserType.GYM_OWNER) {
                    Text(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .clickable {
                                navController.currentBackStackEntry?.savedStateHandle?.apply {
                                    set("user", user)
                                }
                                navController.navigate(UserDest.Equipments.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        text = "Equipments (${user.equipmentList.size})",
                        fontSize = LocalDimens.current.textMedium.sp
                    )
                }

            }

            Spacer(Modifier.height(20.dp))

            AppImagePicker(
                imageUrl = userState.imageUrl,
                bitmap = userState.bitmap,
                onImageReturned = addUserViewModel::onBitmapChanged,
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

            // 🔹 Profile Info Section
            AnimatedSectionCard("Profile Info", Icons.Default.Person, true) {
                AppTextField(
                    value = userState.name,
                    onValueChange = addUserViewModel::onNameChanged,
                    label = "Full Name",
                    enabled = isEditEnabled.value
                )
                Spacer(Modifier.height(12.dp))
                RoleSelectionSection(
                    addUserViewModel.getCurrentUser(),
                    userState,
                    addUserViewModel,
                    isEditEnabled.value,
                    savedStateHandle,
                    navController
                )
            }


            Spacer(Modifier.height(16.dp))

            // 🔹 Credentials Section
            AnimatedSectionCard("Credentials", Icons.Default.Lock) {
                EmailField(
                    text = userState.email,
                    onValueChange = addUserViewModel::onEmailChanged,
                    enabled = isEditEnabled.value
                )
                Spacer(Modifier.height(12.dp))
                PasswordField(
                    text = userState.password,
                    onValueChange = addUserViewModel::onPasswordChanged,
                    enabled = isEditEnabled.value
                )
                if (user == null) {
                    Spacer(Modifier.height(12.dp))
                    PasswordField(
                        label = "Confirm Password",
                        text = userState.confirmPassword,
                        onValueChange = addUserViewModel::onConfirmPassword,
                        enabled = isEditEnabled.value
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // 🔹 Contact Section
            AnimatedSectionCard("Contact Info", Icons.Default.Phone) {
                PhoneNumberField(
                    value = userState.phoneNumber,
                    onValueChange = addUserViewModel::onPhoneNumberChanged,
                    label = "Phone Number",
                    enabled = isEditEnabled.value
                )
                if (userState.userType == UserType.GYM_OWNER) {
                    Spacer(Modifier.height(12.dp))
                    AppTextField(
                        value = userState.gymName,
                        onValueChange = addUserViewModel::onGymNameChanged,
                        label = "Gym Name",
                        enabled = isEditEnabled.value
                    )
                }
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = userState.address,
                    onValueChange = addUserViewModel::onAddressChanged,
                    label = "Address",
                    minLines = 3,
                    enabled = isEditEnabled.value
                )
            }

            Spacer(Modifier.height(24.dp))

            // 🔹 Action button
            Button(
                onClick = {
                    scope.launch {
                        if (user == null) {
                            addUserViewModel.createUser()
                        } else {
                            val user = userState.toUser()
                            addUserViewModel.updateUserToFirebase(user)
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
                    if (user == null) "Create Account" else "Update User",
                    fontSize = LocalDimens.current.textMedium.sp
                )
            }
        }

        // 🔹 Loader
        if (addUserResult is ApiResult.Loading) {
            AppProgressBar(this)
        }

        SnackbarHost(
            hostState = snackBarHostState, modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
