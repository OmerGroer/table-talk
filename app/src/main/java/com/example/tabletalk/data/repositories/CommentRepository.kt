package com.example.tabletalk.data.repositories

import android.content.Context
import android.content.SharedPreferences
import com.example.tabletalk.MyApplication
import com.example.tabletalk.data.local.AppLocalDb
import com.example.tabletalk.data.model.Comment
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

class CommentRepository {
    companion object {
        private const val COLLECTION = "comments"
        private const val LAST_UPDATED = "commentsLastUpdated"

        private val commentRepository = CommentRepository()

        fun getInstance(): CommentRepository {
            return commentRepository
        }
    }

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun save(comment: Comment) {
        val documentRef = if (comment.id.isNotEmpty())
            db.collection(COLLECTION).document(comment.id)
        else
            db.collection(COLLECTION).document().also { comment.id = it.id }

        db.runBatch { batch ->
            batch.set(documentRef, comment)
            batch.update(documentRef, Comment.TIMESTAMP_KEY, FieldValue.serverTimestamp())
        }.await()

        refresh()
    }

    suspend fun delete(commentId: String, onError: () -> Unit) {
        try {
            db.collection(COLLECTION).document(commentId).delete().await()
            AppLocalDb.getInstance().commentDao().delete(commentId)
        } catch (e: Exception) {
            onError()
        }
    }

    suspend fun refresh() {
        var time: Long = getLastUpdate()

        val comments = db.collection(COLLECTION)
            .whereGreaterThanOrEqualTo(Comment.TIMESTAMP_KEY, Timestamp(Date(time)))
            .get().await().documents.map { document -> document.data?.let { Comment.fromJSON(it).apply { id = document.id } }}

        for (comment in comments) {
            if (comment == null) continue

            AppLocalDb.getInstance().commentDao().insertAll(comment)
            val lastUpdated = comment.lastUpdated
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