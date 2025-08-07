package com.amc.amcapp.equipments

import com.amc.amcapp.Equipment
import com.amc.amcapp.Gym
import com.amc.amcapp.ui.ApiResult
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


interface IEquipmentsRepository {
    suspend fun addEquipment(equipment: Equipment): Flow<ApiResult<Equipment>>
    suspend fun getEquipments(gymId: String): Flow<ApiResult<List<Equipment>>>
}

class EquipmentsRepository(database: FirebaseFirestore = Firebase.firestore) :
    IEquipmentsRepository {

    private val gymRef = database.collection("equipments")
    override suspend fun addEquipment(equipment: Equipment): Flow<ApiResult<Equipment>> {
        return callbackFlow {
            trySend(ApiResult.Loading)
            gymRef.add(equipment).addOnSuccessListener { documentReference ->
                trySend(ApiResult.Success(equipment.copy(id = documentReference.id)))
            }.addOnFailureListener { exception ->
                trySend(ApiResult.Error(exception.message ?: "Unknown error"))
            }
            awaitClose { }
        }
    }

    override suspend fun getEquipments(gymId: String): Flow<ApiResult<List<Equipment>>> {
        return callbackFlow {
            trySend(ApiResult.Loading)
            gymRef.whereEqualTo("gymId", gymId).get().addOnSuccessListener { querySnapshot ->
                val equipments = querySnapshot.documents.mapNotNull { it.toObject(Equipment::class.java) }
                trySend(ApiResult.Success(equipments))
            }.addOnFailureListener { exception ->
                trySend(ApiResult.Error(exception.message ?: "Unknown error"))
            }
            awaitClose { }
        }
    }

}