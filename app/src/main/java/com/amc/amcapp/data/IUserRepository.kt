package com.amc.amcapp.data

import android.content.Context
import com.amc.amcapp.data.datastore.PreferenceKeys
import com.amc.amcapp.dataStore
import com.amc.amcapp.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

interface IUserRepository {

    suspend fun addUserToFirebase(user: User): User?
    suspend fun updateUser(user: User): Flow<Boolean>
    suspend fun refreshCurrentUserDetails(userId: String): User?
    suspend fun getAllUsers(): List<User>
    var userName: Flow<String>
}

class UserRepository(
    private val context: Context, private val firestore: FirebaseFirestore
) : IUserRepository {

    val currentUser = MutableStateFlow<User?>(null)


    override suspend fun addUserToFirebase(user: User): User? {
        try {
            firestore.collection("users").document(user.firebaseId).set(user).await()
            return user
        } catch (e: Exception) {
            return null
        }
    }

    override suspend fun updateUser(user: User): Flow<Boolean> {
        return callbackFlow {
            try {
                val task = firestore.collection("users").document(user.firebaseId).set(user)
                    .addOnSuccessListener {
                        trySend(true)
                        close()
                    }.addOnFailureListener {
                        trySend(false)
                        close(it)
                    }
                awaitClose { task.isComplete }
            } catch (e: Exception) {
                trySend(false)
                close(e)
            }
        }
    }


    override suspend fun refreshCurrentUserDetails(userId: String): User? {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            if (document.exists()) {
                currentUser.value = document.toObject(User::class.java)
                return currentUser.value
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