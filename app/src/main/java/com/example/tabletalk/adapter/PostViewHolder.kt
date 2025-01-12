package com.example.tabletalk.adapter

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

class PostViewHolder(
    itemView: View,
    restaurantListener: OnPostItemClickListener?,
    userListener: OnPostItemClickListener?,
    editPostListener: OnPostItemClickListener?,
    fragmentManager: FragmentManager?,
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

    private var post: InflatedPost? = null

    init {
        username.setOnClickListener {
            userListener?.onClickListener(post!!)
        }
        avatar.setOnClickListener {
            userListener?.onClickListener(post!! )
        }
        restaurant.setOnClickListener {
            restaurantListener?.onClickListener(post!!)
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

    fun bind(post: InflatedPost?, position: Int, postType: PostType) {
        layout.alpha = 1F
        this.post = post

        username.text = post?.userName
        restaurant.text = post?.restaurantName
        review.text = post?.review

        Glide.with(itemView.context)
            .load(post?.restaurantUrl)
            .into(restaurantImage)
        Glide.with(itemView.context)
            .load(post?.avatarUrl)
            .into(avatar)

        val rate = post?.rating ?: 5
        val startsSize = stars.size - 1

        for (i in rate..startsSize) {
            stars.get(i).visibility = View.GONE
        }

        val isMenuShown = post?.userId == UserRepository.getInstance().getLoggedUserId()
        if (isMenuShown) {
            menu.visibility = View.VISIBLE
        }

        when (postType) {
            PostType.PROFILE -> {
                username.visibility = View.GONE
                avatar.visibility = View.GONE

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