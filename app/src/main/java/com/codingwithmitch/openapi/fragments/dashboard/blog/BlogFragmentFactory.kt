package com.codingwithmitch.openapi.fragments.dashboard.blog

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.codingwithmitch.openapi.di.auth.AuthScope
import com.codingwithmitch.openapi.di.dashboard.DashboardScope
import com.codingwithmitch.openapi.ui.auth.ForgotPasswordFragment
import com.codingwithmitch.openapi.ui.auth.LauncherFragment
import com.codingwithmitch.openapi.ui.auth.LoginFragment
import com.codingwithmitch.openapi.ui.auth.RegisterFragment
import com.codingwithmitch.openapi.ui.dashboard.account.AccountFragment
import com.codingwithmitch.openapi.ui.dashboard.account.ChangePasswordFragment
import com.codingwithmitch.openapi.ui.dashboard.account.UpdateAccountFragment
import com.codingwithmitch.openapi.ui.dashboard.blog.BlogFragment
import com.codingwithmitch.openapi.ui.dashboard.blog.UpdateBlogFragment
import com.codingwithmitch.openapi.ui.dashboard.blog.ViewBlogFragment
import javax.inject.Inject

@DashboardScope
class BlogFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            BlogFragment::class.java.name -> {
                BlogFragment(viewModelFactory, requestManager)
            }

            ViewBlogFragment::class.java.name -> {
                ViewBlogFragment(viewModelFactory, requestManager)
            }

            UpdateBlogFragment::class.java.name -> {
                UpdateBlogFragment(viewModelFactory, requestManager)
            }

            else -> {
                BlogFragment(viewModelFactory, requestManager)
            }
        }
}