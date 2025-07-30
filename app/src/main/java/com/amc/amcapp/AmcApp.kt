package com.amc.amcapp

import android.app.Application
import com.amc.amcapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AmcApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AmcApp)
            modules(appModule)
        }
    }
}
