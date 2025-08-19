package com.amc.amcapp.viewmodel

import androidx.lifecycle.ViewModel
import com.amc.amcapp.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

sealed class ForgotPasswordResult {
    data class Loading(val message: String) : ForgotPasswordResult()
    data class CodeVerified(val user: FirebaseUser?) : ForgotPasswordResult()
    data class CodeSent(val code: String) : ForgotPasswordResult()
    data class Error(val message: String?) : ForgotPasswordResult()
    object Nothing : ForgotPasswordResult()
}

class ForgotPasswordViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _forgotPasswordFlow: MutableStateFlow<ForgotPasswordResult> = MutableStateFlow(ForgotPasswordResult.Nothing)
    val forgotPasswordFlow = _forgotPasswordFlow.asStateFlow()

    var showToast = MutableSharedFlow<String>()


    suspend fun sendPasswordResetEmail(email: String) {
        withContext(Dispatchers.Main) {
            _forgotPasswordFlow.value = ForgotPasswordResult.Loading("Sending code...")
        }
        authRepository.sendPasswordResetEmail(email).collect { result ->
            if (result is ForgotPasswordResult.CodeVerified) {
                showToast.emit("Code has been sent to your Email Id")
            } else if (result is ForgotPasswordResult.Error) {
                result.message?.let {
                    showToast.emit(it)
                }
            }
            _forgotPasswordFlow.value = result
        }
    }

    suspend fun verifyPasswordResetCode(code: String) {
        withContext(Dispatchers.Main) {
            _forgotPasswordFlow.value = ForgotPasswordResult.Loading("Signing in...")
        }
        delay(5000)
        authRepository.verifyPasswordResetCode(code).collect { result ->
            _forgotPasswordFlow.value = result
        }
    }

    fun getResetLabel(code: String): String {
        return if (code.isEmpty()) {
            "Send Code"
        } else {
            "Verify Code"
        }
    }
}