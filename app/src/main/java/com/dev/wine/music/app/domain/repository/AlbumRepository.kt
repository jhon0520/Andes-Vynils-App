package com.dev.wine.music.app.domain.repository

import com.dev.wine.music.app.domain.model.Album

/** Contract the domain needs. The data layer decides how to fulfil it. */
interface AlbumRepository {
    suspend fun getAlbums(): List<Album>
    suspend fun getAlbum(albumId: Int): Album
}
