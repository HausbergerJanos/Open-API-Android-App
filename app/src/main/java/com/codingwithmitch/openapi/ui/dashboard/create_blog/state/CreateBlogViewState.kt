package com.codingwithmitch.openapi.ui.dashboard.create_blog.state

import android.net.Uri

data class CreateBlogViewState(
    var newBlogTitle: String? = null,
    var newBlogBody: String? = null,
    var newImageUri: Uri? = null
)