package com.example.tabletalk.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tabletalk.R
import com.example.tabletalk.data.model.User
import com.example.tabletalk.data.repositories.UserRepository
import com.example.tabletalk.utils.ImageLoaderViewModel

class UserViewHolder(
    itemView: View,
    listener: OnUserItemClickListener?,
    private val imageLoaderViewModel: ImageLoaderViewModel
) : RecyclerView.ViewHolder(itemView) {
    private var name: TextView = itemView.findViewById(R.id.user_row_username)
    private var avatar: ImageView = itemView.findViewById(R.id.user_row_avatar)
    private var progressBarAvatar: View = itemView.findViewById(R.id.progress_bar_avatar)

    private var user: User? = null

    init {
        itemView.setOnClickListener {
            val user = user
            if (user != null) listener?.onUsernameClickListener(user)
        }
    }

    fun bind(user: User?, position: Int) {
        this.user = user
        val userId = user?.id ?: return

        name.text = user.username
        Glide.with(itemView.context).clear(avatar)
        progressBarAvatar.visibility = View.VISIBLE
        imageLoaderViewModel.getImageUrl(userId, UserRepository.getInstance()) {
            if (userId == this.user?.id) {
                Glide.with(itemView.context)
                    .load(it)
                    .into(avatar)
                progressBarAvatar.visibility = View.GONE
            }
        }
    }
}