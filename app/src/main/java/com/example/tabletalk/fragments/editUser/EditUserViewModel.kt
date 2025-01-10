package com.example.tabletalk.fragments.editUser

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabletalk.data.repositories.UserRepository
import com.example.tabletalk.utils.Validator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditUserViewModel : ViewModel() {
    val name = MutableLiveData("")
    val oldPassword = MutableLiveData("")
    val password = MutableLiveData("")
    val confirmPassword = MutableLiveData("")
    val avatarUri = MutableLiveData("")

    val isNameValid = MutableLiveData(true)
    val isPasswordValid = MutableLiveData(true)
    val isConfirmPasswordValid = MutableLiveData(true)

    val isLoading = MutableLiveData(false)

    val isFormValid: Boolean
        get() = isNameValid.value == true && isPasswordValid.value == true &&
                isConfirmPasswordValid.value == true

    private val validator = Validator()

    init {
        fetchUserDetails()
    }

    private fun fetchUserDetails() {
        isLoading.value = true
        try {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val userId = UserRepository.getInstance().getLoggedUserId()
                        ?: throw Exception("User not logged in")
                    val user = UserRepository.getInstance().getById(userId)
                        ?: throw Exception("User not found")

                    withContext(Dispatchers.Main) {
                        name.value = user.username
                        avatarUri.value = user.avatarUrl!!
                    }
                } catch (e: Exception) {
                    Log.e("Edit", "Error fetching user details", e)
                } finally {
                    withContext(Dispatchers.Main) { isLoading.value = false }
                }
            }
        } catch (e: Exception) {
            Log.e("Edit", "Error fetching user details", e)
        }
    }

    fun submit(onSuccess: () -> Unit, onFailure: (error: Exception?) -> Unit) {
        validateForm()

        if (!isFormValid) {
            onFailure(null)
            return
        }

        try {
            isLoading.value = true
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    UserRepository.getInstance()
                        .update(oldPassword.value!!, password.value!!, name.value!!, avatarUri.value!!)

                    withContext(Dispatchers.Main) { onSuccess() }
                } catch (e: Exception) {
                    Log.e("Edit", "Error registering user", e)
                    withContext(Dispatchers.Main) { onFailure(e) }
                } finally {
                    withContext(Dispatchers.Main) { isLoading.value = false }
                }
            }
        } catch (e: Exception) {
            Log.e("Edit", "Error registering user", e)
            onFailure(e)
        }
    }

    private fun validateForm() {
        isNameValid.value = name.value?.isNotEmpty() == true

        if (password.value?.isNotEmpty() == true) {
            isPasswordValid.value = validator.validatePassword(password.value)
            isConfirmPasswordValid.value =
                validator.validateConfirmPassword(password.value, confirmPassword.value)
        }
    }
}