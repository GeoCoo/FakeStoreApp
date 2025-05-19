package com.android.di

import com.android.api.ProductsInteractor
import com.android.api.UserAuthInteractor
import com.android.impl.ProductsInteractorImpl
import com.android.impl.UserAuthInteractorImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class InteractorModule {

    @Binds
    abstract fun bindProductsInteractor(impl: ProductsInteractorImpl): ProductsInteractor

    @Binds
    abstract fun providesAuthInteractor(impl: UserAuthInteractorImpl): UserAuthInteractor
}