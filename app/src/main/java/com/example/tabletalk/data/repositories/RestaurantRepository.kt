package com.example.tabletalk.data.repositories

import com.example.tabletalk.data.local.AppLocalDb
import com.example.tabletalk.data.model.Restaurant
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

const val RESTAURANTS_COLLECTION = "restaurants"

class RestaurantRepository {
    companion object {
        private val restaurantRepository = RestaurantRepository()

        fun getInstance(): RestaurantRepository {
            return restaurantRepository
        }
    }

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun save(restaurantId: Int, rating: Int, restaurant: Restaurant?) {
        val documentRef = db.collection(RESTAURANTS_COLLECTION).document(restaurantId.toString())

        db.runTransaction { transaction ->
            val restaurantDB = transaction.get(documentRef)
            val ratingCount = (restaurantDB.getDouble("ratingCount") ?: 0.0).toInt()
            val newRating = (restaurantDB.getDouble("rating") ?: 0.0) * ratingCount + rating

            if (restaurant != null) {
                transaction.set(documentRef, restaurant)
            }

            transaction.update(documentRef, "lastUpdated", FieldValue.serverTimestamp())
            transaction.update(documentRef, "rating", newRating)
            transaction.update(documentRef, "ratingCount", ratingCount + 1)
        }.await()

        // TODO: refresh all restaurants
    }

    suspend fun getById(id: Int): Restaurant? {
        var restaurant = AppLocalDb.getInstance().restaurantDao().getById(id)

        if (restaurant == null) {
            restaurant = db.collection(RESTAURANTS_COLLECTION)
                .document(id.toString())
                .get()
                .await()
                .toObject(Restaurant::class.java)

            if (restaurant == null) return null

            AppLocalDb.getInstance().restaurantDao().insertAll(restaurant)
        }

        return restaurant
    }
}