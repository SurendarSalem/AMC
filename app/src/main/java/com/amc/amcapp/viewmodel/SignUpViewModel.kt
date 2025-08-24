package com.amc.amcapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.amc.amcapp.AuthRepository
import com.amc.amcapp.data.IUserRepository
import com.amc.amcapp.data.datastore.PreferenceHelper
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

class SignUpViewModel(
    private val authRepository: AuthRepository,
    private val preferenceHelper: PreferenceHelper,
    private val userRepository: IUserRepository
) : ViewModel() {
    private val _authState: MutableStateFlow<AuthResult> = MutableStateFlow(AuthResult.Nothing())

    private var _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    val authState = _authState.asStateFlow()

    var notifyState = MutableSharedFlow<NotifyState>()

    suspend fun createUser(user: User) {
        _authState.value = AuthResult.Loading("Signing in...")
        authRepository.createUser(user).collect { result ->
            _authState.value = result
            if (result is AuthResult.Success) {
                userRepository.addUserToFirebase(user)
                notifyState.emit(NotifyState.ShowToast("User added successfully"))
                delay(500)
                notifyState.emit(NotifyState.Navigate(Screen.LoginScreen.route))
            } else if (result is AuthResult.Error) {
                _authState.value = AuthResult.Error(result.message)
            }

        }
    }

    fun isValidUser(username: String, password: String): Boolean {
        return username.isNotEmpty() && username.length > 4 && password.isNotEmpty() && password.length >= 8
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("Surendar", "AuthViewModel cleared")
    }
}