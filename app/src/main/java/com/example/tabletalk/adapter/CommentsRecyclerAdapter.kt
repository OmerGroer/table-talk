package com.example.tabletalk.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tabletalk.R
import com.example.tabletalk.data.model.InflatedComment

class CommentsRecyclerAdapter(private var comments: List<InflatedComment>?) :
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

    fun updateComments(newComments: List<InflatedComment>) {
        comments = newComments
        notifyDataSetChanged()
    }

}