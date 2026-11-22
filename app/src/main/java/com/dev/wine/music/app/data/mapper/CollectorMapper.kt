package com.dev.wine.music.app.data.mapper

import com.dev.wine.music.app.data.remote.dto.CollectorAlbumDto
import com.dev.wine.music.app.data.remote.dto.CollectorDto
import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.domain.model.CollectorAlbum
import com.dev.wine.music.app.domain.model.CollectorAlbumStatus
import com.dev.wine.music.app.domain.model.CollectorDetail
import com.dev.wine.music.app.domain.model.CollectorReview
import com.dev.wine.music.app.util.fastMap

fun CollectorDto.toDomain(): Collector = Collector(
    id = id,
    name = name,
    telephone = telephone,
    email = email,
    commentIds = comments.fastMap { it.id },
)

/**
 * The collector with favorites and reviews. The collection comes from another endpoint and the
 * album of each review is unknown here: the use case fills both.
 */
fun CollectorDto.toDetail(): CollectorDetail = CollectorDetail(
    collector = toDomain(),
    favoriteArtists = favoritePerformers.fastMap { it.toDomain() },
    reviews = comments.fastMap { CollectorReview(id = it.id, description = it.description, rating = it.rating) },
)

/** Null when the backend sends the entry without its album: there is nothing to show or open. */
fun CollectorAlbumDto.toDomain(): CollectorAlbum? = album?.let {
    CollectorAlbum(
        id = id,
        price = price,
        status = if (status.equals(ACTIVE, ignoreCase = true)) CollectorAlbumStatus.ACTIVE else CollectorAlbumStatus.INACTIVE,
        album = it.toDomain(),
    )
}

private const val ACTIVE = "Active"
