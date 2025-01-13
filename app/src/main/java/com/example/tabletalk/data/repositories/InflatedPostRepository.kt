package com.example.tabletalk.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tabletalk.data.local.AppLocalDb
import com.example.tabletalk.data.model.InflatedPost
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class InflatedPostRepository {
    companion object {
        private val inflatedPostRepository = InflatedPostRepository()

        fun getInstance(): InflatedPostRepository {
            return inflatedPostRepository
        }
    }

    private val executor = Executors.newSingleThreadExecutor()
    private var pool = ThreadPoolExecutor(
        1, 1, 0,
        TimeUnit.SECONDS, LinkedBlockingQueue(1), ThreadPoolExecutor.DiscardPolicy()
    )
    private val isLoading = MutableLiveData(false)

    fun getAll(): LiveData<List<InflatedPost>> {
        refresh()
        val loggedUserId = UserRepository.getInstance().getLoggedUserId() ?: throw Exception("User not logged in")
        return AppLocalDb.getInstance().inflatedPostDao().getAll(loggedUserId)
    }

    fun getByUserId(userId: String): LiveData<List<InflatedPost>> {
        refresh()
        return AppLocalDb.getInstance().inflatedPostDao().getByUserId(userId)
    }

    fun getByRestaurantId(restaurantId: String): LiveData<List<InflatedPost>> {
        refresh()
        val loggedUserId = UserRepository.getInstance().getLoggedUserId() ?: throw Exception("User not logged in")
        return AppLocalDb.getInstance().inflatedPostDao().getByRestaurantId(restaurantId, loggedUserId)
    }

    fun getIsLoading(): LiveData<Boolean> {
        return isLoading
    }

    fun refresh() {
        pool.execute {
            refreshHandler()
        }
    }

    @Synchronized
    private fun refreshHandler() {
        isLoading.postValue(true)
        val futures = listOf(Callable {
            runBlocking {
                PostRepository.getInstance().refresh()
            }
        }, Callable {
            runBlocking {
                RestaurantRepository.getInstance().refresh()
            }
        }, Callable {
            runBlocking {
                UserRepository.getInstance().refresh()
            }
        })

        executor.invokeAll(futures)
        isLoading.postValue(false)
    }
}