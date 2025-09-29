package com.amc.amcapp.ui.technician

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import com.amc.amcapp.AuthRepository
import com.amc.amcapp.R
import com.amc.amcapp.data.IUserRepository
import com.amc.amcapp.data.UserRepository
import com.amc.amcapp.data.datastore.PreferenceHelper
import com.amc.amcapp.ui.theme.AMCTheme
import com.amc.amcapp.util.image.AppImagePicker
import org.koin.android.ext.android.inject

@RequiresApi(Build.VERSION_CODES.O)
class TechnicianActivity : ComponentActivity() {

    private val userRepository: IUserRepository by inject()
    private val authRepository: AuthRepository by inject()
    private val preferenceHelper: PreferenceHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            setTheme(R.style.Theme_AMC)
        }
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            AMCTheme {
                TechnicianHomeScreen(authRepository, preferenceHelper)
            }
        }
    }
}