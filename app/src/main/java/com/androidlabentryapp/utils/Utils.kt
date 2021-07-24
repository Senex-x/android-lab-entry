package com.androidlabentryapp.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.androidlabentryapp.models.User
import com.google.gson.Gson
import java.math.BigInteger
import java.security.MessageDigest
import java.util.regex.Pattern

internal const val EMAIL_VALIDATION_REGEX =
    "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

internal const val PASSWORD_VALIDATION_REGEX =
    "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+\$"

internal fun Context.toast(message: String?) =
    message?.let {
        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
    }

internal fun log(message: String?) =
    Log.d("App-debug", message ?: "null")

internal fun Activity.hideKeyboard() =
    currentFocus?.let {
        it.clearFocus()
        (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            it.windowToken,
            0
        )
    }

internal fun isStringPresent(string: String) =
    string.isNotEmpty() && string.isNotBlank()

internal fun isEmailValid(email: String) =
    Pattern.compile(EMAIL_VALIDATION_REGEX).matcher(email).matches()

internal fun isPasswordValid(password: String) =
    password.length > 5 &&
            Pattern.compile(PASSWORD_VALIDATION_REGEX).matcher(password).matches()