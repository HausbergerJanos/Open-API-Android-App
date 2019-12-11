package com.codingwithmitch.openapi.di.dashboard

import com.codingwithmitch.openapi.api.dashboard.ApiDashboardService
import com.codingwithmitch.openapi.persistance.AccountPropertiesDao
import com.codingwithmitch.openapi.repository.dashboard.AccountRepository
import com.codingwithmitch.openapi.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class DashboardModule {

    @DashboardScope
    @Provides
    fun provideApiDashboardService(retrofitBuilder: Retrofit.Builder): ApiDashboardService {
        return retrofitBuilder
            .build()
            .create(ApiDashboardService::class.java)
    }

    @DashboardScope
    @Provides
    fun provideDashboardRepository(
        apiDashboardService: ApiDashboardService,
        accountPropertiesDao: AccountPropertiesDao,
        sessionManager: SessionManager
    ): AccountRepository {
        return AccountRepository(
            apiDashboardService,
            accountPropertiesDao,
            sessionManager
        )
    }
}