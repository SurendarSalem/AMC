package com.amc.amcapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amc.amcapp.data.IAmcPackageRepository
import com.amc.amcapp.model.AmcPackage
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.ui.ApiResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddAmcPackageViewModel(val amcPackageRepository: IAmcPackageRepository) : ViewModel() {

    var _amcPackagesList = MutableStateFlow<ApiResult<AmcPackage>>(ApiResult.Empty)
    val amcPackagesList = _amcPackagesList.asStateFlow()

    val notify = MutableSharedFlow<NotifyState>()

    fun addAmcPackage(amcPackage: AmcPackage) {
        viewModelScope.launch {
            amcPackageRepository.addOrUpdateAmc(amcPackage).collect { result ->
                _amcPackagesList.value = result
                if (result is ApiResult.Success) {
                    notify.emit(NotifyState.ShowToast("Package added successfully"))
                    delay(300)
                    notify.emit(NotifyState.GoBack)
                } else if (result is ApiResult.Error) {
                    notify.emit(NotifyState.ShowToast(result.message))
                }
            }
        }
    }

    fun getErrorMessage(amcPackage: AmcPackage): String {
        if (amcPackage.name.isEmpty()) {
            return "Package name is required"
        }
        if (amcPackage.description.isEmpty()) {
            return "Package description is required"
        }
        if (amcPackage.price == 0.0) {
            return "Package price is required and should be > 0"
        }
        if (amcPackage.duration == 0) {
            return "Package duration is required and should be > 0"
        }
        return ""
    }
}