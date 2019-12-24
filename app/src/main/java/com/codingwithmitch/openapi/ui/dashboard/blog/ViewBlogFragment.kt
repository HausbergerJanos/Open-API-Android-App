package com.codingwithmitch.openapi.ui.dashboard.blog

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.ui.AreYouSureCallback
import com.codingwithmitch.openapi.ui.UIMessageType
import com.codingwithmitch.openapi.ui.UIMessageType.*
import com.codingwithmitch.openapi.ui.UiMessage
import com.codingwithmitch.openapi.ui.dashboard.blog.state.BlogStateEvent
import com.codingwithmitch.openapi.ui.dashboard.blog.state.BlogStateEvent.*
import com.codingwithmitch.openapi.ui.dashboard.blog.viewmodel.getBlogPost
import com.codingwithmitch.openapi.ui.dashboard.blog.viewmodel.isAuthorOfBlogPost
import com.codingwithmitch.openapi.ui.dashboard.blog.viewmodel.setIsAuthorOfBlogPost
import com.codingwithmitch.openapi.ui.dashboard.blog.viewmodel.setUpdatedBlogFields
import com.codingwithmitch.openapi.util.DateUtils
import com.codingwithmitch.openapi.util.SuccessHandling.Companion.SUCCESS_BLOG_DELETED
import kotlinx.android.synthetic.main.fragment_view_blog.*

class ViewBlogFragment : BaseBlogFragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        setHasOptionsMenu(true)
        subscribeObservers()
        checkIsAuthorOfBlogPost()
        stateChangeListener.expandAppbar()

        delete_button.setOnClickListener {
            confirmDeleteBlogRequest()
        }
    }

    private fun confirmDeleteBlogRequest() {
        val callback: AreYouSureCallback = object: AreYouSureCallback {
            override fun proceed() {
                deleteBlogPost()
            }

            override fun cancel() {
                // ignore
            }

        }

        uiCommunicationListener.onUIMessageReceived(
            uiMessage = UiMessage(
                message = getString(R.string.are_you_sure_delete),
                messageType = AreYouSureDialog(callback)
            )
        )
    }

    private fun deleteBlogPost() {
        viewModel.setStateEvent(DeleteBlogPostEvent)
    }

    private fun checkIsAuthorOfBlogPost() {
        viewModel.setIsAuthorOfBlogPost(false) // reset
        viewModel.setStateEvent(CheckAuthorOfBlogPost)
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            dataState?.let { blogDataState ->
                stateChangeListener.onDataStateChange(blogDataState)

                blogDataState.data?.let { data ->
                    data.data?.getContentIfNotHandled()?.let { blogViewState ->
                        viewModel.setIsAuthorOfBlogPost(
                            blogViewState.viewBlogFields.isAuthorOfBlogPost
                        )
                    }

                    data.response?.peekContent()?.let { response ->
                        if (response.message == SUCCESS_BLOG_DELETED) {
                            viewModel.removeDeletedBlogPost()
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let { blogViewState ->
                blogViewState.viewBlogFields.blogPost?.let { blogPost ->
                    setBlogProperties(blogPost)
                }

                if (blogViewState.viewBlogFields.isAuthorOfBlogPost) {
                    adaptViewToAuthorMode()
                }
            }
        })
    }

    private fun adaptViewToAuthorMode() {
        activity?.invalidateOptionsMenu()
        delete_button.visibility = View.VISIBLE
    }

    private fun setBlogProperties(blogPost: BlogPost) {
        requestManager
            .load(blogPost.image)
            .into(blog_image)

        blog_title.text = blogPost.title
        blog_author.text = blogPost.userName
        blog_update_date.text = DateUtils.convertLongToStringDate(blogPost.dateUpdated)
        blog_body.text = blogPost.body
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (viewModel.isAuthorOfBlogPost()) {
            inflater.inflate(R.menu.edit_view_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (viewModel.isAuthorOfBlogPost()) {
            when (item.itemId) {
                R.id.edit -> {
                    navToUpdateBlogFragment()
                    return true
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun navToUpdateBlogFragment() {
        try {
            // Prep for next fragment
            viewModel.setUpdatedBlogFields(
                title = viewModel.getBlogPost().title,
                body = viewModel.getBlogPost().body,
                uri = viewModel.getBlogPost().image.toUri()
            )

            findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}