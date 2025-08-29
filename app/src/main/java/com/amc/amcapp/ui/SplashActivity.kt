package com.amc.amcapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.amc.amcapp.MainActivity

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Forward to MainActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtras(intent) // forward extras if any
        }
        startActivity(intent)
        finish() // Kill SplashActivity so back button won’t return to it
    }
}
