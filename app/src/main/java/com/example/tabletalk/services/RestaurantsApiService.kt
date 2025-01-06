package com.example.tabletalk.services

import retrofit2.http.GET
import retrofit2.http.Query

interface RestaurantsApiService {
    @GET("tripadvisor_restaurants_search_v2")
    suspend fun getRestaurantsByLocation(
        @Query("location") location: String,
        @Query("cursor") cursor: String? = null,
    ): RestaurantsPage

    companion object {
        private val apiService: RestaurantsApiService = create()

        private fun create(): RestaurantsApiService {
            return NetworkModule().retrofit.create(RestaurantsApiService::class.java)
        }

        suspend fun getRestaurantsByLocation(location: String, cursor: String? = null): RestaurantsPage {
            return apiService.getRestaurantsByLocation(location, cursor)
        }
    }
}