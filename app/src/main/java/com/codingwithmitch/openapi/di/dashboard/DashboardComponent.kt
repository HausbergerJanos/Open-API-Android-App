package com.codingwithmitch.openapi.di.dashboard

import com.codingwithmitch.openapi.ui.auth.AuthActivity
import com.codingwithmitch.openapi.ui.auth.LoginFragment
import com.codingwithmitch.openapi.ui.dashboard.DashboardActivity
import com.codingwithmitch.openapi.ui.dashboard.blog.BlogFragment
import dagger.Subcomponent

@DashboardScope
@Subcomponent(
    modules = [
        DashboardModule::class,
        DashboardViewModelModule::class,
        DashboardFragmentsModule::class
    ]
)
interface DashboardComponent {

    @Subcomponent.Factory
    interface Factory {

        fun create(): DashboardComponent
    }

    fun inject(dashboardActivity: DashboardActivity)
}