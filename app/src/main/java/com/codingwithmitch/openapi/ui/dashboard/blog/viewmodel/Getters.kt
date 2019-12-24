package com.codingwithmitch.openapi.ui.dashboard.blog.viewmodel

import android.net.Uri
import com.codingwithmitch.openapi.models.BlogPost

fun BlogViewModel.getPage(): Int {
    getCurrentViewState().let {
        return it.blogFields.page
    }
}

fun BlogViewModel.getSearchQuery(): String {
    getCurrentViewState().let {
        return it.blogFields.searchQuery
    }
}

fun BlogViewModel.isQueryExhausted(): Boolean {
    getCurrentViewState().let {
        return it.blogFields.isQueryExhausted
    }
}

fun BlogViewModel.isQueryInProgress(): Boolean {
    getCurrentViewState().let {
        return it.blogFields.isQueryInProgress
    }
}

fun BlogViewModel.getFilter(): String {
    getCurrentViewState().let {
        return it.blogFields.filter
    }
}

fun BlogViewModel.getOrder(): String {
    getCurrentViewState().let {
        return it.blogFields.order
    }
}

fun BlogViewModel.getSlug(): String {
    getCurrentViewState().let {
        return it.viewBlogFields.blogPost?.slug ?: ""
    }
}

fun BlogViewModel.isAuthorOfBlogPost(): Boolean {
    getCurrentViewState().let {
        return it.viewBlogFields.isAuthorOfBlogPost
    }
}

fun BlogViewModel.getBlogPost(): BlogPost {
    getCurrentViewState().let {
        return it.viewBlogFields.blogPost ?: getFallbackBlogPost()
    }
}

fun BlogViewModel.getFallbackBlogPost(): BlogPost {
    return BlogPost(
        id = -1,
        slug = "",
        title = "",
        body = "",
        image = "",
        dateUpdated = 0,
        userName = ""
    )
}

fun BlogViewModel.getUpdatedBlogUri(): Uri? {
    getCurrentViewState().let {
        return it.updateBlogFields.updatedImageUri
    }
}

