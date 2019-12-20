package com.codingwithmitch.openapi.ui.dashboard.blog.viewmodel

import com.codingwithmitch.openapi.ui.dashboard.blog.state.BlogStateEvent.*
import com.codingwithmitch.openapi.ui.dashboard.blog.state.BlogViewState

fun BlogViewModel.resetPage() {
    val update = getCurrentViewState()
    update.blogFields.page = 1
    setViewState(update)
}

fun BlogViewModel.loadFirstPage() {
    setQueryInProgress(true)
    setQueryExhausted(false)
    resetPage()
    setStateEvent(BlogSearchEvent())
}

fun BlogViewModel.incrementPageNumber() {
    val update = getCurrentViewState()
    val page = update.copy().blogFields.page
    update.blogFields.page = page + 1
    setViewState(update)
}

fun BlogViewModel.nextPage() {
    if (!isQueryExhausted() && !isQueryInProgress()) {
        incrementPageNumber()
        setQueryInProgress(true)
        setStateEvent(BlogSearchEvent())
    }
}

fun BlogViewModel.handleIncomingBlogListData(viewState: BlogViewState) {
    setQueryExhausted(viewState.blogFields.isQueryExhausted)
    setQueryInProgress(viewState.blogFields.isQueryInProgress)
    setBlogListData(viewState.blogFields.blogList)
}