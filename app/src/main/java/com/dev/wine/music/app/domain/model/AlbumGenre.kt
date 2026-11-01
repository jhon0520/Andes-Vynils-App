package com.dev.wine.music.app.domain.model

/** Genres accepted by the backend; [apiValue] is the exact text it stores and validates. */
enum class AlbumGenre(val apiValue: String) {
    SALSA("Salsa"),
    ROCK("Rock"),
    CLASSICAL("Classical"),
    FOLK("Folk"),
    ;

    companion object {
        fun fromApiValue(value: String): AlbumGenre? = entries.firstOrNull { it.apiValue == value }
    }
}
