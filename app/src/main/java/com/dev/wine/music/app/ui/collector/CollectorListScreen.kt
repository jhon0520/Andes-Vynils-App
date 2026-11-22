package com.dev.wine.music.app.ui.collector

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.components.EmptyView
import com.dev.wine.music.app.ui.components.ErrorView
import com.dev.wine.music.app.ui.components.LoadingView
import com.dev.wine.music.app.ui.components.NoResultsView
import com.dev.wine.music.app.ui.theme.WineMusicAppTheme

/** HU05 · Coleccionistas: search by name or email and open a collector. */
@Composable
fun CollectorListScreen(
    onCollectorClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CollectorListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CollectorListContent(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onCollectorClick = onCollectorClick,
        onRetry = viewModel::loadCollectors,
        modifier = modifier,
    )
}

@Composable
fun CollectorListContent(
    uiState: CollectorListUiState,
    onQueryChange: (String) -> Unit,
    onCollectorClick: (Int) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.collectors),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp).semantics { heading() },
        )
        SearchField(
            query = uiState.query,
            onQueryChange = onQueryChange,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        )
        Box(modifier = Modifier.weight(1f)) {
            when (val collectors = uiState.collectors) {
                UiState.Loading -> LoadingView()
                is UiState.Error -> ErrorView(onRetry = onRetry, type = collectors.type)
                is UiState.Success -> when {
                    uiState.totalCount == 0 -> EmptyView()
                    collectors.data.isEmpty() -> NoResultsView(
                        message = stringResource(R.string.collector_no_results_query, uiState.query),
                        onClear = { onQueryChange("") },
                    )
                    else -> CollectorList(collectors = collectors.data, onCollectorClick = onCollectorClick)
                }
            }
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    // A filled, rounded field without the bottom line, as in the HU05 wireframe.
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(stringResource(R.string.collector_search_hint)) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }, modifier = Modifier.testTag("collectors_search_clear_button")) {
                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.clear_search))
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("collectors_search"),
    )
}

@Composable
private fun CollectorList(collectors: List<Collector>, onCollectorClick: (Int) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().testTag("collectors_list")) {
        items(collectors, key = { it.id }) { collector ->
            CollectorItem(collector = collector, onClick = { onCollectorClick(collector.id) })
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class) // invisibleToUser
@Composable
private fun CollectorItem(collector: Collector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("collector_item_${collector.id}"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = collectorInitials(collector.name),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                // The initials repeat the name read next to them, so TalkBack skips them.
                modifier = Modifier.semantics { invisibleToUser() },
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = collector.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = collector.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        // The whole row is the click target; the chevron only says that it opens something.
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CollectorListPreview() {
    WineMusicAppTheme {
        CollectorListContent(
            uiState = CollectorListUiState(
                collectors = UiState.Success(
                    listOf(
                        Collector(100, "Manolo Bellón", "3502457896", "manollo@caracol.com.co"),
                        Collector(101, "Jaime Monsalve", "3012357936", "jmonsalve@rtvc.com.co"),
                    ),
                ),
                totalCount = 2,
            ),
            onQueryChange = {},
            onCollectorClick = {},
            onRetry = {},
        )
    }
}
