package com.amc.amcapp

import com.amc.amcapp.model.User
import com.amc.amcapp.ui.AuthResult
import com.amc.amcapp.viewmodel.ForgotPasswordResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface IAuthRepository {
    suspend fun signIn(email: String, password: String): Flow<AuthResult>
    suspend fun createUserInFirebase(user: User): Flow<AuthResult>
    suspend fun sendPasswordResetEmail(email: String): Flow<ForgotPasswordResult>
    suspend fun verifyPasswordResetCode(email: String): Flow<ForgotPasswordResult>
    suspend fun signOut()
}

class AuthRepository(private val firebaseAuth: FirebaseAuth) : IAuthRepository {

    override suspend fun signIn(email: String, password: String): Flow<AuthResult> = callbackFlow {
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    trySend(AuthResult.Success(user))
                } else {
                    trySend(AuthResult.Error(task.exception?.message))
                }
            }
        } catch (e: Exception) {
            trySend(AuthResult.Error(e.message))
        }
        awaitClose { }
    }

    override suspend fun createUserInFirebase(user: User): Flow<AuthResult> = callbackFlow {
        try {
            firebaseAuth.createUserWithEmailAndPassword(user.email, user.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = firebaseAuth.currentUser
                        firebaseUser?.apply {
                            user.firebaseId = uid
                        }
                        trySend(AuthResult.Success(firebaseUser))
                    } else {
                        trySend(AuthResult.Error(task.exception?.message))
                    }
                }
        } catch (e: Exception) {
            trySend(AuthResult.Error(e.message))
        }
        awaitClose { }
    }

    override suspend fun sendPasswordResetEmail(email: String): Flow<ForgotPasswordResult> =
        callbackFlow {
            try {
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        trySend(ForgotPasswordResult.CodeSent("Code has been sent to your Email Id"))
                    } else {
                        trySend(ForgotPasswordResult.Error(task.exception?.message))
                    }
                }
            } catch (e: Exception) {
                trySend(ForgotPasswordResult.Error(e.message))
            }
            awaitClose { }
        }


    override suspend fun verifyPasswordResetCode(email: String): Flow<ForgotPasswordResult> =
        callbackFlow {
            try {
                firebaseAuth.verifyPasswordResetCode(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        trySend(ForgotPasswordResult.CodeVerified(user))
                    } else {
                        task.exception?.message?.let {
                            trySend(ForgotPasswordResult.Error(it))
                        } ?: run {
                            trySend(ForgotPasswordResult.Error("Unknown error occurred"))
                        }
                    }
                }
            } catch (e: Exception) {
                trySend(ForgotPasswordResult.Error(e.message))
            }
            awaitClose { }
        }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }
}


