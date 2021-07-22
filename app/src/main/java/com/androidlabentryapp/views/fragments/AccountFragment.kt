package com.androidlabentryapp.views.fragments

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

class AccountFragment : Fragment() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = this.findNavController()
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
        val imageView = rootView.findViewById<ImageView>(R.id.account_image_photo)

        val nameTextView = rootView.findViewById<TextView>(R.id.account_text_name)

        val emailTextView = rootView.findViewById<TextView>(R.id.account_text_email)

        val logOutButton = rootView.findViewById<Button>(R.id.account_button_log_out).apply {
            setOnClickListener {
                navController.navigate(R.id.action_accountFragment_to_authFragment)
            }
        }
    }
}