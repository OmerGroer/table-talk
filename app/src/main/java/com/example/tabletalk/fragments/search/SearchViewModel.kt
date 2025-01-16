package com.example.tabletalk.fragments.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tabletalk.data.repositories.RestaurantRepository
import com.example.tabletalk.data.repositories.UserRepository
import com.example.tabletalk.utils.ImageLoaderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel : ImageLoaderViewModel() {
    var searchType: SearchType = SearchType.RESTAURANTS
    var search = ""
    private var previousRestaurantSearch = ""
    private var previousUserSearch = ""
    val restaurants = MutableLiveData(RestaurantRepository.getInstance().getByIncluding(search))
    val users = MutableLiveData(UserRepository.getInstance().getByIncluding(search))
    val isLoading = MutableLiveData(false)
    val isRefreshing = MutableLiveData(false)

    init {
        refresh()
    }

    fun refresh() {
        isRefreshing.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            RestaurantRepository.getInstance().refresh()
            UserRepository.getInstance().refresh()
            withContext(Dispatchers.Main) { isRefreshing.postValue(false) }
        }
    }

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