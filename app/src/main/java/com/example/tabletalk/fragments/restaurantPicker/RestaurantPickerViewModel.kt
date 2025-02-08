package com.example.tabletalk.fragments.restaurantPicker

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabletalk.data.model.Restaurant
import com.example.tabletalk.data.services.restaurantsApi.RestaurantsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RestaurantPickerViewModel : ViewModel() {
    val restaurants = MutableLiveData<List<Restaurant>>()
    val isLoading = MutableLiveData(false)

    var search = ""

    var endCursor: String? = null
    var hasNextPage: Boolean = false
    var location: String = ""

    fun searchRestaurants(): Boolean {
        if (search.isEmpty()) return false
        if (search != location) fetchRestaurants(search)

        return true
    }

    private fun fetchRestaurants(location: String) {
        isLoading.value = true
        this.location = location
        endCursor = null

        viewModelScope.launch(Dispatchers.IO) {
            restaurants.postValue(fetchRestaurants())
            withContext(Dispatchers.Main) { isLoading.value = false }
        }
    }

    fun loadMoreRestaurants() {
        if (!hasNextPage) return

        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val currentRestaurants = restaurants.value ?: emptyList()
            restaurants.postValue(currentRestaurants + fetchRestaurants())
            withContext(Dispatchers.Main) { isLoading.value = false }
        }
    }

    private suspend fun fetchRestaurants(): List<Restaurant> {
        val restaurantsPage = RestaurantsApiService.getRestaurantsByLocation(location = location, cursor = endCursor)
        endCursor = restaurantsPage.pageInfo.endCursor
        hasNextPage = restaurantsPage.pageInfo.hasNextPage
        return restaurantsPage.data.map { Restaurant(id = it.id.toString(), name = it.name, address = it.address.fullAddress, category = it.cuisines?.joinToString("/"), priceTypes = it.priceTypes) }
    }
}