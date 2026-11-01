package com.dev.wine.music.app.data.remote

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

class SafeApiCallTest {

    private fun httpError(code: Int, body: String) = HttpException(
        Response.error<Any>(code, body.toResponseBody("application/json".toMediaType())),
    )

    private suspend fun failureOf(error: Exception): Throwable? =
        runCatching { safeApiCall<Unit> { throw error } }.exceptionOrNull()

    @Test
    fun `404 becomes NotFound with the backend message`() = runTest {
        val error = failureOf(
            httpError(404, """{"statusCode":404,"message":"The album with the given id was not found"}"""),
        )

        assertTrue(error is AppError.NotFound)
        assertEquals("The album with the given id was not found", error?.message)
    }

    @Test
    fun `400 becomes Validation`() = runTest {
        val error = failureOf(httpError(400, """{"statusCode":400,"message":"\"genre\" must be one of [Classical, Salsa, Rock, Folk]"}"""))

        assertTrue(error is AppError.Validation)
    }

    @Test
    fun `500 becomes Server`() = runTest {
        assertTrue(failureOf(httpError(500, "{}")) is AppError.Server)
    }

    @Test
    fun `no connection becomes Network`() = runTest {
        assertTrue(failureOf(IOException("timeout")) is AppError.Network)
    }

    @Test
    fun `a successful call returns its value`() = runTest {
        assertEquals(42, safeApiCall { 42 })
    }
}
