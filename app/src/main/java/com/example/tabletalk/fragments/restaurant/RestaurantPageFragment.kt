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
import com.example.tabletalk.utils.BasicAlert

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
        bindViews()

        viewModel = RestaurantPageViewModel(args.restaurantId)

        setupList()
        setupRestaurant()
        setupToolbar()

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
        adapter.userListener = object : OnPostItemClickListener {
            override fun onClickListener(post: InflatedPost) {
                val action =
                    RestaurantPageFragmentDirections.actionGlobalUserPageFragment(post.userId)
                findNavController().navigate(action)
            }
        }
        adapter.editPostListener = object : OnPostItemClickListener {
            override fun onClickListener(post: InflatedPost) {
                val action =
                    RestaurantPageFragmentDirections.actionRestaurantPageFragmentToEditPostFragment(
                        post.id
                    )
                findNavController().navigate(action)
            }
        }

        adapter.postType = PostType.RESTAURANT

        viewModel?.posts?.observe(viewLifecycleOwner) { posts ->
            adapter.updatePosts(posts)
        }
    }

    private fun setupRestaurant() {
        viewModel?.restaurantName?.observe(viewLifecycleOwner) {
            binding?.restaurantName?.text = it
        }

        viewModel?.rating?.observe(viewLifecycleOwner) {
            binding?.restaurantRate?.text = "$it"
        }

        viewModel?.priceTypes?.observe(viewLifecycleOwner) {
            binding?.restaurantPrice?.text = it
        }

        viewModel?.category?.observe(viewLifecycleOwner) {
            binding?.restaurantCategory?.text = it
        }

        viewModel?.address?.observe(viewLifecycleOwner) {
            binding?.restaurantAddress?.text = it
        }
    }

    private fun setupToolbar() {
        binding?.restaurantToolbar?.setNavigationIcon(R.drawable.arrow_back)
        binding?.restaurantToolbar?.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding?.restaurantToolbar?.inflateMenu(R.menu.restaurant_menu)
        binding?.restaurantToolbar?.setOnMenuItemClickListener {

            true
        }
    }
}