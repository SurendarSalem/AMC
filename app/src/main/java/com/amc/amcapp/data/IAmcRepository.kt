package com.amc.amcapp.data

import com.amc.amcapp.model.AMC
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface IAmcRepository {
    suspend fun getAllAMCs(): List<AMC>
}

class AmcRepository(private val firestore: FirebaseFirestore) : IAmcRepository {
    override suspend fun getAllAMCs(): List<AMC> {
        return try {
            val snapshot = firestore.collection("amcs").get().await()
            snapshot.toObjects(AMC::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}