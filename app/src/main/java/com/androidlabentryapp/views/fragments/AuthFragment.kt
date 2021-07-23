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
import com.androidlabentryapp.utils.*
import com.androidlabentryapp.views.dialogs.LoadingDialogFragment

class AuthFragment : Fragment() {
    private lateinit var navController: NavController
    private lateinit var contextState: Context

    private val navigationActionToAccount: (View?) -> Unit = {
        navController.navigate(R.id.action_authFragment_to_accountFragment)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = this.findNavController()
        contextState = requireContext()

        try {
            contextState.getTextFromFile(CURRENT_USER_FILE_NAME)
            log("Local user found")
            navigationActionToAccount.invoke(null)
        } catch (e: Exception) {
            log("Local user not found")
        }
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
        with(rootView) {
            val emailEditText = findViewById<EditText>(R.id.auth_edit_text_login)

            val passwordEditText = findViewById<EditText>(R.id.auth_edit_text_password)

            findViewById<Button>(R.id.auth_button_enter).setOnClickListener {
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
                        navigationActionToAccount.invoke(null)
                    } else {
                        contextState.toast("Пользователь не найден")
                    }
                    loadingDialog.dismiss()
                }
            }

            val navigationAction: (View?) -> Unit = {
                navController.navigate(R.id.action_authFragment_to_registerFragment)
            }

            findViewById<TextView>(R.id.auth_text_register_desc)
                .setOnClickListener(navigationAction)

            findViewById<TextView>(R.id.auth_text_register)
                .setOnClickListener(navigationAction)
        }
    }
}
