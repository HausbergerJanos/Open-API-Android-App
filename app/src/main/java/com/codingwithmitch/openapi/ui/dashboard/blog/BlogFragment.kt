package com.codingwithmitch.openapi.ui.dashboard.blog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.ui.dashboard.blog.BlogRecyclerViewAdapter.Interaction
import com.codingwithmitch.openapi.ui.dashboard.blog.state.BlogStateEvent
import com.codingwithmitch.openapi.util.TopItemSpacingDecoration
import kotlinx.android.synthetic.main.fragment_blog.*
import javax.inject.Inject

class BlogFragment : BaseBlogFragment(), Interaction{

    @Inject
    lateinit var requestManager: RequestManager

    private lateinit var adapter: BlogRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
        goViewBlogFragment?.setOnClickListener {
            findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
        }
        */

        initRecyclerView()
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

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let { blogViewState ->
                adapter.submitList(
                    list = blogViewState.blogFields.blogList,
                    isQueryExhausted = true
                )
            }
        })
    }

    private fun initRecyclerView() {
        blog_post_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@BlogFragment.context)

            val topItemSpacingDecoration = TopItemSpacingDecoration(30)
            removeItemDecoration(topItemSpacingDecoration)
            addItemDecoration(topItemSpacingDecoration)

            this@BlogFragment.adapter = BlogRecyclerViewAdapter(
                requestManager = requestManager,
                interaction = this@BlogFragment
            )

            addOnScrollListener(object: RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == this@BlogFragment.adapter.itemCount.minus(1)) {
                        Toast.makeText(this@BlogFragment.context, "Load next page", Toast.LENGTH_SHORT).show()
                    }
                }
            })

            adapter = this@BlogFragment.adapter
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear references
        blog_post_recyclerview?.adapter = null
    }

    override fun onItemSelected(position: Int, item: BlogPost) {
        Toast.makeText(context, position.toString(), Toast.LENGTH_SHORT).show()
    }
}