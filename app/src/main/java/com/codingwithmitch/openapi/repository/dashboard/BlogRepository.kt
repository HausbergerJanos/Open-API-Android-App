package com.codingwithmitch.openapi.repository.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.codingwithmitch.openapi.api.GenericResponse
import com.codingwithmitch.openapi.api.dashboard.ApiDashboardService
import com.codingwithmitch.openapi.api.dashboard.responses.BlogCreateUpdateResponse
import com.codingwithmitch.openapi.api.dashboard.responses.BlogListSearchResponse
import com.codingwithmitch.openapi.di.dashboard.DashboardScope
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.persistance.BlogPostDao
import com.codingwithmitch.openapi.persistance.returnOrderedBlogQuery
import com.codingwithmitch.openapi.repository.JobManager
import com.codingwithmitch.openapi.repository.NetworkBoundResource
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.Response
import com.codingwithmitch.openapi.ui.ResponseType.*
import com.codingwithmitch.openapi.ui.dashboard.blog.state.BlogViewState
import com.codingwithmitch.openapi.ui.dashboard.blog.state.BlogViewState.*
import com.codingwithmitch.openapi.util.*
import com.codingwithmitch.openapi.util.Constants.Constants.Companion.PAGINATION_PAGE_SIZE
import com.codingwithmitch.openapi.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.codingwithmitch.openapi.util.SuccessHandling.Companion.RESPONSE_HAS_PERMISSION_TO_EDIT
import com.codingwithmitch.openapi.util.SuccessHandling.Companion.SUCCESS_BLOG_DELETED
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception
import javax.inject.Inject

@DashboardScope
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
        filterAndOrder: String,
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
                    ordering = filterAndOrder,
                    page = page
                )
            }

            override fun loadFromCache(): LiveData<BlogViewState> {
                return blogPostDao.returnOrderedBlogQuery(
                    query = query,
                    filterAndOrder = filterAndOrder,
                    page = page
                )
                    .switchMap {  list ->
                        object: LiveData<BlogViewState>() {
                            override fun onActive() {
                                super.onActive()
                                value = BlogViewState(
                                    blogFields = BlogFields(
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

    fun restoreBlogListFromCache(
        query: String,
        filterAndOrder: String,
        page: Int
    ): LiveData<DataState<BlogViewState>> {
        return object: NetworkBoundResource<BlogListSearchResponse, List<BlogPost>, BlogViewState>(
            isNetworkAvailable = sessionManager.isConnectedToTheInternet(),
            isNetworkRequest = false,
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

            // Not used in this case
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<BlogListSearchResponse>) {

            }

            override fun createCall(): LiveData<GenericApiResponse<BlogListSearchResponse>> {
                return AbsentLiveData.create()
            }

            override fun loadFromCache(): LiveData<BlogViewState> {
                return blogPostDao.returnOrderedBlogQuery(
                    query = query,
                    filterAndOrder = filterAndOrder,
                    page = page
                )
                    .switchMap {  list ->
                        object: LiveData<BlogViewState>() {
                            override fun onActive() {
                                super.onActive()
                                value = BlogViewState(
                                    blogFields = BlogFields(
                                        blogList = list,
                                        isQueryInProgress = true
                                    )
                                )
                            }
                        }
                    }
            }

            // Not used in this case
            override suspend fun updateLocalDb(cacheObject: List<BlogPost>?) {

            }

            override fun setJob(job: Job) {
                addJob("restoreBlogListFromCache", job)
            }

        }.asLiveData()
    }

    fun isAuthorOfBlogPost(
        authToken: AuthToken,
        slug: String
    ): LiveData<DataState<BlogViewState>> {
        return object: NetworkBoundResource<GenericResponse, Any, BlogViewState>(
           isNetworkAvailable = sessionManager.isConnectedToTheInternet(),
            isNetworkRequest = true,
            shouldLoadFromCache = false,
            shouldCancelIfNoInternet = true
        ) {

            // Not used in this case
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                withContext(Main) {
                    var isAuthor = false
                    if (response.body.response == RESPONSE_HAS_PERMISSION_TO_EDIT) {
                        isAuthor = true
                    }

                    onCompleteJob(
                        DataState.data(
                            data = BlogViewState(
                                viewBlogFields = ViewBlogFields(
                                    isAuthorOfBlogPost = isAuthor
                                )
                            ),
                            response = null
                        )
                    )
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return apiDashboardService.isAuthorOfBlogPost(
                    "Token ${authToken.token}",
                    slug
                )
            }

            // Not used in this case
            override fun loadFromCache(): LiveData<BlogViewState> {
                return AbsentLiveData.create()
            }

            // Not used in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

            override fun setJob(job: Job) {
                addJob("isAuthorOfBlogPost", job)
            }

        }.asLiveData()
    }

    fun deleteBlogPost(
        authToken: AuthToken,
        blogPost: BlogPost
    ): LiveData<DataState<BlogViewState>> {
        return object: NetworkBoundResource<GenericResponse, BlogPost, BlogViewState>(
            isNetworkAvailable = sessionManager.isConnectedToTheInternet(),
            isNetworkRequest = true,
            shouldCancelIfNoInternet = true,
            shouldLoadFromCache = false
        ) {

            // Not used in this case
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                if (response.body.response == SUCCESS_BLOG_DELETED) {
                    updateLocalDb(blogPost)
                } else {
                    onCompleteJob(
                        dataState = DataState.error(
                            Response(
                                message = ERROR_UNKNOWN,
                                responseType = Dialog
                            )
                        )
                    )
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return apiDashboardService.deleteBlogPost(
                    "Token ${authToken.token}",
                    blogPost.slug
                )
            }

            // Not used in this case
            override fun loadFromCache(): LiveData<BlogViewState> {
                return AbsentLiveData.create()
            }

            // Not used in this case
            override suspend fun updateLocalDb(cacheObject: BlogPost?) {
                cacheObject?.let { blogPost ->
                    blogPostDao.deleteBlogPost(blogPost)

                    onCompleteJob(
                        dataState = DataState.data(
                            data = null,
                            response = Response(
                                SUCCESS_BLOG_DELETED,
                                Toast
                            )
                        )
                    )
                }
            }

            override fun setJob(job: Job) {
                addJob("deleteBlogPost", job)
            }

        }.asLiveData()
    }

    fun updateBlogPost(
        authToken: AuthToken,
        slug: String,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?
    ): LiveData<DataState<BlogViewState>> {
        return object: NetworkBoundResource<BlogCreateUpdateResponse, BlogPost, BlogViewState>(
            isNetworkAvailable = sessionManager.isConnectedToTheInternet(),
            isNetworkRequest = true,
            shouldCancelIfNoInternet = true,
            shouldLoadFromCache = false
        ) {

            // Not used in this case
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<BlogCreateUpdateResponse>) {

                val updatedBlogPost = BlogPost(
                    id = response.body.id,
                    title = response.body.title,
                    slug = response.body.slug,
                    body = response.body.body,
                    image = response.body.image,
                    dateUpdated = DateUtils.convertServerStringDateToLong(response.body.dateUpdated),
                    userName = response.body.userName
                )

                updateLocalDb(updatedBlogPost)

                withContext(Main) {
                    onCompleteJob(
                        dataState = DataState.data(
                            data = BlogViewState(
                                viewBlogFields = ViewBlogFields(
                                    blogPost = updatedBlogPost
                                )
                            ),
                            response = Response(
                                message = response.body.response,
                                responseType = Toast
                            )
                        )
                    )
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogCreateUpdateResponse>> {
                return apiDashboardService.updateBlog(
                    authorization = "Token ${authToken.token}",
                    slug = slug,
                    title = title,
                    body = body,
                    image = image
                )
            }

            // Not used in this case
            override fun loadFromCache(): LiveData<BlogViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: BlogPost?) {
                cacheObject?.let { blogPost ->
                    blogPostDao.updateBlogPost(
                        blogPost.id,
                        blogPost.title,
                        blogPost.body,
                        blogPost.image
                    )
                }
            }

            override fun setJob(job: Job) {
                addJob("updateBlogPost", job)
            }

        }.asLiveData()
    }
}