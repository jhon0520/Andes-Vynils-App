package com.dev.wine.music.app.ui.artist.detail

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.ArtistDetail
import com.dev.wine.music.app.domain.model.ArtistPrize
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.components.DetailTopBar
import com.dev.wine.music.app.ui.components.EmptySection
import com.dev.wine.music.app.ui.components.ErrorView
import com.dev.wine.music.app.ui.components.LoadingView
import com.dev.wine.music.app.ui.components.SectionTitle
import com.dev.wine.music.app.ui.theme.DetailMono

@Composable
fun ArtistDetailScreen(
    onBack: () -> Unit,
    onAlbumClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArtistDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ArtistDetailContent(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::loadArtist,
        onAlbumClick = onAlbumClick,
        modifier = modifier,
    )
}

@Composable
fun ArtistDetailContent(
    uiState: UiState<ArtistDetail>,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    onAlbumClick: (Int) -> Unit = {},
) {
    Column(modifier = modifier.fillMaxSize().testTag("artist_detail")) {
        DetailTopBar(eyebrow = stringResource(R.string.artist_detail_eyebrow), onBack = onBack)
        when (uiState) {
            UiState.Loading -> LoadingView()
            is UiState.Error -> ErrorView(onRetry = onRetry, type = uiState.type)
            is UiState.Success -> ArtistDetailBody(detail = uiState.data, onAlbumClick = onAlbumClick)
        }
    }
}

@Composable
private fun ArtistDetailBody(detail: ArtistDetail, onAlbumClick: (Int) -> Unit) {
    val artist = detail.artist
    val isBand = artist.type == ArtistType.BAND
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("artist_detail_body"),
    ) {
        // The image shape tells the type apart, as in the list: square for bands, round for musicians.
        AsyncImage(
            model = artist.image,
            // "Foto de …", not the bare name: the name is the heading right below.
            contentDescription = stringResource(R.string.artist_image_description, artist.name),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(160.dp)
                .clip(if (isBand) RoundedCornerShape(12.dp) else CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .testTag(if (isBand) "artist_image_band" else "artist_image_musician"),
        )
        Text(
            text = artist.name,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 24.dp).semantics { heading() }.testTag("artist_name"),
        )
        ArtistFacts(detail = detail, isBand = isBand, modifier = Modifier.padding(top = 8.dp))
        if (artist.description.isNotBlank()) {
            Text(
                text = artist.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 20.dp).testTag("artist_description"),
            )
        }
        if (isBand) {
            Members(members = detail.members, modifier = Modifier.padding(top = 32.dp))
        } else {
            BandOfMusician(band = detail.band, modifier = Modifier.padding(top = 32.dp))
        }
        Discography(albums = detail.albums, onAlbumClick = onAlbumClick, modifier = Modifier.padding(top = 32.dp))
        Prizes(prizes = detail.prizes, modifier = Modifier.padding(top = 32.dp, bottom = 24.dp))
    }
}

/** "BANDA · CREADA EL 01/01/1970" or "MÚSICO · NACIÓ EL 16/07/1948". */
@Composable
private fun ArtistFacts(detail: ArtistDetail, isBand: Boolean, modifier: Modifier = Modifier) {
    val type = stringResource(if (isBand) R.string.band else R.string.musician)
    val date = if (detail.date.isBlank()) {
        ""
    } else {
        stringResource(if (isBand) R.string.artist_created_on else R.string.artist_born_on, detail.day)
    }
    Text(
        text = listOf(type, date).filter { it.isNotBlank() }.joinToString(" · ").uppercase(),
        style = DetailMono,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.testTag("artist_facts"),
    )
}

@Composable
private fun Members(members: List<Artist>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        SectionTitle(stringResource(R.string.artist_members_count, members.size))
        if (members.isEmpty()) {
            EmptySection(stringResource(R.string.artist_members_empty), "artist_members_empty")
        } else {
            members.forEach { member ->
                PersonRow(name = member.name, image = member.image, tag = "member_item_${member.id}")
            }
        }
    }
}

@Composable
private fun BandOfMusician(band: Artist?, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        SectionTitle(stringResource(R.string.band))
        if (band == null) {
            EmptySection(stringResource(R.string.artist_band_empty), "artist_band_empty")
        } else {
            PersonRow(name = band.name, image = band.image, tag = "artist_band", square = true)
        }
    }
}

@Composable
private fun PersonRow(name: String, image: String, tag: String, square: Boolean = false) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        // Image and name are read as one item.
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp).semantics(mergeDescendants = true) {}.testTag(tag),
    ) {
        AsyncImage(
            model = image,
            contentDescription = stringResource(R.string.artist_image_description, name),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(if (square) RoundedCornerShape(8.dp) else CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
        Text(text = name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun Discography(albums: List<Album>, onAlbumClick: (Int) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.testTag("artist_albums")) {
        SectionTitle(stringResource(R.string.artist_albums_count, albums.size))
        if (albums.isEmpty()) {
            EmptySection(stringResource(R.string.artist_albums_empty), "artist_albums_empty")
        } else {
            albums.forEach { album -> AlbumRow(album = album, onClick = { onAlbumClick(album.id) }) }
        }
    }
}

/** Cover, name and "1984 · ELEKTRA"; a tap opens the album detail. */
@Composable
private fun AlbumRow(album: Album, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(top = 16.dp)
            .testTag("artist_album_${album.id}"),
    ) {
        AsyncImage(
            model = album.cover,
            contentDescription = stringResource(R.string.album_cover_description, album.name),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
        Column {
            Text(text = album.name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Text(
                text = listOf(album.releaseYear, album.recordLabel).filter { it.isNotBlank() }.joinToString(" · ").uppercase(),
                style = DetailMono,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Prizes(prizes: List<ArtistPrize>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.testTag("artist_prizes")) {
        SectionTitle(stringResource(R.string.artist_prizes_count, prizes.size))
        if (prizes.isEmpty()) {
            EmptySection(stringResource(R.string.artist_prizes_empty), "artist_prizes_empty")
        } else {
            prizes.forEach { prize -> PrizeRow(prize) }
        }
    }
}

/** Prize name, then "ORGANIZATION · 10/12/1978". */
@Composable
private fun PrizeRow(prize: ArtistPrize) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp).testTag("prize_item_${prize.id}")) {
        Text(
            text = prize.name.ifBlank { stringResource(R.string.artist_prize_unnamed) },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = listOf(prize.organization, prize.premiationDay).filter { it.isNotBlank() }.joinToString(" · ").uppercase(),
            style = DetailMono,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
