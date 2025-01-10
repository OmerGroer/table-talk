package com.example.tabletalk.data.repositories

import androidx.core.net.toUri
import com.example.tabletalk.data.local.AppLocalDb
import com.example.tabletalk.data.model.Post
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

const val POSTS_COLLECTION = "posts"

class PostRepository {
    companion object {
        private val postRepository = PostRepository()

        fun getInstance(): PostRepository {
            return postRepository
        }
    }

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun savePost(post: Post) {
        val documentRef = if (post.id.isNotEmpty())
            db.collection(POSTS_COLLECTION).document(post.id)
        else
            db.collection(POSTS_COLLECTION).document().also { post.id = it.id }

        db.runBatch { batch ->
            batch.set(documentRef, post)
            batch.update(documentRef, "restaurantUrl", null)
            batch.update(documentRef, "lastUpdated", FieldValue.serverTimestamp())
        }.await()
        uploadImage(post.restaurantUrl, post.id)

        // TODO: refresh all posts
    }

    private suspend fun uploadImage(imageUri: String, reviewId: String) {
        ImageRepository.getInstance().upload(imageUri.toUri(), reviewId)
    }

    suspend fun getById(id: String): Post? {
        var post = AppLocalDb.getInstance().postDao().getById(id)

        if (post == null) {
            post = db.collection(POSTS_COLLECTION)
                .document(id)
                .get()
                .await()
                .toObject(Post::class.java)

            post?.restaurantUrl = ImageRepository.getInstance().downloadAndCacheImage(
                ImageRepository.getInstance().getImageRemoteUri(id),
                id
            )

            if (post == null) return null

            AppLocalDb.getInstance().postDao().insertAll(post)
        }

        return post.apply { restaurantUrl = ImageRepository.getInstance().getImagePathById(id) }
    }
}