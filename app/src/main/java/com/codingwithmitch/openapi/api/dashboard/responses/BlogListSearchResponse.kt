package com.codingwithmitch.openapi.api.dashboard.responses

import com.codingwithmitch.openapi.models.BlogPost
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

    fun toList(): List<BlogPost> {
        val blogPostList: ArrayList<BlogPost> = ArrayList()
        for (blogPostResponse in results) {
            blogPostList.add(
                blogPostResponse.toBlogPost()
            )
        }
        return blogPostList
    }

    override fun toString(): String {
        return "BlogListSearchResponse(results=$results, detail='$detail')"
    }
}