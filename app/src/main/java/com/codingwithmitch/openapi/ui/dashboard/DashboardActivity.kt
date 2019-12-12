package com.codingwithmitch.openapi.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.BaseActivity
import com.codingwithmitch.openapi.ui.auth.AuthActivity
import com.codingwithmitch.openapi.ui.dashboard.account.BaseAccountFragment
import com.codingwithmitch.openapi.ui.dashboard.account.ChangePasswordFragment
import com.codingwithmitch.openapi.ui.dashboard.account.UpdateAccountFragment
import com.codingwithmitch.openapi.ui.dashboard.blog.BaseBlogFragment
import com.codingwithmitch.openapi.ui.dashboard.blog.UpdateBlogFragment
import com.codingwithmitch.openapi.ui.dashboard.blog.ViewBlogFragment
import com.codingwithmitch.openapi.ui.dashboard.create_blog.BaseCreateBlogFragment
import com.codingwithmitch.openapi.util.BottomNavController
import com.codingwithmitch.openapi.util.BottomNavController.*
import com.codingwithmitch.openapi.util.setUpNavigation
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : BaseActivity(),
    NavGraphProvider, OnNavigationGraphChanged, OnNavigationReselectedListener {

    private val bottomNavController by lazy(LazyThreadSafetyMode.NONE) {
        BottomNavController(
            this,
            R.id.main_nav_host_fragment,
            R.id.nav_blog,
            this,
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        setUpActionBar()
        bottom_navigation_view?.setUpNavigation(bottomNavController, this)
        if (savedInstanceState == null) {
            bottomNavController.onNavigationItemSelected()
        }

        subscribeObservers()
    }

    private fun setUpActionBar() {
        setSupportActionBar(tool_bar)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() = bottomNavController.onBackPressed()

    private fun subscribeObservers() {
        sessionManager.cachedToken.observe(this, Observer { authToken ->
            Log.d(TAG, "subscribeObservers: AuthToken: $authToken")

            if (authToken == null || authToken.account_pk == -1 || authToken.token == null) {
                navToAuthActivity()
            }
        })
    }

    private fun navToAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun displayProgressBar(isLoading: Boolean) {
        progress_bar?.let {
            if (isLoading) {
                it.visibility = View.VISIBLE
            } else {
                it.visibility = View.GONE
            }
        }
    }

    override fun getNavGraphId(itemId: Int) =
        when (itemId) {

            R.id.nav_blog -> {
                R.navigation.nav_blog
            }

            R.id.nav_account -> {
                R.navigation.nav_account
            }

            R.id.nav_create_blog -> {
                R.navigation.nav_create_blog
            }

            else -> {
                R.navigation.nav_blog
            }
        }

    override fun onGraphChange() {
        expandAppbar()
        cancelActiveJobs()
    }

    private fun cancelActiveJobs() {
        val fragments = bottomNavController.fragmentManager.findFragmentById(bottomNavController.containerId)?.childFragmentManager?.fragments
        fragments?.let {
            for (fragment in it) {
                when (fragment) {
                    is BaseAccountFragment -> fragment.cancelActiveJobs()
                    is BaseBlogFragment -> fragment.cancelActiveJobs()
                    is BaseCreateBlogFragment -> fragment.cancelActiveJobs()
                }
            }
        }

        displayProgressBar(false)
    }

    override fun onReselectNavItem(navController: NavController, fragment: Fragment) =
        when (fragment) {

            is ViewBlogFragment -> {
                navController.navigate(R.id.action_viewBlogFragment_to_blogFragment)
            }

            is UpdateBlogFragment -> {
                navController.navigate(R.id.action_updateBlogFragment_to_blogFragment)
            }

            is UpdateAccountFragment -> {
                navController.navigate(R.id.action_updateAccountFragment_to_accountFragment)
            }

            is ChangePasswordFragment -> {
                navController.navigate(R.id.action_changePasswordFragment_to_accountFragment)
            }

            else -> {
                // do nothing
            }
        }

    override fun expandAppbar() {
        findViewById<AppBarLayout>(R.id.app_bar).setExpanded(true)
    }
}
