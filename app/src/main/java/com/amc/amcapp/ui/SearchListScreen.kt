package com.amc.amcapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.amc.amcapp.viewmodel.SearchViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun <T> SearchListScreen(
    searchViewModel: SearchViewModel<T> = koinViewModel(),
    listItem: @Composable (item: T) -> Unit,
    errorMessage: String = "No items found"
) {

    val itemsState by searchViewModel.itemsState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        when (itemsState) {
            is ApiResult.Loading -> {
                AppLoadingBar(this@Box)
            }

            is ApiResult.Error -> {
                AppError(errorMessage = (itemsState as ApiResult.Error).message)
            }

            is ApiResult.Success -> {
                val items = (itemsState as ApiResult.Success<List<T>>).data
                if (items.isEmpty()) {
                    AppError(errorMessage = errorMessage)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(items) { item ->
                            listItem(item)
                        }

                    }
                }
            }

            ApiResult.Empty -> TODO()
        }
    }
}