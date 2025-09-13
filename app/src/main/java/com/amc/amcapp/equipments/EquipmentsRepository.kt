package com.amc.amcapp.equipments

import com.amc.amcapp.Complaint
import com.amc.amcapp.Equipment
import com.amc.amcapp.Gym
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


interface IEquipmentsRepository {
    suspend fun addEquipment(equipment: Equipment): Flow<ApiResult<Equipment>>
    suspend fun getEquipments(gymId: String): Flow<ApiResult<List<Equipment>>>
    suspend fun uploadBytesToFirebase(bytes: ByteArray, pathPrefix: String = "images"): String
}

class EquipmentsRepository(database: FirebaseFirestore = Firebase.firestore) :
    IEquipmentsRepository {

    private val gymRef = database.collection("equipments")
    override suspend fun addEquipment(equipment: Equipment): Flow<ApiResult<Equipment>> =
        callbackFlow {
            trySend(ApiResult.Loading)

            val task = if (equipment.id.isNotEmpty()) {
                // Update existing
                gymRef.document(equipment.id).set(equipment).addOnSuccessListener {
                        trySend(ApiResult.Success(equipment))
                        close() // complete flow
                    }.addOnFailureListener { exception ->
                        trySend(ApiResult.Error(exception.message ?: "Unknown error"))
                        close(exception)
                    }
            } else {
                // Add new
                val ref = gymRef.document().id
                val equipment = equipment.copy(id = ref)
                gymRef.document(equipment.id).set(equipment).addOnSuccessListener { documentReference ->
                        trySend(ApiResult.Success(equipment))
                        close()
                    }.addOnFailureListener { exception ->
                        trySend(ApiResult.Error(exception.message ?: "Unknown error"))
                        close(exception)
                    }
            }

            awaitClose { task.isComplete } // just ensures cleanup
        }


    override suspend fun getEquipments(gymId: String): Flow<ApiResult<List<Equipment>>> {
        return callbackFlow {
            trySend(ApiResult.Loading)
            gymRef.whereEqualTo("gymId", gymId).get().addOnSuccessListener { querySnapshot ->
                val equipments = querySnapshot.documents.mapNotNull {
                    it.toObject(Equipment::class.java)
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
        val taskSnapshot =
            uploadTask.await() // extension available if using kotlinx-coroutines-play-services OR you can use Tasks API with await
        val downloadUrl = ref.downloadUrl.await()
        downloadUrl.toString()
    }

}