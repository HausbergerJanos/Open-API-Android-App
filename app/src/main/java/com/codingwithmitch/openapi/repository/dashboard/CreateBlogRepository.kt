package com.codingwithmitch.openapi.repository.dashboard

import com.codingwithmitch.openapi.di.dashboard.DashboardScope
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.ui.dashboard.create_blog.state.CreateBlogViewState
import com.codingwithmitch.openapi.util.DataState
import com.codingwithmitch.openapi.util.StateEvent
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

@DashboardScope
interface CreateBlogRepository {

    fun createNewBlogPost(
        authToken: AuthToken,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?,
        stateEvent: StateEvent
    ): Flow<DataState<CreateBlogViewState>>
}