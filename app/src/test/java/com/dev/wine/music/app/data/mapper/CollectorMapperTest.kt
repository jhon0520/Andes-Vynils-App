package com.dev.wine.music.app.data.mapper

import com.dev.wine.music.app.data.remote.dto.CollectorDto
import com.dev.wine.music.app.data.remote.dto.CommentDto
import org.junit.Assert.assertEquals
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
}
