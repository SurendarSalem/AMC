package com.amc.amcapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amc.amcapp.data.IUserRepository
import com.amc.amcapp.data.datastore.PreferenceHelper
import com.amc.amcapp.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed class SplashState {
    object Loading : SplashState()
    object LoggedIn : SplashState()
    object LoggedOut : SplashState()
}


@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModel(
    private val preferenceHelper: PreferenceHelper, val userRepository: IUserRepository
) : ViewModel() {

    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)

    val state: StateFlow<SplashState> = _state

    var userDetail: MutableStateFlow<User?> = MutableStateFlow(null)

    init {
        checkLogin()
    }

    private fun checkLogin() {
        viewModelScope.launch {
            combine(
                preferenceHelper.isLoggedIn(), preferenceHelper.getFirebaseUserId()
            ) { loggedIn, userId ->
                if (loggedIn) {
                    val user = userRepository.refreshCurrentUserDetails(userId)
                    userDetail.value = user
                    if (user != null) SplashState.LoggedIn else SplashState.LoggedOut
                    SplashState.LoggedIn
                } else {
                    SplashState.LoggedOut
                }
            }.collect { splashState ->
                if (splashState is SplashState.LoggedOut) {
                    delay(3000)
                }
                _state.value = splashState
            }
        }
    }

}
