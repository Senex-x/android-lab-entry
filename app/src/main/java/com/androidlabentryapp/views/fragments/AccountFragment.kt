package com.androidlabentryapp.views.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.androidlabentryapp.R
import com.androidlabentryapp.databinding.FragmentAccountBinding
import com.androidlabentryapp.databinding.FragmentAuthBinding
import com.androidlabentryapp.databinding.FragmentRegisterBinding
import com.androidlabentryapp.models.User
import com.androidlabentryapp.utils.*

class AccountFragment : Fragment() {
    private val pickPhotoCode = 0xaaa

    private lateinit var navController: NavController
    private lateinit var contextState: Context
    private lateinit var currentUser: User

    private lateinit var photoImageView: ImageView

    private var _binding: FragmentAccountBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = findNavController()
        contextState = requireContext()
        currentUser = contextState.getCurrentUser()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.apply { initUi() }.root
    }

    @SuppressLint("SetTextI18n")
    private fun FragmentAccountBinding.initUi() {
        photoImageView = accountImagePhoto.apply {
            val imageString = currentUser.image
            if (imageString.isNotEmpty()) {
                setBackgroundColor(resources.getColor(R.color.white))
                setImageBitmap(imageString.convertToBitmap())
            }
        }

        accountButtonUploadPhoto.setOnClickListener {
            startActivityForResult(
                Intent(Intent.ACTION_PICK).apply {
                    type = "image/*"
                }, pickPhotoCode
            )
        }

        accountTextName.text =
            currentUser.name + " " + currentUser.surname

        accountTextEmail.text =
            currentUser.email

        accountButtonLogOut.setOnClickListener {
            contextState.deleteCurrentUser()
            navController.navigate(R.id.action_accountFragment_to_authFragment)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != pickPhotoCode || resultCode != Activity.RESULT_OK || data == null || data.data == null) {
            contextState.toast(getString(R.string.error_fail))
            return
        }

        val bitmap = contextState.handleBitmap(imageUri = data.data!!)

        photoImageView.run {
            setImageBitmap(bitmap)
            setBackgroundColor(resources.getColor(R.color.white))
        }

        val imageString = bitmap.compress(30).convertToString()

        currentUser = currentUser.run {
            User(
                email,
                password,
                name,
                surname,
                imageString
            ).apply {
                updateImage(imageString)
                saveToLocal(contextState)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}