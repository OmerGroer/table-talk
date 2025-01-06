package com.example.tabletalk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tabletalk.adapter.CommentsRecyclerAdapter
import com.example.tabletalk.data.model.Model

private const val POST_ID = "postId"
private const val TAG = "comments"

class CommentsFragment : DialogFragment() {
    private var postId: String? = null

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
        val view = inflater.inflate(R.layout.fragment_comments, container, false)

        val comments = Model.shared.getCommentsByPostId(postId as String)

        val recyclerView: RecyclerView = view.findViewById(R.id.comments_recycler_view)
        recyclerView.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = CommentsRecyclerAdapter(comments)

        val sendButton: ImageView = view.findViewById(R.id.send_button)
        sendButton.setOnClickListener {
//            Model.shared.addComment()
        }

        val closeButton: ImageView = view.findViewById(R.id.close_button)
        closeButton.setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    companion object {
        fun display(fragmentManager: FragmentManager, postId: String) {
            val fragment = CommentsFragment()
            val args = Bundle()
            args.putString(POST_ID, postId)
            fragment.arguments = args
            fragment.show(fragmentManager, TAG)
        }
    }
}