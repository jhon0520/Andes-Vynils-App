package com.dev.wine.music.app.data.repository

import com.dev.wine.music.app.data.cache.RepositoryCache
import com.dev.wine.music.app.data.remote.api.CollectorApi
import com.dev.wine.music.app.data.remote.dto.AlbumDto
import com.dev.wine.music.app.data.remote.dto.CollectorAlbumDto
import com.dev.wine.music.app.data.remote.dto.CollectorDto
import com.dev.wine.music.app.data.remote.dto.CommentDto
import com.dev.wine.music.app.data.remote.dto.PerformerDto
import com.dev.wine.music.app.domain.model.AppError
import com.dev.wine.music.app.fakes.testDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class CollectorRepositoryImplTest {

    private class FakeCollectorApi(
        private val collectors: List<CollectorDto> = emptyList(),
        private val collection: List<CollectorAlbumDto> = emptyList(),
        private val error: Exception? = null,
    ) : CollectorApi {
        var calls = 0
            private set

        override suspend fun getCollectors(): List<CollectorDto> {
            calls++
            return error?.let { throw it } ?: collectors
        }

        override suspend fun getCollector(collectorId: Int): CollectorDto {
            calls++
            error?.let { throw it }
            return collectors.first { it.id == collectorId }
        }

        override suspend fun getCollectorAlbums(collectorId: Int): List<CollectorAlbumDto> {
            calls++
            return error?.let { throw it } ?: collection
        }
    }

    private fun httpError(code: Int) = HttpException(
        Response.error<Any>(code, """{"statusCode":$code,"message":"Error $code"}""".toResponseBody("application/json".toMediaType())),
    )

    private val manolo = CollectorDto(
        id = 100,
        name = "Manolo Bellon",
        comments = listOf(CommentDto(id = 100, description = "The most relevant album of Ruben Blades", rating = 5)),
        favoritePerformers = listOf(PerformerDto(id = 100, name = "Rubén Blades", birthDate = "1948-07-16T05:00:00.000Z")),
    )

    @Test
    fun `maps the collectors from the api`() = runTest {
        val repository = CollectorRepositoryImpl(FakeCollectorApi(collectors = listOf(CollectorDto(id = 100, name = "Manolo Bellon"))), RepositoryCache(), testDispatcher)

        assertEquals("Manolo Bellon", repository.getCollectors().single().name)
    }

    @Test
    fun `no connection becomes Network`() = runTest {
        val repository = CollectorRepositoryImpl(FakeCollectorApi(error = IOException("timeout")), RepositoryCache(), testDispatcher)

        assertTrue(runCatching { repository.getCollectors() }.exceptionOrNull() is AppError.Network)
    }

    @Test
    fun `the second time the collectors come from the cache without calling the api`() = runTest {
        val api = FakeCollectorApi(collectors = listOf(CollectorDto(id = 100, name = "Manolo Bellon")))
        val repository = CollectorRepositoryImpl(api, RepositoryCache(), testDispatcher)

        repository.getCollectors()
        repository.getCollectors()

        assertEquals(1, api.calls)
    }

    @Test
    fun `the detail brings the favorites and the reviews of the collector`() = runTest {
        val repository = CollectorRepositoryImpl(FakeCollectorApi(collectors = listOf(manolo)), RepositoryCache(), testDispatcher)

        val detail = repository.getCollector(100)

        assertEquals("Manolo Bellon", detail.collector.name)
        assertEquals("Rubén Blades", detail.favoriteArtists.single().name)
        assertEquals(5, detail.reviews.single().rating)
    }

    @Test
    fun `a missing collector becomes NotFound`() = runTest {
        val repository = CollectorRepositoryImpl(FakeCollectorApi(error = httpError(404)), RepositoryCache(), testDispatcher)

        assertTrue(runCatching { repository.getCollector(999) }.exceptionOrNull() is AppError.NotFound)
    }

    @Test
    fun `the collection skips entries without album`() = runTest {
        val collection = listOf(
            CollectorAlbumDto(id = 1, price = 35, status = "Active", album = AlbumDto(id = 100, name = "Buscando América")),
            CollectorAlbumDto(id = 2, price = 20, status = "Inactive"),
        )
        val repository = CollectorRepositoryImpl(FakeCollectorApi(collection = collection), RepositoryCache(), testDispatcher)

        val albums = repository.getCollectorAlbums(100)

        assertEquals(listOf(100), albums.map { it.album.id })
    }

    @Test
    fun `the detail is not cached so it always shows the latest collection`() = runTest {
        val api = FakeCollectorApi(collectors = listOf(manolo))
        val repository = CollectorRepositoryImpl(api, RepositoryCache(), testDispatcher)

        repository.getCollector(100)
        repository.getCollector(100)

        assertEquals(2, api.calls)
    }
}
