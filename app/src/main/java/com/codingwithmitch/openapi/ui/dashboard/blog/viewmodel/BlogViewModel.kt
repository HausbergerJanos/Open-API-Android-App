package com.codingwithmitch.openapi.ui.dashboard.blog.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.bumptech.glide.RequestManager
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.persistance.BlogQueryUtils
import com.codingwithmitch.openapi.persistance.BlogQueryUtils.Companion.BLOG_FILTER_DATE_UPDATED
import com.codingwithmitch.openapi.persistance.BlogQueryUtils.Companion.BLOG_ORDER_ASC
import com.codingwithmitch.openapi.repository.dashboard.BlogRepository
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.BaseViewModel
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.Loading
import com.codingwithmitch.openapi.ui.dashboard.blog.state.BlogStateEvent
import com.codingwithmitch.openapi.ui.dashboard.blog.state.BlogStateEvent.*
import com.codingwithmitch.openapi.ui.dashboard.blog.state.BlogViewState
import com.codingwithmitch.openapi.util.AbsentLiveData
import com.codingwithmitch.openapi.util.PreferenceKeys.Companion.BLOG_FILTER
import com.codingwithmitch.openapi.util.PreferenceKeys.Companion.BLOG_ORDER
import javax.inject.Inject

class BlogViewModel
@Inject
constructor(
    private val blogRepository: BlogRepository,
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor,
    private val sessionManager: SessionManager
) : BaseViewModel<BlogStateEvent, BlogViewState>() {

    init {
        setBlogFilter(
            sharedPreferences.getString(
                BLOG_FILTER,
                BLOG_FILTER_DATE_UPDATED
            )
        )

        setBlogOrder(
            sharedPreferences.getString(
                BLOG_ORDER,
                BLOG_ORDER_ASC
            )
        )
    }

    override fun initNewViewState(): BlogViewState {
        return BlogViewState()
    }

    override fun handleStateEvent(stateEvent: BlogStateEvent): LiveData<DataState<BlogViewState>> {
        return when(stateEvent) {

            is BlogSearchEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.searchBlogPosts(
                        authToken = authToken,
                        query = getSearchQuery(),
                        page = getPage(),
                        filterAndOrder = getOrder() + getFilter()
                    )
                }?: AbsentLiveData.create()
            }

            is CheckAuthorOfBlogPost -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.isAuthorOfBlogPost(
                        authToken = authToken,
                        slug = getSlug()
                    )
                }?: AbsentLiveData.create()
            }

            is DeleteBlogPostEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.deleteBlogPost(
                        authToken = authToken,
                        blogPost = getBlogPost()
                    )
                }?: AbsentLiveData.create()
            }

            is None -> {
                object: LiveData<DataState<BlogViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        value = DataState(
                            error = null,
                            loading = Loading(false),
                            data = null
                        )
                    }
                }
            }
        }
    }

    fun saveFilterOptions(filter: String, order: String) {
        editor.putString(BLOG_FILTER, filter)
        editor.apply()

        editor.putString(BLOG_ORDER, order)
        editor.apply()
    }

    fun cancelActiveJobs() {
        blogRepository.cancelActiveJobs()
        handlePendingData()
    }

    private fun handlePendingData() {
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}