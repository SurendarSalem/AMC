package com.amc.amcapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amc.amcapp.data.ISparesRepository
import com.amc.amcapp.model.Spare
import com.amc.amcapp.ui.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SparesListViewModel(val sparesRepository: ISparesRepository) : ViewModel() {

    var _spareList = MutableStateFlow<ApiResult<List<Spare>>>(ApiResult.Empty)

    val spares = _spareList.asStateFlow()

    init {
        viewModelScope.launch {
            getAllSpares()
        }
    }

    suspend fun getAllSpares() {
        sparesRepository.getAllSpares().collect { result ->
            _spareList.value = result
        }
    }
}