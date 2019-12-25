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
import javax.inject.Inject

class CreateBlogViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val createBlogRepository: CreateBlogRepository
) : BaseViewModel<CreateBlogStateEvent, CreateBlogViewState>() {

    override fun initNewViewState(): CreateBlogViewState {
        return CreateBlogViewState()
    }

    override fun handleStateEvent(stateEvent: CreateBlogStateEvent): LiveData<DataState<CreateBlogViewState>> {
        return when(stateEvent) {

            is CreateNewBlogEvent -> {
                AbsentLiveData.create()
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