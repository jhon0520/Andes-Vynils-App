package com.dev.wine.music.app.domain.usecase

import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.fakes.FakeArtistRepository
import com.dev.wine.music.app.fakes.TestData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetArtistsUseCaseTest {

    @Test
    fun `returns musicians and bands together sorted by name`() = runTest {
        val repository = FakeArtistRepository(
            artists = listOf(
                TestData.artist(id = 1, name = "Rubén Blades"),
                TestData.artist(id = 2, name = "Queen", type = ArtistType.BAND),
            ),
        )

        val result = GetArtistsUseCase(repository)()

        assertEquals(listOf("Queen", "Rubén Blades"), result.map { it.name })
    }

    @Test
    fun `each musician gets the band that lists them as a member`() = runTest {
        val repository = FakeArtistRepository(
            artists = listOf(
                TestData.artist(id = 1, name = "Freddie Mercury"),
                TestData.artist(id = 2, name = "Queen", type = ArtistType.BAND, memberIds = listOf(1)),
            ),
        )

        val result = GetArtistsUseCase(repository)()

        assertEquals("Queen", result.first { it.id == 1 }.bandName)
    }

    @Test
    fun `a musician without band has no band name`() = runTest {
        val repository = FakeArtistRepository(
            artists = listOf(
                TestData.artist(id = 1, name = "Rubén Blades"),
                TestData.artist(id = 2, name = "Queen", type = ArtistType.BAND),
            ),
        )

        val result = GetArtistsUseCase(repository)()

        assertNull(result.first { it.id == 1 }.bandName)
        assertNull(result.first { it.id == 2 }.bandName)
    }
}
