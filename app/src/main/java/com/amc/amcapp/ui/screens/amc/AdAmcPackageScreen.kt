package com.amc.amcapp.ui.screens.amc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.amc.amcapp.model.AmcPackage
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.ui.ApiResult
import com.amc.amcapp.ui.AppProgressBar
import com.amc.amcapp.ui.AppTextField
import com.amc.amcapp.ui.showSnackBar
import com.amc.amcapp.ui.theme.LocalDimens
import com.amc.amcapp.viewmodel.AddAmcPackageViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAmcPackageScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    addAmcPackageViewModel: AddAmcPackageViewModel = koinViewModel()
) {
    var amcPackage by remember { mutableStateOf(AmcPackage()) }
    var priceText by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf("") }
    val amcPackagesList by addAmcPackageViewModel.amcPackagesList.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            addAmcPackageViewModel.notify.collectLatest { state ->
                when (state) {
                    is NotifyState.ShowToast -> {
                        showSnackBar(
                            scope, snackBarHostState, state.message
                        )
                    }

                    is NotifyState.GoBack -> {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("refresh", true)   // send refresh flag
                        navController.popBackStack()
                    }

                    else -> Unit
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .padding(LocalDimens.current.spacingLarge.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(LocalDimens.current.spacingSmall.dp)
        ) {
            AppTextField(
                value = amcPackage.name,
                onValueChange = { amcPackage = amcPackage.copy(name = it) },
                label = "Package Name"
            )

            AppTextField(
                value = amcPackage.description,
                onValueChange = { amcPackage = amcPackage.copy(description = it) },
                label = "Description",
                minLines = 3
            )

            AppTextField(
                value = priceText,
                onValueChange = { priceText = it },
                label = "Price",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            AppTextField(
                value = durationText,
                onValueChange = { durationText = it },
                label = "Duration (Months)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    fontSize = LocalDimens.current.textMedium.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    scope.launch {
                        val packageToSubmit = amcPackage.copy(
                            price = priceText.toDoubleOrNull() ?: 0.0,
                            duration = durationText.toIntOrNull() ?: 0
                        )
                        errorMessage = addAmcPackageViewModel.getErrorMessage(packageToSubmit)
                        if (errorMessage.isEmpty()) {
                            addAmcPackageViewModel.addAmcPackage(packageToSubmit)
                        }
                    }
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Create Package", fontSize = LocalDimens.current.textLarge.sp
                )
            }
        }

        SnackbarHost(
            hostState = snackBarHostState, modifier = Modifier.align(Alignment.BottomCenter)
        )

        if (amcPackagesList is ApiResult.Loading) {
            AppProgressBar(this)
        }
    }


}
