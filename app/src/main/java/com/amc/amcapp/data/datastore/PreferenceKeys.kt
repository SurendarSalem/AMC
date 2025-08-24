package com.amc.amcapp.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val FIREBASE_USER_ID = stringPreferencesKey("firebaseUserId")
    val IS_LOGGED_IN = booleanPreferencesKey("isLoggedIn")
}
