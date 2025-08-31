package com.amc.amcapp.ui.screens.customer

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.model.User
import com.amc.amcapp.model.UserType
import com.amc.amcapp.ui.*
import com.amc.amcapp.ui.theme.LocalDimens
import com.amc.amcapp.util.AppImagePicker
import com.amc.amcapp.util.BubbleProgressBar
import com.amc.amcapp.util.openAppSettings
import com.amc.amcapp.viewmodel.AddUserState
import com.amc.amcapp.viewmodel.AddUserViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
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
                        showSnackBar(scope, snackBarHostState, message.message)
                    }

                    is NotifyState.Navigate -> {
                        navController.navigate(message.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        }
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
                    Button(
                        modifier = Modifier.align(Alignment.TopEnd),
                        onClick = {
                            navController.currentBackStackEntry?.savedStateHandle?.apply {
                                set("user", user as User)
                            }
                            navController.navigate(UserDest.Equipments.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Equipments", style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

            }

            Spacer(Modifier.height(20.dp))

            AppImagePicker(
                imageUrl = userState.imageUrl,
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
                RoleSelectionSection(userState, addUserViewModel, isEditEnabled.value)
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
                onClick = { scope.launch { addUserViewModel.createUser() } },
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
            BubbleProgressBar(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center)
            )
        }
    }
}
