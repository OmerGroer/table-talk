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
import com.example.tabletalk.data.model.Model

private const val USER_ID = "user_ID"
private const val USERNAME = "username"

interface OnCreateListener {
    fun onCreate(view: View)
}

class UserFragment : Fragment() {
    private var onCreateListener: OnCreateListener? = null
    private var onRestaurantClickListener: OnPostItemClickListener? = null
    private var onEditPostListener: OnPostItemClickListener? = null

    private var userId: String? = null
    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(USER_ID)
            username = it.getString(USERNAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        val posts = Model.shared.getPostsByUserId(userId as String)

        val usernameTextView: TextView = view.findViewById(R.id.profile_username)
        usernameTextView.text = username as String

        val recyclerView: RecyclerView = view.findViewById(R.id.posts_recycler_view)
        recyclerView.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        val adapter = PostsRecyclerAdapter(posts)

        adapter.restaurantListener = onRestaurantClickListener
        adapter.editPostListener = onEditPostListener
        adapter.fragmentManager = getChildFragmentManager()

        adapter.postType = PostType.PROFILE

        recyclerView.adapter = adapter

        this.onCreateListener?.onCreate(view)

        return view
    }

    companion object {
        fun newInstance(userId: String, username: String) =
            UserFragment().apply {
                arguments = Bundle().apply {
                    putString(USER_ID, userId)
                    putString(USERNAME, username)
                }
            }
    }

    fun setOnCreate(listener: OnCreateListener): UserFragment {
        this.onCreateListener = listener
        return this
    }

    fun setOnRestaurantClickListener(listener: OnPostItemClickListener): UserFragment {
        this.onRestaurantClickListener = listener
        return this
    }

    fun setOnEditPostListener(listener: OnPostItemClickListener): UserFragment {
        this.onEditPostListener = listener
        return this
    }
}