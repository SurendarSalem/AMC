package com.amc.amcapp.viewmodel

import androidx.lifecycle.ViewModel
import com.amc.amcapp.AuthRepository
import com.amc.amcapp.data.IUserRepository
import com.amc.amcapp.data.UserRepository
import com.amc.amcapp.data.datastore.PreferenceHelper
import com.amc.amcapp.model.User
import kotlinx.coroutines.flow.MutableStateFlow

class LandingViewModel(
    private val authRepository: AuthRepository,
    private val preferenceHelper: PreferenceHelper,
    private val userRepository: IUserRepository
) : ViewModel() {

    val user: MutableStateFlow<User?> = (userRepository as UserRepository).currentUser

    suspend fun logout() {
        authRepository.signOut()
        userRepository.resetUser()
        preferenceHelper.clearAll()
    }

    override fun onCleared() {
        super.onCleared()
    }
}