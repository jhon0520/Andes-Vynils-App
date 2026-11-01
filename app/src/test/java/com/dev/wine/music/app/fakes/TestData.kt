package com.dev.wine.music.app.fakes

import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.domain.model.Comment
import com.dev.wine.music.app.domain.model.Track

/** Builders with sensible defaults, so each test only sets what it cares about. */
object TestData {

    fun album(
        id: Int = 1,
        name: String = "Buscando América",
        genre: String = "Salsa",
        releaseDate: String = "1984-08-01T00:00:00.000Z",
        tracks: List<Track> = emptyList(),
        performers: List<Artist> = emptyList(),
        comments: List<Comment> = emptyList(),
    ) = Album(
        id = id,
        name = name,
        cover = "https://example.com/cover.jpg",
        releaseDate = releaseDate,
        description = "Description",
        genre = genre,
        recordLabel = "Elektra",
        tracks = tracks,
        performers = performers,
        comments = comments,
    )

    fun artist(
        id: Int = 1,
        name: String = "Rubén Blades",
        type: ArtistType = ArtistType.MUSICIAN,
    ) = Artist(id = id, name = name, image = "", description = "", type = type)

    fun collector(
        id: Int = 1,
        name: String = "Manolo Bellon",
        commentIds: List<Int> = emptyList(),
    ) = Collector(
        id = id,
        name = name,
        telephone = "3502457896",
        email = "manollo@caracol.com.co",
        commentIds = commentIds,
    )

    fun track(id: Int = 1, name: String = "Decisiones", duration: String = "5:05") =
        Track(id = id, name = name, duration = duration)

    fun comment(id: Int = 1, description: String = "The most relevant album of Ruben Blades", rating: Int = 5) =
        Comment(id = id, description = description, rating = rating)
}
