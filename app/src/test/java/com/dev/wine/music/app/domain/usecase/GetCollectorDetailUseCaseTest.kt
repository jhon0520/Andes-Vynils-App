package com.dev.wine.music.app.domain.usecase

import com.dev.wine.music.app.domain.model.AppError
import com.dev.wine.music.app.fakes.FakeAlbumRepository
import com.dev.wine.music.app.fakes.FakeCollectorRepository
import com.dev.wine.music.app.fakes.TestData
import com.dev.wine.music.app.fakes.testDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GetCollectorDetailUseCaseTest {

    private val buscando = TestData.album(id = 100, name = "Buscando América", comments = listOf(TestData.comment(id = 7)))
    private val anotherAlbum = TestData.album(id = 101, name = "A Night at the Opera")

    private val collectorRepository = FakeCollectorRepository().apply {
        details = listOf(
            TestData.collectorDetail(
                collector = TestData.collector(id = 1),
                favoriteArtists = listOf(TestData.artist(id = 2, name = "queen"), TestData.artist(id = 1, name = "Rubén Blades")),
                reviews = listOf(TestData.collectorReview(id = 7), TestData.collectorReview(id = 8)),
            ),
        )
        collections = mapOf(
            1 to listOf(TestData.collectorAlbum(id = 1, album = buscando), TestData.collectorAlbum(id = 2, album = anotherAlbum)),
        )
    }
    private val albumRepository = FakeAlbumRepository(albums = listOf(buscando, anotherAlbum))

    private val useCase = GetCollectorDetailUseCase(collectorRepository, albumRepository, testDispatcher)

    @Test
    fun `joins the collector with its collection`() = runTest {
        val detail = useCase(1)

        assertEquals(1, detail.collector.id)
        assertEquals(listOf(101, 100), detail.albums.map { it.album.id })
    }

    @Test
    fun `favorites and collection are sorted by name ignoring case`() = runTest {
        val detail = useCase(1)

        assertEquals(listOf("queen", "Rubén Blades"), detail.favoriteArtists.map { it.name })
        assertEquals(listOf("A Night at the Opera", "Buscando América"), detail.albums.map { it.album.name })
    }

    @Test
    fun `each review gets the album whose comments include it`() = runTest {
        val reviews = useCase(1).reviews

        assertEquals("Buscando América", reviews.first { it.id == 7 }.album?.name)
        assertNull(reviews.first { it.id == 8 }.album)
    }

    @Test
    fun `if the catalog fails the collector still shows without the albums of the reviews`() = runTest {
        albumRepository.shouldFail = true

        val detail = useCase(1)

        assertEquals(2, detail.reviews.size)
        assertTrue(detail.reviews.all { it.album == null })
    }

    @Test
    fun `a missing collector propagates NotFound`() = runTest {
        collectorRepository.shouldFail = true
        collectorRepository.error = AppError.NotFound("The collector with the given id was not found")

        assertTrue(runCatching { useCase(1) }.exceptionOrNull() is AppError.NotFound)
    }
}
