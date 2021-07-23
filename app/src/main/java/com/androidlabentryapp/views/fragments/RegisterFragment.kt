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
import com.androidlabentryapp.utils.isEmailValid
import com.androidlabentryapp.utils.isPasswordValid
import com.androidlabentryapp.utils.isStringPresent
import com.androidlabentryapp.utils.toast

class RegisterFragment : Fragment() {
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
        val rootView = inflater.inflate(R.layout.fragment_register, container, false)

        initUi(rootView)

        return rootView
    }

    private fun initUi(rootView: View) {
        val enterTextView = rootView.findViewById<TextView>(R.id.register_text_enter).apply {
            setOnClickListener {
                navController.navigate(R.id.action_registerFragment_to_authFragment)
            }
        }

        val nameEditText = rootView.findViewById<EditText>(R.id.register_edit_text_name)
        val surnameEditText = rootView.findViewById<EditText>(R.id.register_edit_text_surname)
        val emailEditText = rootView.findViewById<EditText>(R.id.register_edit_text_login)
        val passwordEditText = rootView.findViewById<EditText>(R.id.register_edit_text_password)
        val confirmPasswordEditText = rootView.findViewById<EditText>(R.id.register_edit_text_password_confirm)

        val registerButton = rootView.findViewById<Button>(R.id.register_button_register).apply {
            setOnClickListener {
                val name = nameEditText.text.toString()
                val surname = surnameEditText.text.toString()
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                val confirmPassword = confirmPasswordEditText.text.toString()

                if(!isStringPresent(name)) {
                    contextState.toast("Введите имя")
                    return@setOnClickListener
                }

                if(!isStringPresent(surname)) {
                    contextState.toast("Введите фамилию")
                    return@setOnClickListener
                }

                if(!isEmailValid(email)) {
                    contextState.toast("Недопустимый почтовый адрес")
                    return@setOnClickListener
                }

                if(!isPasswordValid(password)) {
                    passwordEditText.setText("")
                    confirmPasswordEditText.setText("")
                    contextState.toast("Недопустимый пароль")
                    return@setOnClickListener
                }

                if(password != confirmPassword) {
                    passwordEditText.setText("")
                    confirmPasswordEditText.setText("")
                    contextState.toast("Пароли не совпадают")
                    return@setOnClickListener
                }

                navController.navigate(R.id.action_registerFragment_to_accountFragment)
            }
        }
    }




}