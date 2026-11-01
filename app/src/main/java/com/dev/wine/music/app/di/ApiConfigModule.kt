package com.dev.wine.music.app.di

import com.dev.wine.music.app.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

/** The URL of BackVynils that Retrofit calls. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiBaseUrl

/**
 * Kept apart from [NetworkModule] so instrumented tests can replace only the URL
 * (`@UninstallModules(ApiConfigModule::class)`) and point the real app at a fake backend.
 */
@Module
@InstallIn(SingletonComponent::class)
object ApiConfigModule {

    @Provides
    @ApiBaseUrl
    fun provideApiBaseUrl(): String = BuildConfig.API_BASE_URL
}
