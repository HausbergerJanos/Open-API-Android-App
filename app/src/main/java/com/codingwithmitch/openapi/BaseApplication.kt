package com.codingwithmitch.openapi

import android.app.Application
import com.codingwithmitch.openapi.di.AppComponent
import com.codingwithmitch.openapi.di.DaggerAppComponent
import com.codingwithmitch.openapi.di.auth.AuthComponent
import com.codingwithmitch.openapi.di.dashboard.DashboardComponent

class BaseApplication: Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        initAppComponent()
    }

    //region AuthComponent
    private var authComponent: AuthComponent? = null

    fun authComponent(): AuthComponent {
        if (authComponent == null) {
            authComponent = appComponent.authComponent().create()
        }
        return authComponent as AuthComponent
    }

    fun releaseAuthComponent() {
        authComponent = null
    }

    //endregion

    //region DashboardComponent
    private var dashboardComponent: DashboardComponent? = null

    fun dashboardComponent(): DashboardComponent {
        if (dashboardComponent == null) {
            dashboardComponent = appComponent.dashboardComponent().create()
        }
        return dashboardComponent as DashboardComponent
    }

    fun releaseDashboardComponent() {
        dashboardComponent = null
    }
    //endregion

    fun initAppComponent() {
        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()
    }
}