package com.dev.wine.music.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.ui.theme.DetailMono
import com.dev.wine.music.app.ui.theme.SansBody

/** Sleeve width over the whole card width: the rest is the record peeking out on the right. */
private const val SLEEVE_FRACTION = 0.88f

/** Catalog card from the mockup: a sleeve with the record peeking out, then name, performers and year · genre. */
@Composable
fun AlbumCard(
    album: Album,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.clickable(onClick = onClick)) {
        AlbumRecord(album = album, sleeveFraction = SLEEVE_FRACTION)
        // Every text reserves a fixed number of lines, so all cards in the grid have the same height.
        Text(
            text = album.name,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, lineHeight = 22.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            minLines = 2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 12.dp),
        )
        // Always drawn (even when blank) to keep the line reserved.
        Text(
            text = album.performerNames,
            style = SansBody,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            minLines = 1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        val genre = album.genreType?.let { stringResource(it.labelRes()) } ?: album.genre
        Text(
            text = listOf(album.releaseYear, genre.uppercase())
                .filter { it.isNotBlank() }
                .joinToString(" · "),
            style = DetailMono,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
