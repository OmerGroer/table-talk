package com.example.tabletalk.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tabletalk.OnRestaurantItemClickListener
import com.example.tabletalk.R
import com.example.tabletalk.model.Restaurant

class RestaurantViewHolder(
    itemView: View,
    listener: OnRestaurantItemClickListener?
) : RecyclerView.ViewHolder(itemView) {
    private var name: TextView? = null
    private var description: TextView? = null
    private var rate: TextView? = null

    private var restaurant: Restaurant? = null

    init {
        name = itemView.findViewById(R.id.restaurant_row_name)
        description = itemView.findViewById(R.id.restaurant_row_description)
        rate = itemView.findViewById(R.id.restaurant_row_rate)

        itemView.setOnClickListener {
            listener?.onRestaurantClickListener(restaurant as Restaurant)
        }
    }

    fun bind(restaurant: Restaurant?, position: Int) {
        this.restaurant = restaurant

        name?.text = restaurant?.name
        description?.text = "${restaurant?.category} - ${restaurant?.location}"
        rate?.text = restaurant?.rate.toString()
    }
}