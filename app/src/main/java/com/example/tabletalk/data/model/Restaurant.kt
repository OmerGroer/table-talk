package com.example.tabletalk.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class Restaurant (
    @PrimaryKey
    val id: Int = 0,
    val name: String = "",
    val rating: Double? = null,
    val ratingCount: Int = 0,
    val category: String? = null,
    val address: String = "",
    val priceTypes: String = "",
    var lastUpdated: Long? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readValue(Long::class.java.classLoader) as? Long
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeValue(rating)
        parcel.writeInt(ratingCount)
        parcel.writeString(category)
        parcel.writeString(address)
        parcel.writeString(priceTypes)
        parcel.writeValue(lastUpdated)
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
    }
}