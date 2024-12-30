package com.example.tabletalk.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tabletalk.OnUserItemClickListener
import com.example.tabletalk.R
import com.example.tabletalk.model.User

class UserViewHolder(
    itemView: View,
    listener: OnUserItemClickListener?
) : RecyclerView.ViewHolder(itemView) {
    private var name: TextView? = null
    private var avatar: ImageView? = null

    private var user: User? = null

    init {
        name = itemView.findViewById(R.id.user_row_username)
        avatar = itemView.findViewById(R.id.user_row_avatar)

        itemView.setOnClickListener {
            listener?.onUsernameClickListener(user as User)
        }
    }

    fun bind(user: User?, position: Int) {
        this.user = user

        name?.text = user?.username
    }
}