package com.amc.amcapp.ui.screens.customer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.amc.amcapp.model.User
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp)
    ) {
        val usersListState by userListViewModel.amcListState.collectAsState()
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
                        items(users) { item ->
                            UserItem(user = item, onClick = {
                            })
                            Spacer(modifier = Modifier.padding(4.dp))
                        }

                    }
                }
            }
        }

        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = {
                navController.navigate(UserDest.AddUser.route)
            }, shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}