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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.amc.amcapp.model.User
import com.amc.amcapp.model.UserType
import com.amc.amcapp.ui.AuthResult
import com.amc.amcapp.ui.EmailField
import com.amc.amcapp.ui.PasswordField
import com.amc.amcapp.ui.theme.Dimens
import com.amc.amcapp.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(navController: NavController, authViewModel: AuthViewModel = koinViewModel()) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    val loginResult by authViewModel.authState.collectAsState()
    var selectedRole by remember { mutableStateOf(UserType.GYM_OWNER) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (loginResult is AuthResult.Loading) {
                CircularProgressIndicator()
            }
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
                text = username,
                onValueChange = { username = it },
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
                        authViewModel.createUser(user)
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
    }
}