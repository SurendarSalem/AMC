package com.amc.amcapp.ui

import com.google.firebase.auth.FirebaseUser

// data/AuthResult.kt
sealed class AuthResult {
    data class Loading(val message: String) : AuthResult()
    data class Success(val user: FirebaseUser?) : AuthResult()
    data class Error(val message: String?) : AuthResult()
    data class Nothing(val message: String? = null) : AuthResult()
}