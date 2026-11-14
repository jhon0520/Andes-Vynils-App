package com.dev.wine.music.app.data.repository

import com.dev.wine.music.app.data.mapper.toDomain
import com.dev.wine.music.app.data.remote.api.AlbumApi
import com.dev.wine.music.app.data.remote.safeApiCall
import com.dev.wine.music.app.domain.coroutines.IoDispatcher
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.repository.AlbumRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** The network call, the JSON parsing and the mapping run on [ioDispatcher], never on the main thread. */
class AlbumRepositoryImpl @Inject constructor(
    private val api: AlbumApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : AlbumRepository {

    override suspend fun getAlbums(): List<Album> = withContext(ioDispatcher) {
        safeApiCall { api.getAlbums().map { it.toDomain() } }
    }

    override suspend fun getAlbum(albumId: Int): Album = withContext(ioDispatcher) {
        safeApiCall { api.getAlbum(albumId).toDomain() }
    }
}
