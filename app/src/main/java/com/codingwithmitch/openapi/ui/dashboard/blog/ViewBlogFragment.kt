package com.codingwithmitch.openapi.ui.dashboard.blog

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.ui.dashboard.blog.state.BlogStateEvent
import com.codingwithmitch.openapi.ui.dashboard.blog.state.BlogStateEvent.*
import com.codingwithmitch.openapi.ui.dashboard.blog.viewmodel.isAuthorOfBlogPost
import com.codingwithmitch.openapi.ui.dashboard.blog.viewmodel.setIsAuthorOfBlogPost
import com.codingwithmitch.openapi.util.DateUtils
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
        setHasOptionsMenu(true)
        subscribeObservers()
        checkIsAuthorOfBlogPost()
        stateChangeListener.expandAppbar()
    }

    private fun checkIsAuthorOfBlogPost() {
        viewModel.setIsAuthorOfBlogPost(false) // reset
        viewModel.setStateEvent(CheckAuthorOfBlogPost())
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
        findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment)
    }
}