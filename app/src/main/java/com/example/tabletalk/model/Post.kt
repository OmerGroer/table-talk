package com.example.tabletalk.model

class Post (
    var id: String,
    var userName: String,
    var avatarUrl: String,
    var restaurantName: String,
    var review: String,
    var restaurantUrl: String,
    var rate: Int,
    var lastUpdated: Long?
)