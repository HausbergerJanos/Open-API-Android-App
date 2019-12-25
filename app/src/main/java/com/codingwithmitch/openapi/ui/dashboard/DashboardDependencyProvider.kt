package com.codingwithmitch.openapi.ui.dashboard

import com.bumptech.glide.RequestManager
import com.codingwithmitch.openapi.di.ViewModelProviderFactory

interface DashboardDependencyProvider {

    fun getVMProviderFactory(): ViewModelProviderFactory

    fun getGlideRequestManager(): RequestManager
}