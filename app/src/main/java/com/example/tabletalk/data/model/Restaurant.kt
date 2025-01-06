package com.example.tabletalk.data.model

class Restaurant (
    val id: Int,
    val name: String,
    val rating: Double? = null,
    val category: String? = null,
    val address: String
)