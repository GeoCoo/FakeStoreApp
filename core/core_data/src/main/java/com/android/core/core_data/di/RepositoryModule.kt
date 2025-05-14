package com.android.core.core_data.di

import com.android.core.core_data.repository.AuthRepository
import com.android.core.core_data.repository.AuthRepositoryImpl
import com.android.core.core_data.repository.ProductsRepository
import com.android.core.core_data.repository.ProductsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provieProductsRepository(impl: ProductsRepositoryImpl): ProductsRepository = impl

    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl
}

