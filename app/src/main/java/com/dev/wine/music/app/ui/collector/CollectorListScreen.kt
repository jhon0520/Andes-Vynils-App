package com.dev.wine.music.app.ui.collector

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
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
import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.components.EmptyView
import com.dev.wine.music.app.ui.components.ErrorView
import com.dev.wine.music.app.ui.components.LoadingView

@Composable
fun CollectorListScreen(
    modifier: Modifier = Modifier,
    viewModel: CollectorListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CollectorListContent(uiState = uiState, onRetry = viewModel::loadCollectors, modifier = modifier)
}

@Composable
fun CollectorListContent(
    uiState: UiState<List<Collector>>,
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
                        text = stringResource(R.string.collectors),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
                items(uiState.data, key = { it.id }) { collector ->
                    ListItem(
                        headlineContent = { Text(collector.name) },
                        supportingContent = { Text(collector.email) },
                        trailingContent = { Text(collector.telephone) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
