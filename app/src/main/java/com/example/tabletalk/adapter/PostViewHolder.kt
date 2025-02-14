package com.example.tabletalk.adapter

import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.PopupMenu.OnMenuItemClickListener
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tabletalk.fragments.comments.CommentsFragment
import com.example.tabletalk.R
import com.example.tabletalk.data.model.InflatedPost
import com.example.tabletalk.data.repositories.PostRepository
import com.example.tabletalk.data.repositories.UserRepository
import com.example.tabletalk.utils.BasicAlert
import com.example.tabletalk.utils.ImageLoaderViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

class PostViewHolder(
    itemView: View,
    restaurantListener: OnPostItemClickListener?,
    userListener: OnPostItemClickListener?,
    editPostListener: OnPostItemClickListener?,
    fragmentManager: FragmentManager?,
    private val imageLoaderViewModel: ImageLoaderViewModel,
    private val postType: PostType
): RecyclerView.ViewHolder(itemView) {
    private var layout: ConstraintLayout = itemView.findViewById(R.id.post_row_main)
    private var menu: ImageView = itemView.findViewById(R.id.post_row_menu)
    private var username: TextView = itemView.findViewById(R.id.post_row_username)
    private var restaurant: TextView = itemView.findViewById(R.id.post_row_restaurant)
    private var stars: Array<ImageView> = arrayOf(
        itemView.findViewById(R.id.post_row_first_star),
        itemView.findViewById(R.id.post_row_second_star),
        itemView.findViewById(R.id.post_row_third_star),
        itemView.findViewById(R.id.post_row_fourth_star),
        itemView.findViewById(R.id.post_row_fifth_star)
    )
    private var review: TextView = itemView.findViewById(R.id.post_row_review)
    private var restaurantImage: ImageView = itemView.findViewById(R.id.post_row_restaurant_image)
    private var avatar: ImageView = itemView.findViewById(R.id.post_row_avatar)
    private var comment: Button = itemView.findViewById(R.id.post_row_comment_button)
    private var date: TextView = itemView.findViewById(R.id.date)
    private var progressBarAvatar: View = itemView.findViewById(R.id.progress_bar_avatar)
    private var progressBarRestaurant: View = itemView.findViewById(R.id.progress_bar_restaurant)

    private var post: InflatedPost? = null

    init {
        username.setOnClickListener {
            val post = post
            if (post != null) userListener?.onClickListener(post)
        }
        avatar.setOnClickListener {
            val post = post
            if (post != null) userListener?.onClickListener(post)
        }
        restaurant.setOnClickListener {
            val post = post
            if (post != null) restaurantListener?.onClickListener(post)
        }
        comment.setOnClickListener {
            val postId = post?.id
            if (fragmentManager != null && postId != null) CommentsFragment.display(fragmentManager, postId)
        }

        menu.setOnClickListener {
            PopupMenu(menu.context, menu).apply {
                setOnMenuItemClickListener(object : OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem?): Boolean {
                        val post = post ?: return false
                        when (item?.itemId) {
                            R.id.edit_post -> {
                                editPostListener?.onClickListener(post)
                            }
                            R.id.delete_post -> {
                                layout.alpha = 0.4f
                                PostRepository.getInstance().delete(post.id) {
                                    layout.alpha = 1F
                                    BasicAlert("Fail", "Failed to delete post", itemView.context).show()
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

    fun bind(post: InflatedPost?, position: Int) {
        layout.alpha = 1F
        this.post = post

        if (post == null) return

        username.text = post.userName
        restaurant.text = post.restaurantName
        review.text = post.review

        val lastUpdated = post.lastUpdated
        if (lastUpdated != null) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            date.text = dateFormat.format(Timestamp(Date(lastUpdated)).toDate())
        }

        Glide.with(itemView.context).clear(restaurantImage)
        progressBarRestaurant.visibility = View.VISIBLE
        imageLoaderViewModel.getImageUrl(post.id, PostRepository.getInstance()) {
            if (post.id == this.post?.id) {
                Glide.with(itemView.context)
                    .load(it)
                    .into(restaurantImage)
                progressBarRestaurant.visibility = View.GONE
            }
        }
        if (postType != PostType.PROFILE) {
            Glide.with(itemView.context).clear(avatar)
            progressBarAvatar.visibility = View.VISIBLE
            imageLoaderViewModel.getImageUrl(post.userId, UserRepository.getInstance()) {
                if (post.id == this.post?.id) {
                    Glide.with(itemView.context)
                        .load(it)
                        .into(avatar)
                    progressBarAvatar.visibility = View.GONE
                }
            }
        }

        val rate = post.rating
        val startsSize = stars.size - 1

        for (i in 0..startsSize) {
            stars.get(i).visibility = if (i < rate) View.VISIBLE else View.GONE
        }

        val isMenuShown = post.userId == UserRepository.getInstance().getLoggedUserId()
        if (isMenuShown) {
            menu.visibility = View.VISIBLE
        } else {
            menu.visibility = View.GONE
        }

        when (postType) {
            PostType.PROFILE -> {
                username.visibility = View.GONE
                avatar.visibility = View.GONE
                progressBarAvatar.visibility = View.GONE

                val constraintSet = ConstraintSet()
                constraintSet.clone(layout)
                reorderStars(constraintSet, isMenuShown)
                constraintSet.connect(R.id.post_row_restaurant, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                constraintSet.applyTo(layout)
            }
            PostType.RESTAURANT -> {
                restaurant.visibility = View.INVISIBLE

                val constraintSet = ConstraintSet()
                constraintSet.clone(layout)
                reorderStars(constraintSet, isMenuShown)
                constraintSet.connect(R.id.post_row_restaurant, ConstraintSet.TOP, R.id.post_row_avatar, ConstraintSet.TOP)
                constraintSet.connect(R.id.post_row_restaurant, ConstraintSet.BOTTOM, R.id.post_row_avatar, ConstraintSet.BOTTOM)
                constraintSet.connect(R.id.post_row_review, ConstraintSet.TOP, R.id.post_row_avatar, ConstraintSet.BOTTOM)
                constraintSet.applyTo(layout)
            }
            else -> { }
        }
    }

    private fun reorderStars(constraintSet: ConstraintSet, isMenuShown: Boolean) {
        if (isMenuShown) {
            constraintSet.connect(R.id.post_row_first_star, ConstraintSet.END, R.id.post_row_menu, ConstraintSet.START)
        } else {
            constraintSet.connect(R.id.post_row_first_star, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        }
        constraintSet.clear(R.id.post_row_first_star, ConstraintSet.START)
        constraintSet.connect(R.id.post_row_second_star, ConstraintSet.END, R.id.post_row_first_star, ConstraintSet.START)
        constraintSet.clear(R.id.post_row_second_star, ConstraintSet.START)
        constraintSet.connect(R.id.post_row_third_star, ConstraintSet.END, R.id.post_row_second_star, ConstraintSet.START)
        constraintSet.clear(R.id.post_row_third_star, ConstraintSet.START)
        constraintSet.connect(R.id.post_row_fourth_star, ConstraintSet.END, R.id.post_row_third_star, ConstraintSet.START)
        constraintSet.clear(R.id.post_row_fourth_star, ConstraintSet.START)
        constraintSet.connect(R.id.post_row_fifth_star, ConstraintSet.END, R.id.post_row_fourth_star, ConstraintSet.START)
        constraintSet.clear(R.id.post_row_fifth_star, ConstraintSet.START)
    }
}