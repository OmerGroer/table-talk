package com.example.tabletalk.fragments.restaurant

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabletalk.data.model.Restaurant
import com.example.tabletalk.data.repositories.InflatedPostRepository
import com.example.tabletalk.data.repositories.RestaurantRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RestaurantPageViewModel(private val restaurantId: String) : ViewModel() {
    val posts = InflatedPostRepository.getInstance().getByRestaurantId(restaurantId)
    val restaurantData: MutableLiveData<LiveData<Restaurant>> = MutableLiveData(null)

    val isLoading = MutableLiveData(false)
    var wasLoaded = false

    init {
        fetchRestaurant()
    }

    fun fetchRestaurant() {
        isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val restaurant = RestaurantRepository.getInstance().getById(restaurantId) ?: throw Exception("Restaurant not found")
                withContext(Dispatchers.Main) {
                    restaurantData.value = restaurant
                    wasLoaded = true
                }
            } catch (e: Exception) {
                Log.e("Restaurant Page", "Error fetching restaurant", e)
            } finally {
                withContext(Dispatchers.Main) { isLoading.value = false }
            }
        }
    }
}