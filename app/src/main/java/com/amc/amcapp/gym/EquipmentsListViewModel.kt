package com.amc.amcapp.gym

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amc.amcapp.Equipment
import com.amc.amcapp.Gym
import com.amc.amcapp.Location
import com.amc.amcapp.equipments.EquipmentsRepository
import com.amc.amcapp.equipments.IEquipmentsRepository
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.ui.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EquipmentsListViewModel(private val equipmentsRepository: IEquipmentsRepository) :
    ViewModel() {
    private val _equipmentsListState: MutableStateFlow<ApiResult<List<Equipment>>> =
        MutableStateFlow(ApiResult.Loading)
    val equipmentsListState = _equipmentsListState.asStateFlow()
    var showToast = MutableSharedFlow<NotifyState>()
    var userId: String = ""


     suspend fun fetchEquipments(userId: String) {
        equipmentsRepository.getEquipments(userId).collect { result ->
            _equipmentsListState.value = result
        }
    }


    fun resetState() {
        _equipmentsListState.value = ApiResult.Loading
    }

    fun preFillUserId(userId: String) {
        this.userId = userId
    }

}