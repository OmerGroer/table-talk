package com.example.tabletalk.data.model

class Comment (
    var id: String,
    var userId: String,
    var userName: String,
    var avatarUrl: String,
    var content: String,
    var postId: String,
    var lastUpdated: Long?
)