package com.dev.wine.music.app.ui.album.list

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.testTag as semanticsTestTag
import com.dev.wine.music.app.domain.model.UserSession
import com.dev.wine.music.app.ui.theme.LabelMono
import com.dev.wine.music.app.ui.theme.SansBody
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.dev.wine.music.app.ui.theme.RecordBlack
import com.dev.wine.music.app.ui.theme.SleeveText
import com.dev.wine.music.app.ui.theme.Terracotta
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
    onCreateAlbumClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlbumListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val session by viewModel.session.collectAsStateWithLifecycle()
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
        session = session,
        onCreateAlbumClick = onCreateAlbumClick,
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
    session: UserSession? = null,
    onCreateAlbumClick: () -> Unit = {},
) {
    // Only a collector can create albums, so only a collector sees the button.
    val canCreateAlbum = session is UserSession.Collector
    Box(modifier = modifier.fillMaxSize()) {
        AlbumCatalogBody(
            uiState = uiState,
            query = query,
            selectedGenre = selectedGenre,
            onQueryChange = onQueryChange,
            onGenreSelect = onGenreSelect,
            onClearFilters = onClearFilters,
            onAlbumClick = onAlbumClick,
            onRetry = onRetry,
            session = session,
            // Leaves room under the last row so the button never covers an album.
            bottomPadding = if (canCreateAlbum) 88.dp else 16.dp,
        )
        if (canCreateAlbum) {
            CreateAlbumButton(
                onClick = onCreateAlbumClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
            )
        }
    }
}

@Composable
private fun AlbumCatalogBody(
    uiState: UiState<AlbumCatalog>,
    query: String,
    selectedGenre: AlbumGenre?,
    onQueryChange: (String) -> Unit,
    onGenreSelect: (AlbumGenre?) -> Unit,
    onClearFilters: () -> Unit,
    onAlbumClick: (albumId: Int) -> Unit,
    onRetry: () -> Unit,
    session: UserSession?,
    bottomPadding: Dp,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        CatalogHeader(session = session)
        when (uiState) {
            UiState.Loading -> LoadingView()
            is UiState.Error -> ErrorView(onRetry = onRetry, type = uiState.type)
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
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                top = 16.dp,
                                end = 16.dp,
                                bottom = bottomPadding,
                            ),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp),
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

/** Terracotta button from the catalog mockup, with a darker bottom edge like a pressed key. */
@Composable
private fun CreateAlbumButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(4.dp)
    Button(
        onClick = onClick,
        shape = shape,
        colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = SleeveText),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        modifier = modifier
            .background(RecordBlack, shape)
            .padding(bottom = 3.dp)
            .testTag("create_album_button"),
    ) {
        Icon(Icons.Filled.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(R.string.create_album),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun CatalogHeader(session: UserSession?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.catalog_eyebrow).uppercase(),
                style = LabelMono,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.albums),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        if (session != null) ProfileBadge(session = session)
    }
}

/** Round badge with the current role: filled terracotta for a collector, outlined for a visitor. */
@Composable
private fun ProfileBadge(session: UserSession) {
    val isCollector = session is UserSession.Collector
    val description = when (session) {
        is UserSession.Collector -> stringResource(R.string.profile_badge_collector_description, session.name)
        UserSession.Visitor -> stringResource(R.string.profile_badge_visitor_description)
    }
    val ink = MaterialTheme.colorScheme.onBackground
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .then(
                if (isCollector) {
                    Modifier.background(Terracotta)
                } else {
                    Modifier.border(BorderStroke(1.5.dp, ink), CircleShape)
                },
            )
            // Read as one label ("Perfil: ..."); the tag goes inside because clearAndSetSemantics drops the rest.
            .clearAndSetSemantics {
                contentDescription = description
                semanticsTestTag = "profile_badge"
            },
    ) {
        Text(
            text = stringResource(if (isCollector) R.string.profile_badge_collector else R.string.profile_badge_visitor),
            style = SansBody.copy(fontSize = 15.sp),
            fontWeight = FontWeight.Bold,
            color = if (isCollector) SleeveText else ink,
        )
    }
}

@Composable
private fun AlbumSearchField(query: String, onQueryChange: (String) -> Unit) {
    val focusManager = LocalFocusManager.current
    val ink = MaterialTheme.colorScheme.onBackground
    TextField(
        value = query,
        onValueChange = onQueryChange,
        textStyle = SansBody.copy(fontSize = 18.sp),
        placeholder = { Text(stringResource(R.string.album_search_hint), style = SansBody.copy(fontSize = 18.sp)) },
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
        // Just an ink underline, like the mockup.
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = ink,
            unfocusedIndicatorColor = ink,
            cursorColor = ink,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("album_search_input"),
    )
}

/** Text tabs from the mockup: dark ink and bold when selected, with a terracotta underline as wide as the word. */
@Composable
private fun GenreTabs(selectedGenre: AlbumGenre?, onGenreSelect: (AlbumGenre?) -> Unit) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            GENRE_OPTIONS.forEach { genre ->
                val selected = genre == selectedGenre
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .selectable(selected = selected, role = Role.Tab, onClick = { onGenreSelect(genre) })
                        .testTag("album_genre_tab_${genre?.name?.lowercase() ?: "all"}"),
                ) {
                    Text(
                        text = stringResource(genre?.labelRes() ?: R.string.filter_all),
                        style = SansBody.copy(fontSize = 17.sp),
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 12.dp),
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(if (selected) Terracotta else Color.Transparent),
                    )
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}
