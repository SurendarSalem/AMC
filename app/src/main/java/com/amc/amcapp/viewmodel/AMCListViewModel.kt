package com.amc.amcapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amc.amcapp.data.IAmcRepository
import com.amc.amcapp.equipments.IEquipmentsRepository
import com.amc.amcapp.model.AMC
import com.amc.amcapp.ui.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AMCListViewModel(
    val amcRepository: IAmcRepository,
    val equipmentRepository: IEquipmentsRepository
) : ViewModel() {
    private val _amcListState: MutableStateFlow<ApiResult<List<AMC>>> =
        MutableStateFlow(ApiResult.Loading)
    val amcListState: StateFlow<ApiResult<List<AMC>>> = _amcListState.asStateFlow()

    init {
        viewModelScope.launch {
            refresh()
        }
    }

    suspend fun refresh() {
        fetchAllAMCs(amcRepository)
    }

    private suspend fun fetchAllAMCs(amcRepository: IAmcRepository) {
        try {
            _amcListState.value = ApiResult.Loading
            val amcList = amcRepository.getAllAMCs()
            amcList.forEach { amc ->
                equipmentRepository.getEquipmentsByIds(amc.equipmentIds).collect {
                    if (it is ApiResult.Success) {
                        amc.equipments = it.data
                    }
                }
            }
            _amcListState.value = ApiResult.Success(amcList)
        } catch (e: Exception) {
            _amcListState.value = ApiResult.Error(e.message ?: "Unknown error occurred")
        }
    }

}
