package com.dev.wine.music.app.domain.usecase

import com.dev.wine.music.app.fakes.FakeAlbumRepository
import com.dev.wine.music.app.fakes.FakeCollectorRepository
import com.dev.wine.music.app.fakes.TestData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetAlbumDetailUseCaseTest {

    private val albumRepository = FakeAlbumRepository(
        albums = listOf(
            TestData.album(
                id = 100,
                comments = listOf(TestData.comment(id = 100), TestData.comment(id = 101)),
            ),
        ),
    )

    private val collectorRepository = FakeCollectorRepository(
        collectors = listOf(
            TestData.collector(id = 100, name = "Manolo Bellon", commentIds = listOf(100)),
            TestData.collector(id = 101, name = "Jaime Monsalve", commentIds = emptyList()),
        ),
    )

    private val useCase = GetAlbumDetailUseCase(albumRepository, collectorRepository)

    @Test
    fun `fills each review author from the collector who wrote it`() = runTest {
        val album = useCase(albumId = 100)

        assertEquals("Manolo Bellon", album.comments.first { it.id == 100 }.author)
    }

    @Test
    fun `a review no collector claims stays without author`() = runTest {
        val album = useCase(albumId = 100)

        assertNull(album.comments.first { it.id == 101 }.author)
    }

    @Test
    fun `if collectors fail the album still loads with anonymous reviews`() = runTest {
        collectorRepository.shouldFail = true

        val album = useCase(albumId = 100)

        assertEquals(100, album.id)
        assertEquals(listOf(null, null), album.comments.map { it.author })
    }

    @Test(expected = java.io.IOException::class)
    fun `if the album fails the error reaches the caller`() = runTest {
        albumRepository.shouldFail = true

        useCase(albumId = 100)
    }
}
