package com.example.tabletalk.fragments.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.tabletalk.R
import com.example.tabletalk.adapter.OnPostItemClickListener
import com.example.tabletalk.data.model.InflatedPost
import com.example.tabletalk.databinding.FragmentUserBinding

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
        fragment.setOnCreate(object : OnCreateListener {
            override fun onCreate(binding: FragmentUserBinding?) {
                binding?.profileToolbar?.setNavigationIcon(R.drawable.arrow_back)
                binding?.profileToolbar?.setNavigationOnClickListener {
                    findNavController().popBackStack()
                }
            }
        })
        getChildFragmentManager().beginTransaction()
            .replace(R.id.container, fragment)
            .commitNow()

        return view
    }
}