package com.amc.amcapp.model

import android.app.Activity
import androidx.activity.ComponentActivity
import com.amc.amcapp.ui.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlin.reflect.KClass

sealed class NotifyState {
    data class ShowToast(val message: String) : NotifyState()
    data class Navigate(val route: String, val data: Any? = null) : NotifyState()
    object LaunchActivity : NotifyState()
}
