package com.dev.wine.music.app.data.remote.dto

import kotlinx.serialization.Serializable

/** GET /collectors/{id}/albums: an album of the collection with its price and status ("Active"/"Inactive"). */
@Serializable
data class CollectorAlbumDto(
    val id: Int,
    val price: Int = 0,
    val status: String = "",
    val album: AlbumDto? = null,
)
