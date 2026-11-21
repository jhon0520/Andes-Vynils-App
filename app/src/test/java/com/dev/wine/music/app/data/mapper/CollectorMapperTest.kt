package com.dev.wine.music.app.data.mapper

import com.dev.wine.music.app.data.remote.dto.AlbumDto
import com.dev.wine.music.app.data.remote.dto.CollectorAlbumDto
import com.dev.wine.music.app.data.remote.dto.CollectorDto
import com.dev.wine.music.app.data.remote.dto.CommentDto
import com.dev.wine.music.app.data.remote.dto.PerformerDto
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.domain.model.CollectorAlbumStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CollectorMapperTest {

    @Test
    fun `keeps the ids of the comments the collector wrote`() {
        val dto = CollectorDto(
            id = 100,
            name = "Manolo Bellon",
            comments = listOf(CommentDto(id = 100, rating = 5), CommentDto(id = 103, rating = 3)),
        )

        assertEquals(listOf(100, 103), dto.toDomain().commentIds)
    }

    @Test
    fun `favorites tell musicians and bands apart by their dates`() {
        val dto = CollectorDto(
            id = 100,
            name = "Manolo Bellon",
            favoritePerformers = listOf(
                PerformerDto(id = 100, name = "Rubén Blades", birthDate = "1948-07-16T05:00:00.000Z"),
                PerformerDto(id = 101, name = "Queen", creationDate = "1970-01-01T05:00:00.000Z"),
            ),
        )

        assertEquals(listOf(ArtistType.MUSICIAN, ArtistType.BAND), dto.toDetail().favoriteArtists.map { it.type })
    }

    @Test
    fun `reviews keep text and rating and wait for the use case to know their album`() {
        val dto = CollectorDto(id = 100, name = "Manolo Bellon", comments = listOf(CommentDto(id = 7, description = "Great", rating = 4)))

        val review = dto.toDetail().reviews.single()

        assertEquals("Great", review.description)
        assertEquals(4, review.rating)
        assertNull(review.album)
    }

    @Test
    fun `the status of an album in the collection becomes ACTIVE or INACTIVE`() {
        val album = AlbumDto(id = 100, name = "Buscando América")

        assertEquals(CollectorAlbumStatus.ACTIVE, CollectorAlbumDto(1, 35, "Active", album).toDomain()?.status)
        assertEquals(CollectorAlbumStatus.INACTIVE, CollectorAlbumDto(2, 35, "Inactive", album).toDomain()?.status)
        assertNull(CollectorAlbumDto(3, 35, "Active", album = null).toDomain())
    }
}
