package com.dev.wine.music.app.ui.album.track

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.ui.common.ErrorType
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.components.MaskedDigitsTransformation
import com.dev.wine.music.app.ui.components.TerracottaAddButton
import com.dev.wine.music.app.ui.theme.DetailMono
import com.dev.wine.music.app.ui.theme.LabelMono

private val DurationMask = MaskedDigitsTransformation(separator = ':', separatorsAfter = listOf(2))

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AddTrackSheet(
    album: Album,
    onDismiss: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AddTrackViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DisposableEffect(viewModel) { onDispose { viewModel.reset() } }
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onSaved()
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        // A sheet is its own window: it does not inherit the flag set on the root of the UI.
        modifier = Modifier.semantics { testTagsAsResourceId = true },
    ) {
        AddTrackContent(
            album = album,
            uiState = uiState,
            onNameChange = viewModel::onNameChange,
            onDurationChange = viewModel::onDurationChange,
            onNameBlur = viewModel::onNameBlur,
            onDurationBlur = viewModel::onDurationBlur,
            onSave = viewModel::save,
            onCancel = onDismiss,
        )
    }
}

@Composable
fun AddTrackContent(
    album: Album,
    uiState: AddTrackUiState,
    onNameChange: (String) -> Unit,
    onDurationChange: (String) -> Unit,
    onNameBlur: () -> Unit,
    onDurationBlur: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Only the content scrolls: with the keyboard open on a small phone the actions stay on screen.
    Column(
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
            .testTag("add_track_sheet"),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .weight(1f, fill = false)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
        ) {
            Text(
                text = stringResource(R.string.add_track),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = album.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.testTag("add_track_album"),
            )
            CurrentTracks(album)
            Text(
                text = stringResource(R.string.add_track_position, album.nextTrackPosition),
                style = DetailMono,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("add_track_position"),
            )
            TrackField(
                value = uiState.name,
                onValueChange = onNameChange,
                onBlur = onNameBlur,
                label = R.string.track_field_name,
                error = uiState.nameError,
                tag = "track_name_input",
                enabled = !uiState.isSubmitting,
            )
            TrackField(
                value = uiState.duration,
                onValueChange = onDurationChange,
                onBlur = onDurationBlur,
                label = R.string.track_field_duration,
                error = uiState.durationError,
                tag = "track_duration_input",
                enabled = !uiState.isSubmitting,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
                visualTransformation = DurationMask,
            )
        }
        uiState.submitError?.let { SubmitError(it) }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
        ) {
            TextButton(
                onClick = onCancel,
                enabled = !uiState.isSubmitting,
                modifier = Modifier.testTag("add_track_cancel"),
            ) {
                Text(stringResource(R.string.add_track_cancel))
            }
            TerracottaAddButton(
                text = stringResource(if (uiState.isSubmitting) R.string.add_track_saving else R.string.add_track),
                onClick = onSave,
                enabled = uiState.canSave,
                modifier = Modifier.testTag("add_track_submit"),
            )
        }
    }
}

/** An album can have dozens of tracks: the list scrolls so the fields stay on screen. */
@Composable
private fun CurrentTracks(album: Album) {
    if (album.tracks.isEmpty()) return
    Column {
        Text(text = stringResource(R.string.add_track_current).uppercase(), style = LabelMono)
        Column(
            modifier = Modifier
                .padding(top = 4.dp)
                .heightIn(max = 120.dp)
                .verticalScroll(rememberScrollState())
                .testTag("add_track_current"),
        ) {
            album.numberedTracks.forEach { numbered ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                    Text(text = numbered.position, style = DetailMono, modifier = Modifier.width(44.dp))
                    Text(text = numbered.track.name, modifier = Modifier.weight(1f))
                    Text(text = numbered.track.duration, style = DetailMono)
                }
            }
        }
    }
}

@Composable
private fun TrackField(
    value: String,
    onValueChange: (String) -> Unit,
    onBlur: () -> Unit,
    @StringRes label: Int,
    @StringRes error: Int?,
    tag: String,
    enabled: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    var wasFocused by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(label)) },
        isError = error != null,
        supportingText = error?.let { { Text(stringResource(it), modifier = Modifier.testTag("${tag}_error")) } },
        singleLine = true,
        enabled = enabled,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focus ->
                if (wasFocused && !focus.isFocused) onBlur()
                wasFocused = focus.isFocused
            }
            .testTag(tag),
    )
}

@Composable
private fun SubmitError(error: UiState.Error) {
    val message = when (error.type) {
        ErrorType.NETWORK -> R.string.error_network
        ErrorType.NOT_FOUND -> R.string.error_not_found
        ErrorType.VALIDATION -> R.string.add_track_rejected
        else -> R.string.error_loading
    }
    Text(
        text = stringResource(message),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.testTag("add_track_error"),
    )
}
