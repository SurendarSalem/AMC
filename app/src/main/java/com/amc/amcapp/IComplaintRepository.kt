package com.amc.amcapp

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface IComplaintRepository {
    suspend fun getAllComplaints(): List<Complaint>
}

class ComplaintRepository(val firestore: FirebaseFirestore) : IComplaintRepository {
    override suspend fun getAllComplaints(): List<Complaint> {
        return try {
            val snapshot = firestore.collection("complaints").get().await()
            snapshot.toObjects(Complaint::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}