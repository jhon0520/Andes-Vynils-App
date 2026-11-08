package com.dev.wine.music.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.ui.theme.LabelMono
import com.dev.wine.music.app.ui.theme.RecordBlack
import com.dev.wine.music.app.ui.theme.RecordGreen
import com.dev.wine.music.app.ui.theme.RecordGroove
import com.dev.wine.music.app.ui.theme.SleeveBrown
import com.dev.wine.music.app.ui.theme.SleeveSand
import com.dev.wine.music.app.ui.theme.SleeveShadow
import com.dev.wine.music.app.ui.theme.SleeveText
import com.dev.wine.music.app.ui.theme.Terracotta

private val SleeveShape = RoundedCornerShape(2.dp)

/** Record radius over the sleeve side. */
private const val RECORD_RADIUS = 0.46f

/**
 * The sleeve with the record peeking out on the right, its shadow and the cover on top when it loads.
 * [sleeveFraction] is the sleeve width over the whole width: smaller shows more record.
 * [large] is the album detail version: bigger sleeve text, the year next to the label, and grooves.
 */
@Composable
fun AlbumRecord(
    album: Album,
    sleeveFraction: Float,
    modifier: Modifier = Modifier,
    large: Boolean = false,
) {
    val context = LocalContext.current
    val coverRequest = remember(album.cover) {
        ImageRequest.Builder(context).data(album.cover).crossfade(true).build()
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f / sleeveFraction),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val side = size.height
            val radius = side * RECORD_RADIUS
            val center = Offset(size.width - radius, side / 2)
            drawCircle(color = RecordBlack, radius = radius, center = center)
            if (large) {
                val groove = Stroke(width = 1.dp.toPx())
                drawCircle(color = RecordGroove, radius = radius * 0.8f, center = center, style = groove)
                drawCircle(color = RecordGroove, radius = radius * 0.6f, center = center, style = groove)
                drawCircle(color = Terracotta, radius = radius * 0.33f, center = center)
            }
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
            AlbumSleeve(album = album, large = large, modifier = Modifier.matchParentSize())
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
private fun AlbumSleeve(album: Album, large: Boolean, modifier: Modifier = Modifier) {
    val (background, content) = sleeveColors(album.id)
    val footer = if (large) {
        listOf(album.recordLabel.uppercase(), album.releaseYear).filter { it.isNotBlank() }.joinToString(" · ")
    } else {
        album.recordLabel.uppercase()
    }
    Column(
        modifier = modifier
            .background(background)
            .padding(if (large) 20.dp else 12.dp)
            // The name is already read next to the cover; tests find the sleeve by its tag.
            .clearAndSetSemantics { testTag = "album_sleeve_${album.id}" },
    ) {
        Text(
            text = album.name,
            style = if (large) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = content,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = footer,
            style = LabelMono,
            color = content,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
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
