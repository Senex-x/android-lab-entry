package com.androidlabentryapp.views.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.androidlabentryapp.R
import com.androidlabentryapp.database.getUserOrNull
import com.androidlabentryapp.models.User
import com.androidlabentryapp.utils.*
import com.androidlabentryapp.utils.CURRENT_USER_FILE_NAME
import com.androidlabentryapp.utils.EMAIL_VALIDATION_REGEX
import com.androidlabentryapp.utils.saveTextToFile
import com.androidlabentryapp.utils.toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.regex.Pattern

class AuthFragment : Fragment() {
    private lateinit var navController: NavController
    private lateinit var contextState: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = this.findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_auth, container, false)

        contextState = requireContext()

        initUi(rootView)

        return rootView
    }

    private fun initUi(rootView: View) {
        val emailEditText = rootView.findViewById<EditText>(R.id.auth_edit_text_login)

        val passwordEditText = rootView.findViewById<EditText>(R.id.auth_edit_text_password)

        val enterButton = rootView.findViewById<Button>(R.id.auth_button_enter).apply {
            setOnClickListener {
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()

                if (!isEmailValid(email)) {
                    contextState.toast("Логин указан неверно")
                    return@setOnClickListener
                }

                if (!isPasswordValid(password)) {
                    contextState.toast("Пароль указан неверно")
                    passwordEditText.setText("")
                    return@setOnClickListener
                }

                getUserOrNull(email, password) {
                    if (it != null) {
                        emailEditText.setText("")
                        passwordEditText.setText("")

                        saveUserState(it)

                        navController.navigate(R.id.action_authFragment_to_accountFragment)                    } else {
                        contextState.toast("Пользователь не найден")
                    }
                }
            }
        }

        val registerTextView = rootView.findViewById<TextView>(R.id.auth_text_register).apply {
            setOnClickListener {
                navController.navigate(R.id.action_authFragment_to_registerFragment)
            }
        }
    }

    private fun saveUserState(user: User) {
        log("Saving current user info to file")
        contextState.saveTextToFile(CURRENT_USER_FILE_NAME, serializeObject(user))
    }

    private fun isEmailValid(email: String) =
        Pattern.compile(EMAIL_VALIDATION_REGEX).matcher(email).matches()

    private fun isPasswordValid(password: String) =
        password.isNotEmpty() || password.length > 5
}
