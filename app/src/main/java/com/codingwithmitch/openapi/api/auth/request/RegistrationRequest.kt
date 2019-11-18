package com.codingwithmitch.openapi.api.auth.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RegistrationRequest(
    @Expose
    var email: String,

    @SerializedName("username")
    @Expose
    var userName: String,

    @Expose
    var password: String,

    @SerializedName("password2")
    @Expose
    var confirmationPassword: String
)