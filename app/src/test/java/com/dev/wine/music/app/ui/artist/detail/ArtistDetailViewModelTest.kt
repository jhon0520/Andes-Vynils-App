package com.dev.wine.music.app.ui.artist.detail

import androidx.lifecycle.SavedStateHandle
import com.dev.wine.music.app.domain.model.AppError
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.domain.usecase.GetArtistDetailUseCase
import com.dev.wine.music.app.fakes.FakeArtistRepository
import com.dev.wine.music.app.fakes.TestData
import com.dev.wine.music.app.fakes.testDispatcher
import com.dev.wine.music.app.ui.common.ErrorType
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ArtistDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val ruben = TestData.artist(id = 1, name = "Rubén Blades")
    private val queen = TestData.artist(id = 2, name = "Queen", type = ArtistType.BAND)

    private val repository = FakeArtistRepository(artists = listOf(ruben, queen)).apply {
        details = listOf(TestData.artistDetail(artist = ruben), TestData.artistDetail(artist = queen))
    }

    private fun createViewModel(artistId: Int = 2, type: ArtistType = ArtistType.BAND) = ArtistDetailViewModel(
        SavedStateHandle(
            mapOf(
                ArtistDetailViewModel.ARTIST_ID_KEY to artistId,
                ArtistDetailViewModel.TYPE_KEY to type.name,
            ),
        ),
        GetArtistDetailUseCase(repository, testDispatcher),
    )

    @Test
    fun `loads the artist whose id and type come from navigation`() {
        val state = createViewModel().uiState.value

        assertTrue(state is UiState.Success)
        assertEquals("Queen", (state as UiState.Success).data.artist.name)
        assertEquals(ArtistType.BAND, state.data.artist.type)
    }

    @Test
    fun `a musician id loads the musician`() {
        val state = createViewModel(artistId = 1, type = ArtistType.MUSICIAN).uiState.value as UiState.Success

        assertEquals("Rubén Blades", state.data.artist.name)
    }

    @Test
    fun `emits a network error when the artist cannot load`() {
        repository.shouldFail = true
        repository.error = AppError.Network()

        val state = createViewModel().uiState.value as UiState.Error

        assertEquals(ErrorType.NETWORK, state.type)
    }

    @Test
    fun `a missing artist shows NOT_FOUND`() {
        repository.shouldFail = true
        repository.error = AppError.NotFound("The band with the given id was not found")

        val state = createViewModel().uiState.value as UiState.Error

        assertEquals(ErrorType.NOT_FOUND, state.type)
    }

    @Test
    fun `retrying after an error loads the artist`() {
        repository.shouldFail = true
        val viewModel = createViewModel()
        assertTrue(viewModel.uiState.value is UiState.Error)

        repository.shouldFail = false
        viewModel.loadArtist()

        assertTrue(viewModel.uiState.value is UiState.Success)
    }
}
