package com.dev.wine.music.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.ui.theme.DetailMono
import com.dev.wine.music.app.ui.theme.LabelMono
import com.dev.wine.music.app.ui.theme.RecordBlack
import com.dev.wine.music.app.ui.theme.RecordGreen
import com.dev.wine.music.app.ui.theme.SleeveBrown
import com.dev.wine.music.app.ui.theme.SleeveSand
import com.dev.wine.music.app.ui.theme.SleeveShadow
import com.dev.wine.music.app.ui.theme.SansBody
import com.dev.wine.music.app.ui.theme.SleeveText

/** Sleeve width over the whole card width: the rest is the record peeking out on the right. */
private const val SLEEVE_FRACTION = 0.88f
private val SleeveShape = RoundedCornerShape(2.dp)

/** Catalog card from the mockup: a sleeve with the record peeking out, then name, performers and year · genre. */
@Composable
fun AlbumCard(
    album: Album,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.clickable(onClick = onClick)) {
        AlbumRecord(album = album)
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

/** The record behind the sleeve, the sleeve's shadow and the sleeve itself (with the cover on top when it loads). */
@Composable
private fun AlbumRecord(album: Album) {
    val context = LocalContext.current
    val coverRequest = remember(album.cover) {
        ImageRequest.Builder(context).data(album.cover).crossfade(true).build()
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f / SLEEVE_FRACTION),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val side = size.height
            val radius = side * 0.46f
            drawCircle(color = RecordBlack, radius = radius, center = Offset(size.width - radius, side / 2))
            val shadow = 3.dp.toPx()
            drawRoundRect(
                color = SleeveShadow,
                topLeft = Offset(shadow, shadow),
                size = Size(side, side),
                cornerRadius = CornerRadius(2.dp.toPx()),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .clip(SleeveShape),
        ) {
            // The sleeve is the placeholder while loading and stays visible if the cover fails.
            AlbumSleeve(album = album, modifier = Modifier.matchParentSize())
            AsyncImage(
                model = coverRequest,
                contentDescription = stringResource(R.string.album_cover_description, album.name),
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
        }
    }
}

/** Colored sleeve with the album name and label, like the back of a record without its printed cover. */
@Composable
private fun AlbumSleeve(album: Album, modifier: Modifier = Modifier) {
    val (background, content) = sleeveColors(album.id)
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
            fontWeight = FontWeight.Bold,
            color = content,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = album.recordLabel.uppercase(),
            style = LabelMono,
            color = content,
            maxLines = 1,
        )
    }
}

/** Picks one of the four mockup sleeves from the id, so each album keeps the same color. */
private fun sleeveColors(albumId: Int): Pair<Color, Color> =
    when (albumId.mod(4)) {
        0 -> RecordGreen to SleeveText
        1 -> SleeveBrown to SleeveText
        2 -> SleeveSand to RecordBlack
        else -> RecordBlack to SleeveText
    }
