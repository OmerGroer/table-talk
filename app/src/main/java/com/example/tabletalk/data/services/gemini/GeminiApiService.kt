package com.example.tabletalk.data.services.gemini

import android.util.Log
import com.example.tabletalk.data.model.Recommendation
import com.google.gson.Gson
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun getRestaurantsByLocation(
        @Body requestBody: ContentRequest,
        @Query("key") cursor: String = TOKEN
    ): GeminiResponse

    companion object {
        private const val TOKEN = "AIzaSyBsairWPkwIRpSKW0QXp-keG0s0gNiU_nA"
        private val GSON = Gson()

        private val apiService: GeminiApiService = create()

        private fun create(): GeminiApiService {
            return NetworkModule().retrofit.create(GeminiApiService::class.java)
        }

        suspend fun getRecommendations(): Recommendation {
            val requestBody = ContentRequest(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(
                                text = """
                            Write review for a good restaurant in JSON format.
  Rating field is integer from 1 to 5.
  priceTypes field is in format like "${'$'}${'$'} - ${'$'}${'$'}${'$'}".
  I want in the response only the json so I will do JSON.parse in JS and I will get an object.

  Use this JSON schema:
  
  Address = {'fullAddress': str}
  Restaurant = {'name': str, 'address': Address, 'cuisines': str[], 'priceTypes': str}
  Return: {'review': str, 'rating': number, 'restaurant': Restaurant}
                        """.trimIndent()
                            )
                        )
                    )
                ),
                generationConfig = GenerationConfig()
            )

            val jsonString =
                apiService.getRestaurantsByLocation(requestBody).candidates.get(0).content.parts.get(
                    0
                ).text

            return GSON.fromJson(jsonString, Recommendation::class.java)
        }
    }
}