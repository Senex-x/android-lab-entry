package com.androidlabentryapp.views.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.androidlabentryapp.R
import com.androidlabentryapp.databinding.FragmentRegisterBinding
import com.androidlabentryapp.models.User
import com.androidlabentryapp.utils.*
import com.androidlabentryapp.views.dialogs.LoadingDialogFragment

class RegisterFragment : Fragment() {
    private lateinit var navController: NavController
    private lateinit var contextState: Context

    private lateinit var photoImageView: ImageView
    private var uploadedImageString: String? = null

    private var _binding: FragmentRegisterBinding? = null
    private val binding
        get() = _binding!!

    private val getContentActivityLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent(),
        handleChosenUri()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = findNavController()
        contextState = requireContext()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.apply { initUi() }.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun FragmentRegisterBinding.initUi() {
        photoImageView = registerImageViewPhoto

        registerButtonUploadPhoto.setOnClickListener {
            getContentActivityLauncher.launch("image/*")
        }

        registerButtonRegister.setOnClickListener {
            val (name, surname, email, password, confirmPassword) =
                getTextFrom(
                    registerEditTextName,
                    registerEditTextSurname,
                    registerEditTextLogin,
                    registerEditTextPassword,
                    registerEditTextPasswordConfirm
                )

            when (false) {
                name.isValidString(minLength = 2) -> {
                    R.string.message_enter_name
                }
                surname.isValidString(minLength = 2) -> {
                    R.string.message_enter_surname
                }
                isEmailValid(email) -> {
                    R.string.error_unacceptable_email
                }
                isPasswordValid(password) -> {
                    clearTextFor(
                        registerEditTextPassword,
                        registerEditTextPasswordConfirm
                    )
                    R.string.error_unacceptable_password
                }
                password == confirmPassword -> {
                    clearTextFor(
                        registerEditTextPassword,
                        registerEditTextPasswordConfirm
                    )
                    R.string.error_password_mismatch
                }
                else -> {
                    val loadingDialog = LoadingDialogFragment().apply {
                        show(
                            requireActivity().supportFragmentManager,
                            getString(R.string.tag_loading_dialog)
                        )
                    }

                    isEmailPresent(email) check@{
                        loadingDialog.dismiss()
                        if (this) {
                            log("Email is already taken")
                            contextState.getString(R.string.error_email_taken)
                            return@check
                        }

                        User(
                            email,
                            password,
                            name = name[0].toUpperCase() +
                                    if (name.length > 1) name.substring(1) else "",
                            surname = surname[0].toUpperCase() +
                                    if (surname.length > 1) surname.substring(1) else "",
                            image = uploadedImageString ?: ""
                        ).run {
                            saveToCloud()
                            saveToLocal(contextState)
                        }

                        navController.navigate(R.id.action_registerFragment_to_accountFragment)
                    }
                    null
                }
            }.showToast(contextState)
        }

        val navigationAction: (View) -> Unit = {
            navController.navigate(R.id.action_registerFragment_to_authFragment)
        }

        registerTextEnter.setOnClickListener(navigationAction)

        registerTextEnterDesc.setOnClickListener(navigationAction)
    }

    private fun handleChosenUri(): (Uri?) -> Unit = handler@{
        if(it == null) {
            contextState.toast(R.string.error_photo_not_picked)
            return@handler
        }
        val bitmap = contextState.handleBitmap(imageUri = it)

        uploadedImageString = bitmap.compress(quality = 30).convertToString()

        photoImageView.run {
            setImageBitmap(bitmap)
            setBackgroundColor(resources.getColor(R.color.white))
        }
    }
}