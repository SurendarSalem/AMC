package com.amc.amcapp

import FullImageUploaderScreen
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.amc.amcapp.data.IUserRepository
import com.amc.amcapp.data.datastore.PreferenceHelper
import com.amc.amcapp.equipments.IEquipmentsRepository
import com.amc.amcapp.model.UserType
import com.amc.amcapp.ui.LandingActivity
import com.amc.amcapp.ui.NavigationStack
import com.amc.amcapp.ui.technician.TechnicianActivity
import com.amc.amcapp.ui.theme.AMCTheme
import com.amc.amcapp.viewmodel.SplashState
import com.amc.amcapp.viewmodel.SplashViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModel()

    private val complaintRepository: IComplaintRepository by inject()

    private val equipmentsRepository: IEquipmentsRepository by inject()


    private var keepSplash = true

    private val userRepository: IUserRepository by inject()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            complaintRepository.getAllComplaints()
            equipmentsRepository.getEquipments()
        }
        splashScreen.setKeepOnScreenCondition { keepSplash }
        enableEdgeToEdge()
        actionBar?.hide()

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    SplashState.LoggedIn -> {
                        keepSplash = false // 👈 release splash only after navigation
                        userRepository.currentUser.value?.let {
                            if (it.userType == UserType.ADMIN) {
                                startActivity(
                                    Intent(
                                        this@MainActivity, LandingActivity::class.java
                                    )
                                )
                            } else if (it.userType == UserType.TECHNICIAN) {
                                startActivity(
                                    Intent(
                                        this@MainActivity, TechnicianActivity::class.java
                                    )
                                )
                            }
                            finish()
                        }
                    }

                    SplashState.LoggedOut -> {
                        keepSplash = false // 👈 release splash only when UI is ready
                        setContent {
                            AMCTheme {
                                NavigationStack()
                            }
                        }
                    }

                    SplashState.Loading -> {
                        // Do nothing, still showing splash
                    }
                }
            }
        }
    }
}



