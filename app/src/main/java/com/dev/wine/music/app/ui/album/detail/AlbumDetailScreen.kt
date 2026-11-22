package com.dev.wine.music.app.ui.album.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.domain.model.Comment
import com.dev.wine.music.app.domain.model.NumberedTrack
import com.dev.wine.music.app.domain.model.UserSession
import com.dev.wine.music.app.ui.album.track.AddTrackSheet
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.components.AlbumRecord
import com.dev.wine.music.app.ui.components.DetailTopBar
import com.dev.wine.music.app.ui.components.EmptySection
import com.dev.wine.music.app.ui.components.ErrorView
import com.dev.wine.music.app.ui.components.LoadingView
import com.dev.wine.music.app.ui.components.RatingStars
import com.dev.wine.music.app.ui.components.SectionTitle
import com.dev.wine.music.app.ui.components.TerracottaAddButton
import com.dev.wine.music.app.ui.components.labelRes
import com.dev.wine.music.app.ui.theme.DetailMono
import com.dev.wine.music.app.ui.theme.LabelMono
import com.dev.wine.music.app.ui.theme.SansBody
import com.dev.wine.music.app.ui.theme.Terracotta
import com.dev.wine.music.app.util.fastForEach

/** Lines of description shown before «Leer más». */
private const val DESCRIPTION_PREVIEW_LINES = 3

/** Sleeve width over the cover width: shows more record than the catalog card, as in the mockup. */
private const val SLEEVE_FRACTION = 0.73f

/** Cover width over the screen width. */
private const val COVER_WIDTH = 0.82f

@Composable
fun AlbumDetailScreen(
    onBack: () -> Unit,
    onArtistClick: (artistId: Int, type: ArtistType) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlbumDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val session by viewModel.session.collectAsStateWithLifecycle()
    var showAddTrack by rememberSaveable { mutableStateOf(viewModel.openAddTrackOnArrival) }
    AlbumDetailContent(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::loadAlbum,
        modifier = modifier,
        session = session,
        onAddTrackClick = { showAddTrack = true },
        onArtistClick = onArtistClick,
    )
    val album = (uiState as? UiState.Success)?.data
    if (showAddTrack && album != null && session is UserSession.Collector) {
        AddTrackSheet(
            album = album,
            onDismiss = { showAddTrack = false },
            onSaved = {
                showAddTrack = false
                viewModel.loadAlbum()
            },
        )
    }
}

@Composable
fun AlbumDetailContent(
    uiState: UiState<Album>,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    session: UserSession? = null,
    onAddTrackClick: () -> Unit = {},
    onArtistClick: (artistId: Int, type: ArtistType) -> Unit = { _, _ -> },
) {
    Column(modifier = modifier.fillMaxSize().testTag("album_detail")) {
        DetailTopBar(eyebrow = stringResource(R.string.album_detail_eyebrow), onBack = onBack)
        when (uiState) {
            UiState.Loading -> LoadingView()
            is UiState.Error -> ErrorView(onRetry = onRetry, type = uiState.type)
            is UiState.Success -> AlbumDetailBody(
                album = uiState.data,
                // Only a collector can add tracks, so only a collector sees the button.
                canAddTrack = session is UserSession.Collector,
                onAddTrackClick = onAddTrackClick,
                onArtistClick = onArtistClick,
            )
        }
    }
}

@Composable
private fun AlbumDetailBody(
    album: Album,
    canAddTrack: Boolean,
    onAddTrackClick: () -> Unit,
    onArtistClick: (Int, ArtistType) -> Unit,
) {
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
            Performers(
                performers = album.performers,
                onArtistClick = onArtistClick,
                modifier = Modifier.padding(top = 8.dp),
            )
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

/**
 * "por Rubén Blades, Seis del Solar", with each name underlined in terracotta as in the mockup.
 * Each name is its own link to the artist detail (HU04), so an album with several performers
 * opens the one that was tapped.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Performers(
    performers: List<Artist>,
    onArtistClick: (Int, ArtistType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val style = SansBody.copy(fontSize = 17.sp)
    FlowRow(
        modifier = modifier.testTag("album_performers"),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.album_performers_prefix),
            style = style,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterVertically).padding(end = 8.dp),
        )
        performers.forEachIndexed { index, artist ->
            PerformerLink(
                artist = artist,
                // The comma belongs to the name before it, so it never starts a new line.
                text = if (index < performers.lastIndex) "${artist.name}," else artist.name,
                style = style,
                onClick = { onArtistClick(artist.id, artist.type) },
                modifier = Modifier.align(Alignment.CenterVertically).padding(end = 8.dp),
            )
        }
    }
}

@Composable
private fun PerformerLink(
    artist: Artist,
    text: String,
    style: TextStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val openArtist = stringResource(R.string.album_performer_open, artist.name)
    Text(
        text = text,
        style = style,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
            .clickable(role = Role.Button, onClickLabel = openArtist, onClick = onClick)
            // At least 48 dp tall so the link is easy to tap (accessibility).
            .heightIn(min = 48.dp)
            .wrapContentHeight(Alignment.CenterVertically)
            .drawBehind {
                val stroke = 2.dp.toPx()
                val y = size.height - stroke / 2
                drawLine(Terracotta, Offset(0f, y), Offset(size.width, y), strokeWidth = stroke)
            }
            .testTag("album_performer_${artist.id}"),
    )
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
                sideTracks.fastForEach { TrackRow(it) }
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
            comments.fastForEach { ReviewItem(it) }
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

