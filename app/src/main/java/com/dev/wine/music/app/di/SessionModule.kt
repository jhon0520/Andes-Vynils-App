package com.dev.wine.music.app.di

import com.dev.wine.music.app.data.repository.InMemorySessionRepository
import com.dev.wine.music.app.domain.repository.SessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Own module so tests can swap the session with @TestInstallIn. */
@Module
@InstallIn(SingletonComponent::class)
abstract class SessionModule {

    @Binds
    @Singleton
    abstract fun bindSessionRepository(impl: InMemorySessionRepository): SessionRepository
}
