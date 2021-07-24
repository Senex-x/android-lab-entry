package com.androidlabentryapp.views.fragments

import android.app.Activity
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
    private lateinit var activityState: Activity

    private val navigationActionToAccount: (View?) -> Unit = {
        activityState.hideKeyboard()
        navController.navigate(R.id.action_authFragment_to_accountFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = this.findNavController()
        contextState = requireContext()
        activityState = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_auth, container, false)

        if (contextState.isCurrentUserPresent()) {
            navigationActionToAccount.invoke(null)
        }

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

                contextState.toast(
                    when (false) {
                        isEmailValid(email) -> {
                            getString(R.string.error_unacceptable_email)
                        }
                        isPasswordValid(password) -> {
                            passwordEditText.setText("")
                            getString(R.string.error_unacceptable_password)
                        }
                        else -> {
                            val loadingDialog = LoadingDialogFragment()
                            loadingDialog.show(
                                requireActivity().supportFragmentManager,
                                getString(R.string.tag_loading_dialog)
                            )

                            getUserOrNull(email, password) {
                                if (it != null) {
                                    emailEditText.setText("")
                                    passwordEditText.setText("")

                                    contextState.saveCurrentUser(it)
                                    navigationActionToAccount.invoke(null)
                                } else {
                                    contextState.toast(getString(R.string.error_user_not_found))
                                }
                                loadingDialog.dismiss()
                            }
                            null
                        }
                    })
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
