package com.example.tabletalk

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tabletalk.fragments.postForm.PostFormFragment

class AddPostFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_post, container, false)

        val restaurant = AddPostFragmentArgs.fromBundle(requireArguments()).restaurant
        getChildFragmentManager().beginTransaction()
            .replace(R.id.container, PostFormFragment.newInstance(restaurant))
            .commitNow()

        return view
    }
}