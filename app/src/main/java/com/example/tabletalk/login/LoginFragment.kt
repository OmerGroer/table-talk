package com.example.tabletalk.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tabletalk.R
import com.example.tabletalk.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container, false
        )
        bindViews(binding)

//        if (FirebaseAuth.getInstance().currentUser != null) {
//            val action =
//                LoginFragmentDirections.actionLoginFragmentToPostsListFragment()
//            Navigation.findNavController(view).navigate(action)
//        }

        binding.registerButton.setOnClickListener {
            TODO("implement")
        }

        binding.loginButton.setOnClickListener {
            showProgressBar()
            viewModel.login({ onLoginSuccess() }, { error -> onLoginFailure(error) })
        }

        return binding.root
    }

    private fun bindViews(binding: FragmentLoginBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun onLoginSuccess() {
    }

    private fun onLoginFailure(error: Exception?) {
        if (error != null) {
            Log.e("Login", "Error signing in user", error)
            handleLoginError(error)
        }

        showLoginButton()
    }

    private fun handleLoginError(error: Exception) {
        when (error) {
//            is FirebaseAuthInvalidUserException, is FirebaseAuthInvalidCredentialsException -> {
//                BasicAlert("Login Error", "User not found", requireContext()).show()
//            }
//
//            else -> {
//                BasicAlert("Login Error", "An error occurred", requireContext()).show()
//            }
        }
    }

    private fun showLoginButton() {
        binding.loginButton.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.loginButton.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }
}