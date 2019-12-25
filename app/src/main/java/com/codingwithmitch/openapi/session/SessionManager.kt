package com.codingwithmitch.openapi.session

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.persistance.AuthTokenDao
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
) {
    private val TAG = javaClass.simpleName + "-->"

    private val _cachedAuthToken = MutableLiveData<AuthToken>()

    val cachedToken: LiveData<AuthToken>
        get() = _cachedAuthToken

    fun login(newAuthToken: AuthToken) {
        setToken(newAuthToken)
    }

    fun logout() {
        Log.d(TAG, "Logout...")

        GlobalScope.launch(IO) {
            var errorMessage: String? = null

            try {
                cachedToken.value!!.account_pk?.let {
                    authTokenDao.nullifyToken(it)
                }
            } catch (e: CancellationException) {
                Log.d(TAG, "Logout erroe: $e")
                errorMessage = e.message
            } catch (e: Exception) {
                Log.d(TAG, "Logout erroe: $e")
                errorMessage = errorMessage + "\n" + e.message
            } finally {
                errorMessage?.let {
                    Log.d(TAG, "Logout erroe: $errorMessage")
                }

                Log.d(TAG, "Logout finally....")
                setToken(null)
            }
        }
    }

    fun setToken(newAuthToken: AuthToken?) {
        GlobalScope.launch(Main) {
            if (_cachedAuthToken.value != newAuthToken) {
                _cachedAuthToken.value = newAuthToken
            }
        }
    }

    fun isConnectedToTheInternet(): Boolean {
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        try {
            return cm.activeNetworkInfo.isConnected
        } catch (e: Exception) {
            Log.d(TAG, "isConnectedToTheInternet: " + e.message)
        }

        return false
    }
}