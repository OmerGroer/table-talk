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
import com.example.tabletalk.CommentsFragment
import com.example.tabletalk.R
import com.example.tabletalk.data.model.Model
import com.example.tabletalk.data.model.Post
import com.example.tabletalk.data.repositories.UserRepository

class PostViewHolder(
    itemView: View,
    restaurantListener: OnPostItemClickListener?,
    userListener: OnPostItemClickListener?,
    editPostListener: OnPostItemClickListener?,
    fragmentManager: FragmentManager?,
): RecyclerView.ViewHolder(itemView) {
    private var layout: ConstraintLayout? = null
    private var menu: ImageView? = null
    private var username: TextView? = null
    private var restaurant: TextView? = null
    private var stars: Array<ImageView>? = null
    private var review: TextView? = null
    private var restaurantImage: ImageView? = null
    private var avatar: ImageView? = null
    private var comment: Button? = null

    private var post: Post? = null

    init {
        layout = itemView.findViewById(R.id.post_row_main)
        menu = itemView.findViewById(R.id.post_row_menu)
        username = itemView.findViewById(R.id.post_row_username)
        avatar = itemView.findViewById(R.id.post_row_avatar)
        restaurant = itemView.findViewById(R.id.post_row_restaurant)
        stars = arrayOf(
            itemView.findViewById(R.id.post_row_first_star),
            itemView.findViewById(R.id.post_row_second_star),
            itemView.findViewById(R.id.post_row_third_star),
            itemView.findViewById(R.id.post_row_fourth_star),
            itemView.findViewById(R.id.post_row_fifth_star)
        )
        review = itemView.findViewById(R.id.post_row_review)
        restaurantImage = itemView.findViewById(R.id.post_row_restaurant_image)
        comment = itemView.findViewById(R.id.post_row_comment_button)

        username?.setOnClickListener {
            userListener?.onClickListener(post as Post)
        }
        avatar?.setOnClickListener {
            userListener?.onClickListener(post as Post)
        }
        restaurant?.setOnClickListener {
            restaurantListener?.onClickListener(post as Post)
        }
        comment?.setOnClickListener {
            if (fragmentManager != null) CommentsFragment.display(fragmentManager, post?.id as String)
        }

        menu?.setOnClickListener {
            PopupMenu(menu?.context, menu).apply {
                setOnMenuItemClickListener(object : OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem?): Boolean {
                        when (item?.itemId) {
                            R.id.edit_post -> {
                                editPostListener?.onClickListener(post as Post)
                            }
                            R.id.delete_post -> {
                                Model.shared.deletePost(post as Post, object : Model.Listener<Post> {
                                    override fun onComplete(data: Post) {
                                    }
                                })
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

    fun bind(post: Post?, position: Int, postType: PostType) {
        this.post = post

        username?.text = post?.userName
        restaurant?.text = post?.restaurantName
        review?.text = post?.review

        val rate = post?.rating as Int
        val startsSize = stars?.size as Int - 1

        for (i in rate..startsSize) {
            stars?.get(i)?.visibility = View.GONE
        }

        val isMenuShown = post.userId == UserRepository.getInstance().getLoggedUserId()
        if (isMenuShown) {
            menu?.visibility = View.VISIBLE
        }

        when (postType) {
            PostType.PROFILE -> {
                username?.visibility = View.GONE
                avatar?.visibility = View.GONE

                val constraintSet = ConstraintSet()
                constraintSet.clone(layout)
                reorderStars(constraintSet, isMenuShown)
                constraintSet.connect(R.id.post_row_restaurant, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                constraintSet.applyTo(layout)
            }
            PostType.RESTAURANT -> {
                restaurant?.visibility = View.INVISIBLE

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