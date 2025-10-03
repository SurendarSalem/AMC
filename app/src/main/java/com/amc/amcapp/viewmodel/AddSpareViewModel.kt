package com.amc.amcapp.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amc.amcapp.data.ISparesRepository
import com.amc.amcapp.model.Spare
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.model.SpareUiState
import com.amc.amcapp.model.toSpare
import com.amc.amcapp.ui.ApiResult
import com.amc.amcapp.util.FirebaseHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddSpareViewModel(
    val firebaseHelper: FirebaseHelper,
    val sparesRepository: ISparesRepository
) : ViewModel() {

    var _spareUiState = MutableStateFlow(SpareUiState())
    val spareUiState = _spareUiState.asStateFlow()

    var _addSpareState = MutableStateFlow<ApiResult<Spare>>(ApiResult.Empty)
    val addSpareState = _addSpareState.asStateFlow()

    val notify = MutableSharedFlow<NotifyState>()

    fun addSpare(spareUiState: SpareUiState) {
        viewModelScope.launch {
            _addSpareState.value = ApiResult.Loading
            spareUiState.imageUri?.let { imageUri ->
                if (spareUiState.shouldUseUri) {
                    val downloadUrl = FirebaseHelper().uploadImageToFirebase(imageUri)
                    spareUiState.imageUrl = downloadUrl
                    sparesRepository.addSpare(spareUiState.toSpare()).collect { result ->
                        _addSpareState.value = result
                        if (result is ApiResult.Success) {
                            notify.emit(NotifyState.ShowToast("Spare added successfully"))
                            delay(300)
                            notify.emit(NotifyState.GoBack)
                        } else if (result is ApiResult.Error) {
                            notify.emit(NotifyState.ShowToast(result.message))
                        }
                    }
                }
            } ?: run {
                _addSpareState.value = ApiResult.Error("Spare image is required")
                notify.emit(NotifyState.ShowToast("Spare image missing!. Please select"))
            }
        }
    }

    fun updateSpare(spareUiState: SpareUiState) {
        viewModelScope.launch {
            _addSpareState.value = ApiResult.Loading
            if (spareUiState.shouldUseUri) {
                spareUiState.imageUri?.let { imageUri ->
                    val downloadUrl = FirebaseHelper().uploadImageToFirebase(imageUri)
                    spareUiState.imageUrl = downloadUrl
                }
            }
            sparesRepository.updateSpare(spareUiState.toSpare()).collect { result ->
                _addSpareState.value = result
                if (result is ApiResult.Success) {
                    notify.emit(NotifyState.ShowToast("Spare added successfully"))
                    delay(300)
                    notify.emit(NotifyState.GoBack)
                } else if (result is ApiResult.Error) {
                    notify.emit(NotifyState.ShowToast(result.message))
                }
            }
        }
    }


    fun getErrorMessage(spare: SpareUiState): String {
        if (spare.imageUrl.isEmpty() && spare.imageUri == null) {
            return "Spare image is required"
        }
        if (spare.name.isEmpty()) {
            return "Spare name is required"
        }
        if (spare.description.isEmpty()) {
            return "Spare description is required"
        }
        if (spare.price == 0.0) {
            return "Spare price is required and should be > 0"
        }
        return ""
    }

    fun onImageUriChanged(uri: Uri) {
        _spareUiState.value = _spareUiState.value.copy(
            imageUri = uri, shouldUseUrl = false, shouldUseUri = true
        )
    }

    fun onNameChanged(name: String) {
        _spareUiState.value = _spareUiState.value.copy(name = name)
    }

    fun onDescriptionChanged(description: String) {
        _spareUiState.value = _spareUiState.value.copy(description = description)
    }

    fun onPriceChanged(price: String) {
        val priceValue = price.toDoubleOrNull() ?: 0.0
        _spareUiState.value = _spareUiState.value.copy(price = priceValue)
    }

    fun onStockQuantityChanged(quantity: String) {
        val quantityValue = quantity.toIntOrNull() ?: 0
        _spareUiState.value = _spareUiState.value.copy(stockQuantity = quantityValue)
    }

    fun preFillData(spare: Spare) {
        _spareUiState.value = SpareUiState(
            id = spare.id,
            name = spare.name,
            description = spare.description,
            price = spare.price,
            stockQuantity = spare.stockQuantity,
            imageUrl = spare.imageUrl,
            imageUri = null,
            shouldUseUrl = true,
            shouldUseUri = false
        )
    }


}