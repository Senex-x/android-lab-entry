package com.androidlabentryapp.views.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.androidlabentryapp.R
import com.androidlabentryapp.databinding.FragmentAuthBinding
import com.androidlabentryapp.utils.*
import com.androidlabentryapp.views.dialogs.LoadingDialogFragment

class AuthFragment : Fragment() {
    private lateinit var navController: NavController
    private lateinit var contextState: Context
    private lateinit var activityState: Activity

    private var _binding: FragmentAuthBinding? = null
    private val binding
        get() = _binding!!

    private val navigationActionToAccount = { _: View? ->
        navController.navigate(R.id.action_authFragment_to_accountFragment)
    }

    private fun navigateToAccount() = navigationActionToAccount(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = findNavController()
        contextState = requireContext()
        activityState = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)

        if (contextState.isCurrentUserPresent()) {
            navigateToAccount()
        }

        return binding.apply { initUi() }.root
    }

    private fun FragmentAuthBinding.initUi() {
        val emailEditText = authEditTextLogin
        val passwordEditText = authEditTextPassword

        authButtonEnter.setOnClickListener {
            activityState.hideKeyboard()

            val (email, password) = getTextFrom(
                emailEditText, passwordEditText
            )

            when (false) {
                isEmailValid(email) -> {
                    R.string.error_unacceptable_email
                }
                isPasswordValid(password) -> {
                    passwordEditText.clearText()
                    R.string.error_unacceptable_password
                }
                else -> {
                    val loadingDialog = LoadingDialogFragment()
                    loadingDialog.show(
                        requireActivity().supportFragmentManager,
                        getString(R.string.tag_loading_dialog)
                    )

                    getUserOrNull(email, password) {
                        this?.run {
                            clearTextFor(
                                emailEditText,
                                passwordEditText
                            )

                            saveToLocal(contextState)
                            navigateToAccount()
                        } ?: R.string.error_user_not_found.showToast(contextState)

                        loadingDialog.dismiss()
                    }
                    null
                }
            }.showToast(contextState)
        }

        val navigationAction = { _: View? ->
            navController.navigate(R.id.action_authFragment_to_registerFragment)
        }

        authTextRegister
            .setOnClickListener(navigationAction)

        authTextRegisterDesc
            .setOnClickListener(navigationAction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
