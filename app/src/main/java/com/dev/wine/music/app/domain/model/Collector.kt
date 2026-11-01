package com.dev.wine.music.app.domain.model

data class Collector(
    val id: Int,
    val name: String,
    val telephone: String,
    val email: String,
    /** Ids of the album comments this collector wrote; the album endpoint does not say who wrote them. */
    val commentIds: List<Int> = emptyList(),
)
