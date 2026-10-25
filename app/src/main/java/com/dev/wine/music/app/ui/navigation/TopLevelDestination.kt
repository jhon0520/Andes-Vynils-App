package com.dev.wine.music.app.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector
import com.dev.wine.music.app.R

/** The tabs shown in the bottom navigation bar. */
enum class TopLevelDestination(
    val route: Any,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    ALBUMS(AlbumsRoute, R.string.albums, Icons.Filled.PlayArrow),
    ARTISTS(ArtistsRoute, R.string.artists, Icons.Filled.Person),
    COLLECTORS(CollectorsRoute, R.string.collectors, Icons.Filled.Favorite),
}
