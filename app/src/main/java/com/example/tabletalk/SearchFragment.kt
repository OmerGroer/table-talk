package com.example.tabletalk

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tabletalk.adapter.OnRestaurantItemClickListener
import com.example.tabletalk.adapter.OnUserItemClickListener
import com.example.tabletalk.adapter.RestaurantsRecyclerAdapter
import com.example.tabletalk.adapter.UsersRecyclerAdapter
import com.example.tabletalk.model.Model
import com.example.tabletalk.model.Restaurant
import com.example.tabletalk.model.User

enum class SearchType {
    RESTAURANTS, PEOPLE;
}

class SearchFragment : Fragment() {
    private var restaurantButton: TextView? = null
    private var peopleButton: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var searchInput: SearchView? = null

    private var users: MutableList<User>? = null
    private var restaurants: MutableList<Restaurant>? = null

    private var userRecyclerAdapter: UsersRecyclerAdapter = UsersRecyclerAdapter(users)
    private var restaurantRecyclerAdapter: RestaurantsRecyclerAdapter = RestaurantsRecyclerAdapter(restaurants)

    private var searchType: SearchType = SearchType.RESTAURANTS

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        restaurants = Model.shared.getAllRestaurants("")
        users = Model.shared.getAllUsers("")

        restaurantButton = view.findViewById(R.id.search_restaurant)
        peopleButton = view.findViewById(R.id.search_people)
        searchInput = view.findViewById(R.id.search_input)

        recyclerView = view.findViewById(R.id.search_recycler_view)
        recyclerView?.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = layoutManager

        restaurantRecyclerAdapter = RestaurantsRecyclerAdapter(restaurants)
        restaurantRecyclerAdapter.listener = object : OnRestaurantItemClickListener {
            override fun onRestaurantClickListener(restaurant: Restaurant) {
                val action =
                    SearchFragmentDirections.actionSearchFragmentToRestaurantPageFragment(
                        restaurant.name
                    )
                Navigation.findNavController(view).navigate(action)
            }
        }

        userRecyclerAdapter = UsersRecyclerAdapter(users)
        userRecyclerAdapter.listener = object : OnUserItemClickListener {
            override fun onUsernameClickListener(user: User) {
                TODO("Not yet implemented")
            }
        }

        switchToRestaurants(view)

        peopleButton?.setOnClickListener(::switchToPeople)
        restaurantButton?.setOnClickListener(::switchToRestaurants)

        searchInput?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                when (searchType) {
                    SearchType.RESTAURANTS -> setRestaurants()
                    SearchType.PEOPLE -> setUsers()
                }
                return false
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }
        })

        return view
    }

    private fun switchToRestaurants(view: View) {
        searchType = SearchType.RESTAURANTS
        searchInput?.setQuery("", false)

        restaurantButton?.setTextColor(Color.argb(255, 0, 0, 0))
        peopleButton?.setTextColor(Color.argb(64, 0, 0, 0))

        setRestaurants()

        recyclerView?.adapter = restaurantRecyclerAdapter
    }

    private fun switchToPeople(view: View) {
        searchType = SearchType.PEOPLE
        searchInput?.setQuery("", false)

        restaurantButton?.setTextColor(Color.argb(64, 0, 0, 0))
        peopleButton?.setTextColor(Color.argb(255, 0, 0, 0))

        setUsers()

        recyclerView?.adapter = userRecyclerAdapter
    }

    private fun setUsers() {
        users?.clear()
        users?.addAll(Model.shared.getAllUsers(searchInput?.query.toString()))
    }

    private fun setRestaurants() {
        restaurants?.clear()
        restaurants?.addAll(Model.shared.getAllRestaurants(searchInput?.query.toString()))
    }
}