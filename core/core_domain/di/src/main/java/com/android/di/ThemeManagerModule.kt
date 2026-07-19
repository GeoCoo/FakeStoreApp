package com.android.di

import com.android.api.ResourceProvider
import com.android.session.ThemeManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ThemeManagerModule {
    @Provides
    @Singleton
    fun provideThemeManager(resourceProvider: ResourceProvider): ThemeManager {
        return ThemeManager(resourceProvider)
    }
}
