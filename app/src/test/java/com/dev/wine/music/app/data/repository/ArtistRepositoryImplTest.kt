package com.dev.wine.music.app.data.repository

import com.dev.wine.music.app.data.remote.api.ArtistApi
import com.dev.wine.music.app.data.remote.dto.PerformerDto
import com.dev.wine.music.app.domain.model.ArtistType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ArtistRepositoryImplTest {

    private class FakeArtistApi : ArtistApi {
        override suspend fun getMusicians() = listOf(PerformerDto(id = 1, name = "Rubén Blades"))
        override suspend fun getBands() = listOf(
            PerformerDto(id = 2, name = "Queen", musicians = listOf(PerformerDto(id = 1, name = "Rubén Blades"))),
        )
    }

    @Test
    fun `merges musicians and bands with their type`() = runTest {
        val repository = ArtistRepositoryImpl(FakeArtistApi())

        val artists = repository.getArtists()

        assertEquals(2, artists.size)
        assertEquals(ArtistType.MUSICIAN, artists.first { it.name == "Rubén Blades" }.type)
        assertEquals(ArtistType.BAND, artists.first { it.name == "Queen" }.type)
    }

    @Test
    fun `keeps the members of each band`() = runTest {
        val repository = ArtistRepositoryImpl(FakeArtistApi())

        val queen = repository.getArtists().first { it.name == "Queen" }

        assertEquals(listOf(1), queen.memberIds)
    }
}
