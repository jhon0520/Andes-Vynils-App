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
    val comments: List<Comment> = emptyList(),
) {
    /** Release year, e.g. "1984" from "1984-08-01T00:00:00.000Z". */
    val releaseYear: String get() = releaseDate.take(4)

    /** Release date as dd/mm/yyyy, e.g. "01/08/1984". */
    val releaseDay: String get() = isoDateToDay(releaseDate)

    /** Null when the backend sends a genre outside [AlbumGenre]. */
    val genreType: AlbumGenre? get() = AlbumGenre.fromApiValue(genre)

    /**
     * Performer names as shown on the catalog card, e.g. "Rubén Blades, Queen".
     * Built once per album (lazy) instead of on every recomposition of the card.
     */
    val performerNames: String by lazy(LazyThreadSafetyMode.PUBLICATION) {
        performers.joinToString(", ") { it.name }
    }

    /**
     * Tracks numbered by vinyl side: the first half (rounded up) on side A, the rest on side B,
     * e.g. five tracks become A1, A2, A3, B1, B2.
     * Built once per album (lazy, indexed loop) instead of on every recomposition of the detail.
     */
    val numberedTracks: List<NumberedTrack> by lazy(LazyThreadSafetyMode.PUBLICATION) {
        val sideA = (tracks.size + 1) / 2
        val numbered = ArrayList<NumberedTrack>(tracks.size)
        for (index in tracks.indices) {
            val position = if (index < sideA) "A${index + 1}" else "B${index - sideA + 1}"
            numbered.add(NumberedTrack(position = position, track = tracks[index]))
        }
        numbered
    }

    /** Where a new track lands once the sides are split again with one more track. */
    val nextTrackPosition: String
        get() {
            val index = tracks.size
            val sideA = (index + 2) / 2
            return if (index < sideA) "A${index + 1}" else "B${index - sideA + 1}"
        }
}

data class Track(
    val id: Int,
    val name: String,
    val duration: String,
)

data class NumberedTrack(
    val position: String,
    val track: Track,
)

/** A review of an album. [author] is null when no collector claims the comment. */
data class Comment(
    val id: Int,
    val description: String,
    val rating: Int,
    val author: String? = null,
) {
    companion object {
        const val MAX_RATING = 5
    }
}
