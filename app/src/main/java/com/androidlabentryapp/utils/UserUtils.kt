package com.androidlabentryapp.utils

import android.content.Context
import com.androidlabentryapp.models.User
import com.google.gson.Gson

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