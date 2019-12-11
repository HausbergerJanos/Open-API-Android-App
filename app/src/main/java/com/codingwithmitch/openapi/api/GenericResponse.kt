package com.codingwithmitch.openapi.api

import com.google.gson.annotations.Expose

data class GenericResponse(

    @Expose
    var response: String
)