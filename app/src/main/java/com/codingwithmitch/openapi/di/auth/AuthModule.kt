package com.codingwithmitch.openapi.di.auth

import com.codingwithmitch.openapi.api.auth.ApiAuthService
import com.codingwithmitch.openapi.persistance.AccountPropertiesDao
import com.codingwithmitch.openapi.persistance.AuthTokenDao
import com.codingwithmitch.openapi.repository.auth.AuthRepository
import com.codingwithmitch.openapi.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AuthModule{

    // TEMPORARY
    @AuthScope
    @Provides
    fun provideFakeApiService(): ApiAuthService{
        return Retrofit.Builder()
            .baseUrl("https://open-api.xyz")
            .build()
            .create(ApiAuthService::class.java)
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        openApiAuthService: ApiAuthService
    ): AuthRepository {
        return AuthRepository(
            authTokenDao,
            accountPropertiesDao,
            openApiAuthService,
            sessionManager
        )
    }

}