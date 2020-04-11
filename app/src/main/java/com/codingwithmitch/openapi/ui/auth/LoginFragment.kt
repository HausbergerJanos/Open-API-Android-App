package com.codingwithmitch.openapi.ui.auth


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.di.auth.AuthScope
import com.codingwithmitch.openapi.ui.auth.state.AuthStateEvent
import com.codingwithmitch.openapi.ui.auth.state.LoginFields
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

@AuthScope
class LoginFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): Fragment(R.layout.fragment_login) {

    private val TAG: String = "AppDebug"

    val viewModel: AuthViewModel by viewModels{
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeObservers()

        login_button.setOnClickListener {
            login()
        }
    }

    fun subscribeObservers(){
        viewModel.viewState.observe(viewLifecycleOwner, Observer{
            it.loginFields?.let{
                it.loginEmail?.let{input_email.setText(it)}
                it.loginPassword?.let{input_password.setText(it)}
            }
        })
    }

    fun login(){
        viewModel.setStateEvent(
            AuthStateEvent.LoginAttemptEvent(
                input_email.text.toString(),
                input_password.text.toString()
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setLoginFields(
            LoginFields(
                input_email.text.toString(),
                input_password.text.toString()
            )
        )
    }
}

