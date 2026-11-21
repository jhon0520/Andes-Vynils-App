package com.dev.wine.music.app.ui.album.track

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.NewTrack
import com.dev.wine.music.app.domain.usecase.AddTrackUseCase
import com.dev.wine.music.app.ui.album.detail.AlbumDetailViewModel
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.common.toUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** The sheet. [duration] holds only the digits (mmss); the field draws the colon. */
@Immutable
data class AddTrackUiState(
    val name: String = "",
    val duration: String = "",
    val nameTouched: Boolean = false,
    val durationTouched: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitError: UiState.Error? = null,
    val isSaved: Boolean = false,
) {
    @get:StringRes
    val nameError: Int? get() = if (nameTouched && name.isBlank()) R.string.track_error_required else null

    @get:StringRes
    val durationError: Int?
        get() = when {
            !durationTouched || isDurationValid -> null
            duration.isEmpty() -> R.string.track_error_required
            else -> R.string.track_error_duration
        }

    val canSave: Boolean get() = name.isNotBlank() && isDurationValid && !isSubmitting

    private val isDurationValid: Boolean
        get() = duration.length == DURATION_DIGITS && duration.substring(2).toInt() <= MAX_SECONDS

    companion object {
        const val DURATION_DIGITS = 4
        private const val MAX_SECONDS = 59
    }
}

/** Shares the album id of the detail: the sheet lives in the same navigation entry. */
@HiltViewModel
class AddTrackViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addTrack: AddTrackUseCase,
) : ViewModel() {

    private val albumId: Int = checkNotNull(savedStateHandle[AlbumDetailViewModel.ALBUM_ID_KEY])

    private val _uiState = MutableStateFlow(AddTrackUiState())
    val uiState: StateFlow<AddTrackUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, submitError = null) }

    fun onDurationChange(value: String) = _uiState.update {
        it.copy(duration = value.filter(Char::isDigit).take(AddTrackUiState.DURATION_DIGITS), submitError = null)
    }

    fun onNameBlur() = _uiState.update { it.copy(nameTouched = true) }

    fun onDurationBlur() = _uiState.update { it.copy(durationTouched = true) }

    fun save() {
        val state = _uiState.value
        if (!state.canSave) {
            _uiState.update { it.copy(nameTouched = true, durationTouched = true) }
            return
        }
        _uiState.update { it.copy(isSubmitting = true, submitError = null) }
        viewModelScope.launch {
            try {
                addTrack(albumId, NewTrack(state.name, "${state.duration.take(2)}:${state.duration.drop(2)}"))
                _uiState.update { it.copy(isSubmitting = false, isSaved = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSubmitting = false, submitError = e.toUiError()) }
            }
        }
    }

    /** This ViewModel outlives the sheet, so each time it opens it must start empty. */
    fun reset() {
        _uiState.value = AddTrackUiState()
    }
}
