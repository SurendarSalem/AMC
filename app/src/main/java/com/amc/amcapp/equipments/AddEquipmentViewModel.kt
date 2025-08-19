package com.amc.amcapp.equipments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amc.amcapp.Complaint
import com.amc.amcapp.ComplaintRepository
import com.amc.amcapp.Equipment
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.ui.ApiResult
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class AddEquipmentViewModel(
    private val equipmentsRepository: EquipmentsRepository,
    private val complaintRepository: ComplaintRepository
) : ViewModel() {
    private val _addEquipmentState: MutableStateFlow<ApiResult<Equipment>> =
        MutableStateFlow(ApiResult.Loading)
    val addEquipmentState = _addEquipmentState.asStateFlow()

    private val _equipmentState = MutableStateFlow(Equipment())
    val equipmentState = _equipmentState.asStateFlow()

    private val _newComplaint = MutableStateFlow(Complaint())
    val newComplaint = _newComplaint.asStateFlow()

    private val _image: MutableStateFlow<String?> = MutableStateFlow(null)
    val image = _image.asStateFlow()

    var notifyState = MutableSharedFlow<NotifyState>()

    private val _complaintsState: MutableStateFlow<List<Complaint>> = MutableStateFlow(emptyList())
    val complaintsState = _complaintsState.asStateFlow()

    suspend fun addEquipment(equipment: Equipment) {
        withContext(Dispatchers.Main) {
            _addEquipmentState.value = ApiResult.Loading
        }
        equipmentsRepository.addEquipment(equipment).collect { result ->
            notifyState.emit(NotifyState.ShowToast("Added Gym Successfully!"))
            _addEquipmentState.value = result
        }
    }

    fun isValidUser(username: String, password: String): Boolean {
        return username.isNotEmpty() && username.length > 4 && password.isNotEmpty() && password.length >= 8
    }

    fun onNameChange(name: String) {
        _equipmentState.update { it.copy(name = name) }
    }

    fun getAllComplaints() {
        viewModelScope.launch {
            try {
                val complaints = complaintRepository.getAllComplaints()
                _complaintsState.value = complaints
            } catch (e: Exception) {
                notifyState.emit(NotifyState.ShowToast("Failed to load complaints"))
            }
        }
    }


    fun onDescriptionChange(description: String) {
        _equipmentState.update { it.copy(description = description) }
    }

    suspend fun uploadBytesToFirebase(bytes: ByteArray, pathPrefix: String = "images"): String =
        withContext(Dispatchers.IO) {
            val storage = Firebase.storage
            val fileName = "$pathPrefix/${UUID.randomUUID()}.jpg"
            val ref = storage.reference.child(fileName)
            val uploadTask = ref.putBytes(bytes)
            val taskSnapshot =
                uploadTask.await()
            val downloadUrl = ref.downloadUrl.await()
            downloadUrl.toString()
        }

    fun onComplaintAdded() {
        _complaintsState.value = _complaintsState.value + _newComplaint.value
    }

    fun onComplaintRemoved(complaint: Complaint) {
        _complaintsState.value = _complaintsState.value - complaint
    }

    fun onComplaintUpdated(name: String) {
        _newComplaint.update { it.copy(name = name) }
    }


}