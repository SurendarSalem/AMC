package com.amc.amcapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.amc.amcapp.AuthRepository
import com.amc.amcapp.data.IUserRepository
import com.amc.amcapp.data.UserRepository
import com.amc.amcapp.data.datastore.PreferenceHelper
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.model.User
import com.amc.amcapp.ui.AuthResult
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LandingViewModel(
    private val authRepository: AuthRepository,
    private val preferenceHelper: PreferenceHelper,
    private val userRepository: IUserRepository
) : ViewModel() {

    val user = (userRepository as UserRepository).currentUser

    suspend fun logout() {
        authRepository.signOut()
        preferenceHelper.clearAll()
    }

    override fun onCleared() {
        super.onCleared()
    }
}