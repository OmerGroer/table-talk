package com.example.tabletalk.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tabletalk.MyApplication
import com.example.tabletalk.data.local.dao.UserDao
import com.example.tabletalk.data.model.User


@Database(entities = [User::class], version = 1)
abstract class AppLocalDbRepository: RoomDatabase() {
    abstract fun userDao(): UserDao
}

object AppLocalDb {
    val database: AppLocalDbRepository by lazy {
        val context = MyApplication.Globals.context ?: throw IllegalStateException("Application context is missing")
        Room.databaseBuilder(
            context = context,
            klass = AppLocalDbRepository::class.java,
            name = "dbFileName.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}