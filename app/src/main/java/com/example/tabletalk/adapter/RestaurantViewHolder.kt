package com.example.tabletalk.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tabletalk.R
import com.example.tabletalk.model.Restaurant

class RestaurantViewHolder(
    itemView: View,
    listener: OnRestaurantItemClickListener?
) : RecyclerView.ViewHolder(itemView) {
    private var name: TextView = itemView.findViewById(R.id.restaurant_row_name)
    private var description: TextView = itemView.findViewById(R.id.restaurant_row_description)
    private var rate: TextView = itemView.findViewById(R.id.restaurant_row_rate)
    private var rateStar: ImageView = itemView.findViewById(R.id.restaurant_row_star)

    private var restaurant: Restaurant? = null

    init {
        itemView.setOnClickListener {
            listener?.onRestaurantClickListener(restaurant as Restaurant)
        }
    }

    fun bind(restaurant: Restaurant?, position: Int) {
        this.restaurant = restaurant

        name.text = restaurant?.name
        description.text = "${restaurant?.category?.let {"${restaurant.category} - "} ?: ""}${restaurant?.address}"

        val rating = restaurant?.rating
        if (rating == null) {
            rate.visibility = View.GONE
            rateStar.visibility = View.GONE
        } else {
            rate.text = rating.toString()
        }
    }
}