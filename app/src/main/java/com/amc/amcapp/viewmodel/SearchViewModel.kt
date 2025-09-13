package com.amc.amcapp.viewmodel

import androidx.lifecycle.ViewModel
import com.amc.amcapp.data.ISearchRepository
import com.amc.amcapp.ui.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchViewModel<T>(val searchRepository: ISearchRepository<T>) : ViewModel() {
    private val _itemsState: MutableStateFlow<ApiResult<List<T>>> =
        MutableStateFlow(ApiResult.Loading)
    val itemsState: StateFlow<ApiResult<List<T>>> = _itemsState.asStateFlow()

     suspend fun fetchAll(tableName: String) {
        try {
            _itemsState.value = ApiResult.Loading
            val items = searchRepository.getAllItems(tableName)
            _itemsState.value = ApiResult.Success(items)
        } catch (e: Exception) {
            _itemsState.value = ApiResult.Error(e.message ?: "Unknown error occurred")
        }
    }

}
