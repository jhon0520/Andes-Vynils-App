package com.dev.wine.music.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.dev.wine.music.app.ui.album.create.AlbumCreateScreen
import com.dev.wine.music.app.ui.album.detail.AlbumDetailScreen
import com.dev.wine.music.app.ui.album.list.AlbumListScreen
import com.dev.wine.music.app.ui.artist.ArtistListScreen
import com.dev.wine.music.app.ui.artist.detail.ArtistDetailScreen
import com.dev.wine.music.app.ui.collector.CollectorListScreen
import com.dev.wine.music.app.ui.onboarding.CollectorPickerScreen
import com.dev.wine.music.app.ui.onboarding.RoleSelectionScreen

private const val CATALOG_CHANGED_KEY = "catalog_changed"

@Composable
fun WineMusicNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    // The profile is not remembered, so every new process starts in the onboarding.
    NavHost(
        navController = navController,
        startDestination = OnboardingGraph,
        modifier = modifier,
    ) {
        navigation<OnboardingGraph>(startDestination = RoleSelectionRoute) {
            composable<RoleSelectionRoute> {
                RoleSelectionScreen(
                    onVisitorSelected = { navController.finishOnboarding() },
                    onCollectorClick = { navController.navigate(CollectorPickerRoute) },
                )
            }
            composable<CollectorPickerRoute> {
                CollectorPickerScreen(
                    // dropUnlessResumed ignores a second tap while the screen is already leaving,
                    // so a double tap cannot pop the role selection too and leave a blank screen.
                    onBack = dropUnlessResumed { navController.popBackStack() },
                    onCollectorSelected = { navController.finishOnboarding() },
                )
            }
        }
        composable<AlbumsRoute> { entry ->
            val catalogChanged by entry.savedStateHandle
                .getStateFlow(CATALOG_CHANGED_KEY, false)
                .collectAsStateWithLifecycle()
            AlbumListScreen(
                onAlbumClick = { albumId -> navController.navigate(AlbumDetailRoute(albumId)) },
                onCreateAlbumClick = dropUnlessResumed { navController.navigate(AlbumCreateRoute) },
                catalogChanged = catalogChanged,
                onCatalogRefreshed = { entry.savedStateHandle[CATALOG_CHANGED_KEY] = false },
            )
        }
        composable<AlbumCreateRoute> {
            AlbumCreateScreen(
                onBack = dropUnlessResumed { navController.popBackStack() },
                onAddTracks = { albumId -> navController.openCreatedAlbum(albumId, openAddTrack = true) },
                onViewAlbum = { albumId -> navController.openCreatedAlbum(albumId, openAddTrack = false) },
                onNotNow = {
                    navController.markCatalogChanged()
                    navController.popBackStack()
                },
            )
        }
        composable<AlbumDetailRoute> {
            AlbumDetailScreen(
                onBack = dropUnlessResumed { navController.popBackStack() },
            )
        }
        composable<ArtistsRoute> {
            ArtistListScreen(
                onArtistClick = { artistId, type -> navController.navigate(ArtistDetailRoute(artistId, type.name)) },
            )
        }
        composable<ArtistDetailRoute> {
            ArtistDetailScreen(
                onBack = dropUnlessResumed { navController.popBackStack() },
                onAlbumClick = { albumId -> navController.navigate(AlbumDetailRoute(albumId)) },
            )
        }
        composable<CollectorsRoute> {
            CollectorListScreen(
                // TODO: navigate to the collector detail when HU06 is implemented.
                onCollectorClick = {},
            )
        }
    }
}

/** The catalog keeps its ViewModel in the back stack, so it is told to load the new album. */
private fun NavHostController.markCatalogChanged() {
    getBackStackEntry<AlbumsRoute>().savedStateHandle[CATALOG_CHANGED_KEY] = true
}

/** Replaces the form with the album, so back from the detail returns to the catalog. */
private fun NavHostController.openCreatedAlbum(albumId: Int, openAddTrack: Boolean) {
    markCatalogChanged()
    navigate(AlbumDetailRoute(albumId, openAddTrack)) {
        popUpTo<AlbumCreateRoute> { inclusive = true }
    }
}

/** Drops the onboarding from the back stack, so back on the albums screen leaves the app. */
private fun NavHostController.finishOnboarding() {
    navigate(AlbumsRoute) {
        popUpTo<OnboardingGraph> { inclusive = true }
    }
}
