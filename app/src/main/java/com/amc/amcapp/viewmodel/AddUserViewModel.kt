package com.amc.amcapp.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import com.amc.amcapp.AuthRepository
import com.amc.amcapp.data.IUserRepository
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.model.User
import com.amc.amcapp.model.UserType
import com.amc.amcapp.ui.ApiResult
import com.amc.amcapp.ui.AuthResult
import com.amc.amcapp.util.ImageUtils
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID


data class AddUserState(
    val email: String = "tech2@gmail.com",
    val password: String = "User@123",
    val confirmPassword: String = "",
    val name: String = "Tech 2",
    val userType: UserType = UserType.TECHNICIAN,
    var bitmap: Bitmap? = null,
    val imageUrl: String = ""
)

fun AddUserState.toUser(): User {
    return User(
        email = email, name = name, password = password, userType = userType
    )
}


class AddUserViewModel(
    private val authRepository: AuthRepository, private val userRepository: IUserRepository
) : ViewModel() {
    private val _addUserUiState: MutableStateFlow<ApiResult<User>> =
        MutableStateFlow(ApiResult.Empty)
    val addUserUiState = _addUserUiState.asStateFlow()
    private var _addUserState = MutableStateFlow(AddUserState())
    val addUserState = _addUserState.asStateFlow()
    var notifyState = MutableSharedFlow<NotifyState>()


    suspend fun createUser() {
        _addUserUiState.value = ApiResult.Loading
        val user = addUserState.value.toUser()
        authRepository.createUserInFirebase(user).collect { result ->
            if (result is AuthResult.Success) {
                addUserState.value.bitmap?.let {
                    val bytes = ImageUtils.bitmapToByteArray(it)
                    val imageUrl = uploadBytesToFirebase(bytes)
                    user.imageUrl = imageUrl
                }
                val addedUser = addUserToFirebase(user)
                addedUser?.let {
                    _addUserUiState.value = ApiResult.Success(addedUser)
                } ?: run {
                    _addUserUiState.value = ApiResult.Error("Unable to create user")
                    notifyState.emit(
                        NotifyState.ShowToast("Unable to create user")
                    )
                }
            } else if (result is AuthResult.Error) {
                _addUserUiState.value = ApiResult.Error(result.message ?: "Login failed")
                notifyState.emit(NotifyState.ShowToast(result.message ?: "Unable to create user"))
            }
        }
    }

    suspend fun uploadBytesToFirebase(bytes: ByteArray, pathPrefix: String = "images"): String =
        withContext(Dispatchers.IO) {
            val storage = Firebase.storage
            val fileName = "$pathPrefix/${UUID.randomUUID()}.jpg"
            val ref = storage.reference.child(fileName)

            ref.putBytes(bytes).await()
            ref.downloadUrl.await().toString()
        }

    suspend fun addUserToFirebase(user: User): User? {
        val user = userRepository.addUserToFirebase(user)
        if (user != null) {
            notifyState.emit(NotifyState.ShowToast("User added successfully"))
        } else {
            notifyState.emit(NotifyState.ShowToast("Unable to add the User"))
        }
        return user
    }

    fun isValidUser(username: String, password: String): Boolean {
        return username.isNotEmpty() && username.length > 4 && password.isNotEmpty() && password.length >= 8
    }

    fun onNameChanged(name: String) {
        _addUserState.value = _addUserState.value.copy(name = name)
    }

    fun onEmailChanged(email: String) {
        _addUserState.value = _addUserState.value.copy(email = email)
    }

    fun onPasswordChanged(password: String) {
        _addUserState.value = _addUserState.value.copy(password = password)
    }

    fun onConfirmPassword(password: String) {
        _addUserState.value = _addUserState.value.copy(confirmPassword = password)
    }

    fun onRoleChanged(userType: UserType) {
        _addUserState.value = _addUserState.value.copy(userType = userType)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("Surendar", "AuthViewModel cleared")
    }

    fun onBitmapChanged(bitmap: Bitmap?) {
        _addUserState.value = _addUserState.value.copy(bitmap = bitmap)
    }
}