package com.example.tabletalk.model

class Post (
    var id: String,
    var userEmail: String,
    var userName: String,
    var avatarUrl: String,
    var restaurantName: String,
    var review: String,
    var restaurantUrl: String,
    var rating: Int,
    var lastUpdated: Long?
)