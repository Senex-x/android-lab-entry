package com.androidlabentryapp.views.fragments

import android.content.Context
import android.os.Bundle
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
import com.androidlabentryapp.utils.getUserOrNull
import com.androidlabentryapp.utils.*
import com.androidlabentryapp.utils.EMAIL_VALIDATION_REGEX
import com.androidlabentryapp.utils.toast
import com.androidlabentryapp.views.dialogs.LoadingDialogFragment
import java.util.regex.Pattern

class AuthFragment : Fragment() {
    private lateinit var navController: NavController
    private lateinit var contextState: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = this.findNavController()
        contextState = requireContext()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_auth, container, false)

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

                val loadingDialog = LoadingDialogFragment()
                loadingDialog.show(requireActivity().supportFragmentManager, "loadingDialog")

                getUserOrNull(email, password) {
                    if (it != null) {
                        emailEditText.setText("")
                        passwordEditText.setText("")

                        contextState.saveCurrentUser(it)
                        navController.navigate(R.id.action_authFragment_to_accountFragment)
                    } else {
                        contextState.toast("Пользователь не найден")
                    }
                    loadingDialog.dismiss()
                }
            }
        }

        val registerTextView = rootView.findViewById<TextView>(R.id.auth_text_register).apply {
            setOnClickListener {
                navController.navigate(R.id.action_authFragment_to_registerFragment)
            }
        }
    }
}
