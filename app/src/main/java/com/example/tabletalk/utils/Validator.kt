package com.example.tabletalk.utils

import android.util.Patterns

class Validator {
    private val _passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$".toRegex()
    fun validateEmail(email: String?): Boolean {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePassword(password: String?): Boolean {
        return password != null && _passwordPattern.matches(password)
    }

    fun validateConfirmPassword(password: String?, confirmPassword: String?): Boolean {
        return password == confirmPassword
    }
}