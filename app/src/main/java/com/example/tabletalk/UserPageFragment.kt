package com.example.tabletalk

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.tabletalk.adapter.OnPostItemClickListener
import com.example.tabletalk.data.model.Post

class UserPageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_page, container, false)

        val user = UserPageFragmentArgs.fromBundle(requireArguments())
        val fragment = UserFragment.newInstance(user.userId, user.username).setOnRestaurantClickListener(object : OnPostItemClickListener {
            override fun onClickListener(post: Post) {
                val action =
                    UserPageFragmentDirections.actionGlobalRestaurantPageFragment(
                        post.restaurantName
                    )
                Navigation.findNavController(view).navigate(action)
            }
        })
        getChildFragmentManager().beginTransaction()
            .replace(R.id.container, fragment)
            .commitNow()

        return view
    }
}