package com.dev.wine.music.app.ui.album.create

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.AlbumGenre
import com.dev.wine.music.app.domain.model.NewAlbum
import com.dev.wine.music.app.domain.model.RecordLabel
import com.dev.wine.music.app.domain.usecase.CreateAlbumUseCase
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.common.toUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

enum class AlbumField { COVER, NAME, RELEASE_DATE, GENRE, RECORD_LABEL, DESCRIPTION }

/**
 * The form. [releaseDate] holds only the digits (ddMMyyyy); the field draws the slashes.
 * [fieldErrors] has every invalid field, and only the ones the user already [touched] are shown.
 */
@Immutable
data class AlbumCreateUiState(
    val cover: String = "",
    val name: String = "",
    val releaseDate: String = "",
    val genre: AlbumGenre? = null,
    val recordLabel: RecordLabel? = null,
    val description: String = "",
    val fieldErrors: Map<AlbumField, Int> = emptyMap(),
    val touched: Set<AlbumField> = emptySet(),
    val isSubmitting: Boolean = false,
    val submitError: UiState.Error? = null,
    val createdAlbumId: Int? = null,
) {
    val visibleErrors: Map<AlbumField, Int> get() = fieldErrors.filterKeys { it in touched }
    val canSubmit: Boolean get() = fieldErrors.isEmpty() && !isSubmitting
    val hasCoverPreview: Boolean get() = AlbumField.COVER !in fieldErrors
}

@HiltViewModel
class AlbumCreateViewModel @Inject constructor(
    private val createAlbum: CreateAlbumUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlbumCreateUiState().validated())
    val uiState: StateFlow<AlbumCreateUiState> = _uiState.asStateFlow()

    fun onCoverChange(value: String) = edit { copy(cover = value) }

    fun onNameChange(value: String) = edit { copy(name = value) }

    fun onReleaseDateChange(value: String) = edit {
        copy(releaseDate = value.filter(Char::isDigit).take(DATE_DIGITS))
    }

    fun onGenreChange(value: AlbumGenre) = edit { copy(genre = value) }

    fun onRecordLabelChange(value: RecordLabel) = edit { copy(recordLabel = value) }

    fun onDescriptionChange(value: String) = edit {
        copy(description = value.take(MAX_DESCRIPTION))
    }

    /** The field lost focus: from now on its error, if any, is shown. */
    fun onFieldBlur(field: AlbumField) = _uiState.update { it.copy(touched = it.touched + field) }

    fun submit() {
        val state = _uiState.value
        if (!state.canSubmit) {
            _uiState.update { it.copy(touched = it.fieldErrors.keys) }
            return
        }
        _uiState.update { it.copy(isSubmitting = true, submitError = null) }
        viewModelScope.launch {
            try {
                val album = createAlbum(state.toNewAlbum())
                _uiState.update { it.copy(isSubmitting = false, createdAlbumId = album.id) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSubmitting = false, submitError = e.toUiError()) }
            }
        }
    }

    private fun edit(change: AlbumCreateUiState.() -> AlbumCreateUiState) = _uiState.update {
        it.change().copy(submitError = null).validated()
    }

    private fun AlbumCreateUiState.validated() = copy(
        fieldErrors = buildMap {
            coverError(cover)?.let { put(AlbumField.COVER, it) }
            if (name.isBlank()) put(AlbumField.NAME, R.string.album_error_required)
            releaseDateError(releaseDate)?.let { put(AlbumField.RELEASE_DATE, it) }
            if (genre == null) put(AlbumField.GENRE, R.string.album_error_required)
            if (recordLabel == null) put(AlbumField.RECORD_LABEL, R.string.album_error_required)
            if (description.isBlank()) put(AlbumField.DESCRIPTION, R.string.album_error_required)
        },
    )

    @StringRes
    private fun coverError(cover: String): Int? {
        val url = cover.trim()
        return when {
            url.isEmpty() -> R.string.album_error_required
            !url.startsWith(HTTPS) || url.length == HTTPS.length || url.any(Char::isWhitespace) -> R.string.album_error_cover
            else -> null
        }
    }

    @StringRes
    private fun releaseDateError(digits: String): Int? {
        if (digits.isEmpty()) return R.string.album_error_required
        if (digits.length != DATE_DIGITS || digits.takeLast(YEAR_DIGITS).toInt() < MIN_YEAR) {
            return R.string.album_error_date
        }
        val date = try {
            SimpleDateFormat("ddMMyyyy", Locale.US).apply { isLenient = false }.parse(digits)
        } catch (e: ParseException) {
            null
        } ?: return R.string.album_error_date
        val format = SimpleDateFormat("yyyyMMdd", Locale.US)
        return if (format.format(date) > format.format(Date())) R.string.album_error_future_date else null
    }

    /** Midnight in Colombia, the same way the backend's own examples send it. */
    private fun AlbumCreateUiState.toNewAlbum() = NewAlbum(
        name = name,
        cover = cover,
        releaseDate = "${releaseDate.substring(4)}-${releaseDate.substring(2, 4)}-${releaseDate.substring(0, 2)}T00:00:00-05:00",
        description = description,
        genre = checkNotNull(genre),
        recordLabel = checkNotNull(recordLabel),
    )

    companion object {
        const val MAX_DESCRIPTION = 500
        private const val DATE_DIGITS = 8
        private const val YEAR_DIGITS = 4
        private const val MIN_YEAR = 1000
        private const val HTTPS = "https://"
    }
}
