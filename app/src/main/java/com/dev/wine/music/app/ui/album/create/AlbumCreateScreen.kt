package com.dev.wine.music.app.ui.album.create

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.AlbumGenre
import com.dev.wine.music.app.domain.model.RecordLabel
import com.dev.wine.music.app.ui.album.create.AlbumCreateViewModel.Companion.MAX_DESCRIPTION
import com.dev.wine.music.app.ui.common.AnnounceOnChange
import com.dev.wine.music.app.ui.common.ErrorType
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.components.DetailTopBar
import com.dev.wine.music.app.ui.components.MaskedDigitsTransformation
import com.dev.wine.music.app.ui.components.TerracottaAddButton
import com.dev.wine.music.app.ui.components.labelRes

private val DateMask = MaskedDigitsTransformation(separator = '/', separatorsAfter = listOf(2, 4))

/** The events of the form, so the stateless content does not take a dozen lambdas. */
data class AlbumCreateActions(
    val onCoverChange: (String) -> Unit = {},
    val onNameChange: (String) -> Unit = {},
    val onReleaseDateChange: (String) -> Unit = {},
    val onGenreChange: (AlbumGenre) -> Unit = {},
    val onRecordLabelChange: (RecordLabel) -> Unit = {},
    val onDescriptionChange: (String) -> Unit = {},
    val onFieldBlur: (AlbumField) -> Unit = {},
    val onSubmit: () -> Unit = {},
    val onBack: () -> Unit = {},
    val onAddTracks: (Int) -> Unit = {},
    val onViewAlbum: (Int) -> Unit = {},
    val onNotNow: () -> Unit = {},
)

@Composable
fun AlbumCreateScreen(
    onBack: () -> Unit,
    onAddTracks: (albumId: Int) -> Unit,
    onViewAlbum: (albumId: Int) -> Unit,
    onNotNow: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlbumCreateViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actions = remember(viewModel, onBack, onAddTracks, onViewAlbum, onNotNow) {
        AlbumCreateActions(
            onCoverChange = viewModel::onCoverChange,
            onNameChange = viewModel::onNameChange,
            onReleaseDateChange = viewModel::onReleaseDateChange,
            onGenreChange = viewModel::onGenreChange,
            onRecordLabelChange = viewModel::onRecordLabelChange,
            onDescriptionChange = viewModel::onDescriptionChange,
            onFieldBlur = viewModel::onFieldBlur,
            onSubmit = viewModel::submit,
            onBack = onBack,
            onAddTracks = onAddTracks,
            onViewAlbum = onViewAlbum,
            onNotNow = onNotNow,
        )
    }
    AlbumCreateContent(uiState = uiState, actions = actions, modifier = modifier)
}

@Composable
fun AlbumCreateContent(
    uiState: AlbumCreateUiState,
    actions: AlbumCreateActions,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().imePadding().testTag("album_create")) {
        DetailTopBar(eyebrow = stringResource(R.string.album_create_eyebrow), onBack = actions.onBack)
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("album_create_form"),
        ) {
            Text(
                // Not "Crear álbum": the submit button says that, and two equal labels confuse TalkBack.
                text = stringResource(R.string.album_create_title),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { heading() },
            )
            FormNotice(errorCount = uiState.visibleErrors.size)
            AlbumFields(uiState = uiState, actions = actions)
            uiState.submitError?.let { SubmitError(it) }
            TerracottaAddButton(
                text = stringResource(if (uiState.isSubmitting) R.string.album_create_submitting else R.string.create_album),
                onClick = actions.onSubmit,
                enabled = uiState.canSubmit,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 24.dp).testTag("album_create_submit"),
            )
        }
    }
    uiState.createdAlbumId?.let { albumId ->
        AlbumCreatedDialog(
            albumName = uiState.name.trim(),
            onAddTracks = { actions.onAddTracks(albumId) },
            onViewAlbum = { actions.onViewAlbum(albumId) },
            onNotNow = actions.onNotNow,
        )
    }
}

@Composable
private fun FormNotice(errorCount: Int) {
    val notice = if (errorCount > 0) pluralStringResource(R.plurals.album_form_review, errorCount, errorCount) else null
    // D-18: the notice appears while the focus is on another field, so TalkBack says it.
    AnnounceOnChange(message = notice, key = errorCount)
    if (notice == null) return
    Text(
        text = notice,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onErrorContainer,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(4.dp))
            .padding(12.dp)
            .testTag("album_form_notice"),
    )
}

@Composable
private fun AlbumFields(uiState: AlbumCreateUiState, actions: AlbumCreateActions) {
    val errors = uiState.visibleErrors
    val enabled = !uiState.isSubmitting
    FormTextField(
        value = uiState.cover,
        onValueChange = actions.onCoverChange,
        label = R.string.album_field_cover,
        error = errors[AlbumField.COVER],
        tag = "album_cover_input",
        enabled = enabled,
        onBlur = { actions.onFieldBlur(AlbumField.COVER) },
        keyboardType = KeyboardType.Uri,
    )
    if (uiState.hasCoverPreview) {
        AsyncImage(
            model = uiState.cover.trim(),
            // Without a name yet it said "Portada de ." (D-20).
            contentDescription = if (uiState.name.isBlank()) {
                stringResource(R.string.album_cover_preview_description)
            } else {
                stringResource(R.string.album_cover_description, uiState.name.trim())
            },
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .testTag("album_cover_preview"),
        )
    }
    FormTextField(
        value = uiState.name,
        onValueChange = actions.onNameChange,
        label = R.string.album_field_name,
        error = errors[AlbumField.NAME],
        tag = "album_name_input",
        enabled = enabled,
        onBlur = { actions.onFieldBlur(AlbumField.NAME) },
    )
    FormTextField(
        value = uiState.releaseDate,
        onValueChange = actions.onReleaseDateChange,
        label = R.string.album_field_release_date,
        error = errors[AlbumField.RELEASE_DATE],
        tag = "album_release_date_input",
        enabled = enabled,
        onBlur = { actions.onFieldBlur(AlbumField.RELEASE_DATE) },
        keyboardType = KeyboardType.Number,
        visualTransformation = DateMask,
    )
    FormDropdown(
        label = R.string.album_field_genre,
        selected = uiState.genre?.let { stringResource(it.labelRes()) }.orEmpty(),
        options = AlbumGenre.entries,
        optionLabel = { stringResource(it.labelRes()) },
        optionTag = { "album_genre_option_${it.name.lowercase()}" },
        onSelect = actions.onGenreChange,
        onDismiss = { actions.onFieldBlur(AlbumField.GENRE) },
        error = errors[AlbumField.GENRE],
        tag = "album_genre_input",
        enabled = enabled,
    )
    FormDropdown(
        label = R.string.album_field_record_label,
        selected = uiState.recordLabel?.apiValue.orEmpty(),
        options = RecordLabel.entries,
        optionLabel = { it.apiValue },
        optionTag = { "album_label_option_${it.name.lowercase()}" },
        onSelect = actions.onRecordLabelChange,
        onDismiss = { actions.onFieldBlur(AlbumField.RECORD_LABEL) },
        error = errors[AlbumField.RECORD_LABEL],
        tag = "album_label_input",
        enabled = enabled,
    )
    FormTextField(
        value = uiState.description,
        onValueChange = actions.onDescriptionChange,
        label = R.string.album_field_description,
        error = errors[AlbumField.DESCRIPTION],
        tag = "album_description_input",
        enabled = enabled,
        onBlur = { actions.onFieldBlur(AlbumField.DESCRIPTION) },
        singleLine = false,
        imeAction = ImeAction.Default,
        counter = stringResource(R.string.album_description_counter, uiState.description.length, MAX_DESCRIPTION),
    )
}

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes label: Int,
    @StringRes error: Int?,
    tag: String,
    enabled: Boolean,
    onBlur: () -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    counter: String? = null,
) {
    var wasFocused by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(label)) },
        isError = error != null,
        supportingText = if (error != null || counter != null) {
            {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = error?.let { stringResource(it) }.orEmpty(),
                        modifier = Modifier.weight(1f).testTag("${tag}_error"),
                    )
                    counter?.let { Text(it, modifier = Modifier.testTag("${tag}_counter")) }
                }
            }
        } else {
            null
        },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun <T> FormDropdown(
    @StringRes label: Int,
    selected: String,
    options: List<T>,
    optionTag: (T) -> String,
    onSelect: (T) -> Unit,
    onDismiss: () -> Unit,
    @StringRes error: Int?,
    tag: String,
    enabled: Boolean,
    optionLabel: @Composable (T) -> String,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = it },
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(stringResource(label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            isError = error != null,
            supportingText = error?.let { { Text(stringResource(it), modifier = Modifier.testTag("${tag}_error")) } },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled)
                .testTag(tag),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                onDismiss()
            },
            // A menu is its own window: it does not inherit the flag set on the root of the UI.
            modifier = Modifier.semantics { testTagsAsResourceId = true },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                    modifier = Modifier.testTag(optionTag(option)),
                )
            }
        }
    }
}

@Composable
private fun SubmitError(error: UiState.Error) {
    val message = when (error.type) {
        ErrorType.NETWORK -> R.string.error_network
        ErrorType.VALIDATION -> R.string.album_create_rejected
        else -> R.string.error_loading
    }
    Text(
        text = stringResource(message),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.testTag("album_create_error"),
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun AlbumCreatedDialog(
    albumName: String,
    onAddTracks: () -> Unit,
    onViewAlbum: () -> Unit,
    onNotNow: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onNotNow,
        title = { Text(stringResource(R.string.album_created_title)) },
        text = { Text(stringResource(R.string.album_created_message, albumName)) },
        confirmButton = {
            TextButton(onClick = onAddTracks, modifier = Modifier.testTag("album_created_add_tracks")) {
                Text(stringResource(R.string.album_created_add_tracks))
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onNotNow, modifier = Modifier.testTag("album_created_not_now")) {
                    Text(stringResource(R.string.album_created_not_now))
                }
                TextButton(onClick = onViewAlbum, modifier = Modifier.testTag("album_created_view")) {
                    Text(stringResource(R.string.album_created_view))
                }
            }
        },
        // A dialog is its own window: it does not inherit the flag set on the root of the UI.
        modifier = Modifier.semantics { testTagsAsResourceId = true }.testTag("album_created_dialog"),
    )
}
