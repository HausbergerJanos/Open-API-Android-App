package com.codingwithmitch.openapi.api.dashboard.responses

import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.util.DateUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BlogCreateUpdateResponse(

    @SerializedName("response")
    @Expose
    var response: String,

    @SerializedName("pk")
    @Expose
    var id: Int,

    @SerializedName("title")
    @Expose
    var title: String,

    @SerializedName("slug")
    @Expose
    var slug: String,

    @SerializedName("body")
    @Expose
    var body: String,

    @SerializedName("image")
    @Expose
    var image: String,

    @SerializedName("date_updated")
    @Expose
    var dateUpdated: String,

    @SerializedName("username")
    @Expose
    var userName: String
) {
    fun toBlogPost(): BlogPost {
        return BlogPost(
            id = id,
            title = title,
            slug = slug,
            body = body,
            image = image,
            dateUpdated = DateUtils.convertServerStringDateToLong(
                dateUpdated
            ),
            userName = userName
        )
    }
}