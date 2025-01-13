package com.example.tabletalk.fragments.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.tabletalk.R
import com.example.tabletalk.adapter.OnPostItemClickListener
import com.example.tabletalk.adapter.PostType
import com.example.tabletalk.adapter.PostsRecyclerAdapter
import com.example.tabletalk.databinding.FragmentUserBinding

private const val USER_ID = "user_ID"

interface OnCreateListener {
    fun onCreate(binding: FragmentUserBinding?)
}

class UserFragment : Fragment() {
    private var viewModel: UserViewModel? = null
    private var binding: FragmentUserBinding? = null

    private var onCreateListener: OnCreateListener? = null
    private var onRestaurantClickListener: OnPostItemClickListener? = null
    private var onEditPostListener: OnPostItemClickListener? = null

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(USER_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_user, container, false
        )
        viewModel = userId?.let { UserViewModel(it) }
        bindViews()

        setupList()
        setupUser()

        binding?.swipeRefreshLayout?.setOnRefreshListener {
            viewModel?.fetchPosts()
        }

        viewModel?.isLoadingPosts?.observe(viewLifecycleOwner) {
            binding?.swipeRefreshLayout?.isRefreshing = it || viewModel?.isLoadingUser?.value == true
        }

        this.onCreateListener?.onCreate(binding)

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

        adapter.restaurantListener = onRestaurantClickListener
        adapter.editPostListener = onEditPostListener
        adapter.fragmentManager = getChildFragmentManager()

        adapter.postType = PostType.PROFILE

        viewModel?.posts?.observe(viewLifecycleOwner) { posts ->
            adapter.updatePosts(posts)
        }
    }

    private fun setupUser() {
        viewModel?.username?.observe(viewLifecycleOwner) { username ->
            binding?.profileUsername?.text = username
        }

        viewModel?.avatarUrl?.observe(viewLifecycleOwner) { avatarUrl ->
            val avatar = binding?.profileAvatar
            if (avatar != null) {
                Glide.with(requireContext())
                    .load(avatarUrl)
                    .into(avatar)
            }
        }

        viewModel?.isLoadingUser?.observe(viewLifecycleOwner) {
            binding?.swipeRefreshLayout?.isRefreshing = it || viewModel?.isLoadingPosts?.value == true
        }
    }

    companion object {
        fun newInstance(userId: String) =
            UserFragment().apply {
                arguments = Bundle().apply {
                    putString(USER_ID, userId)
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