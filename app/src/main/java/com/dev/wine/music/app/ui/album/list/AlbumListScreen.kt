package com.dev.wine.music.app.ui.album.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.components.AlbumCard
import com.dev.wine.music.app.ui.components.EmptyView
import com.dev.wine.music.app.ui.components.ErrorView
import com.dev.wine.music.app.ui.components.LoadingView

/** Stateful entry point used by navigation: gets the ViewModel and observes it. */
@Composable
fun AlbumListScreen(
    onAlbumClick: (albumId: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlbumListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AlbumListContent(
        uiState = uiState,
        onAlbumClick = onAlbumClick,
        onRetry = viewModel::loadAlbums,
        modifier = modifier,
    )
}

/** Stateless UI: easy to preview and to test without Hilt or a network. */
@Composable
fun AlbumListContent(
    uiState: UiState<List<Album>>,
    onAlbumClick: (albumId: Int) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        UiState.Loading -> LoadingView(modifier)
        is UiState.Error -> ErrorView(onRetry = onRetry, modifier = modifier)
        is UiState.Success -> if (uiState.data.isEmpty()) {
            EmptyView(modifier)
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = modifier,
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = stringResource(R.string.albums),
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }
                items(uiState.data, key = { it.id }) { album ->
                    AlbumCard(album = album, onClick = { onAlbumClick(album.id) })
                }
            }
        }
    }
}
