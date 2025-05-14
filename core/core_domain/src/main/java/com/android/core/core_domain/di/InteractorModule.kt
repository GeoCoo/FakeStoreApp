package com.android.core.core_domain.di

import com.android.core.core_domain.interactor.ProductsInteractor
import com.android.core.core_domain.interactor.ProductsInteractorImpl
import com.android.core.core_domain.interactor.UserAuthInteractor
import com.android.core.core_domain.interactor.UserAuthInteractorImpl
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