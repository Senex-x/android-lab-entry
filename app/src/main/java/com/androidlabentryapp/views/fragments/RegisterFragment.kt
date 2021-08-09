package com.androidlabentryapp.views.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.androidlabentryapp.R
import com.androidlabentryapp.models.User
import com.androidlabentryapp.utils.*
import com.androidlabentryapp.views.dialogs.LoadingDialogFragment

class RegisterFragment : Fragment() {
    private val pickPhotoCode = 0xfff

    private lateinit var navController: NavController
    private lateinit var contextState: Context

    private lateinit var photoImageView: ImageView
    private var uploadedImageString: String? = null

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
        with(rootView) {
            photoImageView = findViewById(R.id.register_image_view_photo)

            findViewById<Button>(R.id.register_button_upload_photo).setOnClickListener {
                startActivityForResult(
                    Intent(Intent.ACTION_PICK).apply {
                        type = "image/*"
                    }, pickPhotoCode
                )
            }

            val nameEditText =
                findViewById<EditText>(R.id.register_edit_text_name)
            val surnameEditText =
                findViewById<EditText>(R.id.register_edit_text_surname)
            val emailEditText =
                findViewById<EditText>(R.id.register_edit_text_login)
            val passwordEditText =
                findViewById<EditText>(R.id.register_edit_text_password)
            val confirmPasswordEditText =
                findViewById<EditText>(R.id.register_edit_text_password_confirm)

            findViewById<Button>(R.id.register_button_register).setOnClickListener {
                val (name, surname, email, password, confirmPassword) =
                    getTextFrom(
                        nameEditText,
                        surnameEditText,
                        emailEditText,
                        passwordEditText,
                        confirmPasswordEditText
                    )

                when (false) {
                    name.isValidString(minLength = 2) -> {
                        getString(R.string.message_enter_name)
                    }
                    surname.isValidString(minLength = 2) -> {
                        getString(R.string.message_enter_surname)
                    }
                    isEmailValid(email) -> {
                        getString(R.string.error_unacceptable_email)
                    }
                    isPasswordValid(password) -> {
                        clearTextFor(
                            passwordEditText,
                            confirmPasswordEditText
                        )
                        getString(R.string.error_unacceptable_password)
                    }
                    password == confirmPassword -> {
                        clearTextFor(
                            passwordEditText,
                            confirmPasswordEditText
                        )
                        getString(R.string.error_password_mismatch)
                    }
                    else -> {
                        val loadingDialog = LoadingDialogFragment()
                        loadingDialog.show(
                            requireActivity().supportFragmentManager,
                            getString(R.string.tag_loading_dialog)
                        )

                        isEmailPresent(email) {
                            loadingDialog.dismiss()
                            if (this) {
                                log("Email taken already")
                                contextState.toast(getString(R.string.error_email_taken))
                            } else {
                                User(
                                    email,
                                    password,
                                    name[0].uppercaseChar() + name.substring(1),
                                    surname[0].uppercaseChar() + surname.substring(1),
                                    uploadedImageString ?: ""
                                ).run {
                                    saveToCloud()
                                    saveToLocal(contextState)
                                }

                                navController.navigate(R.id.action_registerFragment_to_accountFragment)
                            }
                        }
                        null
                    }
                }.showToast(contextState)
            }

            val navigationAction: (View) -> Unit = {
                navController.navigate(R.id.action_registerFragment_to_authFragment)
            }

            findViewById<TextView>(R.id.register_text_enter)
                .setOnClickListener(navigationAction)

            findViewById<TextView>(R.id.register_text_enter_desc)
                .setOnClickListener(navigationAction)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickPhotoCode &&
            resultCode == Activity.RESULT_OK &&
            data != null
        ) {
            data.data?.let { uri ->
                val bitmap = contextState.handleBitmap(uri)

                uploadedImageString = bitmap.compress(30).convertToString()

                photoImageView.run {
                    setImageBitmap(bitmap)
                    setBackgroundColor(resources.getColor(R.color.white))
                }
            }
        }
    }
}