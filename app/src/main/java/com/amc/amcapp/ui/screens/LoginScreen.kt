package com.amc.amcapp.ui.screens

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.model.UserType
import com.amc.amcapp.ui.*
import com.amc.amcapp.ui.technician.TechnicianActivity
import com.amc.amcapp.ui.theme.LocalDimens
import com.amc.amcapp.util.BubbleProgressBar
import com.amc.amcapp.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel = koinViewModel()) {

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val loginResult by loginViewModel.authState.collectAsState()
    val errorMessage by loginViewModel.errorMessage.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            loginViewModel.notifyState.collectLatest { message ->
                when (message) {
                    is NotifyState.ShowToast -> {
                        scope.launch { snackBarHostState.showSnackbar(message.message) }
                    }

                    else -> {
                    }
                }
            }
            loginViewModel.user.collectLatest { user ->
                user?.let {
                    when (it.userType) {
                        UserType.TECHNICIAN -> {
                            val intent = Intent(context, TechnicianActivity::class.java)
                            context.startActivity(intent)
                            (context as Activity).finish()
                        }
                        else -> {
                            val intent = Intent(context, LandingActivity::class.java)
                            context.startActivity(intent)
                            (context as Activity).finish()
                        }
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CurvedBanner()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Welcome back!",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Login into your existing account of AMC",
                modifier = Modifier.padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            EmailField(
                text = username,
                onValueChange = { username = it },
                enabled = loginResult !is AuthResult.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordField(
                text = password,
                onValueChange = { password = it },
                enabled = loginResult !is AuthResult.Loading
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Reactive validation error
            val isValidUser = loginViewModel.isValidUser(username, password)
            if (!isValidUser) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = LocalDimens.current.textMedium.sp,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(horizontal = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Forgot Password?",
                    modifier = Modifier.clickable {
                        if (loginResult !is AuthResult.Loading) {
                            navController.navigate(Screen.ForgotPassword.route)
                        }
                    },
                    fontSize = LocalDimens.current.textMedium.sp,
                )

                Button(
                    onClick = {
                        scope.launch {
                            if (isValidUser) {
                                loginViewModel.signIn(username, password)
                            } else {
                                // Optionally show a quick validation snackbar
                                snackBarHostState.showSnackbar("Please enter valid credentials")
                            }
                        }
                    },
                    modifier = Modifier.wrapContentWidth(),
                    enabled = loginResult !is AuthResult.Loading
                ) {
                    Text(
                        "Login",
                        modifier = Modifier.padding(horizontal = 18.dp),
                        fontSize = LocalDimens.current.textMedium.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Snackbar
        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )

        if (loginResult is AuthResult.Loading) {
            AppProgressBar(this)
        }
    }
}
