package com.dev.wine.music.app.data.remote.dto

import kotlinx.serialization.Serializable

/** Musicians come with a birthDate, bands with a creationDate. */
@Serializable
data class PerformerDto(
    val id: Int,
    val name: String,
    val image: String = "",
    val description: String = "",
    val birthDate: String? = null,
    val creationDate: String? = null,
)
