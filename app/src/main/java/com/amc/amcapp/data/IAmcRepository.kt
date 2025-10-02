package com.amc.amcapp.data

import android.net.Uri
import com.amc.amcapp.Equipment
import com.amc.amcapp.model.AMC
import com.amc.amcapp.ui.ApiResult
import com.amc.amcapp.util.FirebaseHelper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

interface IAmcRepository {
    suspend fun getAllAMCs(): List<AMC>

    suspend fun addAmc(amc: AMC): Flow<ApiResult<AMC>>

    suspend fun uploadImageToFirebase(imageUri: Uri): String
}

class AmcRepository(
    private val firestore: FirebaseFirestore,
    private val firebaseHelper: FirebaseHelper
) : IAmcRepository {
    override suspend fun getAllAMCs(): List<AMC> {
        return try {
            val snapshot = firestore.collection("amcs").get().await()
            val list = snapshot.toObjects(AMC::class.java)
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addAmc(amc: AMC) = flow {
        emit(ApiResult.Loading)
        try {
            val docRef = if (amc.id.isNotEmpty()) {
                firestore.collection("amcs").document(amc.id)
            } else {
                firestore.collection("amcs").document()
            }
            val amcWithId = amc.copy(id = docRef.id)
            docRef.set(amcWithId).await()
            emit(ApiResult.Success(amcWithId))
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message ?: "Unknown error"))
        }
    }


    override suspend fun uploadImageToFirebase(imageUri: Uri): String {
        val downloadUrl = firebaseHelper.uploadImageToFirebase(imageUri)
        return downloadUrl
    }

}