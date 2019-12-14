package com.codingwithmitch.openapi.api.dashboard.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BlogListSearchResponse(

    @SerializedName("results")
    @Expose
    var results: List<BlogSearchResponse>,

    @SerializedName("detail")
    @Expose
    var detail: String
) {

    override fun toString(): String {
        return "BlogListSearchResponse(results=$results, detail='$detail')"
    }
}