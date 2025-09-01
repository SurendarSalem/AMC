package com.amc.amcapp.viewmodel

import android.graphics.Bitmap
import android.util.Log
import android.util.Patterns
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID


data class AddUserState(
    val firebaseId: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val name: String = "",
    val userType: UserType? = null,
    var bitmap: Bitmap? = null,
    val imageUrl: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val gymName: String = "",
    val isLoading: Boolean = false
)

fun AddUserState.toUser(): User {
    return if (userType == UserType.GYM_OWNER) {
        User(
            firebaseId = firebaseId,
            email = email,
            name = name,
            password = password,
            userType = userType,
            imageUrl = imageUrl,
            phoneNumber = phoneNumber,
            address = address
        )
    } else {
        User(
            firebaseId = firebaseId,
            email = email,
            name = name,
            password = password,
            userType = userType,
            imageUrl = imageUrl,
            phoneNumber = phoneNumber,
            address = address
        )
    }
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
    val errorMessage = MutableStateFlow("")

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
                    notifyState.emit(
                        NotifyState.LaunchActivity
                    )
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

    suspend fun updateUserToFirebase(user: User): User? {
        _addUserUiState.value = ApiResult.Loading
        addUserState.value.bitmap?.let {
            val bytes = ImageUtils.bitmapToByteArray(it)
            val imageUrl = uploadBytesToFirebase(bytes)
            user.imageUrl = imageUrl
        }
        val user = userRepository.addUserToFirebase(user)
        if (user != null) {
            _addUserUiState.value = ApiResult.Success(user)
            notifyState.emit(NotifyState.ShowToast("User updated successfully"))
            delay(300)
            notifyState.emit(
                NotifyState.LaunchActivity
            )
        } else {
            _addUserUiState.value = ApiResult.Error("Unable to update the User")
            delay(300)
            notifyState.emit(NotifyState.ShowToast("Unable to add the User"))
        }
        return user
    }

    fun isValidUser(isEditMode: Boolean = false): Boolean {
        if (!isEditMode && addUserState.value.bitmap == null) {
            errorMessage.value = "Please select an image"
            return false
        }
        if (isEditMode && addUserState.value.imageUrl.isEmpty() && addUserState.value.bitmap == null) {
            errorMessage.value = "Please select an image in Edit mode"
            return false
        }
        if (addUserState.value.name.isEmpty() || addUserState.value.name.length < 6) {
            errorMessage.value = "Name should not be empty and should be of atleast 6 characters"
            return false
        }
        if (addUserState.value.userType == null) {
            errorMessage.value = "Please select the User type"
            return false
        }
        if (addUserState.value.email.isEmpty()) {
            errorMessage.value = "Email should not be empty"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(addUserState.value.email).matches()) {
            errorMessage.value = "Please enter a valid email id"
            return false
        }
        if (addUserState.value.password.length < 8) {
            errorMessage.value = "Password should be atleast 8 characters"
            return false
        }
        if (!isEditMode && (addUserState.value.password != addUserState.value.confirmPassword)) {
            errorMessage.value = "Password and Confirm Password does not match"
            return false
        }
        if (addUserState.value.phoneNumber.length < 10) {
            errorMessage.value = "Enter a valid Mobile Number"
            return false
        }
        if (addUserState.value.address.length < 10) {
            errorMessage.value = "Address should be atleast 10 characters"
            return false
        }
        errorMessage.value = ""
        return true
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

    fun onPhoneNumberChanged(name: String) {
        _addUserState.value = _addUserState.value.copy(phoneNumber = name)
    }

    fun onAddressChanged(email: String) {
        _addUserState.value = _addUserState.value.copy(address = email)
    }

    fun onGymNameChanged(password: String) {
        _addUserState.value = _addUserState.value.copy(gymName = password)
    }

    fun onImageUrlChanged(userType: String) {
        _addUserState.value = _addUserState.value.copy(imageUrl = userType)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("Surendar", "AuthViewModel cleared")
    }

    fun onBitmapChanged(bitmap: Bitmap?) {
        _addUserState.value = _addUserState.value.copy(bitmap = bitmap)
        onImageUrlChanged("")
    }

    fun preFillUserState(user: User) {
        _addUserState.value = AddUserState(
            firebaseId = user.firebaseId,
            email = user.email,
            password = user.password,
            name = user.name,
            userType = user.userType,
            imageUrl = user.imageUrl,
            gymName = if (user is User) user.address else "",
            phoneNumber = user.phoneNumber,
            address = user.address
        )
    }
}