package com.amc.amcapp

import android.app.Application
import com.amc.amcapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class AmcApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AmcApp)
            modules(appModule)
        }
    }
}
