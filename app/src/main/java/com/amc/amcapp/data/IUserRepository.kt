package com.amc.amcapp.data

import android.content.Context
import com.amc.amcapp.data.datastore.PreferenceKeys
import com.amc.amcapp.dataStore
import com.amc.amcapp.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

interface IUserRepository {
    suspend fun addUserToFirebase(user: User): User?
    suspend fun updateUser(user: User): Flow<Boolean>
    suspend fun refreshCurrentUserDetails(userId: String): User?
    suspend fun getAllUsers(): List<User>
    val userName: Flow<String>
    var currentUser: MutableStateFlow<User?>
}

class UserRepository(
    private val context: Context,
    private val firestore: FirebaseFirestore
) : IUserRepository {

    override var currentUser: MutableStateFlow<User?> = MutableStateFlow(null)

    override suspend fun addUserToFirebase(user: User): User? {
        return try {
            firestore.collection("users").document(user.firebaseId).set(user).await()
            user
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateUser(user: User): Flow<Boolean> = flow {
        try {
            firestore.collection("users").document(user.firebaseId).set(user).await()
            emit(true)
        } catch (e: Exception) {
            emit(false)
        }
    }

    override suspend fun refreshCurrentUserDetails(userId: String): User? {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            if (document.exists()) {
                val fetchedUser = document.toObject(User::class.java)
                currentUser.value = fetchedUser
                fetchedUser
            } else null
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

    override val userName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.FIREBASE_USER_ID] ?: "Guest"
    }
}
