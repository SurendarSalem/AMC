package com.amc.amcapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.amc.amcapp.ui.LandingActivity
import com.amc.amcapp.ui.NavigationStack
import com.amc.amcapp.ui.theme.AMCTheme
import com.amc.amcapp.viewmodel.SplashState
import com.amc.amcapp.viewmodel.SplashViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModel()

    // 👇 control splash manually
    private var keepSplash = true

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplash }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        actionBar?.hide()

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    SplashState.LoggedIn -> {
                        keepSplash = false // 👈 release splash only after navigation
                        startActivity(Intent(this@MainActivity, LandingActivity::class.java))
                        finish()
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



