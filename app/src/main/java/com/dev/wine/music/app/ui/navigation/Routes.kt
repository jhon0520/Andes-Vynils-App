package com.dev.wine.music.app.ui.navigation

import kotlinx.serialization.Serializable

// Type-safe navigation routes (Navigation Compose 2.8+).
@Serializable data object OnboardingGraph
@Serializable data object RoleSelectionRoute
@Serializable data object CollectorPickerRoute
@Serializable data object AlbumsRoute
@Serializable data object ArtistsRoute
@Serializable data object CollectorsRoute
@Serializable data class AlbumDetailRoute(val albumId: Int)

/** [type] is the [com.dev.wine.music.app.domain.model.ArtistType] name: musicians and bands use different endpoints. */
@Serializable data class ArtistDetailRoute(val artistId: Int, val type: String)
