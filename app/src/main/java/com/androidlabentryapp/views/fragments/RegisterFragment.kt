package com.androidlabentryapp.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.androidlabentryapp.R

class RegisterFragment : Fragment() {
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
        val rootView = inflater.inflate(R.layout.fragment_register, container, false)

        initUi(rootView)

        return rootView
    }

    private fun initUi(rootView: View) {
        val registerButton = rootView.findViewById<Button>(R.id.register_button_register).apply {
            setOnClickListener {
                navController.navigate(R.id.action_registerFragment_to_accountFragment)
            }
        }

        val enterTextView = rootView.findViewById<TextView>(R.id.register_text_enter).apply {
            setOnClickListener {
                navController.navigate(R.id.action_registerFragment_to_authFragment)
            }
        }
    }
}