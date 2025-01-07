package com.example.tabletalk.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tabletalk.R
import com.example.tabletalk.data.model.Comment

class CommentViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {
    private var name: TextView? = null
    private var avatar: ImageView? = null
    private var content: TextView? = null

    private var comment: Comment? = null

    init {
        name = itemView.findViewById(R.id.comment_row_username)
        avatar = itemView.findViewById(R.id.comment_row_avatar)
        content = itemView.findViewById(R.id.comment_row_content)
    }

    fun bind(comment: Comment?, position: Int) {
        this.comment = comment

        name?.text = comment?.userName
        content?.text = comment?.content
    }
}