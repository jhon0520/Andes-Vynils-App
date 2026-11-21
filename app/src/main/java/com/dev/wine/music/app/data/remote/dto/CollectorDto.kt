package com.dev.wine.music.app.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * GET /collectors and GET /collectors/{id}. The [comments] do not say which album they belong to;
 * the [favoritePerformers] come with birthDate (musicians) or creationDate (bands).
 */
@Serializable
data class CollectorDto(
    val id: Int,
    val name: String,
    val telephone: String = "",
    val email: String = "",
    val comments: List<CommentDto> = emptyList(),
    val favoritePerformers: List<PerformerDto> = emptyList(),
)
