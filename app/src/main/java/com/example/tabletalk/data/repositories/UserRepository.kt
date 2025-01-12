package com.example.tabletalk.data.repositories

import android.accounts.AuthenticatorException
import android.content.Context
import android.content.SharedPreferences
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import com.example.tabletalk.MyApplication
import com.example.tabletalk.data.local.AppLocalDb
import com.example.tabletalk.data.model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

interface AuthListener {
    fun onAuthStateChanged()
}

class UserRepository {
    companion object {
        private const val COLLECTION = "users"
        private const val LAST_UPDATED = "usersLastUpdated"

        private val userRepository = UserRepository()

        fun getInstance(): UserRepository {
            return userRepository
        }
    }

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val imageRepository = ImageRepository(COLLECTION)
    private val authListeners: MutableList<AuthListener> = mutableListOf()
    private var isInUserCreation: Boolean = false

    suspend fun create(email: String, password: String, username: String, avatarUri: String) {
        isInUserCreation = true

        createAuthUser(email, password)
        val user = User(
            id = getLoggedUserId()!!,
            username = username,
            email = email,
            avatarUrl = avatarUri
        )
        save(user)
        saveImage(user.avatarUrl!!, user.id)

        isInUserCreation = false

        withContext(Dispatchers.Main) {
            authListeners.forEach { it.onAuthStateChanged() }
        }
    }

    suspend fun update(oldPassword: String, password: String, username: String, avatarUri: String) {
        if (password.isNotEmpty()) {
            if (oldPassword.isEmpty()) {
                throw AuthenticatorException("Old password is required")
            }

            auth.signInWithEmailAndPassword(getLoggedUserEmail()!!, oldPassword).await()
            auth.currentUser?.updatePassword(password)?.await()
        }

        val user = User(
            id = getLoggedUserId()!!,
            username = username,
            email = getLoggedUserEmail()!!,
            avatarUrl = if (!avatarUri.startsWith("file:///")) "file://${avatarUri}" else avatarUri
        )

        save(user)
        saveImage(user.avatarUrl!!, user.id)
    }

    private suspend fun save(user: User) {
        val documentRef = db.collection(COLLECTION).document(user.id)

        db.runBatch { batch ->
            batch.set(documentRef, user)
            batch.update(documentRef, User.IMAGE_URI_KEY, null)
            batch.update(documentRef, User.TIMESTAMP_KEY, FieldValue.serverTimestamp())
        }.await()

        AppLocalDb.getInstance().userDao().insertAll(user)
    }

    suspend fun getById(userId: String): User? {
        var user = AppLocalDb.getInstance().userDao().getById(userId)

        if (user == null) {
            user = db.collection(COLLECTION)
                .document(userId)
                .get()
                .await().let { document -> document.data?.let { User.fromJSON(it).apply { id = document.id } } }
            user?.avatarUrl = imageRepository.downloadAndCacheImage(imageRepository.getImageRemoteUri(userId), userId)

            if (user == null) return null

            AppLocalDb.getInstance().userDao().insertAll(user)
        }

        return user.apply { avatarUrl = imageRepository.getImagePathById(userId) }
    }

    fun getByIncluding(searchString: String): LiveData<List<User>> {
        return AppLocalDb.getInstance().userDao().getByIncluding(searchString)
    }

    private suspend fun createAuthUser(email: String, password: String) {
        val task = auth.createUserWithEmailAndPassword(email, password).await()
        if (task.user?.uid == null) throw Exception("User not created")
    }

    fun getLoggedUserId(): String? {
        return auth.currentUser?.uid
    }

    fun getLoggedUserEmail(): String? {
        return auth.currentUser?.email
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

    private suspend fun saveImage(imageUri: String, userId: String) =
        imageRepository.upload(imageUri.toUri(), userId)

    @Synchronized
    fun refresh() {
        var time: Long = getLastUpdate()

        val users = runBlocking {
            db.collection(COLLECTION)
                .whereGreaterThanOrEqualTo(User.TIMESTAMP_KEY, Timestamp(time, 0))
                .get().await().documents.map { document -> document.data?.let { User.fromJSON(it).apply { id = document.id } }}
        }

        for (user in users) {
            if (user == null) continue

            user.avatarUrl = runBlocking {
                imageRepository.downloadAndCacheImage(imageRepository.getImageRemoteUri(user.id), user.id)
            }

            AppLocalDb.getInstance().userDao().insertAll(user)
            val lastUpdated = user.lastUpdated
            if (lastUpdated != null && lastUpdated > time) {
                time = lastUpdated
            }
        }

        setLastUpdate(time)
    }

    private fun getLastUpdate(): Long {
        val sharedPef: SharedPreferences = MyApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE)
        return sharedPef.getLong(LAST_UPDATED, 0)
    }

    private fun setLastUpdate(time: Long) {
        MyApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE)
            .edit().putLong(LAST_UPDATED, time).apply()
    }
}