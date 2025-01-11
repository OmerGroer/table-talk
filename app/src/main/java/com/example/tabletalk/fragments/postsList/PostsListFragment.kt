package com.example.tabletalk.fragments.postsList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tabletalk.R
import com.example.tabletalk.adapter.OnPostItemClickListener
import com.example.tabletalk.adapter.PostsRecyclerAdapter
import com.example.tabletalk.data.model.InflatedPost
import com.example.tabletalk.databinding.FragmentPostsListBinding

class PostsListFragment : Fragment() {
    private val viewModel: PostsListViewModel by viewModels()
    private var binding: FragmentPostsListBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_posts_list, container, false
        )
        bindViews()

        setupList()

        return binding?.root
    }

    private fun bindViews() {
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun setupList() {
        binding?.postsRecyclerView?.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        binding?.postsRecyclerView?.layoutManager = layoutManager
        val adapter = PostsRecyclerAdapter(emptyList())
        binding?.postsRecyclerView?.adapter = adapter

        adapter.fragmentManager = getChildFragmentManager()
        adapter.restaurantListener = object : OnPostItemClickListener {
            override fun onClickListener(post: InflatedPost) {
                val action =
                    PostsListFragmentDirections.actionGlobalRestaurantPageFragment(
                        post.restaurantId
                    )
                findNavController().navigate(action)
            }
        }
        adapter.userListener = object : OnPostItemClickListener {
            override fun onClickListener(post: InflatedPost) {
                val action =
                    PostsListFragmentDirections.actionGlobalUserPageFragment(post.userId)
                findNavController().navigate(action)
            }
        }

        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            adapter.updatePosts(posts)
        }
    }
}