package com.dev.wine.music.app.ui.artist

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
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

@Composable
fun ArtistListScreen(
    modifier: Modifier = Modifier,
    viewModel: ArtistListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ArtistListContent(uiState = uiState, onRetry = viewModel::loadArtists, modifier = modifier)
}

@Composable
fun ArtistListContent(
    uiState: UiState<List<Artist>>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        UiState.Loading -> LoadingView(modifier)
        is UiState.Error -> ErrorView(onRetry = onRetry, modifier = modifier)
        is UiState.Success -> if (uiState.data.isEmpty()) {
            EmptyView(modifier)
        } else {
            LazyColumn(modifier = modifier, contentPadding = PaddingValues(vertical = 16.dp)) {
                item {
                    Text(
                        text = stringResource(R.string.artists),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
                items(uiState.data, key = { "${it.type}-${it.id}" }) { artist ->
                    ArtistItem(artist)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun ArtistItem(artist: Artist) {
    ListItem(
        headlineContent = { Text(artist.name) },
        supportingContent = {
            Text(
                stringResource(
                    if (artist.type == ArtistType.BAND) R.string.band else R.string.musician,
                ),
            )
        },
        leadingContent = {
            AsyncImage(
                model = artist.image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
            )
        },
    )
}
