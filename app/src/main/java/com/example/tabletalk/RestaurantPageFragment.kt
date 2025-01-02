package com.example.tabletalk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tabletalk.adapter.OnPostItemClickListener
import com.example.tabletalk.adapter.PostType
import com.example.tabletalk.adapter.PostsRecyclerAdapter
import com.example.tabletalk.model.Model
import com.example.tabletalk.model.Post

class RestaurantPageFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_restaurant_page, container, false)

        val loggedUser = Model.shared.getLoggedInUser()
        val restaurantName = RestaurantPageFragmentArgs.fromBundle(requireArguments()).restaurantName
        val restaurant = Model.shared.getRestaurantByName(restaurantName)

        val posts = Model.shared.getPostsByRestaurantName(restaurantName)
        val loggedUserPosts = Model.shared.getPostsByRestaurantNameAndUserEmail(restaurantName, loggedUser.email)
        val allPosts = loggedUserPosts + posts

        val restaurantNameTextView: TextView = view.findViewById(R.id.restaurant_name)
        restaurantNameTextView.text = restaurant.name
        val restaurantRateTextView: TextView = view.findViewById(R.id.restaurant_rate)
        restaurantRateTextView.text = restaurant.rate.toString()
        val descriptionTextView: TextView = view.findViewById(R.id.restaurant_description)
        descriptionTextView.text = "${restaurant?.category} - ${restaurant?.location}"

        val recyclerView: RecyclerView = view.findViewById(R.id.posts_recycler_view)
        recyclerView.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        val adapter = PostsRecyclerAdapter(allPosts)

        adapter.userListener = object : OnPostItemClickListener {
            override fun onClickListener(post: Post) {
                val action =
                    RestaurantPageFragmentDirections.actionGlobalUserPageFragment(
                        post.userName, post.userEmail
                    )
                Navigation.findNavController(view).navigate(action)
            }
        }
        adapter.editPostListener = object : OnPostItemClickListener {
            override fun onClickListener(post: Post) {
                val action =
                    RestaurantPageFragmentDirections.actionRestaurantPageFragmentToEditPostFragment(
                        post.id
                    )
                Navigation.findNavController(view).navigate(action)
            }
        }

        adapter.postType = PostType.RESTAURANT

        recyclerView.adapter = adapter

        return view
    }
}