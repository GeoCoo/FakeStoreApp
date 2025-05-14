package com.android.core.core_api.di

import com.android.core.core_api.api.ApiClient
import com.android.core.core_api.api.ApiClientImpl
import com.android.core.core_api.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(
        ApiService::class.java)

    @Provides
    @Singleton
    fun provideApiClient(impl: ApiClientImpl): ApiClient = impl
}