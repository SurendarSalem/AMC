package com.amc.amcapp.data

import com.amc.amcapp.model.AMC
import com.amc.amcapp.model.AmcPackage
import com.amc.amcapp.ui.ApiResult
import com.amc.amcapp.util.Constants.Table.TABLE_AMC_PACKAGES
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

interface IAmcPackageRepository {

    var amcPackages: List<AmcPackage>

    suspend fun addOrUpdateAmc(amcPackage: AmcPackage): Flow<ApiResult<AmcPackage>>

    suspend fun getAllAmcPackages(): Flow<ApiResult<List<AmcPackage>>>

    suspend fun deleteAmcPackage(amcPackage: AmcPackage)
}

class AmcPackageRepository(val firestore: FirebaseFirestore) : IAmcPackageRepository {

    override var amcPackages: List<AmcPackage> = emptyList()

    override suspend fun addOrUpdateAmc(amcPackage: AmcPackage) = flow {
        emit(ApiResult.Loading)
        try {
            val docRef = if (amcPackage.id.isNotEmpty()) {
                firestore.collection(TABLE_AMC_PACKAGES).document(amcPackage.id)
            } else {
                firestore.collection(TABLE_AMC_PACKAGES).document()
            }
            val amcWithId = amcPackage.copy(id = docRef.id)
            docRef.set(amcWithId).await()
            emit(ApiResult.Success(amcWithId))
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message ?: "Unknown error"))
        }
    }

    override suspend fun getAllAmcPackages(): Flow<ApiResult<List<AmcPackage>>> = flow {
        emit(ApiResult.Loading)
        try {
            val snapshot = firestore.collection(TABLE_AMC_PACKAGES).get().await()
            amcPackages = snapshot.toObjects(AmcPackage::class.java)
            emit(ApiResult.Success(amcPackages))
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message ?: "Unknown error"))
        }
    }

    override suspend fun deleteAmcPackage(amcPackage: AmcPackage) {

    }
}