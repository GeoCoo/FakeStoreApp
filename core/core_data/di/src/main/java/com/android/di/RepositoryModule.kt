package com.android.di


import com.android.api.AuthRepository
import com.android.api.ProductsRepository
import com.android.impl.AuthRepositoryImpl
import com.android.impl.ProductsRepositoryImpl
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

