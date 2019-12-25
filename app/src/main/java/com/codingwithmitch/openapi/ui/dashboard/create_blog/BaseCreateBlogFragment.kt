package com.codingwithmitch.openapi.ui.dashboard.create_blog

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
import com.codingwithmitch.openapi.ui.dashboard.blog.viewmodel.BlogViewModel
import com.codingwithmitch.openapi.ui.dashboard.create_blog.state.CREATE_BLOG_VIEW_STATE_BUNDLE_KEY
import com.codingwithmitch.openapi.ui.dashboard.create_blog.state.CreateBlogStateEvent
import com.codingwithmitch.openapi.ui.dashboard.create_blog.state.CreateBlogViewState
import dagger.android.support.DaggerFragment
import java.lang.Exception
import javax.inject.Inject

abstract class BaseCreateBlogFragment : Fragment(), Injectable {

    val TAG: String = javaClass.simpleName + "-->"

    lateinit var dependencyProvider: DashboardDependencyProvider
    lateinit var stateChangeListener: DataStateChangeListener
    lateinit var uiCommunicationListener: UICommunicationListener

    lateinit var viewModel: CreateBlogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = activity?.run {
            ViewModelProvider(this, dependencyProvider.getVMProviderFactory()).get(CreateBlogViewModel::class.java)
        }?: throw Exception("Invalid Activity")

        cancelActiveJobs()

        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[CREATE_BLOG_VIEW_STATE_BUNDLE_KEY] as CreateBlogViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController(R.id.createBlogFragment, activity as AppCompatActivity)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    fun cancelActiveJobs() {
        viewModel.cancelActiveJobs()
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
                CREATE_BLOG_VIEW_STATE_BUNDLE_KEY,
                viewModel.viewState.value
            )
        }
        super.onSaveInstanceState(outState)
    }

    private fun isViewModelInitialized() = ::viewModel.isInitialized
}