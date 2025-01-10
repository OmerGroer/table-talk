package com.example.tabletalk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.tabletalk.adapter.OnPostItemClickListener
import com.example.tabletalk.data.model.InflatedPost

class UserPageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_page, container, false)

        val user = UserPageFragmentArgs.fromBundle(requireArguments())
        val fragment = UserFragment.newInstance(user.userId).setOnRestaurantClickListener(object : OnPostItemClickListener {
            override fun onClickListener(post: InflatedPost) {
                val action =
                    UserPageFragmentDirections.actionGlobalRestaurantPageFragment(post.restaurantId)
                Navigation.findNavController(view).navigate(action)
            }
        })
        getChildFragmentManager().beginTransaction()
            .replace(R.id.container, fragment)
            .commitNow()

        return view
    }
}