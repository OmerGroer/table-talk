package com.example.tabletalk.data.repositories

import com.example.tabletalk.data.local.AppLocalDb
import com.example.tabletalk.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

const val USERS_COLLECTION = "users"

class UserRepository {
    companion object {
        private val userRepository = UserRepository()

        fun getInstance(): UserRepository {
            return userRepository
        }
    }

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val localDb get() = AppLocalDb.database

    suspend fun saveUserInDB(user: User) {
        db.collection(USERS_COLLECTION)
            .document(user.id)
            .set(user)
            .await()

        localDb.userDao().insertAll(user)
    }

    suspend fun getUserByUserId(userId: String): User? {
        var user = localDb.userDao().getUserByUserId(userId)

        if (user != null) return user

        user = getUserFromFireStore(userId)

        if (user == null) return null

        localDb.userDao().insertAll(user)

        return user
    }

    private suspend fun getUserFromFireStore(userId: String): User? {
        val user = db.collection(USERS_COLLECTION)
            .document(userId)
            .get()
            .await()
            .toObject(User::class.java)

        return user
    }

    suspend fun createAuthUser(email: String, password: String) {
        val task = auth.createUserWithEmailAndPassword(email, password).await()
        if (task.user?.uid == null) throw Exception("User not created")
    }

    fun getLoggedUserId(): String? {
        return auth.currentUser?.uid
    }

    fun isLogged(): Boolean {
        return auth.currentUser != null
    }

    suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    fun logout() {
        auth.signOut()
    }

    fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        auth.addAuthStateListener(listener)
    }
}