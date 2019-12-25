package com.codingwithmitch.openapi.repository.dashboard

import androidx.lifecycle.LiveData
import com.codingwithmitch.openapi.api.dashboard.ApiDashboardService
import com.codingwithmitch.openapi.api.dashboard.responses.BlogCreateUpdateResponse
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.persistance.BlogPostDao
import com.codingwithmitch.openapi.repository.JobManager
import com.codingwithmitch.openapi.repository.NetworkBoundResource
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.Response
import com.codingwithmitch.openapi.ui.ResponseType
import com.codingwithmitch.openapi.ui.dashboard.create_blog.state.CreateBlogViewState
import com.codingwithmitch.openapi.util.AbsentLiveData
import com.codingwithmitch.openapi.util.ApiSuccessResponse
import com.codingwithmitch.openapi.util.DateUtils
import com.codingwithmitch.openapi.util.GenericApiResponse
import com.codingwithmitch.openapi.util.SuccessHandling.Companion.RESPONSE_MUST_BECOME_CODINGWITHMITCH_MEMBER
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class CreateBlogRepository
@Inject
constructor(
    val apiDashboardService: ApiDashboardService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
) : JobManager("CreateBlogRepository") {

    fun createNewBlogPost(
        authToken: AuthToken,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?
    ): LiveData<DataState<CreateBlogViewState>> {
        return object: NetworkBoundResource<BlogCreateUpdateResponse, BlogPost, CreateBlogViewState>(
            isNetworkAvailable = sessionManager.isConnectedToTheInternet(),
            isNetworkRequest = true,
            shouldCancelIfNoInternet = true,
            shouldLoadFromCache = false
        ) {
            // Not used in this case
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<BlogCreateUpdateResponse>) {

                // If they don't have a paid membership account it will still return a 200
                // Need to account for that
                if (response.body.response != RESPONSE_MUST_BECOME_CODINGWITHMITCH_MEMBER) {
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
                }

                withContext(Main) {
                    // finish with success response
                    onCompleteJob(
                        dataState = DataState.data(
                            data = null,
                            response = Response(
                                message = response.body.response,
                                responseType = ResponseType.Dialog()
                            )
                        )
                    )
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogCreateUpdateResponse>> {
                return apiDashboardService.createBlog(
                    authorization = "Token ${authToken.token}",
                    title = title,
                    body = body,
                    image = image
                )
            }

            // Not used in this case
            override fun loadFromCache(): LiveData<CreateBlogViewState> {
                return AbsentLiveData.create()
            }


            override suspend fun updateLocalDb(cacheObject: BlogPost?) {
                cacheObject?.let { blogPost ->
                    blogPostDao.insert(blogPost)
                }
            }

            override fun setJob(job: Job) {
                addJob("createNewBlogPost", job)
            }

        }.asLiveData()
    }
}