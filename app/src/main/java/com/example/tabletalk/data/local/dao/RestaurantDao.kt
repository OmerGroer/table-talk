package com.example.tabletalk.data.local.dao

import androidx.lifecycle.LiveData
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

    @Query("SELECT * FROM restaurants WHERE id = :restaurantId")
    fun getByIdLiveData(restaurantId: String): LiveData<Restaurant>

    @Query("SELECT * FROM restaurants " +
            "WHERE name LIKE '%' || :searchString || '%' " +
            "OR address LIKE '%' || :searchString || '%' " +
            "OR category LIKE '%' || :searchString || '%' " +
            "ORDER BY lastUpdated DESC")
    fun getByIncluding(searchString: String): LiveData<List<Restaurant>>

    @Query("DELETE FROM restaurants WHERE id = :restaurantId")
    fun delete(restaurantId: String)
}