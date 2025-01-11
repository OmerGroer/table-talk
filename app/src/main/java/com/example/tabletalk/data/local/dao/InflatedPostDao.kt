package com.example.tabletalk.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.tabletalk.data.model.InflatedPost

@Dao
interface InflatedPostDao {
    @Query("SELECT * FROM inflatedPosts WHERE userId != :loggedUserId ")
    fun getAll(loggedUserId: String): LiveData<List<InflatedPost>>

    @Query("SELECT * FROM inflatedPosts WHERE userId = :id")
    fun getByUserId(id: String): LiveData<List<InflatedPost>>

    @Query("SELECT * FROM inflatedPosts WHERE restaurantId = :id AND userId = :loggedUserId " +
            "UNION SELECT * FROM inflatedPosts WHERE restaurantId = :id AND userId != :loggedUserId")
    fun getByRestaurantId(id: String, loggedUserId: String): LiveData<List<InflatedPost>>
}