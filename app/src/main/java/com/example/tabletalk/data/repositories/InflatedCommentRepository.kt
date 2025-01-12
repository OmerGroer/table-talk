package com.example.tabletalk.data.repositories

import androidx.lifecycle.LiveData
import com.example.tabletalk.data.local.AppLocalDb
import com.example.tabletalk.data.model.InflatedComment
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class InflatedCommentRepository {
    companion object {
        private val inflatedCommentRepository = InflatedCommentRepository()

        fun getInstance(): InflatedCommentRepository {
            return inflatedCommentRepository
        }
    }

    private val executor = Executors.newSingleThreadExecutor()
    var pool = ThreadPoolExecutor(
        1, 1, 0,
        TimeUnit.SECONDS, LinkedBlockingQueue(1), ThreadPoolExecutor.DiscardPolicy()
    )

    fun getByPostId(id: String): LiveData<List<InflatedComment>> {
        refresh()
        return AppLocalDb.getInstance().inflatedCommentDao().getByPostId(id)
    }

    fun refresh() {
        pool.execute {
            refreshHandler()
        }
    }

    @Synchronized
    private fun refreshHandler() {
        val futures = listOf(Callable {
            runBlocking {
                CommentRepository.getInstance().refresh()
            }
        }, Callable {
            runBlocking {
                UserRepository.getInstance().refresh()
            }
        })

        executor.invokeAll(futures)
    }
}