package com.codingwithmitch.openapi.di

import com.codingwithmitch.openapi.di.auth.AuthComponent
import com.codingwithmitch.openapi.di.dashboard.DashboardComponent
import dagger.Module

@Module(
    subcomponents = [
        AuthComponent::class,
        DashboardComponent::class
    ]
)
class SubComponentsModule {

}