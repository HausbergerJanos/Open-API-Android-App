package com.codingwithmitch.openapi.di.dashboard

import com.codingwithmitch.openapi.api.dashboard.ApiDashboardService
import com.codingwithmitch.openapi.persistance.AccountPropertiesDao
import com.codingwithmitch.openapi.persistance.AppDatabase
import com.codingwithmitch.openapi.persistance.BlogPostDao
import com.codingwithmitch.openapi.repository.dashboard.AccountRepository
import com.codingwithmitch.openapi.repository.dashboard.BlogRepository
import com.codingwithmitch.openapi.repository.dashboard.CreateBlogRepository
import com.codingwithmitch.openapi.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
object DashboardModule {

    @JvmStatic
    @DashboardScope
    @Provides
    fun provideApiDashboardService(retrofitBuilder: Retrofit.Builder): ApiDashboardService {
        return retrofitBuilder
            .build()
            .create(ApiDashboardService::class.java)
    }

    @JvmStatic
    @DashboardScope
    @Provides
    fun provideAccountRepository(
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

    @JvmStatic
    @DashboardScope
    @Provides
    fun provideBlogPostDao(db: AppDatabase): BlogPostDao {
        return db.getBlogPostDao()
    }

    @JvmStatic
    @DashboardScope
    @Provides
    fun provideBlogRepository(
        apiDashboardService: ApiDashboardService,
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ): BlogRepository {
        return BlogRepository(
            apiDashboardService,
            blogPostDao,
            sessionManager
        )
    }

    @JvmStatic
    @DashboardScope
    @Provides
    fun provideCreateBlogRepository(
        apiDashboardService: ApiDashboardService,
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ): CreateBlogRepository {
        return CreateBlogRepository(
            apiDashboardService,
            blogPostDao,
            sessionManager
        )
    }
}