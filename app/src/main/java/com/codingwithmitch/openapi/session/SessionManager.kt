package com.codingwithmitch.openapi.session

import android.app.Application
import com.codingwithmitch.openapi.persistance.AuthTokenDao

class SessionManager
constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
)