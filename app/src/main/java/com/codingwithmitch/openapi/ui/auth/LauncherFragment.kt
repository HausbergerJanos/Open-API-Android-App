package com.codingwithmitch.openapi.ui.auth


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.codingwithmitch.openapi.R
import kotlinx.android.synthetic.main.fragment_launcher.*

class LauncherFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_launcher, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        login?.let { login ->
            login.setOnClickListener {
                navToLogin()
            }
        }

        register?.let { register ->
            register.setOnClickListener {
                navToRegistration()
            }
        }

        forgotPassword?.let { forgotPassword ->
            forgotPassword.setOnClickListener {
                navToForgotPassword()
            }
        }

        // Just prevent a random view focusable bug
        focusable_view.requestFocus()
    }

    private fun navToLogin() {
        findNavController().navigate(R.id.action_launcherFragment_to_loginFragment)
    }

    private fun navToRegistration() {
        findNavController().navigate(R.id.action_launcherFragment_to_registerFragment)
    }

    private fun navToForgotPassword() {
        findNavController().navigate(R.id.action_launcherFragment_to_forgotPasswordFragment)
    }

}
