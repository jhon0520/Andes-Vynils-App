package com.dev.wine.music.app.ui.artist

import com.dev.wine.music.app.domain.usecase.GetArtistsUseCase
import com.dev.wine.music.app.fakes.FakeArtistRepository
import com.dev.wine.music.app.fakes.TestData
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.util.MainDispatcherRule
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ArtistListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `emits Success when artists load`() {
        val viewModel = ArtistListViewModel(
            GetArtistsUseCase(FakeArtistRepository(artists = listOf(TestData.artist()))),
        )

        assertTrue(viewModel.uiState.value is UiState.Success)
    }

    @Test
    fun `emits Error when artists fail to load`() {
        val viewModel = ArtistListViewModel(
            GetArtistsUseCase(FakeArtistRepository(shouldFail = true)),
        )

        assertTrue(viewModel.uiState.value is UiState.Error)
    }
}
