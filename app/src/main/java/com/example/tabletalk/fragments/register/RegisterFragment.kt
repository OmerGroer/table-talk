package com.example.tabletalk.fragments.register

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tabletalk.R
import com.example.tabletalk.databinding.FragmentRegisterBinding
import com.example.tabletalk.utils.BasicAlert
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class RegisterFragment : Fragment() {
    private val viewModel: RegisterViewModel by viewModels()
    private var binding: FragmentRegisterBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_register, container, false
        )
        bindViews(binding)

        binding?.registerButton?.setOnClickListener {
            showProgressBar()
            viewModel.register({ onRegisterSuccess() }, { error -> onRegisterFailure(error) })
        }

        setupToolbar()

        return binding?.root
    }

    private fun bindViews(binding: FragmentRegisterBinding?) {
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
    }

    private fun onRegisterSuccess() {
        findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToPostsListFragment())
    }

    private fun onRegisterFailure(error: Exception?) {
        if (error != null) {
            Log.e("Register", "Error Registering", error)
            handleRegisterError(error)
        }

        showRegisterButton()
    }

    private fun handleRegisterError(error: Exception) {
        when (error) {
            is FirebaseAuthUserCollisionException -> {
                BasicAlert(
                    "Register Error",
                    "User with this email already exists",
                    requireContext()
                ).show()
            }

            else -> {
                BasicAlert("Register Error", "An error occurred", requireContext()).show()
            }
        }
    }

    private fun setupToolbar() {
        binding?.loginToolbar?.setNavigationIcon(R.drawable.arrow_back)
        binding?.loginToolbar?.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun showRegisterButton() {
        binding?.registerButton?.visibility = View.VISIBLE
        binding?.progressBar?.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding?.registerButton?.visibility = View.GONE
        binding?.progressBar?.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}