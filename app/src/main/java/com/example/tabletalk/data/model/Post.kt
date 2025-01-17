package com.example.tabletalk.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity(tableName = "posts")
data class Post (
    @PrimaryKey
    var id: String = "",
    var userId: String = "",
    var restaurantId: String = "",
    var review: String = "",
    var restaurantUrl: String = "",
    var rating: Int = 0,
    var lastUpdated: Long? = null
) {
    companion object {
        const val ID_KEY = "id"
        const val USER_ID_KEY = "userId"
        const val RESTAURANT_ID_KEY = "restaurantId"
        const val REVIEW_KEY = "review"
        const val IMAGE_URI_KEY = "restaurantUrl"
        const val RATING_KEY = "rating"
        const val TIMESTAMP_KEY = "lastUpdated"

        fun fromJSON(json: Map<String, Any>): Post {
            val id = json[ID_KEY] as? String ?: ""
            val userId = json[USER_ID_KEY] as? String ?: ""
            val restaurantId = json[RESTAURANT_ID_KEY] as? String ?: ""
            val review = json[REVIEW_KEY] as? String ?: ""
            val restaurantUrl = json[IMAGE_URI_KEY] as? String ?: ""
            val rating = json[RATING_KEY] as? Long ?: 0
            val timestamp = (json[TIMESTAMP_KEY] as? Timestamp ?: Timestamp(0,0))
            val lastUpdated = timestamp.toDate().time

            return Post(id, userId, restaurantId, review, restaurantUrl, rating.toInt(), lastUpdated)
        }
    }
}