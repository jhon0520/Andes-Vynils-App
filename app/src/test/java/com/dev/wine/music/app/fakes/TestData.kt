package com.dev.wine.music.app.fakes

import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.domain.model.Track

/** Builders with sensible defaults, so each test only sets what it cares about. */
object TestData {

    fun album(
        id: Int = 1,
        name: String = "Buscando América",
        genre: String = "Salsa",
        releaseDate: String = "1984-08-01T00:00:00.000Z",
        tracks: List<Track> = emptyList(),
    ) = Album(
        id = id,
        name = name,
        cover = "https://example.com/cover.jpg",
        releaseDate = releaseDate,
        description = "Description",
        genre = genre,
        recordLabel = "Elektra",
        tracks = tracks,
    )

    fun artist(
        id: Int = 1,
        name: String = "Rubén Blades",
        type: ArtistType = ArtistType.MUSICIAN,
    ) = Artist(id = id, name = name, image = "", description = "", type = type)

    fun collector(
        id: Int = 1,
        name: String = "Manolo Bellon",
    ) = Collector(id = id, name = name, telephone = "3502457896", email = "manollo@caracol.com.co")
}
