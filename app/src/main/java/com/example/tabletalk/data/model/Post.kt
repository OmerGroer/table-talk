package com.example.tabletalk.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
class Post (
    @PrimaryKey
    var id: String = "",
    var userId: String,
    var restaurantId: Int,
    var review: String,
    var restaurantUrl: String,
    var rating: Int,
    var lastUpdated: Long? = null
)