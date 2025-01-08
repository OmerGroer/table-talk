package com.example.tabletalk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.tabletalk.adapter.OnPostItemClickListener
import com.example.tabletalk.data.model.Model
import com.example.tabletalk.data.model.Post
import com.example.tabletalk.data.repositories.UserRepository

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val user = Model.shared.getLoggedInUser()
        val fragment = UserFragment.newInstance(user.id, user.username)
        fragment.setOnCreate(object : OnCreateListener {
            override fun onCreate(view: View) {
                val toolbar: androidx.appcompat.widget.Toolbar = view.findViewById(R.id.profile_toolbar)
                toolbar.inflateMenu(R.menu.profile_menu)
                toolbar.setOnMenuItemClickListener {
                    onMenuItemClick(it)
                }
            }
        })
        fragment.setOnRestaurantClickListener(object : OnPostItemClickListener {
            override fun onClickListener(post: Post) {
                val action =
                    ProfileFragmentDirections.actionGlobalRestaurantPageFragment(
                        post.restaurantName
                    )
                Navigation.findNavController(view).navigate(action)
            }
        })
        fragment.setOnEditPostListener(object : OnPostItemClickListener {
            override fun onClickListener(post: Post) {
                val action =
                    ProfileFragmentDirections.actionProfileFragmentToEditPostFragment(
                        post.id
                    )
                Navigation.findNavController(view).navigate(action)
            }
        })

        getChildFragmentManager().beginTransaction()
            .replace(R.id.container, fragment)
            .commitNow()

        return view
    }

    fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_profile -> {
                TODO("Not yet implemented")
            }
            R.id.logout_profile -> {
                UserRepository.getInstance().logout()
            }
            else -> return false
        }

        return true
    }
}