package com.androidlabentryapp.utils

import android.content.Context

internal fun Context.deleteFileWithLogging(fileName: String) {
    log("""Trying to delete file $fileName
          >Result: ${if (deleteFile(fileName)) "OK" else "ERROR"}""".trimMargin(">"))
}