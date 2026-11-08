package com.dev.wine.music.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.dev.wine.music.app.ui.album.detail.AlbumDetailScreen
import com.dev.wine.music.app.ui.album.list.AlbumListScreen
import com.dev.wine.music.app.ui.artist.ArtistListScreen
import com.dev.wine.music.app.ui.collector.CollectorListScreen
import com.dev.wine.music.app.ui.onboarding.CollectorPickerScreen
import com.dev.wine.music.app.ui.onboarding.RoleSelectionScreen

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
        composable<AlbumsRoute> {
            AlbumListScreen(
                onAlbumClick = { albumId -> navController.navigate(AlbumDetailRoute(albumId)) },
            )
        }
        composable<AlbumDetailRoute> {
            AlbumDetailScreen(onBack = dropUnlessResumed { navController.popBackStack() })
        }
        composable<ArtistsRoute> {
            ArtistListScreen()
        }
        composable<CollectorsRoute> {
            CollectorListScreen()
        }
    }
}

/** Drops the onboarding from the back stack, so back on the albums screen leaves the app. */
private fun NavHostController.finishOnboarding() {
    navigate(AlbumsRoute) {
        popUpTo<OnboardingGraph> { inclusive = true }
    }
}
