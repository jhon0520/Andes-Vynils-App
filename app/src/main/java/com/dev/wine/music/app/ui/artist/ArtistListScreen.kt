package com.dev.wine.music.app.ui.artist

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.components.EmptyView
import com.dev.wine.music.app.ui.components.ErrorView
import com.dev.wine.music.app.ui.components.LoadingView
import com.dev.wine.music.app.ui.components.NoResultsView

/** "Todos" first, then musicians and bands. */
private val TYPE_OPTIONS: List<ArtistType?> = listOf(null, ArtistType.MUSICIAN, ArtistType.BAND)

@Composable
fun ArtistListScreen(
    modifier: Modifier = Modifier,
    viewModel: ArtistListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()
    ArtistListContent(
        uiState = uiState,
        selectedType = selectedType,
        onTypeSelect = viewModel::onTypeSelect,
        onRetry = viewModel::loadArtists,
        modifier = modifier,
    )
}

@Composable
fun ArtistListContent(
    uiState: UiState<ArtistCatalog>,
    selectedType: ArtistType?,
    onTypeSelect: (ArtistType?) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.artists),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
        )
        when (uiState) {
            UiState.Loading -> LoadingView()
            is UiState.Error -> ErrorView(onRetry = onRetry, type = uiState.type)
            is UiState.Success -> {
                val catalog = uiState.data
                if (catalog.totalCount == 0) {
                    EmptyView()
                } else {
                    TypeTabs(selectedType = selectedType, onTypeSelect = onTypeSelect)
                    if (catalog.artists.isEmpty()) {
                        NoResultsView(
                            message = stringResource(R.string.artist_no_results_type),
                            onClear = { onTypeSelect(null) },
                            clearLabel = stringResource(R.string.artist_show_all),
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(vertical = 8.dp),
                            modifier = Modifier.testTag("artist_list"),
                        ) {
                            // Musicians and bands share one id sequence in the backend, but the type keeps keys safe.
                            items(catalog.artists, key = { "${it.type}-${it.id}" }) { artist ->
                                ArtistItem(artist, modifier = Modifier.testTag("artist_item_${artist.id}"))
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TypeTabs(selectedType: ArtistType?, onTypeSelect: (ArtistType?) -> Unit) {
    TabRow(
        selectedTabIndex = TYPE_OPTIONS.indexOf(selectedType),
        containerColor = Color.Transparent,
    ) {
        TYPE_OPTIONS.forEach { type ->
            Tab(
                selected = type == selectedType,
                onClick = { onTypeSelect(type) },
                text = { Text(stringResource(type.tabLabelRes())) },
                modifier = Modifier.testTag("artist_type_tab_${type?.name?.lowercase() ?: "all"}"),
            )
        }
    }
}

@StringRes
private fun ArtistType?.tabLabelRes(): Int = when (this) {
    null -> R.string.artist_type_all
    ArtistType.MUSICIAN -> R.string.artist_type_musicians
    ArtistType.BAND -> R.string.artist_type_bands
}

@Composable
private fun ArtistItem(artist: Artist, modifier: Modifier = Modifier) {
    val isBand = artist.type == ArtistType.BAND
    ListItem(
        headlineContent = { Text(artist.name) },
        supportingContent = {
            Text(
                text = if (isBand) {
                    stringResource(R.string.band)
                } else {
                    stringResource(R.string.musician_of_band, artist.bandName ?: stringResource(R.string.artist_no_band))
                },
            )
        },
        leadingContent = {
            // The avatar shape tells the type apart: square for bands, round for musicians.
            AsyncImage(
                model = artist.image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(if (isBand) RoundedCornerShape(8.dp) else CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .testTag(if (isBand) "artist_avatar_band" else "artist_avatar_musician"),
            )
        },
        modifier = modifier,
    )
}
