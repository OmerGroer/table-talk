package com.example.tabletalk.fragments.postsList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabletalk.data.repositories.InflatedPostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostsListViewModel : ViewModel() {
    val posts = InflatedPostRepository.getInstance().getAll()

    init {
        fetch()
    }

    private fun fetch() {
        viewModelScope.launch(Dispatchers.IO) {
            InflatedPostRepository.getInstance().refresh()
        }
    }
}