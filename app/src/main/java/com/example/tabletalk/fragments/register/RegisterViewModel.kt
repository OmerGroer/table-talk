package com.example.tabletalk.fragments.register

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabletalk.data.model.User
import com.example.tabletalk.data.repositories.UserRepository
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
            createUser(onSuccess, onFailure)
        } catch (e: Exception) {
            Log.e("Register", "Error registering user", e)
            onFailure(e)

        }
    }

    private fun createUser(
        onSuccess: () -> Unit,
        onFailure: (error: Exception?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                UserRepository.getInstance().createAuthUser(email.value!!, password.value!!)
                UserRepository.getInstance().saveUserInDB(buildUser())
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                Log.e("Register", "Error registering user", e)
                withContext(Dispatchers.Main) { onFailure(e) }
            }
        }
    }

    private fun buildUser(): User {
        return User(
            id = UserRepository.getInstance().getLoggedUserId()!!,
            username = name.value!!,
            email = email.value!!
        )
    }

    private fun validateForm() {
        isNameValid.value = name.value?.isNotEmpty() == true
        isEmailValid.value = validator.validateEmail(email.value)
        isPasswordValid.value = validator.validatePassword(password.value)
        isConfirmPasswordValid.value = validator.validateConfirmPassword(password.value, confirmPassword.value)
    }
}