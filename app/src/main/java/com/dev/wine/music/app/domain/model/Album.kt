package com.dev.wine.music.app.domain.model

data class Album(
    val id: Int,
    val name: String,
    val cover: String,
    val releaseDate: String,
    val description: String,
    val genre: String,
    val recordLabel: String,
    val tracks: List<Track> = emptyList(),
    val performers: List<Artist> = emptyList(),
) {
    /** Release year, e.g. "1984" from "1984-08-01T00:00:00.000Z". */
    val releaseYear: String get() = releaseDate.take(4)
}

data class Track(
    val id: Int,
    val name: String,
    val duration: String,
)
