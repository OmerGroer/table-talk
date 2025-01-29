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
        setupRecommendation()

        binding?.swipeRefreshLayout?.setOnRefreshListener {
            viewModel.fetchPosts()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding?.swipeRefreshLayout?.isRefreshing = it || viewModel.isRecommendationLoaded.value == false
        }
        viewModel.isRecommendationLoaded.observe(viewLifecycleOwner) {
            binding?.swipeRefreshLayout?.isRefreshing = it == false || viewModel.isLoading.value == true
        }

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
        val adapter = PostsRecyclerAdapter(emptyList(), viewModel)
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

    private fun setupRecommendation() {
        viewModel.recommendation.observe(viewLifecycleOwner) {
            if (it != null) {
                binding?.recommendation?.visibility = View.VISIBLE
                binding?.recommendationRestaurant?.text = it.restaurant.name

                val rate = it.rating
                val stars = listOf(binding?.recommendationFirstStar, binding?.recommendationSecondStar, binding?.recommendationThirdStar, binding?.recommendationFourthStar, binding?.recommendationFifthStar)
                val startsSize = stars.size - 1

                for (i in 0..startsSize) {
                    stars.get(i)?.visibility = if (i < rate) View.VISIBLE else View.GONE
                }

                binding?.recommendationReview?.text = it.review
                binding?.recommendationCategory?.text = it.restaurant.cuisines?.joinToString("/")
                binding?.recommendationAddress?.text = it.restaurant.address.fullAddress
            }
        }
    }
}