package com.dev.wine.music.app.ui.album.detail

import androidx.lifecycle.SavedStateHandle
import com.dev.wine.music.app.domain.usecase.GetAlbumDetailUseCase
import com.dev.wine.music.app.fakes.FakeAlbumRepository
import com.dev.wine.music.app.fakes.TestData
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AlbumDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads the album whose id comes from navigation`() {
        val repository = FakeAlbumRepository(
            albums = listOf(TestData.album(id = 1), TestData.album(id = 2, name = "Poeta del pueblo")),
        )
        val savedStateHandle = SavedStateHandle(mapOf(AlbumDetailViewModel.ALBUM_ID_KEY to 2))

        val viewModel = AlbumDetailViewModel(savedStateHandle, GetAlbumDetailUseCase(repository))

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals("Poeta del pueblo", (state as UiState.Success).data.name)
    }
}
