package com.codingwithmitch.openapi.ui.dashboard.blog.state

import com.codingwithmitch.openapi.models.BlogPost

data class BlogViewState(

    // Blog fragment variables
    var blogFields: BlogFields = BlogFields()

    // ViewBlogFragment variables
    // UpdateBlogFragment variables
) {

    data class BlogFields(
        var blogList: List<BlogPost> = ArrayList(),
        var searchQuery: String = ""
    )
}