package com.codingwithmitch.openapi.ui.dashboard.blog.viewmodel

import android.net.Uri
import android.os.Parcelable
import com.codingwithmitch.openapi.models.BlogPost

fun BlogViewModel.setQuery(query: String) {
    val update = getCurrentViewState()
    update.blogFields.searchQuery = query
    setViewState(update)
}

fun BlogViewModel.setBlogListData(blogList: List<BlogPost>) {
    val update = getCurrentViewState()
    update.blogFields.blogList = blogList
    setViewState(update)
}

fun BlogViewModel.setBlogPost(blogPost: BlogPost) {
    val update = getCurrentViewState()
    update.viewBlogFields.blogPost = blogPost
    setViewState(update)
}

fun BlogViewModel.setIsAuthorOfBlogPost(isAuthorOfBlogPost: Boolean) {
    val update = getCurrentViewState()
    update.viewBlogFields.isAuthorOfBlogPost = isAuthorOfBlogPost
    setViewState(update)
}

fun BlogViewModel.setQueryExhausted(isExhausted: Boolean) {
    val update = getCurrentViewState()
    update.blogFields.isQueryExhausted = isExhausted
    setViewState(update)
}

fun BlogViewModel.setQueryInProgress(isInProgress: Boolean) {
    val update = getCurrentViewState()
    update.blogFields.isQueryInProgress = isInProgress
    setViewState(update)
}

fun BlogViewModel.setLayoutManagerState(layoutManagerState: Parcelable) {
    val update =  getCurrentViewState()
    update.blogFields.layoutManager = layoutManagerState
    setViewState(update)
}

fun BlogViewModel.clearLayoutManagerState() {
    val update =  getCurrentViewState()
    update.blogFields.layoutManager = null
    setViewState(update)
}

// Filter can be "date_updated" or "username"
fun BlogViewModel.setBlogFilter(filter: String?){
    filter?.let{
        val update = getCurrentViewState()
        update.blogFields.filter = filter
        setViewState(update)
    }
}

// Order can be "-" or ""
// Note: "-" = DESC, "" = ASC
fun BlogViewModel.setBlogOrder(order: String){
    val update = getCurrentViewState()
    update.blogFields.order = order
    setViewState(update)
}

fun BlogViewModel.setUpdatedBlogFields(
    title: String?,
    body: String?,
    uri: Uri?
) {
    val update = getCurrentViewState()
    val updatedBlogFields = update.updateBlogFields.copy()

    title?.let {
        updatedBlogFields.updatedBlogTitle = it
    }

    body?.let {
        updatedBlogFields.updatedBlogBody = it
    }

    uri?.let {
        updatedBlogFields.updatedImageUri = it
    }

    update.updateBlogFields = updatedBlogFields
    setViewState(update)
}

fun BlogViewModel.updateListItem(newBlogPost: BlogPost) {
    val update = getCurrentViewState()
    val blogPostList = update.blogFields.blogList.toMutableList()
    for (i in 0 until blogPostList.size) {
        if (blogPostList[i].id == newBlogPost.id) {
            blogPostList[i] = newBlogPost
            break
        }
    }

    update.blogFields.blogList = blogPostList
    setViewState(update)
}

fun BlogViewModel.onBlogPostUpdateSuccess(blogPost: BlogPost) {
    // Update UpdateBlogFragment (not really necessary since navigating back)
    setUpdatedBlogFields(
        uri = null,
        title = blogPost.title,
        body = blogPost.body
    )

    // Update ViewBlogFragment
    setBlogPost(blogPost)

    // Update BlogFragment
    updateListItem(blogPost)
}