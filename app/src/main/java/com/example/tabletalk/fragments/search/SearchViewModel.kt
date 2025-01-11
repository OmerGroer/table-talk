package com.example.tabletalk.fragments.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabletalk.data.model.User
import com.example.tabletalk.data.repositories.RestaurantRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel : ViewModel() {
    var search = ""
    var previousRestaurantSearch = ""
    val restaurants = MutableLiveData(RestaurantRepository.getInstance().getByIncluding(search))
    val isLoading = MutableLiveData(false)
    val users = MutableLiveData<List<User>>()

    fun searchRestaurants() {
        if (search == previousRestaurantSearch) return

        previousRestaurantSearch = search
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            restaurants.postValue(RestaurantRepository.getInstance().getByIncluding(search))
            withContext(Dispatchers.Main) { isLoading.postValue(false) }
        }
    }
}