package com.codingwithmitch.openapi.ui.dashboard.blog.state

sealed class BlogStateEvent {

    class BlogSearchEvent : BlogStateEvent()

    class None : BlogStateEvent()
}