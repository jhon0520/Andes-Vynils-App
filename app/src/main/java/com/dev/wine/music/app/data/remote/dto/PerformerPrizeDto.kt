package com.dev.wine.music.app.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * A prize given to a performer. Inside a musician or band only id and date arrive;
 * GET /performerprizes also brings the [prize] with its name and organization.
 */
@Serializable
data class PerformerPrizeDto(
    val id: Int,
    val premiationDate: String = "",
    val prize: PrizeDto? = null,
)

@Serializable
data class PrizeDto(
    val id: Int,
    val name: String = "",
    val description: String = "",
    val organization: String = "",
)
