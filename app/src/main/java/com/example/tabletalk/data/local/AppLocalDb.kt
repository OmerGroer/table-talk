package com.example.tabletalk.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tabletalk.MyApplication
import com.example.tabletalk.data.local.dao.CommentDao
import com.example.tabletalk.data.local.dao.ImageDao
import com.example.tabletalk.data.local.dao.InflatedCommentDao
import com.example.tabletalk.data.local.dao.InflatedPostDao
import com.example.tabletalk.data.local.dao.PostDao
import com.example.tabletalk.data.local.dao.RestaurantDao
import com.example.tabletalk.data.local.dao.UserDao
import com.example.tabletalk.data.model.Comment
import com.example.tabletalk.data.model.Image
import com.example.tabletalk.data.model.InflatedComment
import com.example.tabletalk.data.model.InflatedPost
import com.example.tabletalk.data.model.Post
import com.example.tabletalk.data.model.Restaurant
import com.example.tabletalk.data.model.User


@Database(
    entities = [User::class, Image::class, Post::class, Restaurant::class, Comment::class],
    views = [InflatedPost::class, InflatedComment::class],
    version = 8
)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun imageDao(): ImageDao
    abstract fun postDao(): PostDao
    abstract fun restaurantDao(): RestaurantDao
    abstract fun inflatedPostDao(): InflatedPostDao
    abstract fun commentDao(): CommentDao
    abstract fun inflatedCommentDao(): InflatedCommentDao
}

object AppLocalDb {
    private val database: AppLocalDbRepository by lazy {
        Room.databaseBuilder(
            context = MyApplication.context,
            klass = AppLocalDbRepository::class.java,
            name = "dbFileName.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    fun getInstance(): AppLocalDbRepository {
        return database
    }
}