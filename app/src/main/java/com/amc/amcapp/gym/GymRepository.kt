package com.amc.amcapp.gym

import androidx.compose.foundation.layout.add
import com.amc.amcapp.Gym
import com.amc.amcapp.ui.ApiResult
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow


interface IGymRepository {
    suspend fun addGym(gym: Gym): Flow<ApiResult<Gym>>
    suspend fun getGyms(): Flow<ApiResult<List<Gym>>>
    suspend fun deleteGym(gymId: String): Flow<ApiResult<Unit>>
}

class GymRepository(private val database: FirebaseDatabase = Firebase.database) : IGymRepository {

    override suspend fun addGym(gym: Gym): Flow<ApiResult<Gym>> = callbackFlow {
        val gymsRef = database.getReference("gyms")
        gymsRef.push().setValue(gym).addOnSuccessListener {
            trySend(ApiResult.Success(gym))
        }.addOnFailureListener { exception ->
            trySend(ApiResult.Error(message = exception.message ?: "Unknown error"))
        }
        awaitClose { gymsRef.removeValue() }
    }

    override suspend fun getGyms(): Flow<ApiResult<List<Gym>>> = callbackFlow {
        val gymsRef = database.getReference("gyms")
        gymsRef.get().addOnSuccessListener { dataSnapshot ->
            val gyms = dataSnapshot.children.mapNotNull { it.getValue(Gym::class.java) }
            trySend(ApiResult.Success(gyms))
        }.addOnFailureListener { exception ->
            trySend(ApiResult.Error(message = exception.message ?: "Unknown error"))
        }
    }

    override suspend fun deleteGym(gymId: String): Flow<ApiResult<Unit>> = callbackFlow {
        val gymRef = database.getReference("gyms").child(gymId)
        gymRef.removeValue().addOnSuccessListener {
            trySend(ApiResult.Success(Unit))
        }.addOnFailureListener { exception ->
            trySend(ApiResult.Error(message = exception.message ?: "Unknown error"))
        }
        awaitClose { gymRef.removeValue() }
    }
}