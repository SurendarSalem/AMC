package com.amc.amcapp.data

import com.amc.amcapp.model.Spare
import com.amc.amcapp.ui.ApiResult
import com.amc.amcapp.util.Constants.Table.TABLE_SPARES
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

interface ISparesRepository {

    var spares: List<Spare>

    suspend fun addSpare(spare: Spare): Flow<ApiResult<Spare>>

    suspend fun updateSpare(spare: Spare): Flow<ApiResult<Spare>>

    suspend fun getAllSpares(): Flow<ApiResult<List<Spare>>>

    suspend fun deleteSpare(spare: Spare)
}

class SparesRepository(val firestore: FirebaseFirestore) : ISparesRepository {

    override var spares: List<Spare> = emptyList()

    override suspend fun addSpare(spare: Spare) = flow {
        emit(ApiResult.Loading)
        try {
            val docRef = firestore.collection(TABLE_SPARES).document()
            val spareWithId = spare.copy(id = docRef.id)
            docRef.set(spareWithId).await()
            emit(ApiResult.Success(spareWithId))
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message ?: "Unknown error"))
        }
    }

    override suspend fun updateSpare(spare: Spare) = flow {
        emit(ApiResult.Loading)
        try {
            val docRef = firestore.collection(TABLE_SPARES).document(spare.id)
            docRef.set(spare).await()
            emit(ApiResult.Success(spare))
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message ?: "Unknown error"))
        }
    }

    override suspend fun getAllSpares(): Flow<ApiResult<List<Spare>>> = flow {
        emit(ApiResult.Loading)
        try {
            val snapshot = firestore.collection(TABLE_SPARES).get().await()
            spares = snapshot.toObjects(Spare::class.java)
            emit(ApiResult.Success(spares))
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message ?: "Unknown error"))
        }
    }

    override suspend fun deleteSpare(spare: Spare) {

    }
}