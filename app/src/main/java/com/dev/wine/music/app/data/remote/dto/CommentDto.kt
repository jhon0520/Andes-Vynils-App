package com.dev.wine.music.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    val id: Int,
    val description: String = "",
    val rating: Int = 0,
)
