package com.dev.wine.music.app.data.repository

import com.dev.wine.music.app.data.cache.RepositoryCache
import com.dev.wine.music.app.data.remote.api.ArtistApi
import com.dev.wine.music.app.data.remote.dto.PerformerDto
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.fakes.testDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ArtistRepositoryImplTest {

    private class FakeArtistApi : ArtistApi {
        var calls = 0
            private set

        override suspend fun getMusicians(): List<PerformerDto> {
            calls++
            return listOf(PerformerDto(id = 1, name = "Rubén Blades"))
        }

        override suspend fun getBands() = listOf(
            PerformerDto(id = 2, name = "Queen", musicians = listOf(PerformerDto(id = 1, name = "Rubén Blades"))),
        )
    }

    @Test
    fun `merges musicians and bands with their type`() = runTest {
        val repository = ArtistRepositoryImpl(FakeArtistApi(), RepositoryCache(), testDispatcher)

        val artists = repository.getArtists()

        assertEquals(2, artists.size)
        assertEquals(ArtistType.MUSICIAN, artists.first { it.name == "Rubén Blades" }.type)
        assertEquals(ArtistType.BAND, artists.first { it.name == "Queen" }.type)
    }

    @Test
    fun `keeps the members of each band`() = runTest {
        val repository = ArtistRepositoryImpl(FakeArtistApi(), RepositoryCache(), testDispatcher)

        val queen = repository.getArtists().first { it.name == "Queen" }

        assertEquals(listOf(1), queen.memberIds)
    }

    @Test
    fun `the second time the artists come from the cache without calling the api`() = runTest {
        val api = FakeArtistApi()
        val repository = ArtistRepositoryImpl(api, RepositoryCache(), testDispatcher)

        repository.getArtists()
        repository.getArtists()

        assertEquals(1, api.calls)
    }
}
