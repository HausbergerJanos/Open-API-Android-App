package com.codingwithmitch.openapi.api.auth.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginRequest(

    @SerializedName("username")
    @Expose
    var email: String,

    @SerializedName("password")
    @Expose
    var password: String
)