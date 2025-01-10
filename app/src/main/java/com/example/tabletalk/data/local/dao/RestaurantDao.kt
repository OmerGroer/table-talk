package com.example.tabletalk.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tabletalk.data.model.Restaurant

@Dao
interface RestaurantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg restaurant: Restaurant)

    @Query("SELECT * FROM restaurants WHERE id = :restaurantId")
    fun getById(restaurantId: String): Restaurant?
}