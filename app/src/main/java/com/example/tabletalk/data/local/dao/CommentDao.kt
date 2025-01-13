package com.example.tabletalk.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tabletalk.data.model.Comment

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg comment: Comment)

    @Query("DELETE FROM comments WHERE id = :commentId")
    fun delete(commentId: String)
}