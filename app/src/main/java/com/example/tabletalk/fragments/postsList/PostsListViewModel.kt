package com.example.tabletalk.fragments.postsList

import androidx.lifecycle.ViewModel
import com.example.tabletalk.data.repositories.InflatedPostRepository

class PostsListViewModel : ViewModel() {
    val posts = InflatedPostRepository.getInstance().getAll()
}