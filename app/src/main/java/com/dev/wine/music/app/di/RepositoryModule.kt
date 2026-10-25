package com.dev.wine.music.app.di

import com.dev.wine.music.app.data.repository.AlbumRepositoryImpl
import com.dev.wine.music.app.data.repository.ArtistRepositoryImpl
import com.dev.wine.music.app.data.repository.CollectorRepositoryImpl
import com.dev.wine.music.app.domain.repository.AlbumRepository
import com.dev.wine.music.app.domain.repository.ArtistRepository
import com.dev.wine.music.app.domain.repository.CollectorRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Tells Hilt which implementation to use for each domain interface. */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAlbumRepository(impl: AlbumRepositoryImpl): AlbumRepository

    @Binds
    @Singleton
    abstract fun bindArtistRepository(impl: ArtistRepositoryImpl): ArtistRepository

    @Binds
    @Singleton
    abstract fun bindCollectorRepository(impl: CollectorRepositoryImpl): CollectorRepository
}
