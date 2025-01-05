package com.example.tabletalk.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tabletalk.R
import com.example.tabletalk.model.Restaurant
import com.example.tabletalk.utils.DiffCallback

interface OnRestaurantItemClickListener {
    fun onRestaurantClickListener(restaurant: Restaurant)
}

class RestaurantsRecyclerAdapter(private var restaurants: List<Restaurant>) :
    RecyclerView.Adapter<RestaurantViewHolder>() {

    var listener: OnRestaurantItemClickListener? = null

    override fun getItemCount(): Int = restaurants.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.restaurant_row,
            parent,
            false
        )
        return RestaurantViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(
            restaurant = restaurants?.get(position),
            position = position
        )
    }

    fun updateRestaurants(newRestaurants: List<Restaurant>) {
        val diffCallback = DiffCallback(restaurants, newRestaurants)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        restaurants = newRestaurants
        diffResult.dispatchUpdatesTo(this)
    }
}