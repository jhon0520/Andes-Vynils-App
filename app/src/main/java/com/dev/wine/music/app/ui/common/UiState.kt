package com.dev.wine.music.app.ui.common

/** The three states every screen that loads data can be in. */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String? = null) : UiState<Nothing>
}
