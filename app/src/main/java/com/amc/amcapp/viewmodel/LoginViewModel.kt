package com.amc.amcapp.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.amc.amcapp.AuthRepository
import com.amc.amcapp.data.IUserRepository
import com.amc.amcapp.data.UserRepository
import com.amc.amcapp.data.datastore.PreferenceHelper
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.ui.AuthResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val preferenceHelper: PreferenceHelper,
    private val userRepository: IUserRepository
) : ViewModel() {
    private val _authState: MutableStateFlow<AuthResult> = MutableStateFlow(AuthResult.Nothing())

    val user = (userRepository as UserRepository).currentUser


    val authState = _authState.asStateFlow()

    var notifyState = MutableSharedFlow<NotifyState>()

    val errorMessage = MutableStateFlow("")

    suspend fun signIn(email: String, password: String) {
        _authState.value = AuthResult.Loading("Signing in...")
        authRepository.signIn(email, password).collect { result ->
            if (result is AuthResult.Success) {
                result.user?.let { user ->
                    preferenceHelper.saveFirebaseId(user.uid)
                    preferenceHelper.setLoggedIn(true)
                    val user = userRepository.refreshCurrentUserDetails(user.uid)
                    if (user != null) {
                        notifyState.emit(NotifyState.ShowToast("Successfully Logged In"))
                    }
                } ?: run {
                    notifyState.emit(NotifyState.ShowToast("User data is null"))
                }
            } else if (result is AuthResult.Error) {
                result.message?.let {
                    notifyState.emit(NotifyState.ShowToast(it))
                }
            }
            _authState.value = result
        }
    }

    fun isValidUser(username: String, password: String): Boolean {
        if (username.isEmpty()) {
            errorMessage.value = "Email should not be empty"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            errorMessage.value = "Please enter a valid email id"
            return false
        }
        if (password.length < 8) {
            errorMessage.value = "Password should be more than 7 characters"
            return false
        }
        errorMessage.value = ""
        return true
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("Surendar", "AuthViewModel cleared")
    }
}