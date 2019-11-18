package com.codingwithmitch.openapi.repository.auth

import androidx.lifecycle.LiveData
import com.codingwithmitch.openapi.api.auth.ApiAuthService
import com.codingwithmitch.openapi.api.auth.request.LoginRequest
import com.codingwithmitch.openapi.api.auth.request.RegistrationRequest
import com.codingwithmitch.openapi.api.auth.responses.LoginResponse
import com.codingwithmitch.openapi.api.auth.responses.RegistrationResponse
import com.codingwithmitch.openapi.persistance.AccountPropertiesDao
import com.codingwithmitch.openapi.persistance.AuthTokenDao
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.util.GenericApiResponse
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val apiAuthService: ApiAuthService,
    val sessionManager: SessionManager
) {

    fun testLoginRequest(loginRequest: LoginRequest): LiveData<GenericApiResponse<LoginResponse>> {
        return apiAuthService.login(loginRequest)
    }

    fun testRegistrationRequest(registrationRequest: RegistrationRequest): LiveData<GenericApiResponse<RegistrationResponse>> {
        return apiAuthService.register(registrationRequest)
    }
}