package com.example.android_application_course.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tabletalk.R
import com.example.tabletalk.OnItemClickListener
import com.example.tabletalk.model.Post

class PostsRecyclerAdapter(private val posts: List<Post>?): RecyclerView.Adapter<PostViewHolder>() {

    var listener: OnItemClickListener? = null

    override fun getItemCount(): Int = posts?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.post_row,
            parent,
            false
        )
        return PostViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(
            post = posts?.get(position),
            position = position
        )
    }
}