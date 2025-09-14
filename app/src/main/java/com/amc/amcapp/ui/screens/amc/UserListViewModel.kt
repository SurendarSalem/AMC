package com.amc.amcapp.ui.screens.amc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amc.amcapp.data.IUserRepository
import com.amc.amcapp.model.User
import com.amc.amcapp.model.UserType
import com.amc.amcapp.ui.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserListViewModel(val userRepository: IUserRepository) : ViewModel() {
    private val _usersState: MutableStateFlow<ApiResult<List<User>>> =
        MutableStateFlow(ApiResult.Loading)
    val amcListState: StateFlow<ApiResult<List<User>>> = _usersState.asStateFlow()

    var _filterUserType: MutableStateFlow<UserType?> = MutableStateFlow(UserType.GYM_OWNER)

    val filteredUsers = combine(
        _usersState, _filterUserType
    ) { usersResult, filterType ->
        when (usersResult) {
            is ApiResult.Success -> {
                filterType?.let { type ->
                    ApiResult.Success(usersResult.data.filter { it.userType == type })
                } ?: usersResult
            }

            is ApiResult.Error -> usersResult
            ApiResult.Loading -> ApiResult.Loading
            ApiResult.Empty -> TODO()
        }
    }.stateIn(
        viewModelScope, SharingStarted.Lazily, ApiResult.Loading
    )


    init {
        viewModelScope.launch {
            getAllUsers()
        }
    }

    private suspend fun getAllUsers() {
        try {
            _usersState.value = ApiResult.Loading
            val users = userRepository.getAllUsers()
            _usersState.value = ApiResult.Success(users)
        } catch (e: Exception) {
            _usersState.value = ApiResult.Error(e.message ?: "Unknown error occurred")
        }
    }


    fun setFilter(userType: UserType) {
        _filterUserType.value = userType
    }

}
