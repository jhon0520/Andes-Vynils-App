package com.dev.wine.music.app.data.remote

import com.dev.wine.music.app.domain.model.AppError
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException

@Serializable
private data class ApiErrorDto(val statusCode: Int = 0, val message: String = "")

private val errorJson = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

/** Runs a Retrofit call and turns its failures into [AppError], so ui never sees Retrofit types. */
suspend fun <T> safeApiCall(block: suspend () -> T): T = try {
    block()
} catch (e: CancellationException) {
    throw e
} catch (e: HttpException) {
    val message = e.backendMessage()
    throw when (e.code()) {
        404 -> AppError.NotFound(message)
        400, 412 -> AppError.Validation(message)
        else -> AppError.Server(message)
    }
} catch (e: IOException) {
    throw AppError.Network()
} catch (e: SerializationException) {
    throw AppError.Server(e.message)
}

private fun HttpException.backendMessage(): String? {
    val body = response()?.errorBody()?.string() ?: return message()
    return runCatching { errorJson.decodeFromString<ApiErrorDto>(body).message }
        .getOrNull()
        ?.takeIf { it.isNotBlank() }
        ?: message()
}
