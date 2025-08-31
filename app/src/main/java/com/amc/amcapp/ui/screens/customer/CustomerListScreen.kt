package com.amc.amcapp.ui.screens.customer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.amc.amcapp.model.User
import com.amc.amcapp.model.UserType
import com.amc.amcapp.ui.ApiResult
import com.amc.amcapp.ui.AppError
import com.amc.amcapp.ui.AppLoadingBar
import com.amc.amcapp.ui.UserDest
import com.amc.amcapp.ui.screens.amc.UserItem
import com.amc.amcapp.ui.screens.amc.UserListViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomerListScreen(
    navController: NavHostController, userListViewModel: UserListViewModel = koinViewModel()
) {

    Column {
        val selectedUserType by userListViewModel._filterUserType.collectAsState()
        val userTypes = UserType.entries.filter { it != UserType.ADMIN }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(userTypes) { userType ->
                OutlinedButton(
                    onClick = { userListViewModel.setFilter(userType) },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedUserType == userType)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surface,
                        contentColor = if (selectedUserType == userType)
                            Color.White
                        else MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(
                        1.dp,
                        if (selectedUserType == userType) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = userType.label,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(16.dp)
        ) {
            val usersListState by userListViewModel.filteredUsers.collectAsState(initial = ApiResult.Loading)
            when (usersListState) {
                is ApiResult.Loading -> {
                    AppLoadingBar(this@Box)
                }

                is ApiResult.Error -> {
                    AppError(errorMessage = (usersListState as ApiResult.Error).message)
                }

                is ApiResult.Success -> {
                    val users = (usersListState as ApiResult.Success<List<User>>).data
                    if (users.isEmpty()) {
                        AppError(errorMessage = "No Users found.")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(users) { user ->
                                UserItem(user = user, onClick = {
                                    navController.currentBackStackEntry?.savedStateHandle?.apply {
                                            set("user", user)
                                        }
                                    navController.navigate(UserDest.AddUser.route)
                                })
                                Spacer(modifier = Modifier.padding(4.dp))
                            }

                        }
                    }
                }

                ApiResult.Empty -> {}
            }

            FloatingActionButton(
                modifier = Modifier.align(Alignment.BottomEnd), onClick = {
                    navController.currentBackStackEntry?.savedStateHandle?.apply {
                            set("user", null)
                        }
                    navController.navigate(UserDest.AddUser.route)
                }, shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }
}