package com.amc.amcapp.ui.screens

import android.app.Activity
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.ui.AuthResult
import com.amc.amcapp.ui.EmailField
import com.amc.amcapp.ui.LandingActivity
import com.amc.amcapp.ui.PasswordField
import com.amc.amcapp.ui.Screen
import com.amc.amcapp.ui.showSnackBar
import com.amc.amcapp.ui.theme.Dimens
import com.amc.amcapp.ui.ui.CurvedBanner
import com.amc.amcapp.util.BubbleProgressBar
import com.amc.amcapp.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = koinViewModel()) {

    var username by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginResult by authViewModel.authState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    var isButtonClicked by remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }


    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            authViewModel.showToast.collect { message ->
                if (message is NotifyState.ShowToast) {
                    showSnackBar(this, snackBarHostState, message.message)
                } else if (message is NotifyState.Navigate) {
                    val intent = Intent(context, LandingActivity::class.java)
                    context.startActivity(intent)
                }
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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
            )
            Spacer(modifier = Modifier.height(16.dp))
            PasswordField(
                text = password,
                onValueChange = { password = it },
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (isButtonClicked && !authViewModel.isValidUser(username, password)) {
                Text(
                    "Please enter valid email id and password",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = Dimens.MediumText,
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
                    text = "Forgot Password?", modifier = Modifier.clickable {
                        if (loginResult !is AuthResult.Loading) {
                            navController.navigate(Screen.ForgotPassword.route)
                        }
                    }, fontSize = Dimens.MediumText
                )

                Button(
                    onClick = {
                        isButtonClicked = true
                        scope.launch {
                            if (authViewModel.isValidUser(username, password)) {
                                authViewModel.signIn(username, password)
                            }
                        }
                    },
                    modifier = Modifier.wrapContentWidth(),
                    enabled = (loginResult !is AuthResult.Loading)
                ) {
                    Text(
                        "Login",
                        modifier = Modifier.padding(horizontal = 18.dp),
                        fontSize = Dimens.MediumText
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Don't have an account? Sign up",
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontSize = Dimens.MediumText,
                modifier = Modifier
                    .wrapContentWidth()
                    .clickable {
                        if (loginResult !is AuthResult.Loading) {
                            navController.navigate(Screen.SignUpScreen.route)
                        }
                    })
        }

        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )

        if (loginResult is AuthResult.Loading) {
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
