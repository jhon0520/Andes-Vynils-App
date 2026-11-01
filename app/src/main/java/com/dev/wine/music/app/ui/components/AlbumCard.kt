package com.dev.wine.music.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.Album

@Composable
fun AlbumCard(
    album: Album,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val coverRequest = remember(album.cover) {
        ImageRequest.Builder(context).data(album.cover).crossfade(true).build()
    }
    Card(onClick = onClick, modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
        ) {
            // The sleeve sits under the cover: it is the placeholder while loading and stays visible if the cover fails.
            AlbumSleeve(album = album, modifier = Modifier.matchParentSize())
            AsyncImage(
                model = coverRequest,
                contentDescription = stringResource(R.string.album_cover_description, album.name),
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
        }
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = album.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (album.performerNames.isNotBlank()) {
                Text(
                    text = album.performerNames,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            val genre = album.genreType?.let { stringResource(it.labelRes()) } ?: album.genre
            Text(
                text = listOf(album.releaseYear, genre)
                    .filter { it.isNotBlank() }
                    .joinToString(" · "),
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

/** Colored sleeve with the album name and label, like the back of a record without its printed cover. */
@Composable
private fun AlbumSleeve(album: Album, modifier: Modifier = Modifier) {
    val (background, content) = sleeveColors(album.id, MaterialTheme.colorScheme)
    Column(
        modifier = modifier
            .background(background)
            .padding(12.dp)
            // The name is already read below the cover; tests find the sleeve by its tag.
            .clearAndSetSemantics { testTag = "album_sleeve_${album.id}" },
    ) {
        Text(
            text = album.name,
            style = MaterialTheme.typography.titleMedium,
            color = content,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = album.recordLabel.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = content,
            maxLines = 1,
        )
    }
}

/** Picks one of four theme color pairs from the id, so each album keeps the same sleeve color. */
private fun sleeveColors(albumId: Int, colors: ColorScheme): Pair<Color, Color> =
    when (albumId.mod(4)) {
        0 -> colors.primaryContainer to colors.onPrimaryContainer
        1 -> colors.secondaryContainer to colors.onSecondaryContainer
        2 -> colors.tertiaryContainer to colors.onTertiaryContainer
        else -> colors.inverseSurface to colors.inverseOnSurface
    }
