package com.dev.wine.music.app.ui.navigation

import kotlinx.serialization.Serializable

// Type-safe navigation routes (Navigation Compose 2.8+).
@Serializable data object AlbumsRoute
@Serializable data object ArtistsRoute
@Serializable data object CollectorsRoute
@Serializable data class AlbumDetailRoute(val albumId: Int)
