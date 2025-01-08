package com.example.tabletalk.data.repositories

import androidx.core.net.toUri
import com.example.tabletalk.data.local.AppLocalDb
import com.example.tabletalk.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

const val USERS_COLLECTION = "users"

interface AuthListener {
    fun onAuthStateChanged()
}

class UserRepository {
    companion object {
        private val userRepository = UserRepository()

        fun getInstance(): UserRepository {
            return userRepository
        }
    }

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val authListeners: MutableList<AuthListener> = mutableListOf()
    private var isInUserCreation: Boolean = false

    suspend fun createUser(email: String, password: String, username: String, avatarUri: String) {
        isInUserCreation = true

        createAuthUser(email, password)
        val user = User(
            id = getLoggedUserId()!!,
            username = username,
            email = email,
            avatarUrl = avatarUri
        )
        saveUser(user)
        saveUserImage(user.avatarUrl!!, user.id)

        isInUserCreation = false

        withContext(Dispatchers.Main) {
            authListeners.forEach { it.onAuthStateChanged() }
        }
    }

    private suspend fun saveUser(user: User) {
        db.collection(USERS_COLLECTION)
            .document(user.id)
            .set(user)
            .await()

        AppLocalDb.getInstance().userDao().insertAll(user)
    }

    suspend fun getUserByUserId(userId: String): User? {
        var user = AppLocalDb.getInstance().userDao().getUserByUserId(userId)

        if (user != null) return user.apply { avatarUrl = ImageRepository.getInstance().getImagePathById(userId) }

        user = getUserFromFireStore(userId)

        if (user == null) return null

        AppLocalDb.getInstance().userDao().insertAll(user)

        return user.apply { avatarUrl = ImageRepository.getInstance().getImagePathById(userId) }
    }

    private suspend fun getUserFromFireStore(userId: String): User? {
        val user = db.collection(USERS_COLLECTION)
            .document(userId)
            .get()
            .await()
            .toObject(User::class.java)
        user?.avatarUrl = ImageRepository.getInstance().downloadAndCacheImage(ImageRepository.getInstance().getImageRemoteUri(userId), userId)

        return user
    }

    private suspend fun createAuthUser(email: String, password: String) {
        val task = auth.createUserWithEmailAndPassword(email, password).await()
        if (task.user?.uid == null) throw Exception("User not created")
    }

    fun getLoggedUserId(): String? {
        return auth.currentUser?.uid
    }

    fun isLogged(): Boolean {
        return !isInUserCreation && auth.currentUser != null
    }

    suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    fun logout() {
        auth.signOut()
    }

    fun addAuthStateListener(listener: AuthListener) {
        auth.addAuthStateListener {
            listener.onAuthStateChanged()
        }
        authListeners.add(listener)
    }

    private suspend fun saveUserImage(imageUri: String, userId: String) =
        ImageRepository.getInstance().uploadImage(imageUri.toUri(), userId)
}