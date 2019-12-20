package com.codingwithmitch.openapi.repository.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.codingwithmitch.openapi.api.dashboard.ApiDashboardService
import com.codingwithmitch.openapi.api.dashboard.responses.BlogListSearchResponse
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.persistance.BlogPostDao
import com.codingwithmitch.openapi.repository.JobManager
import com.codingwithmitch.openapi.repository.NetworkBoundResource
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.dashboard.blog.state.BlogViewState
import com.codingwithmitch.openapi.util.ApiSuccessResponse
import com.codingwithmitch.openapi.util.Constants.Constants.Companion.PAGINATION_PAGE_SIZE
import com.codingwithmitch.openapi.util.DateUtils
import com.codingwithmitch.openapi.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class BlogRepository
@Inject
constructor(
    val apiDashboardService: ApiDashboardService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
): JobManager("BlogRepository") {

    private val TAG = javaClass.simpleName + "-->"

    fun searchBlogPosts(
        authToken: AuthToken,
        query: String,
        page: Int
    ): LiveData<DataState<BlogViewState>> {
        return object: NetworkBoundResource<BlogListSearchResponse, List<BlogPost>, BlogViewState>(
            isNetworkAvailable = sessionManager.isConnectedToTheInternet(),
            isNetworkRequest = true,
            shouldCancelIfNoInternet = false,
            shouldLoadFromCache = true
        ) {

            // If network is down, view cache only and return
            override suspend fun createCacheRequestAndReturn() {
                withContext(Main) {
                    // Finishing by viewing db cache
                    result.addSource(loadFromCache()) { viewState ->
                        viewState?.let { blogViewState ->
                            blogViewState.blogFields.isQueryInProgress = false
                            if (page * PAGINATION_PAGE_SIZE > viewState.blogFields.blogList.size) {
                                blogViewState.blogFields.isQueryExhausted = true
                            }
                        }

                        onCompleteJob(
                            dataState = DataState.data(
                                data = viewState,
                                response = null
                            )
                        )
                    }
                }
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<BlogListSearchResponse>) {

                val blogPostList: ArrayList<BlogPost> = ArrayList()
                for (blogPostResponse in response.body.results) {
                    blogPostList.add(
                        BlogPost(
                            id = blogPostResponse.id,
                            title = blogPostResponse.title,
                            slug = blogPostResponse.slug,
                            body = blogPostResponse.body,
                            image = blogPostResponse.image,
                            dateUpdated = DateUtils.convertServerStringDateToLong(
                                blogPostResponse.dateUpdated
                            ),
                            userName = blogPostResponse.userName
                        )
                    )
                }

                updateLocalDb(blogPostList)
                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogListSearchResponse>> {
                return apiDashboardService.searchListBlogPosts(
                    "Token ${authToken.token}",
                    query = query,
                    page = page
                )
            }

            override fun loadFromCache(): LiveData<BlogViewState> {
                return blogPostDao.getAllBlogPosts(
                    query = query,
                    page = page
                )
                    .switchMap {  list ->
                        object: LiveData<BlogViewState>() {
                            override fun onActive() {
                                super.onActive()
                                value = BlogViewState(
                                    blogFields = BlogViewState.BlogFields(
                                        blogList = list,
                                        isQueryInProgress = true
                                    )
                                )
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObject: List<BlogPost>?) {
                cacheObject?.let {  list ->
                    withContext(IO) {
                        for (blogPost in list) {
                            try {
                                // Launch each insert as a separate job to be executed in parallel
                                launch {
                                    Log.d(TAG, "updateLocalDb: inserting blog: $blogPost")
                                    blogPostDao.insert(blogPost)
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "updateLocalDb: error updating cache data on blog post with slug: ${blogPost.slug}. " +
                                        "${e.message}")
                            }
                        }
                    }
                }
            }

            override fun setJob(job: Job) {
                addJob("searchBlogPosts", job)
            }

        }.asLiveData()
    }
}