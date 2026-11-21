package com.dev.wine.music.app.domain.repository

import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.NewTrack
import com.dev.wine.music.app.domain.model.Track

/** Contract the domain needs. The data layer decides how to fulfil it. */
interface AlbumRepository {
    suspend fun addTrack(albumId: Int, newTrack: NewTrack): Track
    suspend fun getAlbums(): List<Album>
    suspend fun getAlbum(albumId: Int): Album
}
