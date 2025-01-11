package com.example.tabletalk.fragments.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabletalk.data.repositories.RestaurantRepository
import com.example.tabletalk.data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel : ViewModel() {
    var search = ""
    private var previousRestaurantSearch = ""
    private var previousUserSearch = ""
    val restaurants = MutableLiveData(RestaurantRepository.getInstance().getByIncluding(search))
    val users = MutableLiveData(UserRepository.getInstance().getByIncluding(search))
    val isLoading = MutableLiveData(false)

    fun searchRestaurants() {
        if (search == previousRestaurantSearch) return

        previousRestaurantSearch = search
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            restaurants.postValue(RestaurantRepository.getInstance().getByIncluding(search))
            withContext(Dispatchers.Main) { isLoading.postValue(false) }
        }
    }

    fun searchUser() {
        if (search == previousUserSearch) return

        previousUserSearch = search
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            users.postValue(UserRepository.getInstance().getByIncluding(search))
            withContext(Dispatchers.Main) { isLoading.postValue(false) }
        }
    }
}