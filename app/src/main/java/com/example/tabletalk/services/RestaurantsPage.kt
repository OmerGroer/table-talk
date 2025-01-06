package com.example.tabletalk.services

class RestaurantsPage(
    val data: List<ApiRestaurant>,
    val pageInfo: PageInfo)

class Address(
    val fullAddress: String,
    val country: String
)

class ApiRestaurant(
    val address: Address,
    val cuisines: List<String>,
    val id: Int,
    val name: String,
    val priceTypes: String
)

class PageInfo(
    val endCursor: String,
    val hasNextPage: Boolean
)
