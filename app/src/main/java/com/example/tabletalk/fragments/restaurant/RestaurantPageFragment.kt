package com.example.tabletalk.fragments.restaurant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tabletalk.R
import com.example.tabletalk.adapter.OnPostItemClickListener
import com.example.tabletalk.adapter.PostType
import com.example.tabletalk.adapter.PostsRecyclerAdapter
import com.example.tabletalk.data.model.InflatedPost
import com.example.tabletalk.databinding.FragmentRestaurantPageBinding

class RestaurantPageFragment : Fragment() {
    private val args: RestaurantPageFragmentArgs by navArgs()
    private var viewModel: RestaurantPageViewModel? = null
    private var binding: FragmentRestaurantPageBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_restaurant_page, container, false
        )
        viewModel = RestaurantPageViewModel(args.restaurantId)
        bindViews()

        setupList()
        setupRestaurant()
        setupToolbar()

        binding?.swipeRefreshLayout?.setOnRefreshListener {
            viewModel?.fetchPosts()
        }

        viewModel?.isLoadingPosts?.observe(viewLifecycleOwner) {
            binding?.swipeRefreshLayout?.isRefreshing = it || viewModel?.isLoadingRestaurant?.value == true
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
        val viewModel = viewModel ?: throw Exception("ViewModel is null")
        val adapter = PostsRecyclerAdapter(emptyList(), viewModel)
        binding?.postsRecyclerView?.adapter = adapter

        adapter.fragmentManager = getChildFragmentManager()
        adapter.userListener = object : OnPostItemClickListener {
            override fun onClickListener(post: InflatedPost) {
                val action = RestaurantPageFragmentDirections.actionGlobalUserPageFragment(post.userId)
                findNavController().navigate(action)
            }
        }
        adapter.editPostListener = object : OnPostItemClickListener {
            override fun onClickListener(post: InflatedPost) {
                val action = RestaurantPageFragmentDirections.actionRestaurantPageFragmentToEditPostFormFragment(post.id)
                findNavController().navigate(action)
            }
        }

        adapter.postType = PostType.RESTAURANT

        viewModel?.posts?.observe(viewLifecycleOwner) { posts ->
            adapter.updatePosts(posts)
        }
    }

    private fun setupRestaurant() {
        viewModel?.restaurantData?.observe(viewLifecycleOwner) { restaurant ->
            if (restaurant != null) {
                binding?.restaurantName?.text = restaurant.name
                binding?.restaurantRate?.text = "%.1f".format(restaurant.rating)
                binding?.restaurantPrice?.text = restaurant.priceTypes
                binding?.restaurantCategory?.text = restaurant.category
                binding?.restaurantAddress?.text = restaurant.address
            } else {
                findNavController().popBackStack()
            }
        }

        viewModel?.isLoadingRestaurant?.observe(viewLifecycleOwner) {
            binding?.swipeRefreshLayout?.isRefreshing = it || viewModel?.isLoadingPosts?.value == true
        }
    }

    private fun setupToolbar() {
        binding?.restaurantToolbar?.setNavigationIcon(R.drawable.arrow_back)
        binding?.restaurantToolbar?.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding?.restaurantToolbar?.inflateMenu(R.menu.restaurant_menu)
        binding?.restaurantToolbar?.setOnMenuItemClickListener {
            val restaurant = viewModel?.restaurantData?.value
            if (restaurant != null) {
                val action = RestaurantPageFragmentDirections.actionRestaurantPageFragmentToAddPostFormFragment(restaurant)
                findNavController().navigate(action)
            }
            true
        }
    }
}