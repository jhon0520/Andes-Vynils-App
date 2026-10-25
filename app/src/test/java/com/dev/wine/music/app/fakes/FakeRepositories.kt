package com.dev.wine.music.app.fakes

import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.domain.repository.AlbumRepository
import com.dev.wine.music.app.domain.repository.ArtistRepository
import com.dev.wine.music.app.domain.repository.CollectorRepository
import java.io.IOException

class FakeAlbumRepository(
    var albums: List<Album> = emptyList(),
    var shouldFail: Boolean = false,
) : AlbumRepository {

    override suspend fun getAlbums(): List<Album> {
        if (shouldFail) throw IOException("Network error")
        return albums
    }

    override suspend fun getAlbum(albumId: Int): Album {
        if (shouldFail) throw IOException("Network error")
        return albums.first { it.id == albumId }
    }
}

class FakeArtistRepository(
    var artists: List<Artist> = emptyList(),
    var shouldFail: Boolean = false,
) : ArtistRepository {

    override suspend fun getArtists(): List<Artist> {
        if (shouldFail) throw IOException("Network error")
        return artists
    }
}

class FakeCollectorRepository(
    var collectors: List<Collector> = emptyList(),
    var shouldFail: Boolean = false,
) : CollectorRepository {

    override suspend fun getCollectors(): List<Collector> {
        if (shouldFail) throw IOException("Network error")
        return collectors
    }
}
