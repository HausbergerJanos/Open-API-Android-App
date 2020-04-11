package com.codingwithmitch.openapi.di.dashboard

import com.codingwithmitch.openapi.ui.dashboard.DashboardActivity
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