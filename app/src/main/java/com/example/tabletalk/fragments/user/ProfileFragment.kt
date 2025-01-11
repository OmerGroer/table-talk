package com.example.tabletalk.fragments.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tabletalk.R
import com.example.tabletalk.adapter.OnPostItemClickListener
import com.example.tabletalk.data.model.InflatedPost
import com.example.tabletalk.data.repositories.UserRepository
import com.example.tabletalk.databinding.FragmentUserBinding

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val userId = UserRepository.getInstance().getLoggedUserId() ?: throw Exception("User not logged in")
        val fragment = UserFragment.newInstance(userId)
        fragment.setOnCreate(object : OnCreateListener {
            override fun onCreate(binding: FragmentUserBinding?) {
                binding?.profileToolbar?.inflateMenu(R.menu.profile_menu)
                binding?.profileToolbar?.setOnMenuItemClickListener {
                    onMenuItemClick(it)
                }
            }
        })
        fragment.setOnRestaurantClickListener(object : OnPostItemClickListener {
            override fun onClickListener(post: InflatedPost) {
                val action =
                    ProfileFragmentDirections.actionGlobalRestaurantPageFragment(
                        post.restaurantId
                    )
                findNavController().navigate(action)
            }
        })
        fragment.setOnEditPostListener(object : OnPostItemClickListener {
            override fun onClickListener(post: InflatedPost) {
                val action = ProfileFragmentDirections.actionProfileFragmentToPostFormFragment(post.id)
                findNavController().navigate(action)
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
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToEditUserFragment())
            }
            R.id.logout_profile -> {
                UserRepository.getInstance().logout()
            }
            else -> return false
        }

        return true
    }
}