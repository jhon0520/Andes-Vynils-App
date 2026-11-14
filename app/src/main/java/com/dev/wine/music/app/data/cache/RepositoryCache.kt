package com.dev.wine.music.app.data.cache

import androidx.collection.LruCache
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.ArtistDetail
import com.dev.wine.music.app.domain.model.ArtistPrize
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
 * - Artist details (HU04) work the same, with one cache per type because a musician and a band
 *   can share an id. The prize catalog is one small list.
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

    val musicianDetails = LruCache<Int, ArtistDetail>(MAX_ARTIST_DETAILS)

    val bandDetails = LruCache<Int, ArtistDetail>(MAX_ARTIST_DETAILS)

    @Volatile
    var performerPrizes: List<ArtistPrize>? = null

    /** After creating or changing an album, so the catalog and its detail are fetched again. */
    fun invalidateAlbums() {
        albums = null
        albumDetails.evictAll()
    }

    /** After a write that changes artists (HU12 band members, HU14 prizes, HU15 albums of an artist). */
    fun invalidateArtists() {
        artists = null
        musicianDetails.evictAll()
        bandDetails.evictAll()
        performerPrizes = null
    }

    fun clear() {
        invalidateAlbums()
        invalidateArtists()
        collectors = null
    }

    companion object {
        const val MAX_ALBUM_DETAILS = 20
        const val MAX_ARTIST_DETAILS = 20
    }
}
