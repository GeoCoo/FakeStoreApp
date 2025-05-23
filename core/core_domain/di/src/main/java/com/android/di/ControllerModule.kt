package com.android.di

import com.android.api.FavoriteController
import com.android.api.PreferencesController
import com.android.impl.FavoriteControllerImpl
import com.android.impl.PreferencesControllerImpl
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ControllerBindModule {

    @Binds
    @Singleton
    abstract fun providePreferencesController(impl: PreferencesControllerImpl): PreferencesController

    @Binds
    @Singleton
    abstract fun provideFavoriteController(impl: FavoriteControllerImpl): FavoriteController
}

@Module
@InstallIn(SingletonComponent::class)
object ControllerProvideModule {
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()
}