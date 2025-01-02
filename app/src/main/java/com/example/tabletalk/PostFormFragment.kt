package com.example.tabletalk

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.SearchView
import android.widget.TextView
import androidx.navigation.Navigation
import com.example.tabletalk.model.Model

private const val POST_ID = "postId"

class PostFormFragment : Fragment() {
    private var postId: String? = null

    private var restaurantName: TextView? = null
    private var restaurant: SearchView? = null
    private var review: EditText? = null
    private var rating: RatingBar? = null
    private var saveButton: Button? = null
    private var cancelButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            postId = it.getString(POST_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post_form, container, false)

        review = view.findViewById(R.id.restaurant_review)
        rating = view.findViewById(R.id.ratingBar)
        restaurant = view.findViewById(R.id.search_restaurant)
        restaurantName = view.findViewById(R.id.toolbar_title)
        saveButton = view.findViewById(R.id.save_changes_button)
        cancelButton = view.findViewById(R.id.cancel_button)

        if (postId != null) {
            val post = Model.shared.getPostById(postId as String)
            review?.setText(post.review)
            rating?.rating = post.rating.toFloat()
            restaurant?.visibility = View.GONE
            restaurantName?.text = post.restaurantName
        } else {
            cancelButton?.visibility = View.GONE
        }

        cancelButton?.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(postId: String) =
            PostFormFragment().apply {
                arguments = Bundle().apply {
                    putString(POST_ID, postId)
                }
            }
        fun newInstance() = PostFormFragment()
    }
}