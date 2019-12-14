package com.codingwithmitch.openapi.api.dashboard.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BlogSearchResponse(

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
    override fun toString(): String {
        return "BlogSearchResponse(id=$id, title='$title', slug='$slug',  image='$image', dateUpdated='$dateUpdated', userName='$userName')"
    }
}