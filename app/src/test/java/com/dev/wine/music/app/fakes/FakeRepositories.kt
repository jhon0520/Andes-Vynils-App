package com.dev.wine.music.app.fakes

import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.domain.model.UserSession
import com.dev.wine.music.app.domain.repository.AlbumRepository
import com.dev.wine.music.app.domain.repository.ArtistRepository
import com.dev.wine.music.app.domain.repository.CollectorRepository
import com.dev.wine.music.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException

class FakeAlbumRepository(
    var albums: List<Album> = emptyList(),
    var shouldFail: Boolean = false,
    /** Error to throw when shouldFail is true. */
    var error: Exception = IOException("Network error"),
) : AlbumRepository {

    override suspend fun getAlbums(): List<Album> {
        if (shouldFail) throw error
        return albums
    }

    override suspend fun getAlbum(albumId: Int): Album {
        if (shouldFail) throw error
        return albums.first { it.id == albumId }
    }
}

class FakeArtistRepository(
    var artists: List<Artist> = emptyList(),
    var shouldFail: Boolean = false,
    /** Error to throw when shouldFail is true. */
    var error: Exception = IOException("Network error"),
) : ArtistRepository {

    override suspend fun getArtists(): List<Artist> {
        if (shouldFail) throw error
        return artists
    }
}

class FakeCollectorRepository(
    var collectors: List<Collector> = emptyList(),
    var shouldFail: Boolean = false,
    /** Error to throw when shouldFail is true. */
    var error: Exception = IOException("Network error"),
) : CollectorRepository {

    override suspend fun getCollectors(): List<Collector> {
        if (shouldFail) throw error
        return collectors
    }
}

/** Same behaviour as the in-memory repository, kept here so tests do not depend on the data layer. */
class FakeSessionRepository : SessionRepository {
    private val _session = MutableStateFlow<UserSession?>(null)
    override val session: StateFlow<UserSession?> = _session

    override fun selectVisitor() {
        _session.value = UserSession.Visitor
    }

    override fun selectCollector(collector: Collector) {
        _session.value = UserSession.Collector(collector.id, collector.name, collector.email)
    }

    override fun clear() {
        _session.value = null
    }
}
