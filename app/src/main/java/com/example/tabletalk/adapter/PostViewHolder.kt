package com.example.tabletalk.adapter

import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.PopupMenu.OnMenuItemClickListener
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.example.tabletalk.R
import com.example.tabletalk.model.Model
import com.example.tabletalk.model.Post

class PostViewHolder(
    itemView: View,
    restaurantListener: OnPostItemClickListener?,
    userListener: OnPostItemClickListener?,
): RecyclerView.ViewHolder(itemView) {
    private var layout: ConstraintLayout? = null
    private var menu: ImageView? = null
    private var username: TextView? = null
    private var restaurant: TextView? = null
    private var stars: Array<ImageView>? = null
    private var review: TextView? = null
    private var restaurantImage: ImageView? = null
    private var avatar: ImageView? = null

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

        username?.setOnClickListener {
            userListener?.onClickListener(post as Post)
        }
        avatar?.setOnClickListener {
            userListener?.onClickListener(post as Post)
        }
        restaurant?.setOnClickListener {
            restaurantListener?.onClickListener(post as Post)
        }

        menu?.setOnClickListener {
            PopupMenu(menu?.context, menu).apply {
                setOnMenuItemClickListener(object : OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem?): Boolean {
                        when (item?.itemId) {
                            R.id.edit_post -> {
                                TODO("Not yet implemented")
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

        val rate = post?.rate as Int
        val startsSize = stars?.size as Int - 1

        for (i in rate..startsSize) {
            stars?.get(i)?.visibility = View.GONE
        }

        when (postType) {
            PostType.PROFILE -> {
                username?.visibility = View.GONE
                avatar?.visibility = View.GONE
                menu?.visibility = View.VISIBLE

                ConstraintSet().apply {
                    clone(layout)

                    connect(R.id.post_row_first_star, ConstraintSet.END, R.id.post_row_menu, ConstraintSet.START)
                    clear(R.id.post_row_first_star, ConstraintSet.START)
                    connect(R.id.post_row_second_star, ConstraintSet.END, R.id.post_row_first_star, ConstraintSet.START)
                    clear(R.id.post_row_second_star, ConstraintSet.START)
                    connect(R.id.post_row_third_star, ConstraintSet.END, R.id.post_row_second_star, ConstraintSet.START)
                    clear(R.id.post_row_third_star, ConstraintSet.START)
                    connect(R.id.post_row_fourth_star, ConstraintSet.END, R.id.post_row_third_star, ConstraintSet.START)
                    clear(R.id.post_row_fourth_star, ConstraintSet.START)
                    connect(R.id.post_row_fifth_star, ConstraintSet.END, R.id.post_row_fourth_star, ConstraintSet.START)
                    clear(R.id.post_row_fifth_star, ConstraintSet.START)

                    connect(R.id.post_row_restaurant, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)

                    applyTo(layout)
                }
            }
            else -> { }
        }
    }
}