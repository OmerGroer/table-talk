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
import com.example.tabletalk.data.model.Model
import com.example.tabletalk.data.model.Post

class RestaurantPageFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_restaurant_page, container, false)

        val loggedUser = Model.shared.getLoggedInUser()
        val restaurantId = RestaurantPageFragmentArgs.fromBundle(requireArguments()).restaurantId
        val restaurant = Model.shared.getRestaurantById(restaurantId)

        val posts = Model.shared.getPostsByRestaurantName(restaurantId)
        val loggedUserPosts = Model.shared.getPostsByRestaurantNameAndUserId(restaurantId, loggedUser.id)
        val allPosts = loggedUserPosts + posts

        val restaurantNameTextView: TextView = view.findViewById(R.id.restaurant_name)
        restaurantNameTextView.text = restaurant.name
        val restaurantRateTextView: TextView = view.findViewById(R.id.restaurant_rate)
        restaurantRateTextView.text = restaurant.rating.toString()
        val descriptionTextView: TextView = view.findViewById(R.id.restaurant_description)
        descriptionTextView.text = "${restaurant.category?.let {"${restaurant.category} - "} ?: ""}${restaurant.address}"

        val recyclerView: RecyclerView = view.findViewById(R.id.posts_recycler_view)
        recyclerView.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        val adapter = PostsRecyclerAdapter(allPosts)

        adapter.fragmentManager = getChildFragmentManager()
        adapter.userListener = object : OnPostItemClickListener {
            override fun onClickListener(post: Post) {
                val action =
                    RestaurantPageFragmentDirections.actionGlobalUserPageFragment(post.userId)
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