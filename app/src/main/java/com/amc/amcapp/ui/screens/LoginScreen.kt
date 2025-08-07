package com.amc.amcapp.ui.screens

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.amc.amcapp.R
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.ui.AuthResult
import com.amc.amcapp.ui.HomeActivity
import com.amc.amcapp.ui.Screen
import com.amc.amcapp.ui.ui.CurvedBackground
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
    context as? Activity
    val lifecycleOwner = LocalLifecycleOwner.current
    var isButtonClicked by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            authViewModel.showToast.collect { message ->
                if (message is NotifyState.ShowToast) {
                    Toast.makeText(context, message.message, Toast.LENGTH_SHORT).show()
                } else if (message is NotifyState.Navigate) {
                    val intent = Intent(context, HomeActivity::class.java)
                    context.startActivity(intent)
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        CurvedBackground(
            color = Color(0xFF6650a4), modifier = Modifier
                .fillMaxWidth()
                .align(
                    Alignment.TopCenter
                )
                .height(120.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.gym),
            contentDescription = "A description of my image",
            modifier = Modifier
                .size(90.dp)
                .offset(y = 65.dp)
                .align(Alignment.TopCenter)
                .clip(CircleShape)
                .border(
                    width = 1.dp,
                    color = Color(0xFF6650a4), // Purple border
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (isButtonClicked && !authViewModel.isValidUser(username, password)) {
                Text(
                    "Please enter valid email id and password",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(
                            Alignment.Start
                        )
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
                    }, fontSize = 14.sp
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
                        "Login", modifier = Modifier.padding(horizontal = 18.dp), fontSize = 14.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Don't have an account? Sign up",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .wrapContentWidth()
                    .clickable {
                        if (loginResult !is AuthResult.Loading) {
                            navController.navigate(Screen.SignUpScreen.route)
                        }
                    })
        }

        if (loginResult is AuthResult.Loading) {
            CircularProgressIndicator()
        }
    }
}