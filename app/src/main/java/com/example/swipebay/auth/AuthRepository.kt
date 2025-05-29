package com.example.swipebay.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String, firstName: String, lastName: String, userName: String, region: String): Result<Unit> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val user = auth.currentUser
            user?.let {
                val db = Firebase.firestore
                val userData = hashMapOf(
                    "email" to email,
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "profileImageUrl" to "",
                    "region" to region,
                    "uid" to it.uid,
                    "username" to userName,
                    "createdAt" to System.currentTimeMillis(),
                    "bio" to ""
                )
                db.collection("users").document(it.uid).set(userData).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}