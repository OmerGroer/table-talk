package com.example.tabletalk.fragments.comments

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabletalk.data.model.Comment
import com.example.tabletalk.data.repositories.CommentRepository
import com.example.tabletalk.data.repositories.InflatedCommentRepository
import com.example.tabletalk.data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentsViewModel(private val postId: String) : ViewModel() {
    val comments = InflatedCommentRepository.getInstance().getByPostId(postId)
    val commentInput = MutableLiveData("")
    val commentId = MutableLiveData("")

    val isLoading = MutableLiveData(false)
    val isRefreshing = InflatedCommentRepository.getInstance().getIsLoading()

    fun fetchComments() {
        viewModelScope.launch(Dispatchers.IO) {
            InflatedCommentRepository.getInstance().refresh()
        }
    }

    fun submit(onFailure: (error: Exception?) -> Unit) {
        val text = commentInput.value

        if (text?.isNotEmpty() == true) {
            try {
                isLoading.value = true
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val userId = UserRepository.getInstance().getLoggedUserId() ?: throw Exception("User not logged in")
                        val comment = Comment(
                            postId = postId,
                            content = text,
                            userId = userId,
                            id = commentId.value ?: ""
                        )

                        CommentRepository.getInstance().save(comment)

                        withContext(Dispatchers.Main) {
                            commentId.value = ""
                            commentInput.value = ""
                        }
                    } catch (e: Exception) {
                        Log.e("Add Comment", "Error adding comment", e)
                        withContext(Dispatchers.Main) { onFailure(e) }
                    } finally {
                        withContext(Dispatchers.Main) { isLoading.value = false }
                    }
                }
            } catch (e: Exception) {
                Log.e("Add Comment", "Error adding comment", e)
                onFailure(e)
            }
        }
    }

    fun delete(commentId: String, onError: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            CommentRepository.getInstance().delete(commentId, onError)
        }
    }

    fun edit(commentText: String, commentId: String) {
        this.commentId.value = commentId
        this.commentInput.value = commentText
    }
}