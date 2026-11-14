package com.dev.wine.music.app.domain.usecase

import com.dev.wine.music.app.fakes.FakeAlbumRepository
import com.dev.wine.music.app.fakes.RecordingDispatcher
import com.dev.wine.music.app.fakes.TestData
import com.dev.wine.music.app.fakes.testDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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

        val result = GetAlbumsUseCase(repository, testDispatcher)()

        assertEquals(listOf(2, 3, 1), result.map { it.id })
    }

    @Test
    fun `sorting runs on the default dispatcher, off the main thread`() = runTest {
        val default = RecordingDispatcher()
        val repository = FakeAlbumRepository(albums = listOf(TestData.album(id = 1, name = "Poeta del pueblo")))

        GetAlbumsUseCase(repository, default)()

        assertTrue(default.dispatchCount > 0)
    }
}
