package com.amc.amcapp.ui.screens.spare

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.model.Spare
import com.amc.amcapp.ui.ApiResult
import com.amc.amcapp.ui.AppProgressBar
import com.amc.amcapp.ui.AppTextField
import com.amc.amcapp.ui.showSnackBar
import com.amc.amcapp.ui.theme.LocalDimens
import com.amc.amcapp.util.image.AppImagePicker
import com.amc.amcapp.viewmodel.AddSpareViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSpareScreen(
    modifier: Modifier = Modifier,
    spare: Spare? = null,
    navController: NavController,
    addSpareViewModel: AddSpareViewModel = koinViewModel()
) {
    val spareState = addSpareViewModel.spareUiState.collectAsState()
    val scope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf("") }
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackBarHostState = remember { SnackbarHostState() }
    var addSpareState = addSpareViewModel.addSpareState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        if (spare != null) {
            addSpareViewModel.preFillData(spare)
        }
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            addSpareViewModel.notify.collectLatest { state ->
                when (state) {
                    is NotifyState.ShowToast -> {
                        showSnackBar(
                            scope, snackBarHostState, state.message
                        )
                    }

                    is NotifyState.GoBack -> {
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "refresh", true
                        )   // send refresh flag
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
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(LocalDimens.current.spacingSmall.dp)
        ) {

            AppImagePicker(
                buttonEnabled = true,
                index = 0,
                modifier = Modifier.fillMaxWidth(),
                imageUrl = spareState.value.imageUrl,
                imageUri = spareState.value.imageUri.toString(),
                onImageReturned = { uri ->
                    addSpareViewModel.onImageUriChanged(uri)
                },
                onPermissionDenied = { /* Handle permission denied */ },
                shouldUseUri = spareState.value.shouldUseUri,
                shouldUseUrl = spareState.value.shouldUseUrl,
                contentScale = ContentScale.FillWidth
            )

            AppTextField(
                value = spareState.value.name,
                onValueChange = addSpareViewModel::onNameChanged,
                label = "Spare Name"
            )

            AppTextField(
                value = spareState.value.description,
                onValueChange = addSpareViewModel::onDescriptionChanged,
                label = "Spare Description"
            )

            AppTextField(
                value = spareState.value.price.toString(), keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ), onValueChange = addSpareViewModel::onPriceChanged, label = "Spare Price"
            )

            AppTextField(
                value = spareState.value.stockQuantity.toString(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                onValueChange = addSpareViewModel::onStockQuantityChanged,
                label = "Available Quantity"
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
                        errorMessage = addSpareViewModel.getErrorMessage(spareState.value)
                        if (errorMessage.isEmpty()) {
                            if (spare != null) {
                                addSpareViewModel.updateSpare(spareState.value)
                            } else {
                                addSpareViewModel.addSpare(spareState.value)
                            }

                        }
                    }
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (spare != null) "Update Spare" else "Add Spare",
                    fontSize = LocalDimens.current.textLarge.sp
                )
            }
        }

        SnackbarHost(
            hostState = snackBarHostState, modifier = Modifier.align(Alignment.BottomCenter)
        )

        if (addSpareState.value is ApiResult.Loading) {
            AppProgressBar(this)
        }
    }


}
