package com.example.tabletalk.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tabletalk.R
import com.example.tabletalk.data.model.InflatedPost

enum class PostType {
    REGULAR, PROFILE, RESTAURANT
}

interface OnPostItemClickListener {
    fun onClickListener(post: InflatedPost)
}

class PostsRecyclerAdapter(private var posts: List<InflatedPost>) :
    RecyclerView.Adapter<PostViewHolder>() {

    var restaurantListener: OnPostItemClickListener? = null
    var userListener: OnPostItemClickListener? = null
    var editPostListener: OnPostItemClickListener? = null
    var fragmentManager: FragmentManager? = null
    var postType = PostType.REGULAR

    override fun getItemCount(): Int = posts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.post_row,
            parent,
            false
        )
        return PostViewHolder(
            itemView,
            restaurantListener,
            userListener,
            editPostListener,
            fragmentManager
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(
            post = posts.get(position),
            position = position,
            postType = postType
        )
    }

    fun updatePosts(newPosts: List<InflatedPost>) {
        posts = newPosts
        notifyDataSetChanged()
    }
}