package com.dev.wine.music.app.data.mapper

import com.dev.wine.music.app.data.remote.dto.PerformerDto
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.ArtistType

/**
 * When the endpoint already tells us the type (musicians vs bands) pass it in.
 * Otherwise (performers inside an album) it is inferred from the dates.
 */
fun PerformerDto.toDomain(type: ArtistType? = null): Artist = Artist(
    id = id,
    name = name,
    image = image,
    description = description,
    type = type ?: if (creationDate != null) ArtistType.BAND else ArtistType.MUSICIAN,
)
