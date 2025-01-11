package com.example.tabletalk.fragments.postForm

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tabletalk.R
import com.example.tabletalk.databinding.FragmentPostFormBinding
import com.example.tabletalk.utils.BasicAlert
import com.yalantis.ucrop.UCrop

class PostFormFragment : Fragment() {
    private val args: PostFormFragmentArgs by navArgs()
    private val viewModel: PostFormViewModel by viewModels()
    private var binding: FragmentPostFormBinding? = null

    private val imagePicker: ActivityResultLauncher<String> = getImagePicker()
    private val uCropLauncher: ActivityResultLauncher<Intent> = getUCropLauncher()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_post_form, container, false
        )
        bindViews()

        setupToolbar()
        setupLoading()
        setupImagePicker()

        val postId = args.postId
        if (postId != null) {
            viewModel.initForm(postId)
        } else {
            val restaurant = args.restaurant ?: throw Exception("Restaurant not found")
            viewModel.initForm(restaurant)
        }

        return binding?.root
    }

    private fun bindViews() {
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun setupToolbar() {
        binding?.postToolbar?.setNavigationIcon(R.drawable.arrow_back)
        binding?.postToolbar?.setNavigationOnClickListener {
            if (viewModel.isLoading.value == false) {
                findNavController().popBackStack()
            }
        }
        binding?.postToolbar?.inflateMenu(R.menu.post_form)
        binding?.postToolbar?.setOnMenuItemClickListener {
            if (viewModel.isLoading.value == false) {
                viewModel.submit({
                    BasicAlert("Success", "Review saved successfully", requireContext()).show()
                    findNavController().popBackStack()
                }, { error ->
                    BasicAlert("Fail", if (error == null) "Invalid form" else "Failed to save review", requireContext()).show()
                })
            }
            true
        }

        viewModel.restaurantName.observe(viewLifecycleOwner) { restaurantName ->
            binding?.toolbarTitle?.text = restaurantName
        }
    }

    private fun setupLoading() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) binding?.progressBar?.visibility = View.VISIBLE
            else binding?.progressBar?.visibility = View.INVISIBLE
        }
    }

    private fun setupImagePicker() {
        binding?.imageView?.setOnClickListener {
            imagePicker.launch("image/*")
        }
        viewModel.imageUri.observe(viewLifecycleOwner) { uri ->
            if (uri != "") binding?.imageView?.setImageURI(uri.toUri())
        }
    }

    private fun getImagePicker() =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            binding?.imageView?.getImagePicker(uri, uCropLauncher)
        }

    private fun getUCropLauncher() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = UCrop.getOutput(result.data!!)
                binding?.imageView?.setImageURI(uri)
                viewModel.imageUri.value = uri.toString()
            }
        }
}