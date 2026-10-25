package com.dev.wine.music.app.data.repository

import com.dev.wine.music.app.data.mapper.toDomain
import com.dev.wine.music.app.data.remote.api.AlbumApi
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.repository.AlbumRepository
import javax.inject.Inject

class AlbumRepositoryImpl @Inject constructor(
    private val api: AlbumApi,
) : AlbumRepository {

    override suspend fun getAlbums(): List<Album> =
        api.getAlbums().map { it.toDomain() }

    override suspend fun getAlbum(albumId: Int): Album =
        api.getAlbum(albumId).toDomain()
}
