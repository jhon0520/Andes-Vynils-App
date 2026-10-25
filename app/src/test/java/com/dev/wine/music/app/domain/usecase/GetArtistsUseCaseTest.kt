package com.dev.wine.music.app.domain.usecase

import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.fakes.FakeArtistRepository
import com.dev.wine.music.app.fakes.TestData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
}
