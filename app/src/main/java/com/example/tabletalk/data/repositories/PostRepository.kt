package com.example.tabletalk.data.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.core.net.toUri
import com.example.tabletalk.MyApplication
import com.example.tabletalk.data.local.AppLocalDb
import com.example.tabletalk.data.model.Post
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PostRepository {
    companion object {
        private const val COLLECTION = "posts"
        private const val LAST_UPDATED = "postsLastUpdated"

        private val postRepository = PostRepository()

        fun getInstance(): PostRepository {
            return postRepository
        }
    }

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val imageRepository = ImageRepository(COLLECTION)

    suspend fun save(post: Post) {
        val documentRef = if (post.id.isNotEmpty())
            db.collection(COLLECTION).document(post.id)
        else
            db.collection(COLLECTION).document().also { post.id = it.id }

        db.runBatch { batch ->
            batch.set(documentRef, post)
            batch.update(documentRef, "restaurantUrl", null)
            batch.update(documentRef, "lastUpdated", FieldValue.serverTimestamp())
        }.await()
        uploadImage(post.restaurantUrl, post.id)
    }

    private suspend fun uploadImage(imageUri: String, reviewId: String) {
        imageRepository.upload(imageUri.toUri(), reviewId)
    }

    suspend fun getById(id: String): Post? {
        var post = AppLocalDb.getInstance().postDao().getById(id)

        if (post == null) {
            post = db.collection(COLLECTION)
                .document(id)
                .get()
                .await()
                .toObject(Post::class.java)

            post?.restaurantUrl = imageRepository.downloadAndCacheImage(
                imageRepository.getImageRemoteUri(id),
                id
            )

            if (post == null) return null

            AppLocalDb.getInstance().postDao().insertAll(post)
        }

        return post.apply { restaurantUrl = imageRepository.getImagePathById(id) }
    }

    suspend fun refresh() {
        var time: Long = getLastUpdate()

        val posts = db.collection(COLLECTION)
            .whereGreaterThanOrEqualTo(Post.TIMESTAMP_KEY, Timestamp(time, 0))
            .get().await().documents.map { document -> document.data?.let { Post.fromJSON(it).apply { id = document.id } }}

        for (post in posts) {
            if (post == null) continue
            AppLocalDb.getInstance().postDao().insertAll(post)
            val lastUpdated = post.lastUpdated
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