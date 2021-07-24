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

internal fun Context.toast(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

internal fun log(message: String?) =
    Log.d("App-debug", message ?: "null")

internal const val CURRENT_USER_FILE_NAME = "current-user.txt"

internal fun Context.isCurrentUserPresent() =
    try {
        getTextFromFile(CURRENT_USER_FILE_NAME)
        log("Local user found")
        true
    } catch (e: Exception) {
        log("Local user not found")
        false
    }

internal fun Context.getCurrentUser() =
    deserializeObject<User>(getTextFromFile(CURRENT_USER_FILE_NAME))

internal fun Context.saveCurrentUser(user: User) =
    with(this) {
        log("Saving current user")
        saveTextToFile(CURRENT_USER_FILE_NAME, serializeObject(user))
    }

internal fun Context.deleteCurrentUser() =
    deleteFileWithLogging(CURRENT_USER_FILE_NAME)


internal fun <T> serializeObject(generic: T) =
    Gson().toJson(generic)


internal inline fun <reified T> deserializeObject(serializedSource: String) =
    Gson().fromJson(serializedSource, T::class.java)

internal fun isStringPresent(string: String) =
    string.isNotEmpty() && string.isNotBlank()

internal fun isEmailValid(email: String) =
    Pattern.compile(EMAIL_VALIDATION_REGEX).matcher(email).matches()

internal fun isPasswordValid(password: String) =
    password.length > 5 &&
            Pattern.compile(PASSWORD_VALIDATION_REGEX).matcher(password).matches()

internal fun Activity.hideKeyboard() =
    currentFocus?.let {
        it.clearFocus()
        (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            it.windowToken,
            0
        )
    }