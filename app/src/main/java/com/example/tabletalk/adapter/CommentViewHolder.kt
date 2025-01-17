package com.example.tabletalk.adapter

import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.PopupMenu.OnMenuItemClickListener
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tabletalk.R
import com.example.tabletalk.data.model.InflatedComment
import com.example.tabletalk.data.repositories.UserRepository
import com.example.tabletalk.fragments.comments.CommentsViewModel
import com.example.tabletalk.utils.BasicAlert
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

class CommentViewHolder(
    itemView: View,
    private val viewModel: CommentsViewModel
) : RecyclerView.ViewHolder(itemView) {
    private var layout: ConstraintLayout = itemView.findViewById(R.id.comment_row_main)
    private var name: TextView = itemView.findViewById(R.id.comment_row_username)
    private var avatar: ImageView = itemView.findViewById(R.id.comment_row_avatar)
    private var content: TextView = itemView.findViewById(R.id.comment_row_content)
    private var menu: ImageView = itemView.findViewById(R.id.comment_menu)
    private var date: TextView = itemView.findViewById(R.id.date)
    private var progressBarAvatar: CircularProgressIndicator = itemView.findViewById(R.id.progress_bar_avatar)

    private var comment: InflatedComment? = null

    init {
        menu.setOnClickListener {
            PopupMenu(menu.context, menu).apply {
                setOnMenuItemClickListener(object : OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem?): Boolean {
                        val comment = comment ?: return false
                        when (item?.itemId) {
                            R.id.edit_post -> {
                                viewModel.edit(comment.content, comment.id)
                            }
                            R.id.delete_post -> {
                                layout.alpha = 0.4f
                                viewModel.delete(comment.id) {
                                    layout.alpha = 1F
                                    BasicAlert("Fail", "Failed to delete comment", itemView.context).show()
                                }
                            }
                            else -> return false
                        }
                        return true
                    }
                })
                inflate(R.menu.post_menu)
                show()
            }
        }
    }

    fun bind(comment: InflatedComment?, position: Int) {
        this.comment = comment

        layout.alpha = 1F
        name.text = comment?.userName
        content.text = comment?.content

        val lastUpdated = comment?.lastUpdated
        if (lastUpdated != null) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            date.text = dateFormat.format(Timestamp(Date(lastUpdated)).toDate())
        }

        val comment = comment ?: return
        Glide.with(itemView.context).clear(avatar)
        progressBarAvatar.visibility = View.VISIBLE
        viewModel.getImageUrl(comment.userId, UserRepository.getInstance()) {
            if (comment.id == this.comment?.id) {
                Glide.with(itemView).load(it).into(avatar)
                progressBarAvatar.visibility = View.GONE
            }
        }
    }
}