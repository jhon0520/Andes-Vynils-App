package com.dev.wine.music.app.domain.model

data class Artist(
    val id: Int,
    val name: String,
    val image: String,
    val description: String,
    val type: ArtistType,
    /** For a band, the ids of its musicians. Empty for musicians. */
    val memberIds: List<Int> = emptyList(),
    /** For a musician, the band they belong to; null when they have none. */
    val bandName: String? = null,
)

enum class ArtistType { MUSICIAN, BAND }
