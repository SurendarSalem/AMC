package com.amc.amcapp.ui.screens.amc

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.amc.amcapp.Equipment
import com.amc.amcapp.data.IAmcRepository
import com.amc.amcapp.data.IUserRepository
import com.amc.amcapp.data.UserRepository
import com.amc.amcapp.equipments.IEquipmentsRepository
import com.amc.amcapp.model.AMC
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.model.RecordImage
import com.amc.amcapp.model.RecordItem
import com.amc.amcapp.model.RecordUiItem
import com.amc.amcapp.model.Status
import com.amc.amcapp.model.User
import com.amc.amcapp.model.toRecordItem
import com.amc.amcapp.ui.ApiResult
import com.google.firebase.firestore.util.Util
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class AddAmcViewModel(
    val amcRepository: IAmcRepository,
    val userRepository: IUserRepository
) : ViewModel() {
    private val _addAmcState: MutableStateFlow<ApiResult<AMC>> = MutableStateFlow(ApiResult.Empty)
    val addAmcState = _addAmcState.asStateFlow()

    private val _amcState: MutableStateFlow<AMC> = MutableStateFlow(AMC())

    var notifyState = MutableSharedFlow<NotifyState>()
    val amcState = _amcState.asStateFlow()

    private var _equipmentsState = MutableStateFlow<List<Equipment>>(emptyList())

    val equipmentsState = _equipmentsState.asStateFlow()

    var recordUiItems = MutableStateFlow<List<RecordUiItem>>(emptyList())

    fun onCreatedDateChange(date: Long) {
        _amcState.value = _amcState.value.copy(createdDate = date)
    }

    fun onGymNameChanged(name: String) {
        _amcState.value = _amcState.value.copy(
            gymName = name
        )
    }

    fun onTimeChange(time: String) {
        _amcState.value = _amcState.value.copy(createdTime = time)
    }

    fun onAssignedChange(id: String, name: String, image: String) {
        _amcState.value =
            _amcState.value.copy(assignedId = id, assignedName = name, assigneeImage = image)
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

    suspend fun addAmcToFirebase(equipmentList: List<Equipment>?) {
        _addAmcState.value = ApiResult.Loading
        equipmentList?.let {
            _amcState.value = amcState.value.copy(
                recordItems = it.map {
                    RecordItem(
                        equipmentId = it.id, equipmentName = it.name, addedSpares = emptyList()
                    )
                })
        }

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
        recordUiItems.value = amc.recordItems.map {
            RecordUiItem(
                recordItem = it, beforeImage = RecordImage(
                    imageUrl = it.beforeImageUrl,
                    imageUri = it.beforeImageUri,
                    shouldUseUrl = it.beforeImageUrl.isNotEmpty(),
                    shouldUseUri = it.beforeImageUri.isNotEmpty()
                ), afterImage = RecordImage(
                    imageUrl = it.afterImageUrl,
                    imageUri = it.afterImageUri,
                    shouldUseUrl = it.afterImageUrl.isNotEmpty(),
                    shouldUseUri = it.afterImageUri.isNotEmpty()
                )
            )
        }
    }

    fun getCurrentUser(): User? {
        if (userRepository is UserRepository) {
            return userRepository.currentUser.value
        }
        return null
    }

    fun onRecordUpdated(index: Int, recordItem: RecordUiItem) {
        recordUiItems.value = recordUiItems.value.toMutableList().apply {
            this[index] = recordItem
        }
    }

    suspend fun onUpdateAmcClicked() = coroutineScope {
        _addAmcState.value = ApiResult.Loading
        _amcState.value =
            _amcState.value.copy(recordItems = recordUiItems.value.map { recordUiItem ->
                recordUiItem.toRecordItem()
            }).copy(updatedAt = System.currentTimeMillis())

        val updatedRecordItems = _amcState.value.recordItems.map { recordItem ->
            async {
                if (recordItem.beforeImageUri.isNotEmpty()) {
                    val downloadUrl =
                        amcRepository.uploadImageToFirebase(recordItem.beforeImageUri.toUri())
                    if (downloadUrl.isNotEmpty()) {
                        return@async recordItem.copy(
                            beforeImageUrl = downloadUrl, beforeImageUri = ""
                        )
                    }
                }
                recordItem
            }
        }.map { it.await() }

        _amcState.value = _amcState.value.copy(
            recordItems = updatedRecordItems,
            updatedAt = System.currentTimeMillis(),
            status = Status.PENDING
        )

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

    suspend fun approveReject(approved: Status) {
        _addAmcState.value = ApiResult.Loading
        _amcState.value = _amcState.value.copy(status = approved)
        amcRepository.addAmc(amcState.value).collect { result ->
            when (result) {
                is ApiResult.Success -> {
                    _addAmcState.value = result
                    delay(300)
                    notifyState.emit(NotifyState.ShowToast("AMC scheduled successfully!"))
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
}
