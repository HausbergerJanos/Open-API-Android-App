package com.codingwithmitch.openapi.ui

import android.util.Log
import com.codingwithmitch.openapi.session.SessionManager
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity: DaggerAppCompatActivity(), DataStateChangeListener {

    val TAG: String = javaClass.simpleName

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            GlobalScope.launch(Main) {

                displayProgressBar(dataState.loading.isLoading)

                it.error?.let { errorEvent ->
                    handleStateError(errorEvent)
                }

                it.data?.let {

                    it.response?.let { responseevent ->
                        handleStateResponse(responseevent)
                    }
                }
            }
        }
    }

    private fun handleStateResponse(event: Event<Response>) {

        event.getContentIfNotHandled()?.let {

            when(it.responseType) {

                is ResponseType.Dialog -> {
                    it.message?.let { message ->
                        displaySuccessDialog(message)
                    }
                }

                is ResponseType.Toast -> {
                    it.message?.let { message ->
                        displayToast(message)
                    }
                }

                is ResponseType.None -> {
                    Log.d(TAG, "handleStateError: ${it.message}")
                }
            }
        }
    }

    private fun handleStateError(event: Event<StateError>) {

        event.getContentIfNotHandled()?.let {

            when(it.response.responseType) {

                is ResponseType.Dialog -> {
                    it.response.message?.let { message ->
                        displayErrorDialog(message)
                    }
                }

                is ResponseType.Toast -> {
                    it.response.message?.let { message ->
                        displayToast(message)
                    }
                }

                is ResponseType.None -> {
                    Log.d(TAG, "handleStateError: ${it.response.message}")
                }
            }
        }
    }

    abstract fun displayProgressBar(isLoading: Boolean)
}