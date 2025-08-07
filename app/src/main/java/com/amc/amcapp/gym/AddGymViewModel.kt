package com.amc.amcapp.gym

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.amc.amcapp.Gym
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.ui.ApiResult
import com.amc.amcapp.ui.AuthResult
import com.google.android.gms.common.api.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class AddGymViewModel(private val gymRepository: GymRepository) : ViewModel() {
    private val _addGymState: MutableStateFlow<ApiResult<Gym>> = MutableStateFlow(ApiResult.Loading)
    val addGymState = _addGymState.asStateFlow()

    private val _gymState = MutableStateFlow(Gym())
    val gymState = _gymState.asStateFlow()

    var showToast = MutableSharedFlow<NotifyState>()

    suspend fun addGym(gym: Gym) {
        withContext(Dispatchers.Main) {
            _addGymState.value = ApiResult.Loading
        }
        gymRepository.addGym(gym).collect { result ->
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
            location = com.amc.amcapp.Location(0.0, 0.0),
            phoneNumber = _gymState.value.phoneNumber,
            email = _gymState.value.email,
            imageUrl = _gymState.value.imageUrl, // Optional image URL
            description = _gymState.value.description,
            services = emptyList(),
            equipments = emptyList()
        )
    }
}