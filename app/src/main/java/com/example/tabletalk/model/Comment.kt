package com.example.tabletalk.model

class Comment (
    var id: String,
    var userEmail: String,
    var userName: String,
    var avatarUrl: String,
    var content: String,
    var postId: String,
    var lastUpdated: Long?
)