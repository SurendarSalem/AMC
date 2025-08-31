package com.amc.amcapp.equipments

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amc.amcapp.Complaint
import com.amc.amcapp.Equipment
import com.amc.amcapp.EquipmentType
import com.amc.amcapp.model.User
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.ui.ApiResult
import com.amc.amcapp.util.ImageUtils
import com.google.firebase.Firebase
import com.google.firebase.firestore.Exclude
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.time.delay
import kotlinx.coroutines.withContext
import java.util.UUID


data class AddEquipmentState(
    val id: String = "",
    val gymId: String = "",
    val name: String = "",
    val type: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val bitmap: Bitmap? = null,
    val equipmentType: EquipmentType? = null,
    val availableComplaints: List<Complaint> = emptyList(),
    @Exclude val addedComplaints: List<Complaint> = emptyList()
)

fun AddEquipmentState.toEquipment(): Equipment {
    return Equipment(
        id = id,
        gymId = gymId,
        name = name,
        imageUrl = imageUrl,
        description = description,
        equipmentType = equipmentType,
        addedComplaints = addedComplaints
    )
}


class AddEquipmentViewModel(
    private val equipmentsRepository: IEquipmentsRepository
) : ViewModel() {
    private val _addEquipmentState: MutableStateFlow<ApiResult<Equipment>> =
        MutableStateFlow(ApiResult.Empty)
    val addEquipmentState = _addEquipmentState.asStateFlow()
    private var _equipmentState = MutableStateFlow(AddEquipmentState())
    val equipmentState = _equipmentState.asStateFlow()
    var notifyState = MutableSharedFlow<NotifyState>()
    val errorMessage = MutableStateFlow<String>("")


    suspend fun uploadBytesToFirebase(bytes: ByteArray, pathPrefix: String = "images"): String =
        withContext(Dispatchers.IO) {
            val storage = Firebase.storage
            val fileName = "$pathPrefix/${UUID.randomUUID()}.jpg"
            val ref = storage.reference.child(fileName)

            ref.putBytes(bytes).await()
            ref.downloadUrl.await().toString()
        }

    suspend fun addEquipmentToFirebase() {
        _equipmentState.value.bitmap?.let {
            _addEquipmentState.value = ApiResult.Loading
            val equipment = _equipmentState.value.toEquipment()
            val bytes = ImageUtils.bitmapToByteArray(it)
            val imageUrl = uploadBytesToFirebase(bytes)
            if (!imageUrl.isEmpty()) {
                equipment.imageUrl = imageUrl
                equipmentsRepository.addEquipment(equipment).collect { result ->
                    if (result is ApiResult.Success) {
                        withContext(Dispatchers.Main) {
                            notifyState.emit(NotifyState.ShowToast("Equipment added successfully!"))
                            _addEquipmentState.value = result
                            delay(300)
                            notifyState.emit(NotifyState.LaunchActivity)
                        }
                    } else if (result is ApiResult.Error) {
                        withContext(Dispatchers.Main) {
                            notifyState.emit(NotifyState.ShowToast(result.message))
                            _addEquipmentState.value = result
                        }
                    }
                }
            } else {
                notifyState.emit(NotifyState.ShowToast("Image uploading failed!"))
            }
        } ?: run {
            errorMessage.value = "Please upload image"
        }
    }

    fun isValidUser(username: String, password: String): Boolean {
        return username.isNotEmpty() && username.length > 4 && password.isNotEmpty() && password.length >= 8
    }

    fun onNameChanged(name: String) {
        _equipmentState.value = _equipmentState.value.copy(name = name)
    }

    fun onDescriptionChanged(description: String) {
        _equipmentState.value = _equipmentState.value.copy(description = description)
    }

    fun onImageUrlChanged(userType: String) {
        _equipmentState.value = _equipmentState.value.copy(imageUrl = userType)
    }

    fun onEquipmentTypeChanged(equipmentType: EquipmentType) {
        _equipmentState.value = _equipmentState.value.copy(equipmentType = equipmentType)
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun onBitmapChanged(bitmap: Bitmap?) {
        _equipmentState.value = _equipmentState.value.copy(bitmap = bitmap)
    }

    fun preFillDetails(equipment: Equipment) {
        _equipmentState.value = AddEquipmentState(
            id = equipment.id,
            gymId = equipment.gymId,
            description = equipment.description,
            equipmentType = equipment.equipmentType,
            imageUrl = equipment.imageUrl,
            addedComplaints = equipment.addedComplaints
        )
    }

    fun updateUser(user: User) {
        _equipmentState.value = _equipmentState.value.copy(
            gymId = user.firebaseId
        )
    }
}