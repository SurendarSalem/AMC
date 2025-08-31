package com.amc.amcapp.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import com.amc.amcapp.data.IUserRepository
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.model.User
import com.amc.amcapp.model.UserType
import com.amc.amcapp.ui.ApiResult
import com.amc.amcapp.ui.AuthResult
import com.amc.amcapp.util.ImageUtils
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID


data class UserDetailsState(
    val phoneNumber: String = "",
    val address: String = "",
    val gymName: String = "",
    val bitmap: Bitmap? = null,
    val imageUrl: String = "",
    val isLoading:Boolean = false
)


class UserDetailsViewModel(
    private val userRepository: IUserRepository
) : ViewModel() {

    private var _userDetailsState = MutableStateFlow(UserDetailsState())
    val userDetailsState = _userDetailsState.asStateFlow()
    var notifyState = MutableSharedFlow<NotifyState>()

    suspend fun updateUserDetails(user: User): User? {
        val user = userRepository.addUserToFirebase(user)
        if (user != null) {
            notifyState.emit(NotifyState.ShowToast("User added successfully"))
        } else {
            notifyState.emit(NotifyState.ShowToast("Unable to add the User"))
        }
        return user
    }

    fun isValidUser(username: String, password: String): Boolean {
        return username.isNotEmpty() && username.length > 4 && password.isNotEmpty() && password.length >= 8
    }

    fun onPhoneNumberChanged(name: String) {
        _userDetailsState.value = _userDetailsState.value.copy(phoneNumber = name)
    }

    fun onAddressChanged(email: String) {
        _userDetailsState.value = _userDetailsState.value.copy(address = email)
    }

    fun onGymNameChanged(password: String) {
        _userDetailsState.value = _userDetailsState.value.copy(gymName = password)
    }

    fun onImageUrlChanged(userType: String) {
        _userDetailsState.value = _userDetailsState.value.copy(imageUrl = userType)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("Surendar", "AuthViewModel cleared")
    }

    fun onBitmapChanged(bitmap: Bitmap?) {
        _userDetailsState.value = _userDetailsState.value.copy(bitmap = bitmap)
    }
}