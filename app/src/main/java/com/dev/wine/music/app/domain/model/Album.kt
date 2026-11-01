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

    /**
     * Release date as dd/mm/yyyy, e.g. "01/08/1984". The backend sends midnight in Colombia
     * as 05:00 UTC, so the date part of the ISO text is already the right day.
     */
    val releaseDay: String
        get() {
            val parts = releaseDate.take(10).split("-")
            return if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else releaseDate
        }

    /** Null when the backend sends a genre outside [AlbumGenre]. */
    val genreType: AlbumGenre? get() = AlbumGenre.fromApiValue(genre)

    /** Performer names as shown on the catalog card, e.g. "Rubén Blades, Queen". */
    val performerNames: String get() = performers.joinToString(", ") { it.name }

    /**
     * Tracks numbered by vinyl side: the first half (rounded up) on side A, the rest on side B,
     * e.g. five tracks become A1, A2, A3, B1, B2.
     */
    val numberedTracks: List<NumberedTrack>
        get() {
            val sideA = (tracks.size + 1) / 2
            return tracks.mapIndexed { index, track ->
                val position = if (index < sideA) "A${index + 1}" else "B${index - sideA + 1}"
                NumberedTrack(position = position, track = track)
            }
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
