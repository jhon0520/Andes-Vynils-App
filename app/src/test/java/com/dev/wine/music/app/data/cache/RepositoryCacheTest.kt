package com.dev.wine.music.app.data.cache

import com.dev.wine.music.app.fakes.TestData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class RepositoryCacheTest {

    @Test
    fun `album details are bounded and drop the least recently used first`() {
        val cache = RepositoryCache()

        for (id in 1..RepositoryCache.MAX_ALBUM_DETAILS + 1) {
            cache.albumDetails.put(id, TestData.album(id = id))
        }

        assertNull(cache.albumDetails[1])
        assertNotNull(cache.albumDetails[RepositoryCache.MAX_ALBUM_DETAILS + 1])
        assertEquals(RepositoryCache.MAX_ALBUM_DETAILS, cache.albumDetails.size())
    }

    @Test
    fun `clear empties every list and every detail`() {
        val cache = RepositoryCache()
        cache.albums = listOf(TestData.album(id = 1))
        cache.albumDetails.put(1, TestData.album(id = 1))
        cache.artists = listOf(TestData.artist(id = 1, name = "Queen"))
        cache.collectors = listOf(TestData.collector(id = 1, name = "Manolo Bellon"))

        cache.clear()

        assertNull(cache.albums)
        assertNull(cache.albumDetails[1])
        assertNull(cache.artists)
        assertNull(cache.collectors)
    }

    @Test
    fun `invalidating albums keeps artists and collectors`() {
        val cache = RepositoryCache()
        cache.albums = listOf(TestData.album(id = 1))
        cache.collectors = listOf(TestData.collector(id = 1, name = "Manolo Bellon"))

        cache.invalidateAlbums()

        assertNull(cache.albums)
        assertNotNull(cache.collectors)
    }
}
