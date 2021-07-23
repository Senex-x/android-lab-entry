package com.androidlabentryapp.views.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
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
import com.androidlabentryapp.models.User
import com.androidlabentryapp.utils.*
import com.androidlabentryapp.utils.bitmapToString
import com.androidlabentryapp.utils.compressBitmap
import com.androidlabentryapp.utils.cutBitmapToSquare
import com.androidlabentryapp.utils.deleteCurrentUser
import com.androidlabentryapp.utils.getBitmapByUri
import com.androidlabentryapp.utils.getCurrentUser
import com.androidlabentryapp.utils.handleBitmapCutAndSampling
import com.androidlabentryapp.utils.stringToBitmap

class AccountFragment : Fragment() {
    private val pickPhotoCode = 0xaaa

    private lateinit var navController: NavController
    private lateinit var contextState: Context
    private lateinit var currentUser: User

    private lateinit var photoImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = this.findNavController()
        contextState = requireContext()
        currentUser = contextState.getCurrentUser()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_account, container, false)

        initUi(rootView)

        return rootView
    }

    private fun initUi(rootView: View) {
        with(rootView) {
            photoImageView = findViewById<ImageView>(R.id.account_image_photo).apply {
                val imageString = currentUser.image
                if (imageString.isNotEmpty()) {
                    setBackgroundColor(resources.getColor(R.color.white))
                    setImageBitmap(stringToBitmap(imageString))
                }
            }

            findViewById<Button>(R.id.account_button_upload_photo).setOnClickListener {
                startActivityForResult(
                    Intent(Intent.ACTION_PICK).apply {
                        type = "image/*"
                    }, pickPhotoCode
                )
            }

            val nameTextView = findViewById<TextView>(R.id.account_text_name).apply {
                text = currentUser.name + " " + currentUser.surname
            }

            val emailTextView = findViewById<TextView>(R.id.account_text_email).apply {
                text = currentUser.email
            }

            findViewById<Button>(R.id.account_button_log_out).setOnClickListener {
                contextState.deleteCurrentUser()
                navController.navigate(R.id.action_accountFragment_to_authFragment)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickPhotoCode && resultCode == Activity.RESULT_OK && data != null) {
            data.data?.let { uri ->
                val bitmap = contextState.handleBitmap(uri)

                with(photoImageView) {
                    setImageBitmap(bitmap)
                    setBackgroundColor(resources.getColor(com.androidlabentryapp.R.color.white))
                }

                saveUserImageToCloud(currentUser.email, bitmapToString(compressBitmap(bitmap, 30)))
            }
        }
    }
}