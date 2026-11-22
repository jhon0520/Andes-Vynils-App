package com.dev.wine.music.app.data.remote.dto

import kotlinx.serialization.Serializable

/** Body of POST /albums. The backend rejects any field outside this schema. */
@Serializable
data class AlbumRequestDto(
    val name: String,
    val cover: String,
    val releaseDate: String,
    val description: String,
    val genre: String,
    val recordLabel: String,
)
