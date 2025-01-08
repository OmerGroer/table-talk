package com.example.tabletalk.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
class Image(
    @PrimaryKey
    val id: String = "",
    val uri: String = ""
)
