package com.codingwithmitch.openapi.ui.dashboard.account

import android.app.Activity
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
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.di.Injectable
import com.codingwithmitch.openapi.di.ViewModelProviderFactory
import com.codingwithmitch.openapi.ui.DataStateChangeListener
import com.codingwithmitch.openapi.ui.dashboard.DashboardDependencyProvider
import com.codingwithmitch.openapi.ui.dashboard.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.codingwithmitch.openapi.ui.dashboard.account.state.AccountViewState
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseAccountFragment : Fragment(), Injectable {

    val TAG: String = javaClass.simpleName + "-->"

    lateinit var viewModel: AccountViewModel

    lateinit var dependencyProvider: DashboardDependencyProvider
    lateinit var stateChangeListener: DataStateChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = activity?.run {
            ViewModelProvider(this, dependencyProvider.getVMProviderFactory()).get(AccountViewModel::class.java)
        }?: throw Exception("Invalid Activity")

        cancelActiveJobs()

        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[ACCOUNT_VIEW_STATE_BUNDLE_KEY] as AccountViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController(R.id.accountFragment, activity as AppCompatActivity)
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
            dependencyProvider = context as DashboardDependencyProvider
        } catch (e: ClassCastException){
            Log.e(TAG, "$context must implement DashboardDependencyProvider" )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (isViewModelInitialized()) {
            outState.putParcelable(
                ACCOUNT_VIEW_STATE_BUNDLE_KEY,
                viewModel.viewState.value
            )
        }
        super.onSaveInstanceState(outState)
    }

    private fun isViewModelInitialized() = ::viewModel.isInitialized
}