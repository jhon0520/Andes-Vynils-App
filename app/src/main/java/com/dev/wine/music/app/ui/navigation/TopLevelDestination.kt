package com.dev.wine.music.app.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dev.wine.music.app.R

/** The tabs shown in the bottom navigation bar. */
enum class TopLevelDestination(
    val route: Any,
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int,
) {
    ALBUMS(AlbumsRoute, R.string.albums, R.drawable.ic_nav_album),
    ARTISTS(ArtistsRoute, R.string.artists, R.drawable.ic_nav_artist),
    COLLECTORS(CollectorsRoute, R.string.collectors, R.drawable.ic_nav_collectors),
}
