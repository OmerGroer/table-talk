package com.example.tabletalk.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tabletalk.R
import com.example.tabletalk.model.Comment

class CommentsRecyclerAdapter(private val comments: List<Comment>?) :
    RecyclerView.Adapter<CommentViewHolder>() {

    override fun getItemCount(): Int = comments?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.comment_row,
            parent,
            false
        )
        return CommentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(
            comment = comments?.get(position),
            position = position
        )
    }
}