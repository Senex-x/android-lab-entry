package com.androidlabentryapp.views.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
                val loadingDialog = LoadingDialogFragment()
                loadingDialog.show(
                    requireActivity().supportFragmentManager,
                    getString(R.string.tag_loading_dialog)
                )

                val name = nameEditText.text.toString()
                val surname = surnameEditText.text.toString()
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                val confirmPassword = confirmPasswordEditText.text.toString()

                if (!isStringPresent(name) || name.length < 2) {
                    contextState.toast(getString(R.string.register_message_enter_name))
                    loadingDialog.dismiss()
                    return@setOnClickListener
                }

                if (!isStringPresent(surname) || surname.length < 2) {
                    loadingDialog.dismiss()
                    contextState.toast(getString(R.string.register_message_enter_surname))
                    return@setOnClickListener
                }

                if (!isEmailValid(email)) {
                    loadingDialog.dismiss()
                    contextState.toast(getString(R.string.error_unacceptable_email))
                    return@setOnClickListener
                }

                if (!isPasswordValid(password)) {
                    passwordEditText.setText("")
                    confirmPasswordEditText.setText("")
                    loadingDialog.dismiss()
                    contextState.toast(getString(R.string.error_unacceptable_password))
                    return@setOnClickListener
                }

                if (password != confirmPassword) {
                    passwordEditText.setText("")
                    confirmPasswordEditText.setText("")
                    loadingDialog.dismiss()
                    contextState.toast(getString(R.string.error_password_mismatch))
                    return@setOnClickListener
                }

                log("Checking email")
                isEmailPresent(email) { isPresent ->
                    loadingDialog.dismiss()
                    if (isPresent) {
                        contextState.toast(getString(R.string.error_email_taken))
                    } else {
                        val newUser = User(
                            email,
                            password,
                            name[0].uppercaseChar() + name.substring(1),
                            surname[0].uppercaseChar() + surname.substring(1),
                            uploadedImageString ?: ""
                        )

                        saveUserToCloud(newUser)
                        contextState.saveCurrentUser(newUser)

                        navController.navigate(R.id.action_registerFragment_to_accountFragment)
                    }
                }
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
        if (requestCode == pickPhotoCode && resultCode == Activity.RESULT_OK && data != null) {
            data.data?.let { uri ->
                val bitmap = contextState.handleBitmap(uri)

                with(photoImageView) {
                    setImageBitmap(bitmap)
                    setBackgroundColor(resources.getColor(R.color.white))
                }

                uploadedImageString = bitmapToString(compressBitmap(bitmap, 30))
            }
        }
    }
}