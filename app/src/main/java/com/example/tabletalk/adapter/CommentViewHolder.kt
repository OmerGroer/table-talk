package com.example.tabletalk.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tabletalk.R
import com.example.tabletalk.data.model.InflatedComment

class CommentViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {
    private var name: TextView = itemView.findViewById(R.id.comment_row_username)
    private var avatar: ImageView = itemView.findViewById(R.id.comment_row_avatar)
    private var content: TextView = itemView.findViewById(R.id.comment_row_content)

    private var comment: InflatedComment? = null

    fun bind(comment: InflatedComment?, position: Int) {
        this.comment = comment

        name.text = comment?.userName
        content.text = comment?.content

        Glide.with(itemView).load(comment?.avatarUrl).into(avatar)
    }
}