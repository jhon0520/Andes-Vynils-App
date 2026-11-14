package com.dev.wine.music.app.data.mapper

import com.dev.wine.music.app.data.remote.dto.PerformerDto
import com.dev.wine.music.app.data.remote.dto.PerformerPrizeDto
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.ArtistDetail
import com.dev.wine.music.app.domain.model.ArtistPrize
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.util.fastMap

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
    memberIds = musicians.fastMap { it.id },
)

/** The musician or band endpoint does not say which band a musician is in; the use case adds it. */
fun PerformerDto.toDetail(type: ArtistType): ArtistDetail = ArtistDetail(
    artist = toDomain(type),
    date = (if (type == ArtistType.BAND) creationDate else birthDate).orEmpty(),
    members = musicians.fastMap { it.toDomain(ArtistType.MUSICIAN) },
    albums = albums.fastMap { it.toDomain() },
    prizes = performerPrizes.fastMap { it.toDomain() },
)

fun PerformerPrizeDto.toDomain(): ArtistPrize = ArtistPrize(
    id = id,
    premiationDate = premiationDate,
    name = prize?.name.orEmpty(),
    organization = prize?.organization.orEmpty(),
)
