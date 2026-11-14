package com.dev.wine.music.app.data.repository

import com.dev.wine.music.app.data.cache.RepositoryCache
import com.dev.wine.music.app.data.mapper.toDomain
import com.dev.wine.music.app.data.remote.api.AlbumApi
import com.dev.wine.music.app.data.remote.safeApiCall
import com.dev.wine.music.app.domain.coroutines.IoDispatcher
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.repository.AlbumRepository
import com.dev.wine.music.app.util.fastMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Answers from [cache] when it already has the data. Otherwise the network call, the JSON
 * parsing and the mapping run on [ioDispatcher], never on the main thread.
 */
class AlbumRepositoryImpl @Inject constructor(
    private val api: AlbumApi,
    private val cache: RepositoryCache,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : AlbumRepository {

    override suspend fun getAlbums(): List<Album> {
        cache.albums?.let { return it }
        val albums = withContext(ioDispatcher) {
            safeApiCall { api.getAlbums().fastMap { it.toDomain() } }
        }
        cache.albums = albums
        return albums
    }

    override suspend fun getAlbum(albumId: Int): Album {
        cache.albumDetails[albumId]?.let { return it }
        val album = withContext(ioDispatcher) {
            safeApiCall { api.getAlbum(albumId).toDomain() }
        }
        cache.albumDetails.put(albumId, album)
        return album
    }
}
