package com.example.tabletalk.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tabletalk.data.model.Post

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg review: Post)

    @Query("SELECT * FROM posts WHERE id = :postId")
    fun getById(postId: String): Post?
}