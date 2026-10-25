package com.dev.wine.music.app.domain.usecase

import com.dev.wine.music.app.fakes.FakeAlbumRepository
import com.dev.wine.music.app.fakes.TestData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetAlbumsUseCaseTest {

    @Test
    fun `returns albums sorted by name ignoring case`() = runTest {
        val repository = FakeAlbumRepository(
            albums = listOf(
                TestData.album(id = 1, name = "Poeta del pueblo"),
                TestData.album(id = 2, name = "a Night at the Opera"),
                TestData.album(id = 3, name = "Buscando América"),
            ),
        )

        val result = GetAlbumsUseCase(repository)()

        assertEquals(listOf(2, 3, 1), result.map { it.id })
    }
}
