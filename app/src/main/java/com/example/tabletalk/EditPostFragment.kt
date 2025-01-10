package com.example.tabletalk

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tabletalk.fragments.postForm.PostFormFragment

class EditPostFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_post, container, false)

        val postId = EditPostFragmentArgs.fromBundle(requireArguments()).postId
        getChildFragmentManager().beginTransaction()
            .replace(R.id.container, PostFormFragment.newInstance(postId))
            .commitNow()

        return view
    }
}