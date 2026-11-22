package com.dev.wine.music.app.data.remote.dto

import kotlinx.serialization.Serializable

/** Body of POST /albums/{id}/tracks. */
@Serializable
data class TrackRequestDto(
    val name: String,
    val duration: String,
)
