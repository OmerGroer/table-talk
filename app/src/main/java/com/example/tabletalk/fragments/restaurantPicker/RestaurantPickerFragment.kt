package com.example.tabletalk.fragments.restaurantPicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tabletalk.R
import com.example.tabletalk.adapter.OnRestaurantItemClickListener
import com.example.tabletalk.adapter.RestaurantsRecyclerAdapter
import com.example.tabletalk.databinding.FragmentRestaurantPickerBinding
import com.example.tabletalk.data.model.Restaurant
import com.example.tabletalk.utils.PaginationScrollListener

class RestaurantPickerFragment : Fragment() {
    private val viewModel: RestaurantPickerViewModel by viewModels()
    private var binding: FragmentRestaurantPickerBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_restaurant_picker, container, false
        )
        bindViews(binding)

        setUpList()
        setupLoading()
        setupSearchBar()

        binding?.searchButton?.setOnClickListener {
            viewModel.searchRestaurants()
        }

        return binding?.root
    }

    private fun bindViews(binding: FragmentRestaurantPickerBinding?) {
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun setUpList() {
        binding?.searchRecyclerView?.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        binding?.searchRecyclerView?.layoutManager = layoutManager
        val adapter = RestaurantsRecyclerAdapter(emptyList())
        binding?.searchRecyclerView?.adapter = adapter

        binding?.searchRecyclerView?.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                viewModel.loadMoreRestaurants()
            }

            override fun isLastPage(): Boolean {
                return false
            }

            override fun isLoading(): Boolean {
                return viewModel.isLoading.value ?: false
            }
        })

        adapter.listener = object : OnRestaurantItemClickListener {
            override fun onRestaurantClickListener(restaurant: Restaurant) {
                val action = RestaurantPickerFragmentDirections.actionRestaurantPickerFragmentToAddPostFragment(restaurant.id)
                findNavController().navigate(action)
            }
        }

        viewModel.restaurants.observe(viewLifecycleOwner) { restaurants ->
            adapter.updateRestaurants(restaurants)
        }
    }

    private fun setupLoading() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) binding?.progressBar?.visibility = View.VISIBLE
            else binding?.progressBar?.visibility = View.GONE
        }
    }

    private fun setupSearchBar() {
        binding?.searchInput?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                viewModel.search = p0 ?: ""
                return false
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                viewModel.searchRestaurants()
                return false
            }
        })
    }
}