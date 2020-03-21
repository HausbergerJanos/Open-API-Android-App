package com.codingwithmitch.openapi.di.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codingwithmitch.openapi.di.auth.key.DashboardViewModelKey
import com.codingwithmitch.openapi.ui.dashboard.account.AccountViewModel
import com.codingwithmitch.openapi.ui.dashboard.blog.viewmodel.BlogViewModel
import com.codingwithmitch.openapi.ui.dashboard.create_blog.CreateBlogViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class DashboardViewModelModule {

    @DashboardScope
    @Binds
    abstract fun provideViewModelFactory(factory: DashboardViewModelFactory): ViewModelProvider.Factory

    @DashboardScope
    @Binds
    @IntoMap
    @DashboardViewModelKey(AccountViewModel::class)
    abstract fun bindAuthViewModel(accountViewModel: AccountViewModel): ViewModel

    @DashboardScope
    @Binds
    @IntoMap
    @DashboardViewModelKey(BlogViewModel::class)
    abstract fun bindBlogViewModel(blogViewModel: BlogViewModel): ViewModel

    @DashboardScope
    @Binds
    @IntoMap
    @DashboardViewModelKey(CreateBlogViewModel::class)
    abstract fun bindCreateBlogViewModel(createBlogViewModel: CreateBlogViewModel): ViewModel
}