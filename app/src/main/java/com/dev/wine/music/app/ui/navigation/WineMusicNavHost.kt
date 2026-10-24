package com.dev.wine.music.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dev.wine.music.app.ui.album.detail.AlbumDetailScreen
import com.dev.wine.music.app.ui.album.list.AlbumListScreen
import com.dev.wine.music.app.ui.artist.ArtistListScreen
import com.dev.wine.music.app.ui.collector.CollectorListScreen

@Composable
fun WineMusicNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AlbumsRoute,
        modifier = modifier,
    ) {
        composable<AlbumsRoute> {
            AlbumListScreen(
                onAlbumClick = { albumId -> navController.navigate(AlbumDetailRoute(albumId)) },
            )
        }
        composable<AlbumDetailRoute> {
            AlbumDetailScreen(onBack = { navController.popBackStack() })
        }
        composable<ArtistsRoute> {
            ArtistListScreen()
        }
        composable<CollectorsRoute> {
            CollectorListScreen()
        }
    }
}
