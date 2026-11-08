package com.dev.wine.music.app.ui.album.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.Comment
import com.dev.wine.music.app.domain.model.NumberedTrack
import com.dev.wine.music.app.domain.model.UserSession
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.components.AlbumRecord
import com.dev.wine.music.app.ui.components.ErrorView
import com.dev.wine.music.app.ui.components.LoadingView
import com.dev.wine.music.app.ui.components.TerracottaAddButton
import com.dev.wine.music.app.ui.components.labelRes
import com.dev.wine.music.app.ui.theme.DetailMono
import com.dev.wine.music.app.ui.theme.LabelMono
import com.dev.wine.music.app.ui.theme.SansBody
import com.dev.wine.music.app.ui.theme.Terracotta

/** Lines of description shown before «Leer más». */
private const val DESCRIPTION_PREVIEW_LINES = 3

/** Sleeve width over the cover width: shows more record than the catalog card, as in the mockup. */
private const val SLEEVE_FRACTION = 0.73f

/** Cover width over the screen width. */
private const val COVER_WIDTH = 0.82f

@Composable
fun AlbumDetailScreen(
    onBack: () -> Unit,
    onAddTrackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlbumDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val session by viewModel.session.collectAsStateWithLifecycle()
    AlbumDetailContent(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::loadAlbum,
        modifier = modifier,
        session = session,
        onAddTrackClick = onAddTrackClick,
    )
}

@Composable
fun AlbumDetailContent(
    uiState: UiState<Album>,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    session: UserSession? = null,
    onAddTrackClick: () -> Unit = {},
) {
    Column(modifier = modifier.fillMaxSize().testTag("album_detail")) {
        DetailTopBar(onBack = onBack)
        when (uiState) {
            UiState.Loading -> LoadingView()
            is UiState.Error -> ErrorView(onRetry = onRetry, type = uiState.type)
            is UiState.Success -> AlbumDetailBody(
                album = uiState.data,
                // Only a collector can add tracks, so only a collector sees the button.
                canAddTrack = session is UserSession.Collector,
                onAddTrackClick = onAddTrackClick,
            )
        }
    }
}

/** Back arrow on the left and the "FICHA DEL ÁLBUM" label on the right. */
@Composable
private fun DetailTopBar(onBack: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(start = 4.dp, end = 16.dp, top = 4.dp),
    ) {
        IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
            )
        }
        Text(
            text = stringResource(R.string.album_detail_eyebrow).uppercase(),
            style = LabelMono,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun AlbumDetailBody(album: Album, canAddTrack: Boolean, onAddTrackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("album_detail_body"),
    ) {
        AlbumRecord(
            album = album,
            sleeveFraction = SLEEVE_FRACTION,
            large = true,
            modifier = Modifier.fillMaxWidth(COVER_WIDTH),
        )
        AlbumFacts(album = album, modifier = Modifier.padding(top = 24.dp))
        Text(
            text = album.name,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp),
        )
        if (album.performers.isNotEmpty()) {
            Performers(names = album.performerNames, modifier = Modifier.padding(top = 8.dp))
        }
        if (album.description.isNotBlank()) {
            ExpandableDescription(description = album.description, modifier = Modifier.padding(top = 20.dp))
        }
        if (canAddTrack) {
            TerracottaAddButton(
                text = stringResource(R.string.add_track),
                onClick = onAddTrackClick,
                textStyle = SansBody.copy(fontSize = 18.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .testTag("add_track_button"),
            )
        }
        Tracklist(tracks = album.numberedTracks, modifier = Modifier.padding(top = 32.dp))
        Reviews(comments = album.comments, modifier = Modifier.padding(top = 32.dp, bottom = 24.dp))
    }
}

/** "ELEKTRA · 01/08/1984 · SALSA · 2 TRACKS": each value is its own text so tests can read it. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AlbumFacts(album: Album, modifier: Modifier = Modifier) {
    val genre = album.genreType?.let { stringResource(it.labelRes()) } ?: album.genre
    val facts = listOf(
        album.recordLabel to "album_record_label",
        album.releaseDay to "album_release_date",
        genre to "album_genre",
        pluralStringResource(R.plurals.album_track_count, album.tracks.size, album.tracks.size) to "album_track_count",
    ).filter { (value, _) -> value.isNotBlank() }
    FlowRow(modifier = modifier.fillMaxWidth()) {
        facts.forEachIndexed { index, (value, tag) ->
            if (index > 0) MetaText(" · ")
            MetaText(value.uppercase(), Modifier.testTag(tag))
        }
    }
}

@Composable
private fun MetaText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = DetailMono,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}

/** "por Rubén Blades", with the names underlined in terracotta as in the mockup. */
@Composable
private fun Performers(names: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.Bottom) {
        Text(
            text = stringResource(R.string.album_performers_prefix),
            style = SansBody.copy(fontSize = 17.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = names,
            style = SansBody.copy(fontSize = 17.sp),
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(start = 8.dp)
                .drawBehind {
                    val stroke = 2.dp.toPx()
                    val y = size.height - stroke / 2
                    drawLine(Terracotta, Offset(0f, y), Offset(size.width, y), strokeWidth = stroke)
                }
                .testTag("album_performers"),
        )
    }
}

@Composable
private fun ExpandableDescription(description: String, modifier: Modifier = Modifier) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    // Only offer «Leer más» when the preview actually cuts the text.
    var isCut by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, lineHeight = 28.sp),
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = if (expanded) Int.MAX_VALUE else DESCRIPTION_PREVIEW_LINES,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { if (!expanded) isCut = it.hasVisualOverflow },
            modifier = Modifier.testTag("album_description"),
        )
        if (expanded || isCut) {
            Text(
                text = stringResource(if (expanded) R.string.read_less else R.string.read_more),
                style = SansBody,
                fontWeight = FontWeight.Bold,
                color = Terracotta,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable(role = Role.Button) { expanded = !expanded }
                    .padding(vertical = 8.dp)
                    .testTag("read_more_button"),
            )
        }
    }
}

/** Mono caps title with a solid ink rule under it ("LADO A", "RESEÑAS · 1"). */
@Composable
private fun SectionTitle(text: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = text.uppercase(),
            style = LabelMono,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 10.dp),
        )
        HorizontalDivider(thickness = 1.5.dp, color = MaterialTheme.colorScheme.onBackground)
    }
}

/** Tracks grouped by vinyl side, each side under its own "LADO A" / "LADO B" title. */
@Composable
private fun Tracklist(tracks: List<NumberedTrack>, modifier: Modifier = Modifier) {
    if (tracks.isEmpty()) {
        Column(modifier = modifier) {
            SectionTitle(stringResource(R.string.tracks))
            EmptySection(stringResource(R.string.album_tracks_empty), "album_tracks_empty")
        }
        return
    }
    val sides = remember(tracks) { tracks.groupBy { it.position.take(1) } }
    Column(
        modifier = modifier.testTag("album_tracklist"),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        sides.forEach { (side, sideTracks) ->
            Column {
                SectionTitle(stringResource(R.string.album_side, side))
                sideTracks.forEach { TrackRow(it) }
            }
        }
    }
}

@Composable
private fun TrackRow(numbered: NumberedTrack) {
    val (position, track) = numbered
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).testTag("track_item_${track.id}"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = position,
            style = DetailMono,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(44.dp),
        )
        Text(
            text = track.name,
            style = SansBody.copy(fontSize = 18.sp),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = track.duration,
            style = DetailMono.copy(fontSize = 15.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun Reviews(comments: List<Comment>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        SectionTitle(stringResource(R.string.album_reviews_count, comments.size))
        if (comments.isEmpty()) {
            EmptySection(stringResource(R.string.album_reviews_empty), "album_reviews_empty")
            return@Column
        }
        Column(
            modifier = Modifier.padding(top = 16.dp).testTag("album_reviews"),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            comments.forEach { ReviewItem(it) }
        }
    }
}

/** The review as a quote, then "Author · ★★★★★". */
@Composable
private fun ReviewItem(comment: Comment) {
    Column(modifier = Modifier.fillMaxWidth().testTag("review_item_${comment.id}")) {
        Text(
            text = "“${comment.description}”",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            val meta = SansBody.copy(fontSize = 15.sp)
            Text(
                text = comment.author ?: stringResource(R.string.review_anonymous),
                style = meta,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(text = " · ", style = meta, color = MaterialTheme.colorScheme.onSurfaceVariant)
            RatingStars(comment.rating)
        }
    }
}

/** Five stars, filled up to [rating]; read aloud as "Calificación: 4 de 5". */
@Composable
private fun RatingStars(rating: Int) {
    val description = stringResource(R.string.review_rating_description, rating, Comment.MAX_RATING)
    Text(
        text = "★".repeat(rating) + "☆".repeat(Comment.MAX_RATING - rating),
        style = SansBody.copy(fontSize = 17.sp, letterSpacing = 1.sp),
        color = Terracotta,
        modifier = Modifier.semantics { contentDescription = description },
    )
}

@Composable
private fun EmptySection(text: String, tag: String) {
    Text(
        text = text,
        style = SansBody,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 16.dp).testTag(tag),
    )
}
