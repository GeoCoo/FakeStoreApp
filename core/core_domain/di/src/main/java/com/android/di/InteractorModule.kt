package com.android.di

import com.android.api.ProductsInteractor
import com.android.api.UserAuthInteractor
import com.android.impl.ProductsInteractorImpl
import com.android.impl.UserAuthInteractorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class InteractorModule {

    @Provides
    fun provideProductsInteractor(impl: ProductsInteractorImpl): ProductsInteractor = impl

    @Provides
    fun providesAuthInteractor(impl: UserAuthInteractorImpl): UserAuthInteractor = impl
}