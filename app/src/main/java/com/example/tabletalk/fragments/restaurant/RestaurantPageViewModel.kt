package com.example.tabletalk.fragments.restaurant

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tabletalk.data.model.Restaurant
import com.example.tabletalk.data.repositories.InflatedPostRepository
import com.example.tabletalk.data.repositories.RestaurantRepository
import com.example.tabletalk.utils.ImageLoaderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RestaurantPageViewModel(private val restaurantId: String) : ImageLoaderViewModel() {
    val posts = InflatedPostRepository.getInstance().getByRestaurantId(restaurantId)
    val restaurantData: LiveData<Restaurant> = RestaurantRepository.getInstance().getByIdLiveData(restaurantId)

    val isLoadingPosts = InflatedPostRepository.getInstance().getIsLoading()
    val isLoadingRestaurant = MutableLiveData(false)

    fun fetchPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            InflatedPostRepository.getInstance().refresh()
        }
    }
}