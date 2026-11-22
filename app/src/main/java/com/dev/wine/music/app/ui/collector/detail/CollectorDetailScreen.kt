package com.dev.wine.music.app.ui.collector.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.domain.model.CollectorAlbum
import com.dev.wine.music.app.domain.model.CollectorAlbumStatus
import com.dev.wine.music.app.domain.model.CollectorDetail
import com.dev.wine.music.app.domain.model.CollectorReview
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.components.DetailTopBar
import com.dev.wine.music.app.ui.components.EmptySection
import com.dev.wine.music.app.ui.components.ErrorView
import com.dev.wine.music.app.ui.components.LoadingView
import com.dev.wine.music.app.ui.components.RatingStars
import com.dev.wine.music.app.ui.components.SectionTitle
import com.dev.wine.music.app.ui.theme.DetailMono
import com.dev.wine.music.app.ui.theme.SansBody
import com.dev.wine.music.app.ui.theme.WineMusicAppTheme
import com.dev.wine.music.app.util.fastForEach

@Composable
fun CollectorDetailScreen(
    onBack: () -> Unit,
    onArtistClick: (artistId: Int, type: ArtistType) -> Unit,
    onAlbumClick: (albumId: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CollectorDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CollectorDetailContent(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::loadCollector,
        onArtistClick = onArtistClick,
        onAlbumClick = onAlbumClick,
        modifier = modifier,
    )
}

@Composable
fun CollectorDetailContent(
    uiState: UiState<CollectorDetail>,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    onArtistClick: (artistId: Int, type: ArtistType) -> Unit = { _, _ -> },
    onAlbumClick: (albumId: Int) -> Unit = {},
) {
    Column(modifier = modifier.fillMaxSize().testTag("collector_detail")) {
        DetailTopBar(eyebrow = stringResource(R.string.collector_detail_eyebrow), onBack = onBack)
        when (uiState) {
            UiState.Loading -> LoadingView()
            is UiState.Error -> ErrorView(onRetry = onRetry, type = uiState.type)
            is UiState.Success -> CollectorDetailBody(
                detail = uiState.data,
                onArtistClick = onArtistClick,
                onAlbumClick = onAlbumClick,
            )
        }
    }
}

@Composable
private fun CollectorDetailBody(
    detail: CollectorDetail,
    onArtistClick: (Int, ArtistType) -> Unit,
    onAlbumClick: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("collector_detail_body"),
    ) {
        Text(
            text = detail.collector.name,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.semantics { heading() }.testTag("collector_name"),
        )
        Contact(collector = detail.collector, modifier = Modifier.padding(top = 12.dp))
        Favorites(artists = detail.favoriteArtists, onArtistClick = onArtistClick, modifier = Modifier.padding(top = 32.dp))
        Collection(albums = detail.albums, onAlbumClick = onAlbumClick, modifier = Modifier.padding(top = 32.dp))
        Reviews(
            reviews = detail.reviews,
            onAlbumClick = onAlbumClick,
            modifier = Modifier.padding(top = 32.dp, bottom = 24.dp),
        )
    }
}

@Composable
private fun Contact(collector: Collector, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (collector.telephone.isNotBlank()) {
            ContactRow(
                icon = Icons.Filled.Phone,
                text = collector.telephone,
                description = stringResource(R.string.collector_telephone_description, collector.telephone),
                tag = "collector_telephone",
            )
        }
        if (collector.email.isNotBlank()) {
            ContactRow(
                icon = Icons.Filled.Email,
                text = collector.email,
                description = stringResource(R.string.collector_email_description, collector.email),
                tag = "collector_email",
            )
        }
    }
}

@Composable
private fun ContactRow(icon: ImageVector, text: String, description: String, tag: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.semantics(mergeDescendants = true) { contentDescription = description }.testTag(tag),
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = text, style = SansBody, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun Favorites(artists: List<Artist>, onArtistClick: (Int, ArtistType) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.testTag("collector_favorites")) {
        SectionTitle(stringResource(R.string.collector_favorites_count, artists.size))
        if (artists.isEmpty()) {
            EmptySection(stringResource(R.string.collector_favorites_empty), "collector_favorites_empty")
        } else {
            artists.fastForEach { artist -> FavoriteRow(artist = artist, onClick = { onArtistClick(artist.id, artist.type) }) }
        }
    }
}

/** Round image for musicians and square for bands, as in the artist list. */
@Composable
private fun FavoriteRow(artist: Artist, onClick: () -> Unit) {
    val isBand = artist.type == ArtistType.BAND
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(top = 16.dp)
            .testTag("favorite_item_${artist.id}"),
    ) {
        AsyncImage(
            model = artist.image,
            contentDescription = stringResource(R.string.artist_image_description, artist.name),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(if (isBand) RoundedCornerShape(8.dp) else CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
        Column {
            Text(text = artist.name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Text(
                text = stringResource(if (isBand) R.string.band else R.string.musician).uppercase(),
                style = DetailMono,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Collection(albums: List<CollectorAlbum>, onAlbumClick: (Int) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.testTag("collector_albums")) {
        SectionTitle(stringResource(R.string.collector_albums_count, albums.size))
        if (albums.isEmpty()) {
            EmptySection(stringResource(R.string.collector_albums_empty), "collector_albums_empty")
        } else {
            albums.fastForEach { item -> CollectionRow(item = item, onClick = { onAlbumClick(item.album.id) }) }
        }
    }
}

/** Cover, name and "PRECIO: 35 · ACTIVO"; a tap opens the album detail. */
@Composable
private fun CollectionRow(item: CollectorAlbum, onClick: () -> Unit) {
    val status = stringResource(
        if (item.status == CollectorAlbumStatus.ACTIVE) R.string.collector_album_active else R.string.collector_album_inactive,
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(top = 16.dp)
            .testTag("collector_album_${item.album.id}"),
    ) {
        AlbumCover(item.album)
        Column {
            Text(text = item.album.name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Text(
                text = "${stringResource(R.string.collector_album_price, item.price)} · $status".uppercase(),
                style = DetailMono,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.testTag("collector_album_meta_${item.album.id}"),
            )
        }
    }
}

@Composable
private fun AlbumCover(album: Album) {
    AsyncImage(
        model = album.cover,
        contentDescription = stringResource(R.string.album_cover_description, album.name),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
    )
}

@Composable
private fun Reviews(reviews: List<CollectorReview>, onAlbumClick: (Int) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.testTag("collector_reviews")) {
        SectionTitle(stringResource(R.string.album_reviews_count, reviews.size))
        if (reviews.isEmpty()) {
            EmptySection(stringResource(R.string.collector_reviews_empty), "collector_reviews_empty")
        } else {
            reviews.fastForEach { review -> ReviewRow(review = review, onAlbumClick = onAlbumClick) }
        }
    }
}

/** The album it belongs to, the review as a quote and its stars. A tap opens the album when it is known. */
@Composable
private fun ReviewRow(review: CollectorReview, onAlbumClick: (Int) -> Unit) {
    val album = review.album
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (album != null) Modifier.clickable(role = Role.Button) { onAlbumClick(album.id) } else Modifier)
            .padding(top = 16.dp)
            .testTag("collector_review_${review.id}"),
    ) {
        Text(
            text = (album?.name ?: stringResource(R.string.collector_review_unknown_album)).uppercase(),
            style = DetailMono,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.testTag("collector_review_album_${review.id}"),
        )
        Text(
            text = "“${review.description}”",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 4.dp),
        )
        RatingStars(rating = review.rating, modifier = Modifier.padding(top = 4.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun CollectorDetailPreview() {
    val album = Album(
        id = 100,
        name = "Buscando América",
        cover = "",
        releaseDate = "1984-08-01T05:00:00.000Z",
        description = "",
        genre = "Salsa",
        recordLabel = "Elektra",
    )
    WineMusicAppTheme {
        CollectorDetailContent(
            uiState = UiState.Success(
                CollectorDetail(
                    collector = Collector(100, "Manolo Bellon", "3502457896", "manollo@caracol.com.co"),
                    favoriteArtists = listOf(Artist(100, "Rubén Blades Bellido de Luna", "", "", ArtistType.MUSICIAN)),
                    albums = listOf(CollectorAlbum(100, 35, CollectorAlbumStatus.ACTIVE, album)),
                    reviews = listOf(CollectorReview(100, "The most relevant album of Ruben Blades", 5, album)),
                ),
            ),
            onBack = {},
            onRetry = {},
        )
    }
}
