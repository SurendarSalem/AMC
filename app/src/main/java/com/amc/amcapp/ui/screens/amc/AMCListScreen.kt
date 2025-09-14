package com.amc.amcapp.ui.screens.amc

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.amc.amcapp.model.AMC
import com.amc.amcapp.ui.ApiResult
import com.amc.amcapp.ui.AppError
import com.amc.amcapp.ui.AppLoadingBar
import com.amc.amcapp.ui.AMCItem
import com.amc.amcapp.ui.UserDest
import com.amc.amcapp.viewmodel.AMCListViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AMCListScreen(
    navController: NavHostController, amcListViewModel: AMCListViewModel = koinViewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp)
    ) {
        val amcListState by amcListViewModel.amcListState.collectAsState()
        when (amcListState) {
            is ApiResult.Loading -> {
                AppLoadingBar(this@Box)
            }

            is ApiResult.Error -> {
                AppError(errorMessage = (amcListState as ApiResult.Error).message)
            }

            is ApiResult.Success -> {
                val amcs = (amcListState as ApiResult.Success<List<AMC>>).data
                if (amcs.isEmpty()) {
                    AppError(errorMessage = "No AMC found.\n Please add some AMCs.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(amcs) { amc ->

                            AMCItem(item = amc, onClick = {
                                navController.currentBackStackEntry?.savedStateHandle?.apply {
                                    set("amc", amc)
                                }
                                navController.navigate(UserDest.AddAMC.route)
                            })
                            Spacer(modifier = Modifier.padding(4.dp))
                        }

                    }
                }
            }

            ApiResult.Empty -> TODO()
        }
    }
}