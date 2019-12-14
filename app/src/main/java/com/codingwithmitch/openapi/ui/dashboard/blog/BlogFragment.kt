package com.codingwithmitch.openapi.ui.dashboard.blog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.dashboard.blog.state.BlogStateEvent
import kotlinx.android.synthetic.main.fragment_blog.*

class BlogFragment : BaseBlogFragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        goViewBlogFragment?.setOnClickListener {
            findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
        }

        subscribeObservers()
        executeSearch()
    }

    private fun executeSearch() {
        viewModel.setQuery("")
        viewModel.setStateEvent(
            BlogStateEvent.BlogSearchEvent()
        )
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            dataState?.let {
                stateChangeListener.onDataStateChange(it)
                it.data?.let { viewState ->
                    viewState.data?.let { event ->
                        event.getContentIfNotHandled()?.let { blogViewState ->
                            Log.d(TAG, "BlogFragment, DataState: $it")
                            viewModel.setBlogListData(blogViewState.blogFields.blogList)
                        }
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { blogViewState ->
            Log.d(TAG, "BlogFragment, ViewState: $blogViewState")
        })
    }
}