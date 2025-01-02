package com.example.tabletalk.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tabletalk.R
import com.example.tabletalk.model.Post

enum class PostType {
    REGULAR, PROFILE, RESTAURANT
}

interface OnPostItemClickListener {
    fun onClickListener(post: Post)
}

class PostsRecyclerAdapter(private val posts: List<Post>?) :
    RecyclerView.Adapter<PostViewHolder>() {

    var restaurantListener: OnPostItemClickListener? = null
    var userListener: OnPostItemClickListener? = null
    var editPostListener: OnPostItemClickListener? = null
    var postType = PostType.REGULAR

    override fun getItemCount(): Int = posts?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.post_row,
            parent,
            false
        )
        return PostViewHolder(itemView, restaurantListener, userListener, editPostListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(
            post = posts?.get(position),
            position = position,
            postType = postType
        )
    }
}