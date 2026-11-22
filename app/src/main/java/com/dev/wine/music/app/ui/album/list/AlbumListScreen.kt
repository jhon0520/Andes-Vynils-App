package com.dev.wine.music.app.ui.album.list

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag as semanticsTestTag
import com.dev.wine.music.app.domain.model.UserSession
import com.dev.wine.music.app.ui.common.AnnounceOnChange
import com.dev.wine.music.app.ui.theme.LabelMono
import com.dev.wine.music.app.ui.theme.SansBody
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.dev.wine.music.app.ui.theme.SleeveText
import com.dev.wine.music.app.ui.theme.Terracotta
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
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
import com.dev.wine.music.app.ui.common.resultsMessage
import com.dev.wine.music.app.ui.components.AlbumCard
import com.dev.wine.music.app.ui.components.EmptyView
import com.dev.wine.music.app.ui.components.ErrorView
import com.dev.wine.music.app.ui.components.LoadingView
import com.dev.wine.music.app.ui.components.NoResultsView
import com.dev.wine.music.app.ui.components.TerracottaAddButton
import com.dev.wine.music.app.ui.components.labelRes

/** "Todos" first, then every genre the backend accepts. */
private val GENRE_OPTIONS: List<AlbumGenre?> = listOf(null) + AlbumGenre.entries

/** Stateful entry point used by navigation: gets the ViewModel and observes it. */
@Composable
fun AlbumListScreen(
    onAlbumClick: (albumId: Int) -> Unit,
    onCreateAlbumClick: () -> Unit,
    modifier: Modifier = Modifier,
    catalogChanged: Boolean = false,
    onCatalogRefreshed: () -> Unit = {},
    viewModel: AlbumListViewModel = hiltViewModel(),
) {
    LaunchedEffect(catalogChanged) {
        if (catalogChanged) {
            viewModel.loadAlbums()
            onCatalogRefreshed()
        }
    }
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
    // The button grows with the font size, so the room left under the last row comes from its real height.
    var buttonHeightPx by remember { mutableIntStateOf(0) }
    val buttonHeight = with(LocalDensity.current) { buttonHeightPx.toDp() }
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
            bottomPadding = if (canCreateAlbum) maxOf(88.dp, buttonHeight + 32.dp) else 16.dp,
        )
        if (canCreateAlbum) {
            CreateAlbumButton(
                onClick = onCreateAlbumClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .onSizeChanged { buttonHeightPx = it.height },
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
    val catalog = (uiState as? UiState.Success)?.data
    if (catalog == null || catalog.totalCount == 0) {
        Column(modifier = Modifier.fillMaxSize()) {
            CatalogHeader(session = session)
            when (uiState) {
                UiState.Loading -> LoadingView()
                is UiState.Error -> ErrorView(onRetry = onRetry, type = uiState.type)
                is UiState.Success -> EmptyView()
            }
        }
        return
    }
    // D-18: TalkBack says how many albums are left after a search or a filter.
    AnnounceOnChange(
        message = resultsMessage(catalog.albums.size),
        key = Triple(query, selectedGenre, catalog.albums.size),
    )
    // The header, the search field and the tabs are full-width items of the grid, so they scroll away with
    // the albums. With a large font they used to stay fixed and left the albums an eighth of the screen.
    val showsAlbums = catalog.albums.isNotEmpty()
    val gridState = rememberLazyGridState()
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(top = 0.dp, bottom = bottomPadding),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            // "album_list" keeps meaning "the albums are on screen" for the E2E, Kraken and profiling steps.
            .testTag(if (showsAlbums) "album_list" else "album_catalog"),
    ) {
        fullWidthItem("header") { CatalogHeader(session = session) }
        fullWidthItem("search") { AlbumSearchField(query = query, onQueryChange = onQueryChange) }
        fullWidthItem("tabs") {
            GenreTabs(
                selectedGenre = selectedGenre,
                onGenreSelect = onGenreSelect,
                modifier = Modifier.padding(bottom = 16.dp),
            )
        }
        if (showsAlbums) {
            itemsIndexed(catalog.albums, key = { _, album -> album.id }) { index, album ->
                // The grid has no side padding (the full-width items draw edge to edge), so each card adds
                // the 16 dp margin on its outer side and the 24 dp gap below its row.
                val firstColumn = index % 2 == 0
                AlbumCard(
                    album = album,
                    onClick = { onAlbumClick(album.id) },
                    modifier = Modifier
                        .padding(
                            start = if (firstColumn) 16.dp else 0.dp,
                            end = if (firstColumn) 0.dp else 16.dp,
                            bottom = 24.dp,
                        )
                        .testTag("album_item_${album.id}"),
                )
            }
        } else {
            fullWidthItem("no_results") {
                // NoResultsView scrolls by itself, so it needs a bounded height: most of the visible grid.
                val viewport = with(LocalDensity.current) { gridState.layoutInfo.viewportSize.height.toDp() }
                Box(modifier = Modifier.fillMaxWidth().height(maxOf(280.dp, viewport * 0.6f))) {
                    NoResultsView(
                        message = if (query.isBlank()) {
                            stringResource(R.string.album_no_results_genre)
                        } else {
                            stringResource(R.string.album_no_results_query, query.trim())
                        },
                        onClear = onClearFilters,
                    )
                }
            }
        }
    }
}

private fun LazyGridScope.fullWidthItem(key: String, content: @Composable () -> Unit) {
    item(key = key, span = { GridItemSpan(maxLineSpan) }) { content() }
}

@Composable
private fun CreateAlbumButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    TerracottaAddButton(
        text = stringResource(R.string.create_album),
        onClick = onClick,
        modifier = modifier.testTag("create_album_button"),
    )
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
                modifier = Modifier.semantics { heading() },
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
            }
            // Last in the chain: it sizes the circle that the background and the clip above draw.
            .circleAroundContent(minSize = 48.dp, padding = 6.dp),
    ) {
        Text(
            text = stringResource(if (isCollector) R.string.profile_badge_collector else R.string.profile_badge_visitor),
            style = SansBody.copy(fontSize = 15.sp),
            fontWeight = FontWeight.Bold,
            color = if (isCollector) SleeveText else ink,
        )
    }
}

/**
 * Square layout as big as its content plus [padding], never smaller than [minSize]: with a large font the
 * initials make the circle grow instead of being cut by a fixed 48 dp size.
 */
private fun Modifier.circleAroundContent(minSize: Dp, padding: Dp): Modifier = layout { measurable, constraints ->
    val inset = padding.roundToPx() * 2
    val placeable = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
    val side = maxOf(minSize.roundToPx(), placeable.width + inset, placeable.height + inset)
    layout(side, side) {
        placeable.place((side - placeable.width) / 2, (side - placeable.height) / 2)
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
private fun GenreTabs(
    selectedGenre: AlbumGenre?,
    onGenreSelect: (AlbumGenre?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(top = 8.dp)) {
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
                        // Short labels such as "Rock" still get a 48 dp wide touch target.
                        .sizeIn(minWidth = 48.dp)
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
