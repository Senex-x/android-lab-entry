package com.androidlabentryapp.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.androidlabentryapp.R

class AuthFragment : Fragment() {
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
        val rootView = inflater.inflate(R.layout.fragment_auth, container, false)

        initUi(rootView)

        return rootView
    }

    private fun initUi(rootView: View) {
        val enterButton = rootView.findViewById<Button>(R.id.auth_button_enter).apply {
            setOnClickListener {
                navController.navigate(R.id.action_authFragment_to_accountFragment)
            }
        }

        val registerTextView = rootView.findViewById<TextView>(R.id.auth_text_register).apply {
            setOnClickListener {
                navController.navigate(R.id.action_authFragment_to_registerFragment)
            }
        }
    }
}
