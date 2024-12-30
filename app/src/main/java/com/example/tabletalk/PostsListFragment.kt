package com.example.tabletalk

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tabletalk.adapter.PostsRecyclerAdapter
import com.example.tabletalk.model.Model
import com.example.tabletalk.model.Post

interface OnPostItemClickListener {
    fun onRestaurantClickListener(post: Post)
    fun onUsernameClickListener(post: Post)
}

class PostsListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_posts_list, container, false)

        val posts = Model.shared.getAllPosts()

        val recyclerView: RecyclerView = view.findViewById(R.id.posts_recycler_view)
        recyclerView.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        val adapter = PostsRecyclerAdapter(posts)

        adapter.listener = object : OnPostItemClickListener {
            override fun onRestaurantClickListener(post: Post) {
                TODO("Not yet implemented")
            }

            override fun onUsernameClickListener(post: Post) {
                TODO("Not yet implemented")
            }
        }
        recyclerView.adapter = adapter

        return view
    }
}