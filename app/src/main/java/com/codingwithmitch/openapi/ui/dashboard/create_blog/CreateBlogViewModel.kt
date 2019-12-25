package com.codingwithmitch.openapi.ui.dashboard.create_blog

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.codingwithmitch.openapi.repository.dashboard.CreateBlogRepository
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.BaseViewModel
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.Loading
import com.codingwithmitch.openapi.ui.dashboard.create_blog.state.CreateBlogStateEvent
import com.codingwithmitch.openapi.ui.dashboard.create_blog.state.CreateBlogStateEvent.*
import com.codingwithmitch.openapi.ui.dashboard.create_blog.state.CreateBlogViewState
import com.codingwithmitch.openapi.util.AbsentLiveData
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

class CreateBlogViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val createBlogRepository: CreateBlogRepository
) : BaseViewModel<CreateBlogStateEvent, CreateBlogViewState>() {

    override fun initNewViewState(): CreateBlogViewState {
        return CreateBlogViewState()
    }

    override fun handleStateEvent(stateEvent: CreateBlogStateEvent): LiveData<DataState<CreateBlogViewState>> {
        return when(stateEvent) {

            is CreateNewBlogEvent -> {
                sessionManager.cachedToken.value?.let { authToken ->
                    val title = RequestBody.create(
                        MediaType.parse("text/plain"),
                        stateEvent.title
                    )

                    val body = RequestBody.create(
                        MediaType.parse("text/plain"),
                        stateEvent.body
                    )

                    createBlogRepository.createNewBlogPost(
                        authToken = authToken,
                        title = title,
                        body = body,
                        image = stateEvent.image
                    )
                }?: AbsentLiveData.create()
            }

            is None -> {
                liveData {
                    emit(
                        DataState<CreateBlogViewState>(
                            error = null,
                            data = null,
                            loading = Loading(false)
                        )
                    )
                }
            }
        }
    }

    fun setNewBlogFields(title: String?, body: String?, imageUri: Uri?) {
        val update = getCurrentViewState()

        title?.let {
            update.newBlogTitle = it
        }

        body?.let {
            update.newBlogBody = it
        }

        imageUri?.let {
            update.newImageUri = it
        }

        setViewState(update)
    }

    fun clearNewBlogFields() {
        val update = getCurrentViewState()
        update.newBlogTitle = null
        update.newBlogBody = null
        update.newImageUri = null
        setViewState(update)
    }

    fun getNewImageUri(): Uri? {
        getCurrentViewState().let {
            return it.newImageUri
        }
    }

    fun cancelActiveJobs() {
        createBlogRepository.cancelActiveJobs()
        handlePendingData()
    }

    private fun handlePendingData() {
        setStateEvent(None)
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}