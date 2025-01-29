package com.example.tabletalk.data.model

import com.example.tabletalk.data.services.restaurantsApi.ApiRestaurant

data class Recommendation (
    val restaurant: ApiRestaurant,
    val review: String,
    val rating: Int
)