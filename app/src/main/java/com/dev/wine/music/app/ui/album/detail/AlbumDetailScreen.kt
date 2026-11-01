package com.dev.wine.music.app.ui.album.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.Comment
import com.dev.wine.music.app.domain.model.NumberedTrack
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.components.ErrorView
import com.dev.wine.music.app.ui.components.LoadingView
import com.dev.wine.music.app.ui.components.labelRes

/** Lines of description shown before «Leer más». */
private const val DESCRIPTION_PREVIEW_LINES = 4

@Composable
fun AlbumDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlbumDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AlbumDetailContent(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::loadAlbum,
        modifier = modifier,
    )
}

@Composable
fun AlbumDetailContent(
    uiState: UiState<Album>,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().testTag("album_detail")) {
        IconButton(onClick = onBack, modifier = Modifier.padding(4.dp).testTag("back_button")) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
            )
        }
        when (uiState) {
            UiState.Loading -> LoadingView()
            is UiState.Error -> ErrorView(onRetry = onRetry, type = uiState.type)
            is UiState.Success -> AlbumDetailBody(album = uiState.data)
        }
    }
}

@Composable
private fun AlbumDetailBody(album: Album) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("album_detail_body"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CoverWithVinyl(album)
        Text(text = album.name, style = MaterialTheme.typography.headlineSmall)
        if (album.performers.isNotEmpty()) {
            Text(
                text = album.performerNames,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("album_performers"),
            )
        }
        AlbumFacts(album)
        if (album.description.isNotBlank()) {
            ExpandableDescription(album.description)
        }
        SectionTitle(stringResource(R.string.tracks))
        Tracklist(album.numberedTracks)
        SectionTitle(stringResource(R.string.album_reviews))
        Reviews(album.comments)
    }
}

/** The cover with the record half out of its sleeve, as in the wireframe. */
@Composable
private fun CoverWithVinyl(album: Album) {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .offset(x = 48.dp)
                .size(180.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSurface),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
            )
        }
        AsyncImage(
            model = album.cover,
            contentDescription = stringResource(R.string.album_cover_description, album.name),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .offset(x = (-24).dp)
                .size(190.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
    }
}

@Composable
private fun AlbumFacts(album: Album) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Fact(stringResource(R.string.album_record_label), album.recordLabel, "album_record_label")
        Fact(stringResource(R.string.album_release_date), album.releaseDay, "album_release_date")
        Fact(
            stringResource(R.string.album_genre),
            album.genreType?.let { stringResource(it.labelRes()) } ?: album.genre,
            "album_genre",
        )
        Fact(stringResource(R.string.album_track_count), album.tracks.size.toString(), "album_track_count")
    }
}

@Composable
private fun Fact(label: String, value: String, tag: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(120.dp),
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.testTag(tag))
    }
}

@Composable
private fun ExpandableDescription(description: String) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    // Only offer «Leer más» when the preview actually cuts the text.
    var isCut by remember { mutableStateOf(false) }
    Column {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = if (expanded) Int.MAX_VALUE else DESCRIPTION_PREVIEW_LINES,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { if (!expanded) isCut = it.hasVisualOverflow },
            modifier = Modifier.testTag("album_description"),
        )
        if (expanded || isCut) {
            TextButton(onClick = { expanded = !expanded }, modifier = Modifier.testTag("read_more_button")) {
                Text(stringResource(if (expanded) R.string.read_less else R.string.read_more))
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 8.dp))
}

@Composable
private fun Tracklist(tracks: List<NumberedTrack>) {
    if (tracks.isEmpty()) {
        EmptySection(stringResource(R.string.album_tracks_empty), "album_tracks_empty")
        return
    }
    Column(modifier = Modifier.testTag("album_tracklist")) {
        tracks.forEach { (position, track) ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).testTag("track_item_${track.id}"),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = position,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.width(40.dp),
                )
                Text(text = track.name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                Text(text = track.duration, style = MaterialTheme.typography.bodyMedium)
            }
            HorizontalDivider()
        }
    }
}

@Composable
private fun Reviews(comments: List<Comment>) {
    if (comments.isEmpty()) {
        EmptySection(stringResource(R.string.album_reviews_empty), "album_reviews_empty")
        return
    }
    Column(modifier = Modifier.testTag("album_reviews"), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        comments.forEach { comment ->
            Column(modifier = Modifier.fillMaxWidth().testTag("review_item_${comment.id}")) {
                RatingStars(comment.rating)
                Text(text = comment.description, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = comment.author ?: stringResource(R.string.review_anonymous),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** Five stars, filled up to [rating]; read aloud as "Calificación: 4 de 5". */
@Composable
private fun RatingStars(rating: Int) {
    val description = stringResource(R.string.review_rating_description, rating, Comment.MAX_RATING)
    Text(
        text = "★".repeat(rating) + "☆".repeat(Comment.MAX_RATING - rating),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.semantics { contentDescription = description },
    )
}

@Composable
private fun EmptySection(text: String, tag: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.testTag(tag),
    )
}
