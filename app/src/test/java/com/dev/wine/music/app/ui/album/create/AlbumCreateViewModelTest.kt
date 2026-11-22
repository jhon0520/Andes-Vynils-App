package com.dev.wine.music.app.ui.album.create

import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.AlbumGenre
import com.dev.wine.music.app.domain.model.AppError
import com.dev.wine.music.app.domain.model.RecordLabel
import com.dev.wine.music.app.domain.usecase.CreateAlbumUseCase
import com.dev.wine.music.app.fakes.FakeAlbumRepository
import com.dev.wine.music.app.ui.common.ErrorType
import com.dev.wine.music.app.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AlbumCreateViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeAlbumRepository()
    private val viewModel = AlbumCreateViewModel(CreateAlbumUseCase(repository))
    private val state get() = viewModel.uiState.value

    private fun fillValidForm() = viewModel.apply {
        onCoverChange("https://example.com/siembra.jpg")
        onNameChange("Siembra")
        onReleaseDateChange("01081978")
        onGenreChange(AlbumGenre.SALSA)
        onRecordLabelChange(RecordLabel.FANIA_RECORDS)
        onDescriptionChange("Un clásico de la salsa")
    }

    @Test
    fun `an empty form cannot be submitted and shows no errors yet`() {
        assertFalse(state.canSubmit)
        assertTrue(state.visibleErrors.isEmpty())
    }

    @Test
    fun `an error shows only after leaving the field`() {
        viewModel.onCoverChange("http://example.com")
        assertTrue(state.visibleErrors.isEmpty())

        viewModel.onFieldBlur(AlbumField.COVER)

        assertEquals(R.string.album_error_cover, state.visibleErrors[AlbumField.COVER])
    }

    @Test
    fun `the cover must start with https and then shows a preview`() {
        viewModel.onCoverChange("http://example.com/a.jpg")
        assertFalse(state.hasCoverPreview)

        viewModel.onCoverChange("https://example.com/a.jpg")

        assertTrue(state.hasCoverPreview)
    }

    @Test
    fun `an impossible date is invalid`() {
        fillValidForm()

        viewModel.onReleaseDateChange("31021990")

        assertEquals(R.string.album_error_date, state.fieldErrors[AlbumField.RELEASE_DATE])
    }

    @Test
    fun `an incomplete date is invalid`() {
        fillValidForm()

        viewModel.onReleaseDateChange("0108")

        assertEquals(R.string.album_error_date, state.fieldErrors[AlbumField.RELEASE_DATE])
    }

    @Test
    fun `a future date is rejected and today is accepted`() {
        fillValidForm()
        val format = SimpleDateFormat("ddMMyyyy", Locale.US)
        val calendar = Calendar.getInstance()
        viewModel.onReleaseDateChange(format.format(calendar.time))
        assertNull(state.fieldErrors[AlbumField.RELEASE_DATE])

        calendar.add(Calendar.DAY_OF_YEAR, 1)
        viewModel.onReleaseDateChange(format.format(calendar.time))

        assertEquals(R.string.album_error_future_date, state.fieldErrors[AlbumField.RELEASE_DATE])
    }

    @Test
    fun `the date keeps only digits`() {
        viewModel.onReleaseDateChange("01/08/1978")

        assertEquals("01081978", state.releaseDate)
    }

    @Test
    fun `the description stops at 500 characters`() {
        viewModel.onDescriptionChange("a".repeat(600))

        assertEquals(AlbumCreateViewModel.MAX_DESCRIPTION, state.description.length)
    }

    @Test
    fun `a valid form sends the album with the iso date`() {
        fillValidForm()
        assertTrue(state.canSubmit)

        viewModel.submit()

        val sent = repository.createdAlbums.single()
        assertEquals("1978-08-01T00:00:00-05:00", sent.releaseDate)
        assertEquals(AlbumGenre.SALSA, sent.genre)
        assertEquals(FakeAlbumRepository.CREATED_ALBUM_ID, state.createdAlbumId)
        assertFalse(state.isSubmitting)
    }

    @Test
    fun `submitting an invalid form marks the errors and calls nothing`() {
        viewModel.submit()

        assertEquals(AlbumField.entries.size, state.visibleErrors.size)
        assertTrue(repository.createdAlbums.isEmpty())
    }

    @Test
    fun `a rejection keeps the typed values and lets the user try again`() {
        fillValidForm()
        repository.shouldFail = true
        repository.error = AppError.Validation("rejected")

        viewModel.submit()

        assertEquals(ErrorType.VALIDATION, state.submitError?.type)
        assertEquals("Siembra", state.name)
        assertTrue(state.canSubmit)

        repository.shouldFail = false
        viewModel.submit()

        assertNull(state.submitError)
        assertEquals(FakeAlbumRepository.CREATED_ALBUM_ID, state.createdAlbumId)
    }

    @Test
    fun `a server error keeps the typed values`() {
        fillValidForm()
        repository.shouldFail = true
        repository.error = AppError.Server("boom")

        viewModel.submit()

        assertEquals(ErrorType.SERVER, state.submitError?.type)
        assertEquals("01081978", state.releaseDate)
    }
}
