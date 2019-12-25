package com.codingwithmitch.openapi.ui.dashboard.create_blog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.RequestManager
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.di.ViewModelProviderFactory
import com.codingwithmitch.openapi.ui.DataStateChangeListener
import com.codingwithmitch.openapi.ui.UICommunicationListener
import com.codingwithmitch.openapi.ui.dashboard.blog.viewmodel.BlogViewModel
import dagger.android.support.DaggerFragment
import java.lang.Exception
import javax.inject.Inject

abstract class BaseCreateBlogFragment : DaggerFragment(){

    val TAG: String = javaClass.simpleName + "-->"

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    @Inject
    lateinit var requestManager: RequestManager

    lateinit var stateChangeListener: DataStateChangeListener

    lateinit var uiCommunicationListener: UICommunicationListener

    lateinit var viewModel: CreateBlogViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController(R.id.createBlogFragment, activity as AppCompatActivity)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)

        viewModel = activity?.run {
            ViewModelProvider(this, providerFactory).get(CreateBlogViewModel::class.java)
        }?: throw Exception("Invalid Activity")

        cancelActiveJobs()
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
    }
}