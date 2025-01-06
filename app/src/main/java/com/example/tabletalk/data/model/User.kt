package com.example.tabletalk.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
class User (
    @PrimaryKey val id: String,
    val email: String,
    val username: String,
    val avatarUrl: String,
    val lastUpdated: Long?
)