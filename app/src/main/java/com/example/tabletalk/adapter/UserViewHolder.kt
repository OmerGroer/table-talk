package com.example.tabletalk.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tabletalk.R
import com.example.tabletalk.data.model.User

class UserViewHolder(
    itemView: View,
    listener: OnUserItemClickListener?
) : RecyclerView.ViewHolder(itemView) {
    private var name: TextView = itemView.findViewById(R.id.user_row_username)
    private var avatar: ImageView = itemView.findViewById(R.id.user_row_avatar)

    private var user: User? = null

    init {
        itemView.setOnClickListener {
            val user = user
            if (user != null) listener?.onUsernameClickListener(user)
        }
    }

    fun bind(user: User?, position: Int) {
        this.user = user

        name.text = user?.username
        Glide.with(itemView.context)
            .load(user?.avatarUrl)
            .into(avatar)
    }
}