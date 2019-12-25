package com.codingwithmitch.openapi.ui.dashboard.create_blog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.*
import com.codingwithmitch.openapi.util.Constants
import com.codingwithmitch.openapi.util.Constants.Constants.Companion.GALLERRY_REQUEST_CODE
import com.codingwithmitch.openapi.util.ErrorHandling.Companion.ERROR_SOMETHING_WRONG_WITH_IMAGE
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_create_blog.*

class CreateBlogFragment : BaseCreateBlogFragment(){

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            stateChangeListener.onDataStateChange(dataState)

            dataState?.let { createBlogDataState ->

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
                            responseType = ResponseType.Dialog()
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
}