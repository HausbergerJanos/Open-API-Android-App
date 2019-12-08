package com.codingwithmitch.openapi.di

import com.codingwithmitch.openapi.di.auth.AuthFragmentBuildersModule
import com.codingwithmitch.openapi.di.auth.AuthModule
import com.codingwithmitch.openapi.di.auth.AuthScope
import com.codingwithmitch.openapi.di.auth.AuthViewModelModule
import com.codingwithmitch.openapi.di.dashboard.DashboardFragmentBuildersModule
import com.codingwithmitch.openapi.di.dashboard.DashboardModule
import com.codingwithmitch.openapi.di.dashboard.DashboardScope
import com.codingwithmitch.openapi.di.dashboard.DashboardViewModelModule
import com.codingwithmitch.openapi.ui.auth.AuthActivity
import com.codingwithmitch.openapi.ui.dashboard.DashboardActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

    @DashboardScope
    @ContributesAndroidInjector(
        modules = [DashboardModule::class, DashboardFragmentBuildersModule::class, DashboardViewModelModule::class]
    )
    abstract fun contributeDashboardActivity(): DashboardActivity

}