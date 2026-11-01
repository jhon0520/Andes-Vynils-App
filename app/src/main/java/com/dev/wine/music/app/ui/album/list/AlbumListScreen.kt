package com.dev.wine.music.app.ui.album.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.AlbumGenre
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.components.AlbumCard
import com.dev.wine.music.app.ui.components.EmptyView
import com.dev.wine.music.app.ui.components.ErrorView
import com.dev.wine.music.app.ui.components.LoadingView
import com.dev.wine.music.app.ui.components.NoResultsView
import com.dev.wine.music.app.ui.components.labelRes

/** "Todos" first, then every genre the backend accepts. */
private val GENRE_OPTIONS: List<AlbumGenre?> = listOf(null) + AlbumGenre.entries

/** Stateful entry point used by navigation: gets the ViewModel and observes it. */
@Composable
fun AlbumListScreen(
    onAlbumClick: (albumId: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlbumListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()
    AlbumListContent(
        uiState = uiState,
        query = query,
        selectedGenre = selectedGenre,
        onQueryChange = viewModel::onQueryChange,
        onGenreSelect = viewModel::onGenreSelect,
        onClearFilters = viewModel::clearFilters,
        onAlbumClick = onAlbumClick,
        onRetry = viewModel::loadAlbums,
        modifier = modifier,
    )
}

/** Stateless UI: easy to preview and to test without Hilt or a network. */
@Composable
fun AlbumListContent(
    uiState: UiState<AlbumCatalog>,
    query: String,
    selectedGenre: AlbumGenre?,
    onQueryChange: (String) -> Unit,
    onGenreSelect: (AlbumGenre?) -> Unit,
    onClearFilters: () -> Unit,
    onAlbumClick: (albumId: Int) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        CatalogHeader()
        when (uiState) {
            UiState.Loading -> LoadingView()
            is UiState.Error -> ErrorView(onRetry = onRetry)
            is UiState.Success -> {
                val catalog = uiState.data
                if (catalog.totalCount == 0) {
                    EmptyView()
                } else {
                    AlbumSearchField(query = query, onQueryChange = onQueryChange)
                    GenreTabs(selectedGenre = selectedGenre, onGenreSelect = onGenreSelect)
                    if (catalog.albums.isEmpty()) {
                        NoResultsView(
                            message = if (query.isBlank()) {
                                stringResource(R.string.album_no_results_genre)
                            } else {
                                stringResource(R.string.album_no_results_query, query.trim())
                            },
                            onClear = onClearFilters,
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.testTag("album_list"),
                        ) {
                            items(catalog.albums, key = { it.id }) { album ->
                                AlbumCard(
                                    album = album,
                                    onClick = { onAlbumClick(album.id) },
                                    modifier = Modifier.testTag("album_item_${album.id}"),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogHeader() {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
        Text(
            text = stringResource(R.string.catalog_eyebrow),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.albums),
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}

@Composable
private fun AlbumSearchField(query: String, onQueryChange: (String) -> Unit) {
    val focusManager = LocalFocusManager.current
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(stringResource(R.string.album_search_hint)) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.testTag("album_search_clear_button"),
                ) {
                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.clear_search))
                }
            }
        },
        singleLine = true,
        // Results update while typing; "Buscar" on the keyboard only hides it so the results are visible.
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("album_search_input"),
    )
}

@Composable
private fun GenreTabs(selectedGenre: AlbumGenre?, onGenreSelect: (AlbumGenre?) -> Unit) {
    ScrollableTabRow(
        selectedTabIndex = GENRE_OPTIONS.indexOf(selectedGenre),
        edgePadding = 16.dp,
        containerColor = Color.Transparent,
    ) {
        GENRE_OPTIONS.forEach { genre ->
            Tab(
                selected = genre == selectedGenre,
                onClick = { onGenreSelect(genre) },
                text = { Text(stringResource(genre?.labelRes() ?: R.string.genre_all)) },
                modifier = Modifier.testTag("album_genre_tab_${genre?.name?.lowercase() ?: "all"}"),
            )
        }
    }
}
