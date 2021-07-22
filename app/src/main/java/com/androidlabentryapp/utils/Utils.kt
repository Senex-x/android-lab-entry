package com.androidlabentryapp.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson

internal const val EMAIL_VALIDATION_REGEX =
    "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

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

internal fun <T> serializeObject(generic: T): String {
    return Gson().toJson(generic)
}

internal inline fun <reified T> deserializeObject(serializedSource: String): T {
    return Gson().fromJson(serializedSource, T::class.java)
}