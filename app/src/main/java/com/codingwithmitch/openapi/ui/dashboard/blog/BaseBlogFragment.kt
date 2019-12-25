package com.codingwithmitch.openapi.ui.dashboard.blog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.RequestManager
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.di.Injectable
import com.codingwithmitch.openapi.di.ViewModelProviderFactory
import com.codingwithmitch.openapi.ui.DataStateChangeListener
import com.codingwithmitch.openapi.ui.UICommunicationListener
import com.codingwithmitch.openapi.ui.dashboard.DashboardDependencyProvider
import com.codingwithmitch.openapi.ui.dashboard.blog.state.BLOG_VIEW_STATE_BUNDLE_KEY
import com.codingwithmitch.openapi.ui.dashboard.blog.state.BlogViewState
import com.codingwithmitch.openapi.ui.dashboard.blog.viewmodel.BlogViewModel
import dagger.android.support.DaggerFragment
import java.lang.Exception
import javax.inject.Inject

abstract class BaseBlogFragment  : Fragment(), Injectable {

    val TAG: String = javaClass.simpleName + "-->"

    lateinit var viewModel: BlogViewModel

    lateinit var dependencyProvider: DashboardDependencyProvider
    lateinit var stateChangeListener: DataStateChangeListener
    lateinit var uiCommunicationListener: UICommunicationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = activity?.run {
            ViewModelProvider(this, dependencyProvider.getVMProviderFactory()).get(BlogViewModel::class.java)
        }?: throw Exception("Invalid Activity")

        cancelActiveJobs()

        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[BLOG_VIEW_STATE_BUNDLE_KEY] as BlogViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    fun cancelActiveJobs() {
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController(R.id.blogFragment, activity as AppCompatActivity)
    }

    private fun setUpActionBarWithNavController(fragmentId: Int, activity: AppCompatActivity) {
        val appBarConfiguration = AppBarConfiguration(setOf(fragmentId))
        NavigationUI.setupActionBarWithNavController(
            activity,
            findNavController(),
            appBarConfiguration
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            stateChangeListener = context as DataStateChangeListener
        } catch (e: ClassCastException){
            Log.e(TAG, "$context must implement DataStateChangeListener" )
        }

        try{
            uiCommunicationListener = context as UICommunicationListener
        } catch (e: ClassCastException){
            Log.e(TAG, "$context must implement UICommunicationListener" )
        }

        try{
            dependencyProvider = context as DashboardDependencyProvider
        } catch (e: ClassCastException){
            Log.e(TAG, "$context must implement DashboardDependencyProvider" )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (isViewModelInitialized()) {
            outState.putParcelable(
                BLOG_VIEW_STATE_BUNDLE_KEY,
                viewModel.viewState.value
            )
        }
        super.onSaveInstanceState(outState)
    }

    private fun isViewModelInitialized() = ::viewModel.isInitialized
}