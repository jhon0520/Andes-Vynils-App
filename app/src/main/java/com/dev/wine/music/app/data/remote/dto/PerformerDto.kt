package com.dev.wine.music.app.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Musicians come with a birthDate, bands with a creationDate and their [musicians].
 * Both bring their [albums] and [performerPrizes]; the prizes only carry id and date.
 */
@Serializable
data class PerformerDto(
    val id: Int,
    val name: String,
    val image: String = "",
    val description: String = "",
    val birthDate: String? = null,
    val creationDate: String? = null,
    val musicians: List<PerformerDto> = emptyList(),
    val albums: List<AlbumDto> = emptyList(),
    val performerPrizes: List<PerformerPrizeDto> = emptyList(),
)
