package com.example.tabletalk.data.model

import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.PrimaryKey

@DatabaseView(viewName = "inflatedPosts",
    value = "SELECT posts.*, restaurants.name as restaurantName, users.username as userName, users.avatarUrl as avatarUrl FROM posts " +
            "INNER JOIN restaurants ON posts.restaurantId = restaurants.id " +
            "INNER JOIN users ON posts.userId = users.id " +
            "ORDER BY posts.lastUpdated DESC")
data class InflatedPost (
    @PrimaryKey
    var id: String = "",
    var userId: String = "",
    var userName: String = "",
    var restaurantId: String = "",
    var restaurantName: String = "",
    var review: String = "",
    var restaurantUrl: String = "",
    var avatarUrl: String = "",
    var rating: Int = 0,
    var lastUpdated: Long? = null
)