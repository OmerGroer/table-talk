package com.example.tabletalk.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.tabletalk.data.model.Restaurant

class DiffCallback(
    private val oldList: List<Restaurant>,
    private val newList: List<Restaurant>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}