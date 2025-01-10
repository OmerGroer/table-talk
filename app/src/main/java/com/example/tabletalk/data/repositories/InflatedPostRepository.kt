package com.example.tabletalk.data.repositories

import androidx.lifecycle.LiveData
import com.example.tabletalk.data.local.AppLocalDb
import com.example.tabletalk.data.model.InflatedPost

class InflatedPostRepository {
    companion object {
        private val inflatedPostRepository = InflatedPostRepository()

        fun getInstance(): InflatedPostRepository {
            return inflatedPostRepository
        }
    }

    fun getAll(): LiveData<List<InflatedPost>> {
        return AppLocalDb.getInstance().inflatedPostDao().getAll()
    }

    suspend fun refresh() {
        PostRepository.getInstance().refresh()
        RestaurantRepository.getInstance().refresh()
        UserRepository.getInstance().refresh()
    }
}