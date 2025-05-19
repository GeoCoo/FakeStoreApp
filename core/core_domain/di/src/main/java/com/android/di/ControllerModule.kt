package com.android.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PreferencesModule {

    @Provides
    @Singleton
    fun providePreferencesController(impl: PreferencesControllerImpl): PreferencesController =
        impl

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }
}