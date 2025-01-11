package com.example.tabletalk.fragments.restaurant

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabletalk.data.repositories.InflatedPostRepository
import com.example.tabletalk.data.repositories.RestaurantRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RestaurantPageViewModel(private val restaurantId: String) : ViewModel() {
    val posts = InflatedPostRepository.getInstance().getByRestaurantId(restaurantId)
    val restaurantName = MutableLiveData("")
    val priceTypes = MutableLiveData("")
    val category = MutableLiveData("")
    val address = MutableLiveData("")
    val rating = MutableLiveData(0.0)

    val isLoading = MutableLiveData(false)

    init {
        fetchRestaurant()
    }

    fun fetchRestaurant() {
        isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val restaurant = RestaurantRepository.getInstance().getById(restaurantId)
                    ?: throw Exception("Restaurant not found")
                withContext(Dispatchers.Main) {
                    restaurantName.value = restaurant.name
                    rating.value = restaurant.rating
                    priceTypes.value = restaurant.priceTypes
                    category.value = restaurant.category
                    address.value = restaurant.address
                }
            } catch (e: Exception) {
                Log.e("Restaurant Page", "Error fetching restaurant", e)
            } finally {
                withContext(Dispatchers.Main) { isLoading.value = false }
            }
        }
    }
}