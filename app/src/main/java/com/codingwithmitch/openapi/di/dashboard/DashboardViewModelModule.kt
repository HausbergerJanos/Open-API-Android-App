package com.codingwithmitch.openapi.di.dashboard

import androidx.lifecycle.ViewModel
import com.codingwithmitch.openapi.di.ViewModelKey
import com.codingwithmitch.openapi.ui.dashboard.account.AccountViewModel
import com.codingwithmitch.openapi.ui.dashboard.blog.viewmodel.BlogViewModel
import com.codingwithmitch.openapi.ui.dashboard.create_blog.CreateBlogViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class DashboardViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAuthViewModel(accountViewModel: AccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BlogViewModel::class)
    abstract fun bindBlogViewModel(blogViewModel: BlogViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreateBlogViewModel::class)
    abstract fun bindCreateBlogViewModel(createBlogViewModel: CreateBlogViewModel): ViewModel
}