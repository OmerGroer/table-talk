package com.example.tabletalk

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class AddPostFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_post, container, false)

        val restaurantId = AddPostFragmentArgs.fromBundle(requireArguments()).restaurantId
        getChildFragmentManager().beginTransaction()
            .replace(R.id.container, PostFormFragment.newInstance(restaurantId))
            .commitNow()

        return view
    }
}