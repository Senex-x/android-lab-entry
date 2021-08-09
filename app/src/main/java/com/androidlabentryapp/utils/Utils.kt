package com.androidlabentryapp.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.util.regex.Pattern

internal const val EMAIL_VALIDATION_REGEX =
    "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

internal const val PASSWORD_VALIDATION_REGEX =
    "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+\$"

internal fun Context.toast(message: String?) =
    message?.let {
        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
    }

internal fun String?.showToast(context: Context) =
    context.toast(this)

internal fun log(message: String?) =
    Log.d("App-debug", message ?: "null")

internal fun Activity.hideKeyboard() =
    currentFocus?.let {
        it.clearFocus()
        (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(it.windowToken, 0)
    }

private fun String.present() =
    isNotEmpty() && isNotBlank()

internal fun String.isValidString(minLength: Int) =
    present() || length >= minLength

internal fun isEmailValid(email: String) =
    Pattern.compile(EMAIL_VALIDATION_REGEX).matcher(email).matches()

internal fun isPasswordValid(password: String) =
    password.length >= 6 &&
            Pattern.compile(PASSWORD_VALIDATION_REGEX).matcher(password).matches()

internal fun EditText.clearText() =
    setText("")

internal fun clearTextFor(vararg fields: EditText) {
    for (field in fields) {
        field.clearText()
    }
}

// TODO: Index exception handling
internal fun getTextFrom(vararg fields: EditText) =
    FiveFieldsTextHolder().also(
        fun(holder: FiveFieldsTextHolder) { // Declared as anonymous to allow local return
            for ((i, field) in fields.withIndex()) {
                if (i >= holder.size) {
                    return
                }
                holder[i] = field.text.toString()
            }
        })

internal class FiveFieldsTextHolder(
    private var c0: String = "",
    private var c1: String = "",
    private var c2: String = "",
    private var c3: String = "",
    private var c4: String = ""
) {
    internal val size = 5

    internal operator fun set(index: Int, value: String) {
        when (index) {
            0 -> c0 = value
            1 -> c1 = value
            2 -> c2 = value
            3 -> c3 = value
            4 -> c4 = value
        }
    }

    internal operator fun component1() = c0
    internal operator fun component2() = c1
    internal operator fun component3() = c2
    internal operator fun component4() = c3
    internal operator fun component5() = c4
}