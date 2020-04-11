package com.codingwithmitch.openapi.fragments.dashboard.blog

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.codingwithmitch.openapi.di.dashboard.DashboardScope
import com.codingwithmitch.openapi.ui.dashboard.blog.view.BlogFragment
import com.codingwithmitch.openapi.ui.dashboard.blog.view.UpdateBlogFragment
import com.codingwithmitch.openapi.ui.dashboard.blog.view.ViewBlogFragment
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
                BlogFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            ViewBlogFragment::class.java.name -> {
                ViewBlogFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            UpdateBlogFragment::class.java.name -> {
                UpdateBlogFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            else -> {
                BlogFragment(
                    viewModelFactory,
                    requestManager
                )
            }
        }
}