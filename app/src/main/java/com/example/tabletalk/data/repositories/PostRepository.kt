package com.example.tabletalk.data.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.core.net.toUri
import com.example.tabletalk.MyApplication
import com.example.tabletalk.data.local.AppLocalDb
import com.example.tabletalk.data.model.Post
import com.example.tabletalk.data.model.Restaurant
import com.example.tabletalk.utils.ImageLoader
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.concurrent.Executors

class PostRepository : ImageLoader {
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
    private val executor = Executors.newSingleThreadExecutor()

    suspend fun save(post: Post, restaurant: Restaurant?) {
        val isOldPost = post.id.isNotEmpty()
        val documentRef = if (isOldPost)
            db.collection(COLLECTION).document(post.id)
        else
            db.collection(COLLECTION).document().also { post.id = it.id }

        db.runTransaction { transaction ->
            if (isOldPost) {
                val postDB = transaction.get(documentRef)
                val oldRating = (postDB.getDouble(Post.RATING_KEY) ?: 0.0).toInt()
                RestaurantRepository.getInstance().save(post.restaurantId, post.rating, oldRating, transaction)
            } else {
                if (restaurant != null) {
                    RestaurantRepository.getInstance().save(restaurant, post.rating, transaction)
                }
            }

            transaction.set(documentRef, post)
            transaction.update(documentRef, Post.IMAGE_URI_KEY, null)
            transaction.update(documentRef, Post.TIMESTAMP_KEY, FieldValue.serverTimestamp())
        }.await()
        uploadImage(post.restaurantUrl, post.id)
    }

    private suspend fun uploadImage(imageUri: String, reviewId: String) {
        imageRepository.upload(imageUri.toUri(), reviewId)
    }

    suspend fun getById(postId: String): Post? {
        var post = AppLocalDb.getInstance().postDao().getById(postId)

        if (post == null) {
            post = db.collection(COLLECTION)
                .document(postId)
                .get()
                .await().let { document -> document.data?.let { Post.fromJSON(it).apply { id = document.id } } }

            post?.restaurantUrl = imageRepository.downloadAndCacheImage(
                imageRepository.getImageRemoteUri(postId),
                postId
            )

            if (post == null) return null

            AppLocalDb.getInstance().postDao().insertAll(post)
        }

        return post.apply { restaurantUrl = imageRepository.getImagePathById(postId) }
    }

    override suspend fun getImagePath(imageId: String): String {
        return imageRepository.getImagePathById(imageId)
    }

    suspend fun getByRestaurantIdAndUserId(restaurantId: String, userId: String): Post? {
        var post = AppLocalDb.getInstance().postDao().getByRestaurantIdAndUserId(restaurantId, userId)

        if (post == null) {
            val posts = db.collection(COLLECTION)
                .whereEqualTo(Post.RESTAURANT_ID_KEY, restaurantId)
                .whereEqualTo(Post.USER_ID_KEY, userId)
                .get()
                .await().documents.map { document -> document.data?.let { Post.fromJSON(it).apply { id = document.id } }}
            if (posts.size != 1) return null
            post = posts[0]
            if (post == null) return null
            val postId = post.id

            post.restaurantUrl = imageRepository.downloadAndCacheImage(
                imageRepository.getImageRemoteUri(postId),
                postId
            )

            AppLocalDb.getInstance().postDao().insertAll(post)
        }

        val postId = post.id

        return post.apply { restaurantUrl = imageRepository.getImagePathById(postId) }
    }

    fun delete(postId: String, onError: () -> Unit) {
        executor.submit {
            runBlocking {
                try {
                    val post = getById(postId) ?: return@runBlocking
                    db.collection(COLLECTION).document(postId).delete().await()
                    RestaurantRepository.getInstance().delete(post.restaurantId, post.rating)
                    AppLocalDb.getInstance().postDao().delete(postId)
                    imageRepository.delete(postId)
                } catch (e: Exception) {
                    onError()
                }
            }
        }
    }

    suspend fun refresh() {
        var time: Long = getLastUpdate()

        val posts = db.collection(COLLECTION)
            .whereGreaterThanOrEqualTo(Post.TIMESTAMP_KEY, Timestamp(Date(time)))
            .get().await().documents.map { document -> document.data?.let { Post.fromJSON(it).apply { id = document.id } }}

        for (post in posts) {
            if (post == null) continue

            imageRepository.deleteLocal(post.id)
            AppLocalDb.getInstance().postDao().insertAll(post)
            val lastUpdated = post.lastUpdated
            if (lastUpdated != null && lastUpdated > time) {
                time = lastUpdated
            }
        }

        setLastUpdate(time + 1)
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