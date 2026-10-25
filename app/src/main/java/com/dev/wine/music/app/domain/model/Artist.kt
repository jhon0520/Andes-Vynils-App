package com.dev.wine.music.app.domain.model

data class Artist(
    val id: Int,
    val name: String,
    val image: String,
    val description: String,
    val type: ArtistType,
)

enum class ArtistType { MUSICIAN, BAND }
