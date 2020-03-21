package com.codingwithmitch.openapi.ui.auth


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.di.auth.AuthScope
import com.codingwithmitch.openapi.ui.auth.state.AuthStateEvent
import com.codingwithmitch.openapi.ui.auth.state.RegistrationFields
import kotlinx.android.synthetic.main.fragment_login.input_email
import kotlinx.android.synthetic.main.fragment_login.input_password
import kotlinx.android.synthetic.main.fragment_register.*
import javax.inject.Inject

@AuthScope
class RegisterFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : Fragment(R.layout.fragment_register) {

    val viewModel: AuthViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()

        register_button.setOnClickListener {
            registration()
        }
    }

    private fun subscribeToObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { authViewState ->
            authViewState.registrationFields?.let { registrationFields ->
                registrationFields.registrationEmail?.let { email ->
                    input_email.setText(email)
                }
                registrationFields.registrationUserName?.let { userName ->
                    input_username.setText(userName)
                }
                registrationFields.registrationPassword?.let { password ->
                    input_password.setText(password)
                }
                registrationFields.registrationConfirmPassword?.let { confirmPassword ->
                    input_password_confirm.setText(confirmPassword)
                }
            }
        })
    }

    private fun registration() {
        viewModel.setStateEvent(
            AuthStateEvent.RegistrationAttemptEvent(
                input_email.text.toString(),
                input_username.text.toString(),
                input_password.text.toString(),
                input_password_confirm.text.toString()
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setRegistrationFields(
            RegistrationFields(
                input_email.text.toString(),
                input_username.text.toString(),
                input_password.text.toString(),
                input_password_confirm.text.toString()
            )
        )
    }
}
