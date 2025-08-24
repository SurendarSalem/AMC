package com.amc.amcapp.data

import android.content.Context
import com.amc.amcapp.data.datastore.PreferenceKeys
import com.amc.amcapp.dataStore
import com.amc.amcapp.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

interface IUserRepository {

    suspend fun addUserToFirebase(user: User): User?
    suspend fun updateUser(user: User)
    suspend fun refreshCurrentUserDetails(userId: String): User?
    suspend fun getAllUsers(): List<User>
    var userName: Flow<String>
}

class UserRepository(
    private val context: Context, private val firestore: FirebaseFirestore
) : IUserRepository {

    var currentUser: User? = null


    override suspend fun addUserToFirebase(user: User): User? {
        try {
            firestore.collection("users").document(user.firebaseId).set(user).await()
            return user
        } catch (e: Exception) {
            return null
        }
    }


    override suspend fun updateUser(user: User) {
        try {
            firestore.collection("users").document(user.firebaseId).set(user).await()
        } catch (e: Exception) {
            // Handle error, e.g., log it or rethrow
        }
    }

    override suspend fun refreshCurrentUserDetails(userId: String): User? {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            if (document.exists()) {
                currentUser = document.toObject(User::class.java)
                return currentUser
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

    override var userName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.FIREBASE_USER_ID] ?: "Guest"
    }
}