package com.example.tabletalk.fragments.postsList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tabletalk.data.model.Recommendation
import com.example.tabletalk.data.repositories.InflatedPostRepository
import com.example.tabletalk.data.services.gemini.GeminiApiService
import com.example.tabletalk.utils.ImageLoaderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostsListViewModel : ImageLoaderViewModel() {
    val posts = InflatedPostRepository.getInstance().getAll()
    val isLoading = InflatedPostRepository.getInstance().getIsLoading()
    val recommendation = MutableLiveData<Recommendation>()
    val isRecommendationLoaded = MutableLiveData(false)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val recommendationResult = GeminiApiService.getRecommendations()
            withContext(Dispatchers.Main) {
                recommendation.value = recommendationResult
                isRecommendationLoaded.value = true
            }
        }
    }

    fun fetchPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            InflatedPostRepository.getInstance().refresh()
        }
    }
}