package com.example.tabletalk.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.tabletalk.data.model.InflatedComment

@Dao
interface InflatedCommentDao {
    @Query("SELECT * FROM inflatedComments WHERE postId = :id")
    fun getByPostId(id: String): LiveData<List<InflatedComment>>
}