package com.androidlabentryapp.utils

import android.content.Context

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

internal fun Context.deleteFileWithLogging(fileName: String) =
    log("""Trying to delete file $fileName
          >Result: ${if (deleteFile(fileName)) "OK" else "ERROR"}""".trimMargin(">"))
