package com.example.tabletalk.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity(tableName = "restaurants")
data class Restaurant (
    @PrimaryKey
    var id: String = "",
    val name: String = "",
    val rating: Double? = null,
    val ratingCount: Int = 0,
    val category: String? = null,
    val address: String = "",
    val priceTypes: String = "",
    var lastUpdated: Long? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString()!!,
        null
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeValue(rating)
        parcel.writeInt(ratingCount)
        parcel.writeString(category)
        parcel.writeString(address)
        parcel.writeString(priceTypes)
        null
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Restaurant> {
        override fun createFromParcel(parcel: Parcel): Restaurant {
            return Restaurant(parcel)
        }

        override fun newArray(size: Int): Array<Restaurant?> {
            return arrayOfNulls(size)
        }

        const val ID_KEY = "id"
        const val NAME_KEY = "name"
        const val RATING_KEY = "rating"
        const val RATING_COUNT_KEY = "ratingCount"
        const val CATEGORY_KEY = "category"
        const val ADDRESS_KEY = "address"
        const val PRICE_TYPES_KEY = "priceTypes"
        const val TIMESTAMP_KEY = "lastUpdated"

        fun fromJSON(json: Map<String, Any>): Restaurant {
            val id = json[ID_KEY] as? String ?: ""
            val name = json[NAME_KEY] as? String ?: ""
            val rating = json[RATING_KEY] as? Double ?: 0.0
            val ratingCount = json[RATING_COUNT_KEY] as? Long ?: 0
            val category = json[CATEGORY_KEY] as? String ?: ""
            val address = json[ADDRESS_KEY] as? String ?: ""
            val priceTypes = json[PRICE_TYPES_KEY] as? String ?: ""
            val lastUpdated = (json[TIMESTAMP_KEY] as? Timestamp ?: Timestamp(0,0)).seconds
            return Restaurant(id, name, rating, ratingCount.toInt(), category, address, priceTypes, lastUpdated)
        }
    }
}