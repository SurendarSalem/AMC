package com.amc.amcapp.gym

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amc.amcapp.Equipment
import com.amc.amcapp.data.IUserRepository
import com.amc.amcapp.equipments.IEquipmentsRepository
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.model.User
import com.amc.amcapp.ui.ApiResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EquipmentsListViewModel(
    private val user: User,
    private val equipmentsRepository: IEquipmentsRepository,
    private val userRepository: IUserRepository
) : ViewModel() {

    // Equipments list state
    private val _equipmentsListState =
        MutableStateFlow<ApiResult<List<Equipment>>>(ApiResult.Empty)
    val equipmentsListState = _equipmentsListState.asStateFlow()

    // Update equipment state
    private val _updateEquipmentState =
        MutableStateFlow<ApiResult<Boolean>>(ApiResult.Empty)
    val updateEquipmentState = _updateEquipmentState.asStateFlow()

    // Notifications (for toast/snackbar)
    private val _notifyState = MutableSharedFlow<NotifyState>()
    val notifyState = _notifyState.asSharedFlow()

    init {
        loadEquipments()
    }

    fun loadEquipments() {
        viewModelScope.launch {
            equipmentsRepository.getEquipmentsByIds(user.equipments).collect { result ->
                _equipmentsListState.value = result
            }
        }
    }

    fun onEquipmentsAdded(equipments: List<Equipment>) {
        _equipmentsListState.value = ApiResult.Success(equipments)
    }

    fun updateEquipments(user: User) {
        viewModelScope.launch {
            _updateEquipmentState.value = ApiResult.Loading
            userRepository.updateUser(user).collect { isSuccess ->
                if (isSuccess) {
                    _updateEquipmentState.value = ApiResult.Success(true)
                    delay(300)
                    _notifyState.emit(NotifyState.ShowToast("Equipments updated successfully"))
                } else {
                    _updateEquipmentState.value =
                        ApiResult.Error("Failed to update equipments")
                    delay(300)
                    _notifyState.emit(NotifyState.ShowToast("Failed to update equipments"))
                }
            }
        }
    }
}
