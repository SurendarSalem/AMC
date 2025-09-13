package com.amc.amcapp.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface ISearchRepository<T> {
    suspend fun getAllItems(tableName: String): List<T>
}

class SearchRepository<T>(
    private val firestore: FirebaseFirestore, private val clazz: Class<T>
) : ISearchRepository<T> {

    override suspend fun getAllItems(tableName: String): List<T> {
        return try {
            val snapshot = firestore.collection(tableName).get().await()
            snapshot.toObjects(clazz)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
