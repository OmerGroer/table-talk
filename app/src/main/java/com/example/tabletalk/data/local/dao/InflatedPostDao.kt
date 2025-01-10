package com.example.tabletalk.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.tabletalk.data.model.InflatedPost

@Dao
interface InflatedPostDao {
    @Query("SELECT * FROM inflatedPosts")
    fun getAll(): LiveData<List<InflatedPost>>
}