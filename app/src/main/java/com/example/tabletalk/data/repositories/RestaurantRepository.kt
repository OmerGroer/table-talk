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
import com.google.firebase.firestore.Transaction
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.Date

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

    fun save(restaurant: Restaurant, rating: Int, transaction: Transaction) {
        val documentRef = db.collection(COLLECTION).document(restaurant.id)

        val restaurantDB = transaction.get(documentRef)
        var ratingCount = (restaurantDB.getDouble(Restaurant.RATING_COUNT_KEY) ?: 0.0).toInt()
        val newRating = (restaurantDB.getDouble(Restaurant.RATING_KEY) ?: 0.0) * ratingCount + rating
        ratingCount = ratingCount.inc()

        transaction.set(documentRef, restaurant)

        transaction.update(documentRef, Restaurant.TIMESTAMP_KEY, FieldValue.serverTimestamp())
        transaction.update(documentRef, Restaurant.RATING_KEY, newRating / ratingCount)
        transaction.update(documentRef, Restaurant.RATING_COUNT_KEY, ratingCount)
    }

    fun save(restaurantId: String, rating: Int, oldRating: Int, transaction: Transaction) {
        val documentRef = db.collection(COLLECTION).document(restaurantId)

        val restaurantDB = transaction.get(documentRef)
        val ratingCount = (restaurantDB.getDouble(Restaurant.RATING_COUNT_KEY) ?: 0.0).toInt()
        val newRating = (restaurantDB.getDouble(Restaurant.RATING_KEY) ?: 0.0) * ratingCount - oldRating + rating

        transaction.update(documentRef, Restaurant.TIMESTAMP_KEY, FieldValue.serverTimestamp())
        transaction.update(documentRef, Restaurant.RATING_KEY, newRating / ratingCount)
    }

    fun getByIdLiveData(restaurantId: String): LiveData<Restaurant> {
        return AppLocalDb.getInstance().restaurantDao().getByIdLiveData(restaurantId)
    }

    suspend fun getById(restaurantId: String): Restaurant? {
        var restaurant = AppLocalDb.getInstance().restaurantDao().getById(restaurantId)

        if (restaurant == null) {
            restaurant = db.collection(COLLECTION)
                .document(restaurantId)
                .get()
                .await().let { document -> document.data?.let { Restaurant.fromJSON(it).apply { id = document.id } } }

            if (restaurant == null) return null

            AppLocalDb.getInstance().restaurantDao().insertAll(restaurant)
        }

        return restaurant
    }

    fun getByIncluding(searchString: String): LiveData<List<Restaurant>> {
        return AppLocalDb.getInstance().restaurantDao().getByIncluding(searchString)
    }

    suspend fun delete(restaurantId: String, rating: Int) {
        val documentRef = db.collection(COLLECTION).document(restaurantId)

        val wasDeleted = db.runTransaction { transaction ->
            val restaurantDB = transaction.get(documentRef)
            var ratingCount = (restaurantDB.getDouble(Restaurant.RATING_COUNT_KEY) ?: 0.0).toInt()
            val newRating = (restaurantDB.getDouble(Restaurant.RATING_KEY) ?: 0.0) * ratingCount - rating
            ratingCount = ratingCount.dec()

            if (ratingCount == 0) {
                transaction.delete(documentRef)
                true
            } else {
                transaction.update(documentRef, Restaurant.TIMESTAMP_KEY, FieldValue.serverTimestamp())
                transaction.update(documentRef, Restaurant.RATING_KEY, newRating / ratingCount)
                transaction.update(documentRef, Restaurant.RATING_COUNT_KEY, ratingCount)
                false
            }
        }.await()

        if (wasDeleted) {
            AppLocalDb.getInstance().restaurantDao().delete(restaurantId)
        } else {
            refresh()
        }
    }

    @Synchronized
    fun refresh() {
        var time: Long = getLastUpdate()

        val restaurants = runBlocking {
            db.collection(COLLECTION)
                .whereGreaterThanOrEqualTo(Restaurant.TIMESTAMP_KEY, Timestamp(Date(time)))
                .get().await().documents.map { document -> document.data?.let { Restaurant.fromJSON(it).apply { id = document.id } }}
        }

        for (restaurant in restaurants) {
            if (restaurant == null) continue
            AppLocalDb.getInstance().restaurantDao().insertAll(restaurant)
            val lastUpdated = restaurant.lastUpdated
            if (lastUpdated != null && lastUpdated > time) {
                time = lastUpdated
            }
        }

        setLastUpdate(time + 1)
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