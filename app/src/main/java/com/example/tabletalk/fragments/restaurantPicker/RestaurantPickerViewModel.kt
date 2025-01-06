package com.example.tabletalk.fragments.restaurantPicker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabletalk.data.model.Restaurant
import com.example.tabletalk.data.services.RestaurantsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RestaurantPickerViewModel : ViewModel() {
    private val _restaurants = MutableLiveData<List<Restaurant>>()
    val restaurants: LiveData<List<Restaurant>> get() = _restaurants
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

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
        _isLoading.value = true
        this.location = location
        endCursor = null

        viewModelScope.launch(Dispatchers.IO) {
            _restaurants.postValue(fetchRestaurants())
            withContext(Dispatchers.Main) { _isLoading.value = false }
        }
    }

    fun loadMoreRestaurants() {
        if (!hasNextPage) return

        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val currentMovies = _restaurants.value ?: emptyList()
            _restaurants.postValue(currentMovies + fetchRestaurants())
            withContext(Dispatchers.Main) { _isLoading.value = false }
        }
    }

    private suspend fun fetchRestaurants(): List<Restaurant> {
        val restaurantsPage = RestaurantsApiService.getRestaurantsByLocation(location = location, cursor = endCursor)
        endCursor = restaurantsPage.pageInfo.endCursor
        hasNextPage = restaurantsPage.pageInfo.hasNextPage
        return restaurantsPage.data.map { Restaurant(id = it.id, name = it.name, address = it.address.fullAddress, category = it.cuisines?.joinToString("/")) }
    }
}