package com.codingwithmitch.openapi.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.di.ViewModelProviderFactory
import com.codingwithmitch.openapi.ui.BaseActivity
import com.codingwithmitch.openapi.ui.dashboard.DashboardActivity
import javax.inject.Inject

class AuthActivity : BaseActivity(){

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)

        subscribeObservers()
    }

    fun subscribeObservers() {
        viewModel.viewState.observe(this, Observer {
            it.authToken?.let {
                sessionManager.login(it)
            }
        })

        sessionManager.cachedToken.observe(this, Observer { authToken ->
            Log.d(TAG, "subscribeObservers: AuthToken: $authToken")

            if (authToken != null && authToken.account_pk != -1 && authToken.token != null) {
                navToDashboardActivity()
            }
        })
    }

    private fun navToDashboardActivity() {
        val intent: Intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}