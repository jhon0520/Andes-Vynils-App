package com.dev.wine.music.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CollectorDto(
    val id: Int,
    val name: String,
    val telephone: String = "",
    val email: String = "",
)
