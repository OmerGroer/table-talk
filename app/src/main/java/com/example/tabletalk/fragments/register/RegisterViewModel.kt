package com.example.tabletalk.fragments.register

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabletalk.utils.Validator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel : ViewModel() {
    val name = MutableLiveData("")
    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val confirmPassword = MutableLiveData("")

    val isNameValid = MutableLiveData(true)
    val isEmailValid = MutableLiveData(true)
    val isPasswordValid = MutableLiveData(true)
    val isConfirmPasswordValid = MutableLiveData(true)

    val isFormValid: Boolean
        get() = isNameValid.value == true && isEmailValid.value == true
                && isPasswordValid.value == true && isConfirmPasswordValid.value == true

    private val validator = Validator()

    fun register(onSuccess: () -> Unit, onFailure: (error: Exception?) -> Unit) {
        validateForm()

        if (!isFormValid) {
            onFailure(null)
            return
        }

        try {
            createUserConcurrently(onSuccess, onFailure)
        } catch (e: Exception) {
            Log.e("Register", "Error registering user", e)
            onFailure(e)

        }
    }

    private fun createUserConcurrently(
        onSuccess: () -> Unit,
        onFailure: (error: Exception?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                Log.e("Register", "Error registering user", e)
                withContext(Dispatchers.Main) { onFailure(e) }
            }
        }
    }

    private fun validateForm() {
        isNameValid.value = name.value?.isNotEmpty() == true
        isEmailValid.value = validator.validateEmail(email.value)
        isPasswordValid.value = validator.validatePassword(password.value)
        isConfirmPasswordValid.value = validator.validateConfirmPassword(password.value, confirmPassword.value)
    }
}