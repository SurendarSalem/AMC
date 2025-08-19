package com.amc.amcapp.data

import com.amc.amcapp.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface IUserRepository {
    suspend fun updateUser(user: User)
    suspend fun getUserById(userId: String): User?
    suspend fun getAllUsers(): List<User>
}

class UserRepository(private val firestore: FirebaseFirestore) : IUserRepository {
    override suspend fun updateUser(user: User) {
        try {
            firestore.collection("users").document(user.firebaseId).set(user).await()
        } catch (e: Exception) {
            // Handle error, e.g., log it or rethrow
        }
    }

    override suspend fun getUserById(userId: String): User? {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            if (document.exists()) {
                document.toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getAllUsers(): List<User> {
        return try {
            val snapshot = firestore.collection("users").get().await()
            snapshot.toObjects(User::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}