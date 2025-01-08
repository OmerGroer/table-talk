package com.example.tabletalk.fragments.login

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
import com.example.tabletalk.data.repositories.UserRepository
import com.example.tabletalk.databinding.FragmentLoginBinding
import com.example.tabletalk.utils.BasicAlert
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()
    private var binding: FragmentLoginBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container, false
        )
        bindViews(binding)

        if (UserRepository.getInstance().isLogged()) {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToPostsListFragment())
        }

        binding?.registerButton?.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        binding?.loginButton?.setOnClickListener {
            showProgressBar()
            viewModel.login({ error -> onLoginFailure(error) })
        }

        return binding?.root
    }

    private fun bindViews(binding: FragmentLoginBinding?) {
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
    }

    private fun onLoginFailure(error: Exception?) {
        UserRepository.getInstance().logout()

        if (error != null) {
            Log.e("Login", "Error signing in user", error)
            handleLoginError(error)
        }

        showLoginButton()
    }

    private fun handleLoginError(error: Exception) {
        when (error) {
            is FirebaseAuthInvalidUserException, is FirebaseAuthInvalidCredentialsException -> {
                BasicAlert("Login Error", "User not found", requireContext()).show()
            }
            else -> {
                BasicAlert("Login Error", "An error occurred", requireContext()).show()
            }
        }
    }

    private fun showLoginButton() {
        binding?.loginButton?.visibility = View.VISIBLE
        binding?.progressBar?.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding?.loginButton?.visibility = View.GONE
        binding?.progressBar?.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}