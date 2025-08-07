package com.amc.amcapp.model

import com.amc.amcapp.ui.AuthResult
import com.google.firebase.auth.FirebaseUser

sealed class NotifyState {
    data class ShowToast(val message: String) : NotifyState()
    data class Navigate(val route: String) : NotifyState()
}
