package com.dev.wine.music.app.ui.common

import com.dev.wine.music.app.domain.model.AppError

/** The three states every screen that loads data can be in. */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(
        val message: String? = null,
        val type: ErrorType = ErrorType.UNKNOWN,
    ) : UiState<Nothing>
}

enum class ErrorType { NETWORK, NOT_FOUND, VALIDATION, SERVER, UNKNOWN }

/** Turns any exception into an error state the screen knows how to draw. */
fun Throwable.toUiError(): UiState.Error = UiState.Error(
    message = message,
    type = when (this) {
        is AppError.Network -> ErrorType.NETWORK
        is AppError.NotFound -> ErrorType.NOT_FOUND
        is AppError.Validation -> ErrorType.VALIDATION
        is AppError.Server -> ErrorType.SERVER
        else -> ErrorType.UNKNOWN
    },
)
