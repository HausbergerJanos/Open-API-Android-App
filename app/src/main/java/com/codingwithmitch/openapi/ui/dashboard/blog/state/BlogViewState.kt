package com.codingwithmitch.openapi.ui.dashboard.blog.state

import com.codingwithmitch.openapi.models.BlogPost

data class BlogViewState(

    // BlogFragment variables
    var blogFields: BlogFields = BlogFields(),

    // ViewBlogFragment variables
    var viewBlogFields: ViewBlogFields = ViewBlogFields()

    // UpdateBlogFragment variables
) {

    data class BlogFields(
        var blogList: List<BlogPost> = ArrayList(),
        var searchQuery: String = "",
        var page: Int = 1,
        var isQueryInProgress: Boolean = false,
        var isQueryExhausted: Boolean = false
    )

    data class ViewBlogFields(
        var blogPost: BlogPost? = null,
        var isAuthorOfBlogPost: Boolean = false
    )
}