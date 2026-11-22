package com.dev.wine.music.app.data.repository

import com.dev.wine.music.app.data.cache.RepositoryCache
import com.dev.wine.music.app.data.remote.api.AlbumApi
import com.dev.wine.music.app.data.remote.dto.AlbumDto
import com.dev.wine.music.app.data.remote.dto.AlbumRequestDto
import com.dev.wine.music.app.data.remote.dto.TrackDto
import com.dev.wine.music.app.data.remote.dto.TrackRequestDto
import com.dev.wine.music.app.domain.model.AlbumGenre
import com.dev.wine.music.app.domain.model.AppError
import com.dev.wine.music.app.domain.model.NewAlbum
import com.dev.wine.music.app.domain.model.NewTrack
import com.dev.wine.music.app.domain.model.RecordLabel
import com.dev.wine.music.app.fakes.RecordingDispatcher
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

class AlbumRepositoryImplTest {

    /** Answers with [albums], or throws [error] when it is set. */
    private class FakeAlbumApi(
        private val albums: List<AlbumDto> = emptyList(),
        var error: Exception? = null,
    ) : AlbumApi {
        /** How many requests reached the "network". */
        var calls = 0
            private set

        override suspend fun getAlbums(): List<AlbumDto> {
            calls++
            return error?.let { throw it } ?: albums
        }

        override suspend fun getAlbum(albumId: Int): AlbumDto {
            calls++
            return error?.let { throw it } ?: albums.first { it.id == albumId }
        }

        var createdBody: AlbumRequestDto? = null
            private set

        override suspend fun createAlbum(body: AlbumRequestDto): AlbumDto {
            calls++
            error?.let { throw it }
            createdBody = body
            return AlbumDto(id = 104, name = body.name)
        }

        var trackBody: TrackRequestDto? = null
            private set

        override suspend fun addTrack(albumId: Int, body: TrackRequestDto): TrackDto {
            calls++
            error?.let { throw it }
            trackBody = body
            return TrackDto(id = 500, name = body.name, duration = body.duration)
        }
    }

    private fun httpError(code: Int) = HttpException(
        Response.error<Any>(code, """{"statusCode":$code,"message":"Error $code"}""".toResponseBody("application/json".toMediaType())),
    )

    private suspend fun failureOf(block: suspend () -> Unit): Throwable? = runCatching { block() }.exceptionOrNull()

    @Test
    fun `maps the albums from the api`() = runTest {
        val repository = AlbumRepositoryImpl(FakeAlbumApi(albums = listOf(AlbumDto(id = 100, name = "Buscando América"))), RepositoryCache(), testDispatcher)

        assertEquals("Buscando América", repository.getAlbums().single().name)
        assertEquals(100, repository.getAlbum(100).id)
    }

    @Test
    fun `the network call and the mapping run on the io dispatcher`() = runTest {
        val io = RecordingDispatcher()
        val repository = AlbumRepositoryImpl(FakeAlbumApi(albums = listOf(AlbumDto(id = 100, name = "Buscando América"))), RepositoryCache(), io)

        repository.getAlbums()

        assertTrue(io.dispatchCount > 0)
    }

    @Test
    fun `no connection while loading the catalog becomes Network`() = runTest {
        val repository = AlbumRepositoryImpl(FakeAlbumApi(error = IOException("timeout")), RepositoryCache(), testDispatcher)

        assertTrue(failureOf { repository.getAlbums() } is AppError.Network)
    }

    @Test
    fun `a missing album becomes NotFound`() = runTest {
        val repository = AlbumRepositoryImpl(FakeAlbumApi(error = httpError(404)), RepositoryCache(), testDispatcher)

        assertTrue(failureOf { repository.getAlbum(999) } is AppError.NotFound)
    }

    @Test
    fun `a server error becomes Server`() = runTest {
        val repository = AlbumRepositoryImpl(FakeAlbumApi(error = httpError(500)), RepositoryCache(), testDispatcher)

        assertTrue(failureOf { repository.getAlbums() } is AppError.Server)
    }

    private val buscandoAmerica = AlbumDto(id = 100, name = "Buscando América")

    @Test
    fun `the second time the catalog comes from the cache without calling the api`() = runTest {
        val api = FakeAlbumApi(albums = listOf(buscandoAmerica))
        val repository = AlbumRepositoryImpl(api, RepositoryCache(), testDispatcher)

        repository.getAlbums()
        val again = repository.getAlbums()

        assertEquals("Buscando América", again.single().name)
        assertEquals(1, api.calls)
    }

    @Test
    fun `an album already opened comes from the cache`() = runTest {
        val api = FakeAlbumApi(albums = listOf(buscandoAmerica))
        val repository = AlbumRepositoryImpl(api, RepositoryCache(), testDispatcher)

        repository.getAlbum(100)
        repository.getAlbum(100)

        assertEquals(1, api.calls)
    }

    @Test
    fun `a failed load is not cached so retrying calls the api again`() = runTest {
        val api = FakeAlbumApi(albums = listOf(buscandoAmerica), error = IOException("timeout"))
        val repository = AlbumRepositoryImpl(api, RepositoryCache(), testDispatcher)
        failureOf { repository.getAlbums() }

        api.error = null
        val albums = repository.getAlbums()

        assertEquals(1, albums.size)
        assertEquals(2, api.calls)
    }

    @Test
    fun `after the cache is cleared the catalog is fetched again`() = runTest {
        val api = FakeAlbumApi(albums = listOf(buscandoAmerica))
        val cache = RepositoryCache()
        val repository = AlbumRepositoryImpl(api, cache, testDispatcher)
        repository.getAlbums()

        cache.clear()
        repository.getAlbums()

        assertEquals(2, api.calls)
    }

    private val newAlbum = NewAlbum(
        name = "  Siembra ",
        cover = "https://example.com/siembra.jpg",
        releaseDate = "1978-08-01T00:00:00-05:00",
        description = "Salsa",
        genre = AlbumGenre.CLASSICAL,
        recordLabel = RecordLabel.FANIA_RECORDS,
    )

    @Test
    fun `creating an album sends the exact values the backend validates`() = runTest {
        val api = FakeAlbumApi()
        val repository = AlbumRepositoryImpl(api, RepositoryCache(), testDispatcher)

        val created = repository.createAlbum(newAlbum)

        assertEquals(104, created.id)
        assertEquals("Siembra", api.createdBody?.name)
        assertEquals("Classical", api.createdBody?.genre)
        assertEquals("Fania Records", api.createdBody?.recordLabel)
    }

    @Test
    fun `creating an album drops the cached catalog so the next load asks the api`() = runTest {
        val api = FakeAlbumApi(albums = listOf(AlbumDto(id = 100, name = "Buscando América")))
        val repository = AlbumRepositoryImpl(api, RepositoryCache(), testDispatcher)
        repository.getAlbums()

        repository.createAlbum(newAlbum)
        repository.getAlbums()

        assertEquals(3, api.calls)
    }

    @Test
    fun `a rejected album keeps the cache and reports a validation error`() = runTest {
        val api = FakeAlbumApi(albums = listOf(AlbumDto(id = 100, name = "Buscando América")))
        val repository = AlbumRepositoryImpl(api, RepositoryCache(), testDispatcher)
        repository.getAlbums()
        api.error = httpError(400)

        assertTrue(failureOf { repository.createAlbum(newAlbum) } is AppError.Validation)

        api.error = null
        repository.getAlbums()
        assertEquals(2, api.calls)
    }

    @Test
    fun `adding a track sends the trimmed name and the duration`() = runTest {
        val api = FakeAlbumApi()
        val repository = AlbumRepositoryImpl(api, RepositoryCache(), testDispatcher)

        val track = repository.addTrack(100, NewTrack(" Decisiones ", "05:05"))

        assertEquals(500, track.id)
        assertEquals("Decisiones", api.trackBody?.name)
        assertEquals("05:05", api.trackBody?.duration)
    }

    @Test
    fun `adding a track drops the cached album so the detail shows it`() = runTest {
        val api = FakeAlbumApi(albums = listOf(AlbumDto(id = 100, name = "Buscando América")))
        val repository = AlbumRepositoryImpl(api, RepositoryCache(), testDispatcher)
        repository.getAlbum(100)

        repository.addTrack(100, NewTrack("Decisiones", "05:05"))
        repository.getAlbum(100)

        assertEquals(3, api.calls)
    }

    @Test
    fun `a rejected track keeps the cached album and reports the error`() = runTest {
        val api = FakeAlbumApi(albums = listOf(AlbumDto(id = 100, name = "Buscando América")))
        val repository = AlbumRepositoryImpl(api, RepositoryCache(), testDispatcher)
        repository.getAlbum(100)
        api.error = httpError(404)

        assertTrue(failureOf { repository.addTrack(100, NewTrack("Decisiones", "05:05")) } is AppError.NotFound)

        api.error = null
        repository.getAlbum(100)
        assertEquals(2, api.calls)
    }
}
