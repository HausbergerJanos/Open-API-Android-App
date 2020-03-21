package com.codingwithmitch.openapi.di.dashboard

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.codingwithmitch.openapi.fragments.auth.AuthFragmentFactory
import com.codingwithmitch.openapi.fragments.dashboard.account.AccountFragmentFactory
import com.codingwithmitch.openapi.fragments.dashboard.blog.BlogFragmentFactory
import com.codingwithmitch.openapi.fragments.dashboard.create_blog.CreateBlogFragmentFactory
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
object DashboardFragmentsModule {

    @JvmStatic
    @DashboardScope
    @Provides
    @Named("AccountFragmentFactory")
    fun provideAccountFragmentFactory(viewModelFactory: ViewModelProvider.Factory): FragmentFactory {
        return AccountFragmentFactory(viewModelFactory)
    }

    @JvmStatic
    @DashboardScope
    @Provides
    @Named("BlogFragmentFactory")
    fun provideBlogFragmentFactory(viewModelFactory: ViewModelProvider.Factory, requestManager: RequestManager): FragmentFactory {
        return BlogFragmentFactory(viewModelFactory, requestManager)
    }

    @JvmStatic
    @DashboardScope
    @Provides
    @Named("CreateBlogFragmentFactory")
    fun provideCreateBlogFragmentFactory(viewModelFactory: ViewModelProvider.Factory, requestManager: RequestManager): FragmentFactory {
        return CreateBlogFragmentFactory(viewModelFactory, requestManager)
    }
}