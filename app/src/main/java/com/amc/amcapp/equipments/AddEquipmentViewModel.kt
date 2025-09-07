package com.amc.amcapp.equipments

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.amc.amcapp.Complaint
import com.amc.amcapp.Equipment
import com.amc.amcapp.EquipmentType
import com.amc.amcapp.IComplaintRepository
import com.amc.amcapp.model.User
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.ui.ApiResult
import com.amc.amcapp.util.ImageUtils
import com.google.firebase.Firebase
import com.google.firebase.firestore.Exclude
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
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
    @Exclude val addedComplaints: MutableList<Complaint> = mutableListOf()
)

data class ComplaintUiState(
    val complaint: Complaint, val isSelected: Boolean = false
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
    private val equipmentsRepository: IEquipmentsRepository,
    private val complaintRepository: IComplaintRepository
) : ViewModel() {

    private val _addEquipmentState: MutableStateFlow<ApiResult<Equipment>> =
        MutableStateFlow(ApiResult.Empty)
    val addEquipmentState = _addEquipmentState.asStateFlow()

    private var _equipmentState = MutableStateFlow(AddEquipmentState())
    val equipmentState = _equipmentState.asStateFlow()

    // Safer SharedFlow (avoids dropped events)
    var notifyState = MutableSharedFlow<NotifyState>(replay = 0, extraBufferCapacity = 1)

    val errorMessage = MutableStateFlow("")

    val allComplaints = MutableStateFlow(
        complaintRepository.allComplaints.map {
            ComplaintUiState(complaint = it, isSelected = false)
        })

    /**
     * Uploads image bytes to Firebase Storage and returns a download URL.
     */
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
    suspend fun addEquipmentToFirebase() {
        _equipmentState.value.bitmap?.let {
            _addEquipmentState.value = ApiResult.Loading
            val equipment = _equipmentState.value.toEquipment()
            val bytes = ImageUtils.bitmapToByteArray(it)

            val imageUrl = uploadBytesToFirebase(bytes)
            if (imageUrl.isNotBlank()) {
                val finalEquipment = equipment.copy(imageUrl = imageUrl)

                equipmentsRepository.addEquipment(finalEquipment).collect { result ->
                    when (result) {
                        is ApiResult.Success -> {
                            notifyState.emit(NotifyState.ShowToast("Equipment added successfully!"))
                            _addEquipmentState.value = result
                            notifyState.emit(NotifyState.LaunchActivity)
                        }

                        is ApiResult.Error -> {
                            notifyState.emit(NotifyState.ShowToast(result.message))
                            _addEquipmentState.value = result
                        }

                        else -> Unit
                    }
                }
            } else {
                notifyState.emit(NotifyState.ShowToast("Image uploading failed!"))
            }
        } ?: run {
            errorMessage.value = "Please upload image"
        }
    }

    fun validate(isEditMode: Boolean = false, value: AddEquipmentState): String? {
        return when {
            !isEditMode && value.bitmap == null -> "Please select an image"
            isEditMode && value.imageUrl.isEmpty() && value.bitmap == null -> "Please select an image in Edit mode"
            value.name.isEmpty() || value.name.length < 6 -> "Name should not be empty and should be of atleast 6 characters"
            value.equipmentType == null -> "Please select the Equipment type"
            value.description.isEmpty() || value.description.length < 20 -> "Description should not be empty and should be of atleast 20 characters"
            value.addedComplaints.isEmpty() -> "Please add some complaints"
            else -> null
        }
    }

    // ---------- State Updaters ---------- //

    fun onNameChanged(name: String) {
        _equipmentState.value = _equipmentState.value.copy(name = name)
    }

    fun onDescriptionChanged(description: String) {
        _equipmentState.value = _equipmentState.value.copy(description = description)
    }

    fun onImageUrlChanged(imageUrl: String) {
        _equipmentState.value = _equipmentState.value.copy(imageUrl = imageUrl)
    }

    fun onEquipmentTypeChanged(equipmentType: EquipmentType) {
        _equipmentState.value = _equipmentState.value.copy(equipmentType = equipmentType)
    }

    fun onBitmapChanged(bitmap: Bitmap?) {
        _equipmentState.value = _equipmentState.value.copy(bitmap = bitmap)
    }

    /**
     * Manage complaints dynamically
     */
    fun addComplaint(complaint: Complaint) {
        val updated = _equipmentState.value.addedComplaints.toMutableList()
        if (!updated.contains(complaint)) updated.add(complaint)
        _equipmentState.value = _equipmentState.value.copy(addedComplaints = updated)
    }

    fun removeComplaint(complaint: Complaint) {
        val updated = _equipmentState.value.addedComplaints.toMutableList()
        updated.remove(complaint)
        _equipmentState.value = _equipmentState.value.copy(addedComplaints = updated)
    }

    fun toggleComplaintSelection(complaint: Complaint) {
        allComplaints.value = allComplaints.value.map {
            if (it.complaint.id == complaint.id) {
                val newSelected = !it.isSelected

                // sync with addedComplaints
                if (newSelected) addComplaint(it.complaint)
                else removeComplaint(it.complaint)

                it.copy(isSelected = newSelected)
            } else {
                it
            }
        }
    }

    /**
     * Pre-fills state when editing an existing equipment.
     */
    fun preFillDetails(equipment: Equipment) {
        _equipmentState.value = AddEquipmentState(
            id = equipment.id,
            gymId = equipment.gymId,
            name = equipment.name,
            description = equipment.description,
            equipmentType = equipment.equipmentType,
            imageUrl = equipment.imageUrl,
            addedComplaints = equipment.addedComplaints.toMutableList()
        )

        // also reflect in allComplaints
        allComplaints.value = allComplaints.value.map {
            it.copy(isSelected = equipment.addedComplaints.contains(it.complaint))
        }
    }

    fun updateUser(user: User) {
        _equipmentState.value = _equipmentState.value.copy(
            gymId = user.firebaseId
        )
    }

    override fun onCleared() {
        super.onCleared()
    }
}
