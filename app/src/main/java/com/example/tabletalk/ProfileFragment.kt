package com.example.tabletalk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tabletalk.adapter.OnPostItemClickListener
import com.example.tabletalk.adapter.PostType
import com.example.tabletalk.adapter.PostsRecyclerAdapter
import com.example.tabletalk.model.Model
import com.example.tabletalk.model.Post

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val loggedUser = Model.shared.getLoggedInUser()
        val posts = Model.shared.getPostsByEmail(loggedUser.email)

        val username: TextView = view.findViewById(R.id.profile_username)
        username.text = loggedUser.username

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
        adapter.postType = PostType.PROFILE

        recyclerView.adapter = adapter

        return view
    }
}