package com.example.tabletalk.fragments.postsList

import androidx.lifecycle.viewModelScope
import com.example.tabletalk.data.repositories.InflatedPostRepository
import com.example.tabletalk.utils.ImageLoaderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostsListViewModel : ImageLoaderViewModel() {
    val posts = InflatedPostRepository.getInstance().getAll()
    val isLoading = InflatedPostRepository.getInstance().getIsLoading()

    fun fetchPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            InflatedPostRepository.getInstance().refresh()
        }
    }
}