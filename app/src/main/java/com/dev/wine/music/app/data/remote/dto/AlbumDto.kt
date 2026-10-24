package com.dev.wine.music.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AlbumDto(
    val id: Int,
    val name: String,
    val cover: String = "",
    val releaseDate: String = "",
    val description: String = "",
    val genre: String = "",
    val recordLabel: String = "",
    val tracks: List<TrackDto> = emptyList(),
    val performers: List<PerformerDto> = emptyList(),
)

@Serializable
data class TrackDto(
    val id: Int,
    val name: String,
    val duration: String = "",
)
