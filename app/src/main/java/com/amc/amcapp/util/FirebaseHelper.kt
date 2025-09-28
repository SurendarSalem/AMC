package com.amc.amcapp.util

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseHelper {

    suspend fun uploadImageToFirebase(imageUri: Uri): String =
        suspendCancellableCoroutine { continuation ->
            val storageRef = FirebaseStorage.getInstance().reference
            val fileName = "images/${UUID.randomUUID()}.jpg"
            val imageRef = storageRef.child(fileName)

            val uploadTask = imageRef.putFile(imageUri)

            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    continuation.resume(uri.toString())
                }.addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
            }.addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
        }
}