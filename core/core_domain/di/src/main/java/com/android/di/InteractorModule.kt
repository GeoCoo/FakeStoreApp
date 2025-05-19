package com.android.di

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