package com.example.tabletalk.data.services

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkModule {

    companion object {
        private const val BASE_URL = "https://real-time-tripadvisor-scraper-api.p.rapidapi.com/"
        private const val TOKEN = "5680b0672bmsh243646968bb4ab9p15d643jsn5213068b9ee8"
    }

    private val authInterceptor = Interceptor { chain ->
        val newRequest: Request = chain.request().newBuilder()
            .addHeader("x-rapidapi-host", "real-time-tripadvisor-scraper-api.p.rapidapi.com")
            .addHeader("x-rapidapi-key", TOKEN)
            .addHeader("accept", "application/json")
            .build()
        chain.proceed(newRequest)
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}