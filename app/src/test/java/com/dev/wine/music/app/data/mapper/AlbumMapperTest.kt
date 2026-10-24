package com.dev.wine.music.app.data.mapper

import com.dev.wine.music.app.data.remote.dto.AlbumDto
import com.dev.wine.music.app.data.remote.dto.PerformerDto
import com.dev.wine.music.app.data.remote.dto.TrackDto
import com.dev.wine.music.app.domain.model.ArtistType
import org.junit.Assert.assertEquals
import org.junit.Test

class AlbumMapperTest {

    @Test
    fun `maps album fields and tracks`() {
        val dto = AlbumDto(
            id = 100,
            name = "Buscando América",
            cover = "cover.jpg",
            releaseDate = "1984-08-01T00:00:00.000Z",
            genre = "Salsa",
            recordLabel = "Elektra",
            tracks = listOf(TrackDto(id = 1, name = "Decisiones", duration = "5:05")),
        )

        val album = dto.toDomain()

        assertEquals(100, album.id)
        assertEquals("Buscando América", album.name)
        assertEquals("1984", album.releaseYear)
        assertEquals("Decisiones", album.tracks.single().name)
    }

    @Test
    fun `infers performer type from the dates`() {
        val dto = AlbumDto(
            id = 1,
            name = "Album",
            performers = listOf(
                PerformerDto(id = 1, name = "Rubén Blades", birthDate = "1948-07-16"),
                PerformerDto(id = 2, name = "Queen", creationDate = "1970-01-01"),
            ),
        )

        val types = dto.toDomain().performers.map { it.type }

        assertEquals(listOf(ArtistType.MUSICIAN, ArtistType.BAND), types)
    }
}
