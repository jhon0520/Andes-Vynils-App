package com.dev.wine.music.app.data.cache

import androidx.collection.LruCache
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.Collector
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory copies of what the backend already sent, shared by the repositories so going back
 * to a screen does not repeat the request, the JSON parsing and the mapping.
 *
 * - The lists (catalog, artists, collectors) are small and are kept whole.
 * - Album details go in an [LruCache] with at most [MAX_ALBUM_DETAILS] entries: browsing many
 *   albums can not grow memory without limit, the least recently opened one is dropped first.
 *
 * Only successful answers are stored. [clear] empties everything; the app calls it from
 * onTrimMemory when the system is short of memory. Features that write data (create album,
 * add tracks, comment) must call the matching invalidate function after a successful write.
 */
@Singleton
class RepositoryCache @Inject constructor() {

    @Volatile
    var albums: List<Album>? = null

    val albumDetails = LruCache<Int, Album>(MAX_ALBUM_DETAILS)

    @Volatile
    var artists: List<Artist>? = null

    @Volatile
    var collectors: List<Collector>? = null

    /** After creating or changing an album, so the catalog and its detail are fetched again. */
    fun invalidateAlbums() {
        albums = null
        albumDetails.evictAll()
    }

    fun clear() {
        invalidateAlbums()
        artists = null
        collectors = null
    }

    companion object {
        const val MAX_ALBUM_DETAILS = 20
    }
}
