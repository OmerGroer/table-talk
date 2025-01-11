package com.example.tabletalk.data.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.example.tabletalk.MyApplication
import com.example.tabletalk.data.local.AppLocalDb
import com.example.tabletalk.data.model.Restaurant
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RestaurantRepository {
    companion object {
        private const val COLLECTION = "restaurants"
        private const val LAST_UPDATED = "restaurantsLastUpdated"

        private val restaurantRepository = RestaurantRepository()

        fun getInstance(): RestaurantRepository {
            return restaurantRepository
        }
    }

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun save(restaurant: Restaurant, rating: Int) {
        val documentRef = db.collection(COLLECTION).document(restaurant.id)

        db.runTransaction { transaction ->
            val restaurantDB = transaction.get(documentRef)
            var ratingCount = (restaurantDB.getDouble("ratingCount") ?: 0.0).toInt()
            val newRating = (restaurantDB.getDouble("rating") ?: 0.0) * ratingCount + rating
            ratingCount = ratingCount.inc()

            transaction.set(documentRef, restaurant)

            transaction.update(documentRef, "lastUpdated", FieldValue.serverTimestamp())
            transaction.update(documentRef, "rating", newRating / ratingCount)
            transaction.update(documentRef, "ratingCount", ratingCount)
        }.await()
    }

    suspend fun save(restaurantId: String, rating: Int, oldRating: Int) {
        val documentRef = db.collection(COLLECTION).document(restaurantId)

        db.runTransaction { transaction ->
            val restaurantDB = transaction.get(documentRef)
            val ratingCount = (restaurantDB.getDouble("ratingCount") ?: 0.0).toInt()
            val newRating = (restaurantDB.getDouble("rating") ?: 0.0) * ratingCount - oldRating + rating

            transaction.update(documentRef, "lastUpdated", FieldValue.serverTimestamp())
            transaction.update(documentRef, "rating", newRating / ratingCount)
        }.await()
    }

    suspend fun getById(id: String): Restaurant? {
        var restaurant = AppLocalDb.getInstance().restaurantDao().getById(id)

        if (restaurant == null) {
            restaurant = db.collection(COLLECTION)
                .document(id)
                .get()
                .await()
                .toObject(Restaurant::class.java)

            if (restaurant == null) return null

            AppLocalDb.getInstance().restaurantDao().insertAll(restaurant)
        }

        return restaurant
    }

    fun getByIncluding(searchString: String): LiveData<List<Restaurant>> {
        return AppLocalDb.getInstance().restaurantDao().getByIncluding(searchString)
    }

    suspend fun refresh() {
        var time: Long = getLastUpdate()

        val restaurants = db.collection(COLLECTION)
            .whereGreaterThanOrEqualTo(Restaurant.TIMESTAMP_KEY, Timestamp(time, 0))
            .get().await().documents.map { document -> document.data?.let { Restaurant.fromJSON(it).apply { id = document.id } }}

        for (restaurant in restaurants) {
            if (restaurant == null) continue
            AppLocalDb.getInstance().restaurantDao().insertAll(restaurant)
            val lastUpdated = restaurant.lastUpdated
            if (lastUpdated != null && lastUpdated > time) {
                time = lastUpdated
            }
        }

        setLastUpdate(time)
    }

    private fun getLastUpdate(): Long {
        val sharedPef: SharedPreferences = MyApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE)
        return sharedPef.getLong(LAST_UPDATED, 0)
    }

    private fun setLastUpdate(time: Long) {
        MyApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE)
            .edit().putLong(LAST_UPDATED, time).apply()
    }
}