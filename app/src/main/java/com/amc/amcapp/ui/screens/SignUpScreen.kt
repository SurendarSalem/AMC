package com.amc.amcapp.ui.screens

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.amc.amcapp.R
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.model.User
import com.amc.amcapp.model.UserType
import com.amc.amcapp.ui.AuthResult
import com.amc.amcapp.ui.EmailField
import com.amc.amcapp.ui.PasswordField
import com.amc.amcapp.ui.showSnackBar
import com.amc.amcapp.ui.theme.Dimens
import com.amc.amcapp.ui.theme.Red
import com.amc.amcapp.ui.ui.CurvedBackground
import com.amc.amcapp.util.BubbleProgressBar
import com.amc.amcapp.viewmodel.SignUpViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController, signUpViewModel: SignUpViewModel = koinViewModel()) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    val signUpResult by signUpViewModel.authState.collectAsState()
    var selectedRole by remember { mutableStateOf(UserType.GYM_OWNER) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            signUpViewModel.notifyState.collect { message ->
                if (message is NotifyState.ShowToast) {
                    showSnackBar(this, snackBarHostState, message.message)
                } else if (message is NotifyState.Navigate) {
                    navController.popBackStack()
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CurvedBackground(
            color = Red, modifier = Modifier
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
                    width = 1.dp, color = Color(0xFF6650a4), // Purple border
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
                text = "Let's get started!",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Create an account in AMC to explore all features",
                modifier = Modifier.padding(bottom = 16.dp),
                color = Color.Gray
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
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
                text = username,
                onValueChange = { username = it },
            )
            Spacer(modifier = Modifier.height(8.dp))
            PasswordField(
                text = password,
                onValueChange = { password = it },
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
                            Modifier.clickable { selectedRole = role },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedRole == role),
                                onClick = { selectedRole = role })
                            Text(text = role.label, modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        val user = User(
                            email = username,
                            password = password,
                            name = name,
                            firebaseId = "",
                            userType = selectedRole
                        )
                        signUpViewModel.createUser(user)
                    }
                }, modifier = Modifier.wrapContentWidth()
            ) {
                Text(
                    "Register",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    fontSize = Dimens.MediumText
                )
            }
        }

        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )

        if (signUpResult is AuthResult.Loading) {
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