package com.codingwithmitch.openapi.di.dashboard

import com.codingwithmitch.openapi.ui.dashboard.account.AccountFragment
import com.codingwithmitch.openapi.ui.dashboard.account.ChangePasswordFragment
import com.codingwithmitch.openapi.ui.dashboard.account.UpdateAccountFragment
import com.codingwithmitch.openapi.ui.dashboard.blog.BlogFragment
import com.codingwithmitch.openapi.ui.dashboard.blog.UpdateBlogFragment
import com.codingwithmitch.openapi.ui.dashboard.blog.ViewBlogFragment
import com.codingwithmitch.openapi.ui.dashboard.create_blog.CreateBlogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class DashboardFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeBlogFragment(): BlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeAccountFragment(): AccountFragment

    @ContributesAndroidInjector()
    abstract fun contributeChangePasswordFragment(): ChangePasswordFragment

    @ContributesAndroidInjector()
    abstract fun contributeCreateBlogFragment(): CreateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateBlogFragment(): UpdateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeViewBlogFragment(): ViewBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateAccountFragment(): UpdateAccountFragment
}