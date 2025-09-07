package com.amc.amcapp

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

interface IComplaintRepository {

    var allComplaints: List<Complaint>
    suspend fun getAllComplaints(): List<Complaint>
}

class ComplaintRepository(val firestore: FirebaseFirestore) : IComplaintRepository {

    override var allComplaints: List<Complaint> = emptyList()

    override suspend fun getAllComplaints(): List<Complaint> {
        return try {
            val snapshot = firestore.collection("complaints").get().await()
            allComplaints = snapshot.toObjects(Complaint::class.java)
            return allComplaints
        } catch (e: Exception) {
            emptyList()
        }
    }
}