package com.amc.amcapp.gym

import com.amc.amcapp.Gym
import com.amc.amcapp.ui.ApiResult
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


interface IGymRepository {
    suspend fun addGym(gym: Gym): Flow<ApiResult<Gym>>
    suspend fun getGyms(): Flow<ApiResult<List<Gym>>>
    suspend fun deleteGym(gymId: String): Flow<ApiResult<Unit>>
}

class GymRepository(private val database: FirebaseFirestore = FirebaseFirestore.getInstance()) : IGymRepository {

    override suspend fun addGym(gym: Gym): Flow<ApiResult<Gym>> = callbackFlow {

    }

    override suspend fun getGyms(): Flow<ApiResult<List<Gym>>> = callbackFlow {

    }

    override suspend fun deleteGym(gymId: String): Flow<ApiResult<Unit>> = callbackFlow {

    }
}