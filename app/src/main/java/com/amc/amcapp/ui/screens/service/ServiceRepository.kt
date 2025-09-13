package com.amc.amcapp.ui.screens.service

import com.amc.amcapp.Equipment
import com.amc.amcapp.Service
import com.amc.amcapp.ui.ApiResult
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID


interface IServiceRepository {
    suspend fun addService(service: Service): Flow<ApiResult<Service>>

    suspend fun updateService(service: Service): Flow<ApiResult<Service>>
    suspend fun getServices(gymId: String): Flow<ApiResult<List<Service>>>
    suspend fun uploadBytesToFirebase(bytes: ByteArray, pathPrefix: String = "services"): String
}

class ServiceRepository(database: FirebaseFirestore = Firebase.firestore) : IServiceRepository {
    private val serviceRef = database.collection("services")
    override suspend fun addService(service: Service): Flow<ApiResult<Service>> = callbackFlow {
        trySend(ApiResult.Loading)
        // Add new
        val ref = serviceRef.document().id
        val finalService = service.copy(id = ref)
        val task = serviceRef.document(finalService.id).set(finalService)
            .addOnSuccessListener { documentReference ->
                trySend(ApiResult.Success(finalService))
                close()
            }.addOnFailureListener { exception ->
                trySend(ApiResult.Error(exception.message ?: "Unknown error"))
                close(exception)
            }
        awaitClose { task.isComplete } // just ensures cleanup
    }

    override suspend fun updateService(service: Service): Flow<ApiResult<Service>> = callbackFlow {
        trySend(ApiResult.Loading)

        val task = serviceRef.document(service.id).set(service).addOnSuccessListener {
            trySend(ApiResult.Success(service))
            close() // complete flow
        }.addOnFailureListener { exception ->
            trySend(ApiResult.Error(exception.message ?: "Unknown error"))
            close(exception)
        }
        
        awaitClose { task.isComplete } // just ensures cleanup
    }


    override suspend fun getServices(gymId: String): Flow<ApiResult<List<Service>>> {
        return callbackFlow {
            trySend(ApiResult.Loading)
            serviceRef.whereEqualTo("gymId", gymId).get().addOnSuccessListener { querySnapshot ->
                val equipments = querySnapshot.documents.mapNotNull {
                    it.toObject(Service::class.java)
                }
                trySend(ApiResult.Success(equipments))
            }.addOnFailureListener { exception ->
                trySend(ApiResult.Error(exception.message ?: "Unknown error"))
            }
            awaitClose { }
        }
    }

    override suspend fun uploadBytesToFirebase(
        bytes: ByteArray, pathPrefix: String
    ): String = withContext(Dispatchers.IO) {
        val storage = Firebase.storage
        val fileName = "$pathPrefix/${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child(fileName)
        val uploadTask = ref.putBytes(bytes)
        uploadTask.await()
        val downloadUrl = ref.downloadUrl.await()
        downloadUrl.toString()
    }

}