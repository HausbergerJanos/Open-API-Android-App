package com.codingwithmitch.openapi.api.dashboard

import androidx.lifecycle.LiveData
import com.codingwithmitch.openapi.api.GenericResponse
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.util.GenericApiResponse
import retrofit2.http.*

interface ApiDashboardService {

    @GET("account/properties")
    fun getAccountProperties(
        @Header("Authorization") authorization: String
    ): LiveData<GenericApiResponse<AccountProperties>>

    @PUT("account/properties/update")
    @FormUrlEncoded
    fun saveAccountProperties(
        @Header("Authorization") authorization: String,
        @Field("email") email: String,
        @Field("username") userName: String
    ): LiveData<GenericApiResponse<GenericResponse>>

    @PUT("account/change_password/")
    @FormUrlEncoded
    fun updatePassword(
        @Header("Authorization") authorization: String,
        @Field("old_password") currentPassword: String,
        @Field("new_password") newPassword: String,
        @Field("confirm_new_password") confirmNewPassword: String
    ): LiveData<GenericApiResponse<GenericResponse>>
}