package com.amc.amcapp.equipments

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.amc.amcapp.Equipment
import com.amc.amcapp.Gym
import com.amc.amcapp.Location
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.ui.ApiResult
import com.google.firebase.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.util.UUID

class AddEquipmentViewModel(private val equipmentsRepository: EquipmentsRepository) : ViewModel() {
    private val _addGymState: MutableStateFlow<ApiResult<Equipment>> =
        MutableStateFlow(ApiResult.Loading)
    val addGymState = _addGymState.asStateFlow()

    private val _gymState = MutableStateFlow(Gym())
    val gymState = _gymState.asStateFlow()

    var showToast = MutableSharedFlow<NotifyState>()

    suspend fun addGym(equipment: Equipment) {
        withContext(Dispatchers.Main) {
            _addGymState.value = ApiResult.Loading
        }
        equipmentsRepository.addEquipment(equipment).collect { result ->
            showToast.emit(NotifyState.ShowToast("Added Gym Successfully!"))
            _addGymState.value = result
        }
    }

    fun isValidUser(username: String, password: String): Boolean {
        return username.isNotEmpty() && username.length > 4 && password.isNotEmpty() && password.length >= 8
    }

    fun onNameChange(name: String) {
        _gymState.update { it.copy(name = name) }
    }

    fun onAddressChange(address: String) {
        _gymState.update { it.copy(address = address) }
    }

    fun onPhoneNumberChange(phoneNumber: String) {
        _gymState.update { it.copy(phoneNumber = phoneNumber) }
    }

    fun onEmailChange(email: String) {
        _gymState.update { it.copy(email = email) }
    }

    fun onDescriptionChange(description: String) {
        _gymState.update { it.copy(description = description) }
    }

    fun resetState() {
        _gymState.value = Gym()
        _addGymState.value = ApiResult.Loading
    }

    fun createGym(): Gym {
        return Gym(
            id = "",
            ownerId = "ownerId",
            ownerName = "Owner Name",
            name = _gymState.value.name,
            address = _gymState.value.address,
            location = Location(0.0, 0.0),
            phoneNumber = _gymState.value.phoneNumber,
            email = _gymState.value.email,
            imageUrl = _gymState.value.imageUrl, // Optional image URL
            description = _gymState.value.description,
            services = emptyList(),
            equipments = emptyList()
        )
    }

}