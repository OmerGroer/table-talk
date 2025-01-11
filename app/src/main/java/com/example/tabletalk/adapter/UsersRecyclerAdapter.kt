package com.example.tabletalk.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tabletalk.R
import com.example.tabletalk.data.model.User

interface OnUserItemClickListener {
    fun onUsernameClickListener(user: User)
}

class UsersRecyclerAdapter(private var users: List<User>?) :
    RecyclerView.Adapter<UserViewHolder>() {

    var listener: OnUserItemClickListener? = null

    override fun getItemCount(): Int = users?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.user_row,
            parent,
            false
        )
        return UserViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(
            user = users?.get(position),
            position = position
        )
    }

    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }
}