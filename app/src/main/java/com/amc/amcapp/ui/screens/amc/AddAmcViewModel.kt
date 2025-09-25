package com.amc.amcapp.ui.screens.amc

import androidx.lifecycle.ViewModel
import com.amc.amcapp.Equipment
import com.amc.amcapp.data.IAmcRepository
import com.amc.amcapp.data.IUserRepository
import com.amc.amcapp.data.UserRepository
import com.amc.amcapp.equipments.IEquipmentsRepository
import com.amc.amcapp.model.AMC
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.model.RecordItem
import com.amc.amcapp.model.User
import com.amc.amcapp.ui.ApiResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddAmcViewModel(
    val amcRepository: IAmcRepository,
    val equipmentsRepository: IEquipmentsRepository,
    val userRepository: IUserRepository
) : ViewModel() {
    private val _addAmcState: MutableStateFlow<ApiResult<AMC>> = MutableStateFlow(ApiResult.Empty)
    val addAmcState = _addAmcState.asStateFlow()

    private val _amcState: MutableStateFlow<AMC> = MutableStateFlow(AMC())

    var notifyState = MutableSharedFlow<NotifyState>()
    val amcState = _amcState.asStateFlow()

    private var _equipmentsState = MutableStateFlow<List<Equipment>>(emptyList())

    val equipmentsState = _equipmentsState.asStateFlow()

    var _recordItems = MutableStateFlow<List<RecordItem>>(emptyList())
    val recordItems = _recordItems.asStateFlow()

    fun onCreatedDateChange(date: Long) {
        _amcState.value = _amcState.value.copy(createdDate = date)
    }

    fun onTimeChange(time: String) {
        _amcState.value = _amcState.value.copy(createdTime = time)
    }

    fun onAssignedChange(id: String, name: String, image: String) {
        _amcState.value =
            _amcState.value.copy(assignedId = id, assignedName = name, assigneeImage = image)
    }

    fun onGymNameChanged(name: String) {
        _amcState.value = _amcState.value.copy(gymName = name)
    }

    fun validate(value: AMC): String? {
        return when {
            value.gymName.isEmpty() -> "Gym Owner name should not be empty"
            value.assignedName.isEmpty() -> "Please select a Technician"
            value.createdDate == 0L -> "Please select a valid Date"
            value.createdTime.isEmpty() -> "Please select a valid Time"
            else -> null
        }
    }

    suspend fun addAmcToFirebase() {
        _addAmcState.value = ApiResult.Loading
        amcRepository.addAmc(amcState.value).collect { result ->
            when (result) {
                is ApiResult.Success -> {
                    _addAmcState.value = result
                    delay(300)
                    notifyState.emit(NotifyState.ShowToast("AMC scheduled successfully!"))
                    delay(300)
                    notifyState.emit(NotifyState.LaunchActivity)
                }

                is ApiResult.Error -> {
                    _addAmcState.value = result
                    delay(300)
                    notifyState.emit(NotifyState.ShowToast(result.message))
                }

                else -> Unit
            }
        }
    }

    fun preFillDetails(amc: AMC) {
        _amcState.value = amc
    }

    suspend fun getEquipments(gymId: String = "") {
        equipmentsRepository.getEquipments(gymId).collect { result ->
            if (result is ApiResult.Success) {
                _equipmentsState.value = result.data
                _equipmentsState.value.forEach { equipment ->
                    _recordItems.value = recordItems.value + RecordItem(
                        equipmentId = equipment.id,
                        equipmentName = equipment.name,
                        beforeImageUrl = "",
                        afterImageUrl = "",
                        allocatesSpares = equipment.spares,
                        requireSpares = emptyList()
                    )
                }
            }
        }
    }

    fun getCurrentUser(): User? {
        if (userRepository is UserRepository) {
            return userRepository.currentUser.value
        }
        return null
    }


}
