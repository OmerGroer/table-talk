package com.example.tabletalk.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tabletalk.data.local.dao.ImageDao
import com.example.tabletalk.data.local.dao.PostDao
import com.example.tabletalk.data.local.dao.RestaurantDao
import com.example.tabletalk.data.local.dao.UserDao
import com.example.tabletalk.data.model.Image
import com.example.tabletalk.data.model.Post
import com.example.tabletalk.data.model.Restaurant
import com.example.tabletalk.data.model.User


@Database(entities = [User::class, Image::class, Post::class, Restaurant::class], version = 3)
abstract class AppLocalDbRepository: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun imageDao(): ImageDao
    abstract fun postDao(): PostDao
    abstract fun restaurantDao(): RestaurantDao
}

object AppLocalDb {
    private var database: AppLocalDbRepository? = null

    fun create(context: Context) {
        database = Room.databaseBuilder(
            context = context,
            klass = AppLocalDbRepository::class.java,
            name = "dbFileName.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    fun getInstance(): AppLocalDbRepository {
        return database ?: throw IllegalStateException("App Local DB was not created")
    }
}