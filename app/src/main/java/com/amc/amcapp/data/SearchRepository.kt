package com.amc.amcapp.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

interface ISearchRepository<T> {
    suspend fun getAllItems(tableName: String, filterBy: Pair<String, String>): List<T>
}

class SearchRepository<T>(
    private val firestore: FirebaseFirestore, private val clazz: Class<T>
) : ISearchRepository<T> {

    override suspend fun getAllItems(tableName: String, filterBy: Pair<String, String>): List<T> {
        return try {
            var query: Query = firestore.collection(tableName)

            if (filterBy.first.isNotEmpty() && filterBy.second.isNotEmpty()) {
                query = query.whereEqualTo(filterBy.first, filterBy.second)
            }

            val snapshot = query.get().await()
            snapshot.toObjects(clazz)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
