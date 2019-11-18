package com.codingwithmitch.openapi.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.codingwithmitch.openapi.api.auth.request.LoginRequest
import com.codingwithmitch.openapi.api.auth.request.RegistrationRequest
import com.codingwithmitch.openapi.api.auth.responses.LoginResponse
import com.codingwithmitch.openapi.api.auth.responses.RegistrationResponse
import com.codingwithmitch.openapi.repository.auth.AuthRepository
import com.codingwithmitch.openapi.util.GenericApiResponse
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
) : ViewModel() {

    fun testLogin(): LiveData<GenericApiResponse<LoginResponse>> {
        return authRepository.testLoginRequest(
            LoginRequest(
                email = "hausberger.janos@gmail.com",
                password = "test1234"
            )
        )
    }

    fun testRegister(): LiveData<GenericApiResponse<RegistrationResponse>> {
        return authRepository.testRegistrationRequest(
            RegistrationRequest(
                email = "hausberger.janos@gmail.com",
                userName = "Hausberger Janos",
                password = "test1234",
                confirmationPassword = "test1234"
            ))
    }
}