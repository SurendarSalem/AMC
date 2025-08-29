package com.amc.amcapp.ui

import com.google.firebase.auth.FirebaseUser

// data/AuthResult.kt
sealed class AuthResult {
    data class Loading(val message: String) : AuthResult()
    data class Success(val user: FirebaseUser?) : AuthResult()
    data class Error(val message: String?) : AuthResult()
    data class Nothing(val message: String? = null) : AuthResult()
}

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
    object Empty : ApiResult<Nothing>()
}

