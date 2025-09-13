package com.amc.amcapp.ui.screens.service

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.amc.amcapp.Complaint
import com.amc.amcapp.Equipment
import com.amc.amcapp.EquipmentType
import com.amc.amcapp.ImageUrls
import com.amc.amcapp.Service
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.ui.ApiResult
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.sql.Date
import java.util.UUID

data class AddServiceState(
    val id: String = "",
    val gymId: String = "",
    val createdDate: Date = Date(System.currentTimeMillis()),
    val updatedDate: Date = Date(System.currentTimeMillis()),
    val name: String = "",
    val description: String = "",
    val equipments: List<String> = emptyList(),
    val imageUrls: List<ImageUrls> = emptyList(),
    val total: Double = 0.0,
)

fun AddServiceState.toService(): Service {
    return Service(
        id = id,
        gymId = gymId,
        createdDate = createdDate,
        updatedDate = updatedDate,
        name = name,
        description = description,
        equipments = equipments,
        imageUrls = imageUrls,
        total = total
    )
}

class AddServiceViewModel(
    private val serviceRepository: IServiceRepository
) : ViewModel() {

    private val _addServiceState: MutableStateFlow<ApiResult<Service>> =
        MutableStateFlow(ApiResult.Empty)
    val addServiceState = _addServiceState.asStateFlow()

    private var _serviceState = MutableStateFlow(AddServiceState())
    val serviceState = _serviceState.asStateFlow()

    var notifyState = MutableSharedFlow<NotifyState>(replay = 0, extraBufferCapacity = 1)

    val errorMessage = MutableStateFlow("")

    suspend fun uploadBytesToFirebase(bytes: ByteArray, pathPrefix: String = "images"): String =
        withContext(Dispatchers.IO) {
            try {
                val storage = Firebase.storage
                val fileName = "$pathPrefix/${UUID.randomUUID()}.jpg"
                val ref = storage.reference.child(fileName)

                ref.putBytes(bytes).await()
                ref.downloadUrl.await().toString()
            } catch (e: Exception) {
                ""
            }
        }

    /**
     * Adds new equipment along with its image to Firebase.
     */
    suspend fun addServiceToFirebase() {
        _addServiceState.value = ApiResult.Loading
        val service = _serviceState.value.toService()

        serviceRepository.addService(service).collect { result ->
            when (result) {
                is ApiResult.Success -> {
                    notifyState.emit(NotifyState.ShowToast("Equipment added successfully!"))
                    _addServiceState.value = result
                    notifyState.emit(NotifyState.LaunchActivity)
                }

                is ApiResult.Error -> {
                    notifyState.emit(NotifyState.ShowToast(result.message))
                    _addServiceState.value = result
                }

                else -> Unit
            }
        }
    }

    fun validate(isEditMode: Boolean = false, value: AddServiceState): String? {
        return when {
            value.name.isEmpty() || value.name.length < 6 -> "Name should not be empty and should be of atleast 6 characters"
            value.description.isEmpty() || value.description.length < 20 -> "Description should not be empty and should be of atleast 20 characters"
            else -> null
        }
    }


    fun onNameChanged(name: String) {
        _serviceState.value = _serviceState.value.copy(name = name)
    }

    fun onDescriptionChanged(description: String) {
        _serviceState.value = _serviceState.value.copy(description = description)
    }


    override fun onCleared() {
        super.onCleared()
    }
}
