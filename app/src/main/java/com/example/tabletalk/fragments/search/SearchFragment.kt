package com.example.tabletalk.fragments.search

import android.graphics.Color
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
import com.example.tabletalk.adapter.OnUserItemClickListener
import com.example.tabletalk.adapter.RestaurantsRecyclerAdapter
import com.example.tabletalk.adapter.UsersRecyclerAdapter
import com.example.tabletalk.data.model.Restaurant
import com.example.tabletalk.data.model.User
import com.example.tabletalk.databinding.FragmentSearchBinding

enum class SearchType {
    RESTAURANTS, PEOPLE;
}

class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModels()
    private var binding: FragmentSearchBinding? = null

    private var userRecyclerAdapter: UsersRecyclerAdapter = UsersRecyclerAdapter(emptyList())
    private var restaurantRecyclerAdapter: RestaurantsRecyclerAdapter = RestaurantsRecyclerAdapter(emptyList())

    private var searchType: SearchType = SearchType.RESTAURANTS

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_search, container, false
        )
        bindViews()

        setUpList()

        switchToRestaurants(binding?.root)

        binding?.searchPeople?.setOnClickListener(::switchToPeople)
        binding?.searchRestaurant?.setOnClickListener(::switchToRestaurants)

        binding?.searchInput?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                viewModel.search = p0 ?: ""
                when (searchType) {
                    SearchType.RESTAURANTS -> viewModel.searchRestaurants()
                    SearchType.PEOPLE -> viewModel.searchRestaurants()
                }
                return false
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }
        })

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

    private fun setUpList() {
        binding?.searchRecyclerView?.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        binding?.searchRecyclerView?.layoutManager = layoutManager

        restaurantRecyclerAdapter.listener = object : OnRestaurantItemClickListener {
            override fun onRestaurantClickListener(restaurant: Restaurant) {
                val action = SearchFragmentDirections.actionGlobalRestaurantPageFragment(restaurant.id)
                findNavController().navigate(action)
            }
        }

        viewModel.restaurants.observe(viewLifecycleOwner) { restaurants ->
            restaurants.observe(viewLifecycleOwner) {
                restaurantRecyclerAdapter.updateRestaurants(it)
            }
        }

        userRecyclerAdapter.listener = object : OnUserItemClickListener {
            override fun onUsernameClickListener(user: User) {
                val action = SearchFragmentDirections.actionGlobalUserPageFragment(user.id)
                findNavController().navigate(action)
            }
        }
    }

    private fun switchToRestaurants(view: View?) {
        searchType = SearchType.RESTAURANTS
        binding?.searchInput?.setQuery("", false)

        binding?.searchRestaurant?.setTextColor(Color.argb(255, 0, 0, 0))
        binding?.searchPeople?.setTextColor(Color.argb(64, 0, 0, 0))

        viewModel.searchRestaurants()

        binding?.searchRecyclerView?.adapter = restaurantRecyclerAdapter
    }

    private fun switchToPeople(view: View) {
        searchType = SearchType.PEOPLE
        binding?.searchInput?.setQuery("", false)

        binding?.searchRestaurant?.setTextColor(Color.argb(64, 0, 0, 0))
        binding?.searchPeople?.setTextColor(Color.argb(255, 0, 0, 0))

//        setUsers()

        binding?.searchRecyclerView?.adapter = userRecyclerAdapter
    }
}