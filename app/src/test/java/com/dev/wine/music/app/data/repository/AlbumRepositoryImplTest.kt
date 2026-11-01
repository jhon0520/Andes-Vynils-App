package com.dev.wine.music.app.data.repository

import com.dev.wine.music.app.data.remote.api.AlbumApi
import com.dev.wine.music.app.data.remote.dto.AlbumDto
import com.dev.wine.music.app.domain.model.AppError
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
        private val error: Exception? = null,
    ) : AlbumApi {
        override suspend fun getAlbums(): List<AlbumDto> = error?.let { throw it } ?: albums
        override suspend fun getAlbum(albumId: Int): AlbumDto =
            error?.let { throw it } ?: albums.first { it.id == albumId }
    }

    private fun httpError(code: Int) = HttpException(
        Response.error<Any>(code, """{"statusCode":$code,"message":"Error $code"}""".toResponseBody("application/json".toMediaType())),
    )

    private suspend fun failureOf(block: suspend () -> Unit): Throwable? = runCatching { block() }.exceptionOrNull()

    @Test
    fun `maps the albums from the api`() = runTest {
        val repository = AlbumRepositoryImpl(FakeAlbumApi(albums = listOf(AlbumDto(id = 100, name = "Buscando América"))))

        assertEquals("Buscando América", repository.getAlbums().single().name)
        assertEquals(100, repository.getAlbum(100).id)
    }

    @Test
    fun `no connection while loading the catalog becomes Network`() = runTest {
        val repository = AlbumRepositoryImpl(FakeAlbumApi(error = IOException("timeout")))

        assertTrue(failureOf { repository.getAlbums() } is AppError.Network)
    }

    @Test
    fun `a missing album becomes NotFound`() = runTest {
        val repository = AlbumRepositoryImpl(FakeAlbumApi(error = httpError(404)))

        assertTrue(failureOf { repository.getAlbum(999) } is AppError.NotFound)
    }

    @Test
    fun `a server error becomes Server`() = runTest {
        val repository = AlbumRepositoryImpl(FakeAlbumApi(error = httpError(500)))

        assertTrue(failureOf { repository.getAlbums() } is AppError.Server)
    }
}
