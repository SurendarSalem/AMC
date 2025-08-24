package com.amc.amcapp.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.amc.amcapp.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferenceHelper(private val context: Context) {

    suspend fun saveFirebaseId(name: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.FIREBASE_USER_ID] = name
        }
    }

    suspend fun getFirebaseUserId(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferenceKeys.FIREBASE_USER_ID] ?: ""
        }
    }


    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.IS_LOGGED_IN] = isLoggedIn
        }
    }

    suspend fun isLoggedIn(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferenceKeys.IS_LOGGED_IN] ?: false
        }
    }

    suspend fun clearAll() {
        setLoggedIn(false)
        saveFirebaseId("")
        context.dataStore.edit { it.clear() }
    }
}