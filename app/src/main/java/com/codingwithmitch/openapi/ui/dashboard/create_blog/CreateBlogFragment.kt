package com.codingwithmitch.openapi.ui.dashboard.create_blog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.di.dashboard.DashboardScope
import com.codingwithmitch.openapi.ui.*
import com.codingwithmitch.openapi.ui.dashboard.create_blog.state.CREATE_BLOG_VIEW_STATE_BUNDLE_KEY
import com.codingwithmitch.openapi.ui.dashboard.create_blog.state.CreateBlogStateEvent
import com.codingwithmitch.openapi.ui.dashboard.create_blog.state.CreateBlogStateEvent.*
import com.codingwithmitch.openapi.ui.dashboard.create_blog.state.CreateBlogViewState
import com.codingwithmitch.openapi.util.Constants
import com.codingwithmitch.openapi.util.Constants.Constants.Companion.GALLERRY_REQUEST_CODE
import com.codingwithmitch.openapi.util.ErrorHandling.Companion.ERROR_MUST_SELECT_IMAGE
import com.codingwithmitch.openapi.util.ErrorHandling.Companion.ERROR_SOMETHING_WRONG_WITH_IMAGE
import com.codingwithmitch.openapi.util.SuccessHandling.Companion.SUCCESS_BLOG_CREATED
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_create_blog.*
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@DashboardScope
class CreateBlogFragment
@Inject
constructor(
    private val viewModelProviderFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
) : BaseCreateBlogFragment(R.layout.fragment_create_blog) {

    val viewModel: CreateBlogViewModel by viewModels {
        viewModelProviderFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[CREATE_BLOG_VIEW_STATE_BUNDLE_KEY] as CreateBlogViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            CREATE_BLOG_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }

    override fun cancelActiveJobs() {
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        blog_image.setOnClickListener {
            if (stateChangeListener.isStoragePermissionGranted()) {
                pickFromGallery()
            }
        }

        update_textview.setOnClickListener {
            if (stateChangeListener.isStoragePermissionGranted()) {
                pickFromGallery()
            }
        }

        subscribeObservers()
        setDefaultImage()
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            dataState?.let { createBlogDataState ->
                stateChangeListener.onDataStateChange(dataState)
                createBlogDataState.data?.let { data ->
                    data.response?.let { event ->
                        event.peekContent().let { response ->
                            response.message?.let { message ->
                                if (message == SUCCESS_BLOG_CREATED) {
                                    viewModel.clearNewBlogFields()
                                }
                            }
                        }
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let { createBlogViewState ->
                setBlogProperties(
                    title = createBlogViewState.newBlogTitle,
                    body = createBlogViewState.newBlogBody,
                    imageUri = createBlogViewState.newImageUri
                )
            }
        })
    }

    private fun setBlogProperties(title: String?, body: String?, imageUri: Uri?) {
        blog_title.setText(title)
        blog_body.setText(body)

        imageUri?.let { uri ->
            requestManager
                .load(uri)
                .into(blog_image)
        } ?: setDefaultImage()
    }

    private fun setDefaultImage() {
        requestManager
            .load(R.drawable.default_image)
            .into(blog_image)
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, GALLERRY_REQUEST_CODE)
    }

    private fun launchImageCrop(uri: Uri?) {
        context?.let {
            CropImage
                .activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(it, this)
        }
    }

    private fun showErrorDialog(message: String) {
        stateChangeListener.onDataStateChange(
            DataState(
                loading = Loading(false),
                error = Event(
                    StateError(
                        Response(
                            message = message,
                            responseType = ResponseType.Dialog
                        )
                    )
                ),
                data = Data(
                    Event.dataEvent(
                        data = null
                    ),
                    response = null
                )
            )
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                GALLERRY_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        launchImageCrop(uri)
                    } ?: showErrorDialog(ERROR_SOMETHING_WRONG_WITH_IMAGE)
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    val resultUri = result.uri
                    viewModel.setNewBlogFields(
                        title = null,
                        body = null,
                        imageUri = resultUri
                    )
                }

                CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                    showErrorDialog(ERROR_SOMETHING_WRONG_WITH_IMAGE)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.setNewBlogFields(
            title = blog_title.text.toString(),
            body = blog_body.text.toString(),
            imageUri = null
        )
    }

    private fun publishNewBlogPost() {
        var multipartBody: MultipartBody.Part? = null

        viewModel.getNewImageUri()?.let { imageUri ->
            imageUri.path?.let { filePath ->
                val imageFile = File(filePath)
                val requestBody = RequestBody.create(
                    MediaType.parse("image/*"), imageFile
                )
                multipartBody = MultipartBody.Part.createFormData(
                    "image",
                    imageFile.name,
                    requestBody
                )
            }
        }

        multipartBody?.let {
            viewModel.setStateEvent(
                CreateNewBlogEvent(
                    title = blog_title.text.toString(),
                    body = blog_body.text.toString(),
                    image = it
                )
            )

            stateChangeListener.hideSoftKeyboard()
        } ?: showErrorDialog(ERROR_MUST_SELECT_IMAGE)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.publish_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.publish -> {
                val callback: AreYouSureCallback = object : AreYouSureCallback {
                    override fun proceed() {
                        publishNewBlogPost()
                    }

                    override fun cancel() {

                    }
                }

                uiCommunicationListener.onUIMessageReceived(
                    UiMessage(
                        message = getString(R.string.are_you_sure_publish),
                        messageType = UIMessageType.AreYouSureDialog(callback)
                    )
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }
}