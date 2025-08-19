package com.amc.amcapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.amc.amcapp.AuthRepository
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.model.User
import com.amc.amcapp.ui.AuthResult
import com.amc.amcapp.ui.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _authState: MutableStateFlow<AuthResult> = MutableStateFlow(AuthResult.Nothing())
    val authState = _authState.asStateFlow()

    var showToast = MutableSharedFlow<NotifyState>()

    suspend fun signIn(email: String, password: String) {
        _authState.value = AuthResult.Loading("Signing in...")
        authRepository.signIn(email, password).collect { result ->
            if (result is AuthResult.Success) {
                showToast.emit(NotifyState.ShowToast("Welcome ${result.user?.displayName ?: "User"}!"))
                showToast.emit(NotifyState.Navigate(Screen.AddGymScreen.route))
            } else if (result is AuthResult.Error) {
                result.message?.let {
                    showToast.emit(NotifyState.ShowToast(it))
                }
            }
            _authState.value = result
        }
    }

    suspend fun createUser(user: User) {
        withContext(Dispatchers.Main) {
            _authState.value = AuthResult.Loading("Signing in...")
        }
        authRepository.createUser(user).collect { result ->
            _authState.value = result
        }
    }

    fun isValidUser(username: String, password: String): Boolean {
        return username.isNotEmpty() && username.length > 4 && password.isNotEmpty() && password.length >= 8
    }

    fun getResetLabel(code: String): String {
        return if (code.isEmpty()) {
            "Send Code"
        } else {
            "Verify Code"
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("Surendar", "AuthViewModel cleared")
    }
}