package com.example.tabletalk.fragments.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabletalk.data.repositories.UserRepository
import com.example.tabletalk.utils.Validator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {
    val email = MutableLiveData("")
    val password = MutableLiveData("")

    val isEmailValid = MutableLiveData(true)
    val isPasswordValid = MutableLiveData(true)

    private val validator = Validator()

    val isFormValid: Boolean
        get() = isEmailValid.value == true && isPasswordValid.value == true

    fun login(onFailure: (error: Exception?) -> Unit) {
        validateForm()
        if (!isFormValid) {
            onFailure(null)
            return
        }

        viewModelScope.launch {
            try {
                val email = email.value ?: throw Exception("Email is required")
                val password = password.value ?: throw Exception("Password is required")
                UserRepository.getInstance().signIn(email, password)
            } catch (e: Exception) {
                Log.e("Login", "Error signing in user", e)
                withContext(Dispatchers.Main) { onFailure(e) }
            }
        }
    }

    private fun validateForm() {
        isEmailValid.value = validator.validateEmail(email.value)
        isPasswordValid.value = validator.validatePassword(password.value)
    }
}