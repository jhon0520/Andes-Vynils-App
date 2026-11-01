package com.dev.wine.music.app.domain.model

/** Errors the app knows how to handle. The data layer throws them; ui decides what to show. */
sealed class AppError(message: String? = null) : Exception(message) {
    class Network : AppError()
    class NotFound(message: String? = null) : AppError(message)
    class Validation(message: String? = null) : AppError(message)
    class Server(message: String? = null) : AppError(message)
}
