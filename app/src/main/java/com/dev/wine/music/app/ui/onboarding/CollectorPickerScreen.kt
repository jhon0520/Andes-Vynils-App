package com.dev.wine.music.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
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
import com.dev.wine.music.app.ui.theme.LabelMono
import com.dev.wine.music.app.ui.theme.SleeveText
import com.dev.wine.music.app.ui.theme.Terracotta
import com.dev.wine.music.app.ui.theme.WineMusicAppTheme

/** Background of the selected row: a light wash of terracotta that also works in dark theme. */
private val SelectedRowColor = Terracotta.copy(alpha = 0.16f)

@Composable
fun CollectorPickerScreen(
    onBack: () -> Unit,
    onCollectorSelected: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CollectorPickerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CollectorPickerContent(
        uiState = uiState,
        onBack = onBack,
        onQueryChange = viewModel::onQueryChange,
        onCollectorSelect = viewModel::onCollectorSelect,
        onContinue = { if (viewModel.onContinue()) onCollectorSelected() },
        onRetry = viewModel::loadCollectors,
        modifier = modifier,
    )
}

@Composable
fun CollectorPickerContent(
    uiState: CollectorPickerUiState,
    onBack: () -> Unit,
    onQueryChange: (String) -> Unit,
    onCollectorSelect: (Int) -> Unit,
    onContinue: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("collector_picker"),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
            }
            Text(
                text = stringResource(R.string.collector_profile_title),
                style = LabelMono,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = stringResource(R.string.collector_picker_question),
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        )
        SearchField(
            query = uiState.query,
            onQueryChange = onQueryChange,
            modifier = Modifier.padding(horizontal = 24.dp),
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
                    else -> CollectorList(
                        collectors = collectors.data,
                        selectedId = uiState.selected?.id,
                        onCollectorSelect = onCollectorSelect,
                    )
                }
            }
        }
        Footer(selected = uiState.selected, enabled = uiState.canContinue, onContinue = onContinue)
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    // Only the bottom line is visible, as in the mockup.
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(stringResource(R.string.collector_search_hint)) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Terracotta,
            cursorColor = Terracotta,
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("collector_search"),
    )
}

@Composable
private fun CollectorList(
    collectors: List<Collector>,
    selectedId: Int?,
    onCollectorSelect: (Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .selectableGroup()
            .testTag("collector_list"),
    ) {
        items(collectors, key = { it.id }) { collector ->
            CollectorRow(
                collector = collector,
                selected = collector.id == selectedId,
                onClick = { onCollectorSelect(collector.id) },
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
    }
}

@Composable
private fun CollectorRow(collector: Collector, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .background(if (selected) SelectedRowColor else Color.Transparent)
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .testTag("collector_row_${collector.id}"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (selected) Terracotta else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = collectorInitials(collector.name),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (selected) SleeveText else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = collector.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = collector.email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        // The whole row is the selectable target, so the radio itself ignores clicks.
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(selectedColor = Terracotta),
        )
    }
}

@Composable
private fun Footer(selected: Collector?, enabled: Boolean, onContinue: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = stringResource(R.string.collector_change_later),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onContinue,
            enabled = enabled,
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = SleeveText),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("collector_continue"),
        ) {
            Text(
                text = selected?.let { stringResource(R.string.collector_continue_as, it.name) }
                    ?: stringResource(R.string.collector_continue),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CollectorPickerPreview() {
    val collectors = listOf(
        Collector(1, "Manolo Bellon", "3502457896", "manollo@caracol.com.co"),
        Collector(2, "Jaime Monsalve", "3012357936", "jmonsalve@rtvc.com.co"),
    )
    WineMusicAppTheme(darkTheme = false) {
        CollectorPickerContent(
            uiState = CollectorPickerUiState(
                collectors = UiState.Success(collectors),
                totalCount = collectors.size,
                selected = collectors.first(),
            ),
            onBack = {},
            onQueryChange = {},
            onCollectorSelect = {},
            onContinue = {},
            onRetry = {},
        )
    }
}
