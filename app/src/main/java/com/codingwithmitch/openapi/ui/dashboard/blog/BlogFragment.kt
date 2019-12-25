package com.codingwithmitch.openapi.ui.dashboard.blog

import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.bumptech.glide.RequestManager
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.persistance.BlogQueryUtils.Companion.BLOG_FILTER_DATE_UPDATED
import com.codingwithmitch.openapi.persistance.BlogQueryUtils.Companion.BLOG_FILTER_USERNAME
import com.codingwithmitch.openapi.persistance.BlogQueryUtils.Companion.BLOG_ORDER_ASC
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.dashboard.blog.BlogRecyclerViewAdapter.Interaction
import com.codingwithmitch.openapi.ui.dashboard.blog.state.BlogStateEvent
import com.codingwithmitch.openapi.ui.dashboard.blog.state.BlogViewState
import com.codingwithmitch.openapi.ui.dashboard.blog.viewmodel.*
import com.codingwithmitch.openapi.util.ErrorHandling
import com.codingwithmitch.openapi.util.TopItemSpacingDecoration
import kotlinx.android.synthetic.main.fragment_blog.*
import javax.inject.Inject

class BlogFragment : BaseBlogFragment(), Interaction, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var recyclerAdapter: BlogRecyclerViewAdapter
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        swipe_refresh.setOnRefreshListener(this)

        initRecyclerView()
        subscribeObservers()

        if (savedInstanceState == null) {
            viewModel.loadFirstPage()
        }
    }

    private fun onBlogSearchOrFilter() {
        viewModel.loadFirstPage().let {
            resetUI()
        }
    }

    private fun resetUI() {
        blog_post_recyclerview?.smoothScrollToPosition(0)
        stateChangeListener.hideSoftKeyboard()
        focusable_view.requestFocus()
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            dataState?.let { blogDataState ->
                stateChangeListener.onDataStateChange(blogDataState)
                handlePagination(blogDataState)
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let { blogViewState ->
                recyclerAdapter.apply {
                    preloadGlideImages(
                        requestManager = dependencyProvider.getGlideRequestManager(),
                        list = viewState.blogFields.blogList
                    )

                    submitList(
                        list = blogViewState.blogFields.blogList,
                        isQueryExhausted = blogViewState.blogFields.isQueryExhausted
                    )
                }
            }
        })
    }

    private fun handlePagination(dataState: DataState<BlogViewState>) {
        // Handle incoming data from DataState
        dataState.data?.let { data ->
            data.data?.let { event ->
                event.getContentIfNotHandled()?.let { blogViewState ->
                    viewModel.handleIncomingBlogListData(blogViewState)
                }
            }
        }

        // Check for pagination end (no more results)
        // must do this b/c server will return an ApiErrorResponse if page is not valid,
        // -> meaning there is no more data.
        dataState.error?.let { event ->
            event.peekContent().response.message?.let { message ->
                if (ErrorHandling.isPaginationDone(message)) {
                    // Handle the error message event so it doesn't display in UI
                    event.getContentIfNotHandled()

                    // set query exhausted to update RecyclerView with
                    // "No more results..." list item
                    viewModel.setQueryExhausted(true)
                }
            }
        }
    }

    private fun initSearchView(menu: Menu) {
        activity?.apply {
            val searchManager: SearchManager = getSystemService(SEARCH_SERVICE) as SearchManager
            searchView = menu.findItem(R.id.action_search).actionView as SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.maxWidth = Integer.MAX_VALUE
            searchView.setIconifiedByDefault(true)
            searchView.isSubmitButtonEnabled = true
        }

        // Case 1 : ENTER ON COMPUTER KEYBOARD OR ARROW ON VIRTUAL KEYBOARD
        val searchPlate = searchView.findViewById(R.id.search_src_text) as EditText
        searchPlate.setOnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                || actionId == EditorInfo.IME_ACTION_SEARCH
            ) {
                val searchQuery = v.text.toString()
                Log.e(TAG, "SearchView: (keyboard or arrow) executing search...: $searchQuery")
                viewModel.setQuery(searchQuery).let {
                    onBlogSearchOrFilter()
                }
            }
            true
        }

        // Case 2 : SEARCH BUTTON CLICKED (in toolbar)
        val searchButton = searchView.findViewById(R.id.search_go_btn) as View
        searchButton.setOnClickListener {
            val searchQuery = searchPlate.text.toString()
            Log.e(TAG, "SearchView: (button) executing search...: ${searchQuery}")
            viewModel.setQuery(searchQuery).let {
                onBlogSearchOrFilter()
            }
        }
    }

    private fun initRecyclerView() {
        blog_post_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@BlogFragment.context)

            val topItemSpacingDecoration = TopItemSpacingDecoration(30)
            removeItemDecoration(topItemSpacingDecoration)
            addItemDecoration(topItemSpacingDecoration)

            recyclerAdapter = BlogRecyclerViewAdapter(
                requestManager = dependencyProvider.getGlideRequestManager(),
                interaction = this@BlogFragment
            )

            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == recyclerAdapter.itemCount.minus(1)) {
                        viewModel.nextPage()
                    }
                }
            })

            adapter = recyclerAdapter
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear references
        blog_post_recyclerview?.adapter = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        initSearchView(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_filter_settings -> {
                showFilterDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemSelected(position: Int, item: BlogPost) {
        viewModel.setBlogPost(item)
        findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
    }

    override fun onRefresh() {
        onBlogSearchOrFilter()
        swipe_refresh.isRefreshing = false
    }

    fun showFilterDialog(){

        activity?.let {
            val dialog = MaterialDialog(it)
                .noAutoDismiss()
                .customView(R.layout.layout_blog_filter)

            val view = dialog.getCustomView()

            // Highlight the previous filter options
            val filter = viewModel.getFilter()
            if(filter == BLOG_FILTER_DATE_UPDATED){
                view.findViewById<RadioGroup>(R.id.filter_group).check(R.id.filter_date)
            }
            else{
                view.findViewById<RadioGroup>(R.id.filter_group).check(R.id.filter_author)
            }

            val order = viewModel.getOrder()
            if(order == BLOG_ORDER_ASC){
                view.findViewById<RadioGroup>(R.id.order_group).check(R.id.filter_asc)
            }
            else{
                view.findViewById<RadioGroup>(R.id.order_group).check(R.id.filter_desc)
            }

            // Listen for newly applied filters
            view.findViewById<TextView>(R.id.positive_button).setOnClickListener{
                Log.d(TAG, "FilterDialog: apply filter.")

                val selectedFilter = dialog.getCustomView().findViewById<RadioButton>(
                    dialog.getCustomView().findViewById<RadioGroup>(R.id.filter_group).checkedRadioButtonId
                )
                val selectedOrder= dialog.getCustomView().findViewById<RadioButton>(
                    dialog.getCustomView().findViewById<RadioGroup>(R.id.order_group).checkedRadioButtonId
                )

                var filter = BLOG_FILTER_DATE_UPDATED
                if(selectedFilter.text.toString() == getString(R.string.filter_author)){
                    filter = BLOG_FILTER_USERNAME
                }

                var order = ""
                if(selectedOrder.text.toString() == getString(R.string.filter_desc)){
                    order = "-"
                }

                // Set the filter and order in the viewModel
                // Save the shared preferences
                viewModel.saveFilterOptions(filter, order).let{
                    viewModel.setBlogFilter(filter)
                    viewModel.setBlogOrder(order)
                    onBlogSearchOrFilter()
                }
                dialog.dismiss()
            }

            view.findViewById<TextView>(R.id.negative_button).setOnClickListener {
                Log.d(TAG, "FilterDialog: cancelling filter.")
                dialog.dismiss()
            }

            dialog.show()
        }
    }
}