package com.dev.wine.music.app.domain.model

/** Everything the collector detail shows (HU06). */
data class CollectorDetail(
    val collector: Collector,
    val favoriteArtists: List<Artist> = emptyList(),
    val albums: List<CollectorAlbum> = emptyList(),
    val reviews: List<CollectorReview> = emptyList(),
)

/** An album of the collection. The backend sends the price without currency. */
data class CollectorAlbum(
    val id: Int,
    val price: Int,
    val status: CollectorAlbumStatus,
    val album: Album,
)

enum class CollectorAlbumStatus { ACTIVE, INACTIVE }

/** A review the collector wrote. [album] is null until the use case finds which album it belongs to. */
data class CollectorReview(
    val id: Int,
    val description: String,
    val rating: Int,
    val album: Album? = null,
)
