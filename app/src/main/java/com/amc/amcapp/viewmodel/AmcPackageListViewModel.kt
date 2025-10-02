package com.amc.amcapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amc.amcapp.data.IAmcPackageRepository
import com.amc.amcapp.model.AmcPackage
import com.amc.amcapp.ui.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AmcPackageListViewModel(val amcPackageRepository: IAmcPackageRepository) : ViewModel() {

    var _amcPackagesList = MutableStateFlow<ApiResult<List<AmcPackage>>>(ApiResult.Empty)

    val amcPackagesList = _amcPackagesList.asStateFlow()

    init {
        viewModelScope.launch {
            getAllAmcPackages()
        }
    }

    suspend fun getAllAmcPackages() {
        amcPackageRepository.getAllAmcPackages().collect { result ->
            _amcPackagesList.value = result
        }
    }
}