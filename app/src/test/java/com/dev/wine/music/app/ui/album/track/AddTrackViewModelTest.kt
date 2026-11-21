package com.dev.wine.music.app.ui.album.track

import androidx.lifecycle.SavedStateHandle
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.AppError
import com.dev.wine.music.app.domain.model.NewTrack
import com.dev.wine.music.app.domain.usecase.AddTrackUseCase
import com.dev.wine.music.app.fakes.FakeAlbumRepository
import com.dev.wine.music.app.ui.album.detail.AlbumDetailViewModel
import com.dev.wine.music.app.ui.common.ErrorType
import com.dev.wine.music.app.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AddTrackViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeAlbumRepository()
    private val viewModel = AddTrackViewModel(
        SavedStateHandle(mapOf(AlbumDetailViewModel.ALBUM_ID_KEY to 100)),
        AddTrackUseCase(repository),
    )
    private val state get() = viewModel.uiState.value

    private fun fillValid() = viewModel.apply {
        onNameChange("Decisiones")
        onDurationChange("0505")
    }

    @Test
    fun `an empty sheet cannot be saved and shows no errors yet`() {
        assertFalse(state.canSave)
        assertNull(state.nameError)
        assertNull(state.durationError)
    }

    @Test
    fun `the name is required once the field is left`() {
        viewModel.onNameBlur()

        assertEquals(R.string.track_error_required, state.nameError)
    }

    @Test
    fun `seconds above 59 are invalid`() {
        viewModel.onNameChange("Decisiones")
        viewModel.onDurationChange("0560")
        viewModel.onDurationBlur()

        assertFalse(state.canSave)
        assertEquals(R.string.track_error_duration, state.durationError)
    }

    @Test
    fun `seconds 00 and 59 are valid`() {
        viewModel.onNameChange("Decisiones")

        viewModel.onDurationChange("0500")
        assertTrue(state.canSave)

        viewModel.onDurationChange("0559")
        assertTrue(state.canSave)
    }

    @Test
    fun `an incomplete duration is invalid`() {
        viewModel.onNameChange("Decisiones")
        viewModel.onDurationChange("05")

        assertFalse(state.canSave)
    }

    @Test
    fun `the duration keeps only four digits`() {
        viewModel.onDurationChange("05:0599")

        assertEquals("0505", state.duration)
    }

    @Test
    fun `saving sends the track to the album of the route as mm ss`() {
        fillValid()

        viewModel.save()

        assertEquals(listOf(100 to NewTrack("Decisiones", "05:05")), repository.addedTracks)
        assertTrue(state.isSaved)
    }

    @Test
    fun `a failure keeps the data and lets the user try again`() {
        fillValid()
        repository.shouldFail = true
        repository.error = AppError.Server("boom")

        viewModel.save()

        assertEquals(ErrorType.SERVER, state.submitError?.type)
        assertEquals("Decisiones", state.name)
        assertEquals("0505", state.duration)
        assertFalse(state.isSaved)

        repository.shouldFail = false
        viewModel.save()

        assertTrue(state.isSaved)
    }

    @Test
    fun `reset leaves the sheet empty for the next time it opens`() {
        fillValid()
        viewModel.save()

        viewModel.reset()

        assertEquals(AddTrackUiState(), state)
    }
}
