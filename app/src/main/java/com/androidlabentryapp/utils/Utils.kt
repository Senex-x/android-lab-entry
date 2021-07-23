package com.androidlabentryapp.utils

import android.content.Context
import android.util.Log
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

internal fun Context.saveTextToFile(fileName: String, text: String) =
    openFileOutput(fileName, Context.MODE_PRIVATE).use {
        it.write(text.toByteArray())
    }

internal fun Context.getTextFromFile(fileName: String) =
    buildString {
        openFileInput(fileName).bufferedReader().useLines {
            it.forEach { string ->
                append(string).append("\n")
            }
        }
    }

internal fun Context.getCurrentUserLocally() =
    deserializeObject<User>(getTextFromFile(CURRENT_USER_FILE_NAME))

internal fun Context.saveCurrentUserLocally(user: User) =
    with(this) {
        log("Saving current user")
        saveTextToFile(CURRENT_USER_FILE_NAME, serializeObject(user))
    }

internal fun <T> serializeObject(generic: T): String {
    return Gson().toJson(generic)
}

internal inline fun <reified T> deserializeObject(serializedSource: String): T {
    return Gson().fromJson(serializedSource, T::class.java)
}

internal fun isStringPresent(string: String) =
    string.isNotEmpty() && string.isNotBlank()

internal fun isEmailValid(email: String) =
    Pattern.compile(EMAIL_VALIDATION_REGEX).matcher(email).matches()

internal fun isPasswordValid(password: String) =
    password.length > 5 &&
            Pattern.compile(PASSWORD_VALIDATION_REGEX).matcher(password).matches()

internal fun encodeString(string: String): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(string.toByteArray()))
        .toString(16)
        .padStart(32, '0')
}
