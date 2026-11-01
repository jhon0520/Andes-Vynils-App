package com.dev.wine.music.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dev.wine.music.app.R
import com.dev.wine.music.app.ui.common.ErrorType

@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().testTag("loading_view"), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

/** A missing resource (404) cannot be fixed by retrying, so that case hides the button. */
@Composable
fun ErrorView(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    type: ErrorType = ErrorType.UNKNOWN,
) {
    val notFound = type == ErrorType.NOT_FOUND
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag(if (notFound) "not_found_view" else "error_view"),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(
                when (type) {
                    ErrorType.NETWORK -> R.string.error_network
                    ErrorType.NOT_FOUND -> R.string.error_not_found
                    else -> R.string.error_loading
                },
            ),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        if (!notFound) {
            Button(onClick = onRetry, modifier = Modifier.testTag("retry_button")) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
}

@Composable
fun EmptyView(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().testTag("empty_view"), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.empty_list),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

/** Shown when a search or filter leaves nothing to show; [message] explains what was searched. */
@Composable
fun NoResultsView(message: String, onClear: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        // Scrollable so the message and the button stay reachable when the keyboard shrinks the screen.
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("no_results_view"),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.no_results_count),
            style = MaterialTheme.typography.labelLarge,
        )
        Text(
            text = stringResource(R.string.no_results_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        OutlinedButton(onClick = onClear, modifier = Modifier.testTag("clear_search_button")) {
            Text(text = stringResource(R.string.clear_search))
        }
    }
}
