package com.example.tabletalk.fragments.user

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabletalk.data.repositories.InflatedPostRepository
import com.example.tabletalk.data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(private val userId: String) : ViewModel() {
    val posts = InflatedPostRepository.getInstance().getByUserId(userId)
    val username = MutableLiveData("")
    val avatarUrl = MutableLiveData("")

    val isLoadingPosts = InflatedPostRepository.getInstance().getIsLoading()
    val isLoadingUser = MutableLiveData(false)

    init {
        fetchUser()
    }

    fun fetchUser() {
        isLoadingUser.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = UserRepository.getInstance().getById(userId)
                    ?: throw Exception("User not found")
                withContext(Dispatchers.Main) {
                    username.value = user.username
                    avatarUrl.value = user.avatarUrl
                }
            } catch (e: Exception) {
                Log.e("User Page", "Error fetching user", e)
            } finally {
                withContext(Dispatchers.Main) { isLoadingUser.value = false }
            }
        }
    }

    fun fetchPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            InflatedPostRepository.getInstance().refresh()
        }
    }
}