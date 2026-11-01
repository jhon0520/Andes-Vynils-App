package com.dev.wine.music.app.ui.components

import androidx.annotation.StringRes
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.AlbumGenre

/** Spanish label for each genre; the backend value ("Classical") stays in the domain. */
@StringRes
fun AlbumGenre.labelRes(): Int = when (this) {
    AlbumGenre.SALSA -> R.string.genre_salsa
    AlbumGenre.ROCK -> R.string.genre_rock
    AlbumGenre.CLASSICAL -> R.string.genre_classical
    AlbumGenre.FOLK -> R.string.genre_folk
}
