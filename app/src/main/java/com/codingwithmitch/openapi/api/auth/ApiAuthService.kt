package com.codingwithmitch.openapi.api.auth

import androidx.lifecycle.LiveData
import com.codingwithmitch.openapi.api.auth.request.LoginRequest
import com.codingwithmitch.openapi.api.auth.request.RegistrationRequest
import com.codingwithmitch.openapi.api.auth.responses.LoginResponse
import com.codingwithmitch.openapi.api.auth.responses.RegistrationResponse
import com.codingwithmitch.openapi.util.GenericApiResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * This is where all of the Retrofit authentication requests going to be held.
 */
interface ApiAuthService {

    @POST("account/login")
    @FormUrlEncoded
    fun login(
        @Field("username") email: String,
        @Field("password") password: String
    ): LiveData<GenericApiResponse<LoginResponse>>

    @POST("account/register")
    fun register(
        @Body registrationRequest: RegistrationRequest
    ): LiveData<GenericApiResponse<RegistrationResponse>>
}